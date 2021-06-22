package com.example.sc_hathunethu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.TOP;

public class ViewService extends Service {

    private static final String TAG="TestApp";
    private Context context;
    private WindowManager WM;
    private TextView textView;
    WindowManager.LayoutParams LP;

    private String viewText;

    private Timer mTimer = null;
    private Handler mHandler = new Handler();

    private boolean PM_Check;
    private boolean Bat_Check;
    private boolean Thermal_Check;
    private boolean NetWork_Check;
    private boolean SB_Check;
    private boolean viewTime_Check;

    private int view_Time;

    private long start_mills;

    private String brand;
    private String manufacturer;
    private String model;

    private int color_Setting;
    private int logViewColor;

    private String statusOf5G;

    @Override
    public void onCreate(){
        Log.d(TAG,"onCreate");
       super.onCreate();
        context=getApplicationContext();
    //    viewText="Service Start";

       // ScreenKeepView(viewText);

        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        //設定値の呼び出し
        view_Time= dataStore.getInt("viewInterval",1000);
        PM_Check=dataStore.getBoolean("view_PM",true);
        Bat_Check=dataStore.getBoolean("view_Bat",true);
        Thermal_Check=dataStore.getBoolean("view_Thermal",true);
        NetWork_Check=dataStore.getBoolean("view_Network",true);
        SB_Check=dataStore.getBoolean("view_SB",true);
        viewTime_Check=dataStore.getBoolean("view_Time_Setting",false);

        color_Setting=dataStore.getInt("viewColor",R.id.view_Blue);
        Log.d(TAG,"viewColor:"+color_Setting);
        //設定値から表示する色のリソースＩＤを取得
        switch (color_Setting){
            case R.id.view_Red:
                logViewColor=android.R.color.holo_red_dark;
                break;
            case R.id.view_Blue:
                logViewColor=android.R.color.holo_blue_dark;
                break;
            case R.id.view_Green:
                logViewColor=android.R.color.holo_green_light;
                break;
            case R.id.view_White:
                logViewColor=android.R.color.white;
                break;
            case R.id.view_Black:
                logViewColor=android.R.color.black;
                break;
        }

        //開始時刻をlongで取得
        start_mills=System.currentTimeMillis();
        //端末情報の取得　この後の電流値の補正で用いる
        brand= Build.BRAND;
        Log.d(TAG,"BRAND:"+brand);
        manufacturer=Build.MANUFACTURER;
        Log.d(TAG,"MANUFACTURER:"+manufacturer);
        model=Build.MODEL;
        Log.d(TAG,"MODEL:"+model);
        String product=Build.PRODUCT;
        Log.d(TAG,product);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("debug", "onStartCommand()");



        int requestCode = intent.getIntExtra("REQUEST_CODE",0);
        Context context = getApplicationContext();
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(
                channelId, title , NotificationManager.IMPORTANCE_DEFAULT);

        if(notificationManager != null){
            notificationManager.createNotificationChannel(channel);
            //    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    // android標準アイコンから
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setContentText("Floating View ON!!")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    //            .setGroup(GROUP_KEY_WORK_EMAIL)
                    .build();

            // startForeground 第一引数のidで通知を識別
            startForeground(3, notification);
        }

        setViewTimer(view_Time);

        //return START_NOT_STICKY;
        //return START_STICKY;
        return START_REDELIVER_INTENT;
    }


    public void onDestroy() {
        super.onDestroy();
        //WMの終了
        if(WM!=null) {
            WM.removeViewImmediate(textView);
        }
        //Timerの終了
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        // Service終了
        stopSelf();
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void ScreenKeepView(String string){

        if (textView != null){
            WM.removeView(textView);
        }

        WM = (WindowManager)getSystemService(WINDOW_SERVICE);



        textView = new TextView(ViewService.this);
        textView.setText(string);



        textView.setTextColor(ContextCompat.getColor(ViewService.this,logViewColor));
        textView.setTextSize(16f);

        int LAYOUOT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            LAYOUOT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUOT_FLAG = WindowManager.LayoutParams.TYPE_STATUS_BAR;
        }
        LP = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUOT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        |WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        //     |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        //     |WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT);

        LP.gravity = Gravity.LEFT|TOP;
        LP.x = 50;
        LP.y = 200;

        WM.addView(textView, LP);
    }

    //Timerで定期処理
    public void setViewTimer(int freq){
        // 設定した間隔おきにログを取得する

        mTimer = new Timer(true);

        mTimer.schedule( new TimerTask(){

            @Override

            public void run(){

                mHandler.post( new Runnable(){

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    public void run(){
                        Log.d( TAG , "Timer run" );
                        String viewText=getViewText();
                        ScreenKeepView(viewText);
                    }

                });

            }

        }, 1, freq); //1ミリ秒後にintervalミリ秒ごとの繰り返し
    }

    //Viewに表示するテキストを取得する関数
    public String getViewText(){
        StringBuilder stringBuilder=new StringBuilder();
        GetTimeData gtd=new GetTimeData();
        if(viewTime_Check){
            stringBuilder
                    .append("経過時間：")
                    .append(gtd.getElapsedViewTime(start_mills));
        }else {
            stringBuilder.append(gtd.getNowDate());
        }



        if(PM_Check) {
            // Android10以上対象
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                //Thermal Statusを取得しString化
                PowerManager PM = (PowerManager) getSystemService(Context.POWER_SERVICE);
                int currentStat = PM.getCurrentThermalStatus();
                final String CS = String.valueOf(currentStat);

                stringBuilder
                        .append("\n")
                        .append("PowerManager:").append(CS);
            }else {
                stringBuilder
                        .append("\n")
                        .append("PowerManager:Unsupported");
            }
        }



        if(NetWork_Check){
            GetNetWorkData gtn=new GetNetWorkData(context);
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //5Gの検出機能
            read_5G_status(telephonyManager);

            String con_type=gtn.getConnectionType(capabilities);
            String has_internet=gtn.getInternet(capabilities);
            String data_type=gtn.getDataNetWorkType(telephonyManager);
            String voice_type=gtn.getVoiceNetWorkType(telephonyManager);
            String tether_status=gtn.getTetheringStatus();
            stringBuilder
                    .append("\n").append("Connection:").append(con_type)
                    .append("\n").append("Internet:").append(has_internet)
                    .append("\n").append("Data Type:").append(data_type)
                    .append("\n").append("Voice Type:").append(voice_type)
                    .append("\n").append("Tethering Status:").append(tether_status)
                    .append("\n").append("5G Status:").append(statusOf5G)
                    //帯域幅出してるらしい
                    .append("\n").append("LinkDownstreamBandWidth:").append(capabilities.getLinkDownstreamBandwidthKbps()/1000).append("Mbps")
                    .append("\n").append("LinkUpstreamBandWidth:").append(capabilities.getLinkUpstreamBandwidthKbps()/1000).append("Mbps");
        }

        if(SB_Check){
            // 端末画面の明るさを取得(0～255)
            String value = Settings.System.getString(this.getContentResolver(), "screen_brightness");
            stringBuilder.append("\n").append("ScreenBrightness:").append(value);
        }


        if(Bat_Check) {
            //インテントフィルターの生成とレシーバーへの登録
            IntentFilter intentFilter1 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent intent1 = context.registerReceiver(null, intentFilter1);
            //残量
            int level = intent1.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            //接続状態
            int plugged = intent1.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            //健康状態
            int health = intent1.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            //バッテリーステータス
            int batterystat = intent1.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            //電圧
            double voltage = intent1.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            //電流
            BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
            float ampere = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
            Log.d(TAG,"CurrentOrigin:"+ampere);

            //電流値にメーカーごとに補正をかける
            //SAMSUNGとHUAWEIはいらない
            if(manufacturer.equals("HUAWEI")|manufacturer.equals("Samsung")){
            }else {
                ampere=ampere/1000;
            }
            Log.d(TAG, "Current:"+ ampere);
            //バッテリー温度
            float temp = intent1.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10.0f;
            stringBuilder.append("\nBat Level:").append(level).append("%")
                         .append("\nBat Plugged:").append(plug(plugged))
                         .append("\nBat Health:").append(health(health)).
                          append("\nBat Status:").append(stat(batterystat))
                         .append("\nBat Voltage:").append(voltage / 1000).append("V")
                         .append("\nBat Currennt:").append(ampere).append("mA")
                         .append("\nBat Temp:").append(temp).append("℃");
        }
        /*
        if(Thermal_Check) {
            ArrayList<String> filenames = new ArrayList<>();
            switch (filePlace) {
                case 0:
                    for (int i = 0; i < thermalNum; i++) {
                        filenames.add("sys/devices/virtual/thermal/thermal_zone" + i + "/temp");
                        try (
                                BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames.get(i)))
                        ) {
                            String thermalData = bufferedReader.readLine();
                            //     String thermalzoneView = "thermal_zone" + i + ":" + thermalData + "\n";
                            stringBuilder.append(",").append(thermalData);
                            Log.d(TAG, "追加:"+ i);
                            //       thermalzoneView = "";
                        } catch (IOException e) {
                            Log.d(TAG, "IOException:ThermalZoneが読み取れない");
                            stringBuilder.append(",error");
                            e.printStackTrace();
                        }

                    }
                    break;
                case 1:
                    for (int i = 0; i < thermalNum; i++) {
                        filenames.add("sys/class/thermal/thermal_zone" + i + "/temp");
                        try (
                                BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames.get(i)))
                        ) {
                            String thermalData = bufferedReader.readLine();
                            //        String thermalzoneView = "thermal_zone" + i + ":" + thermalData + "\n";
                            stringBuilder.append(",").append(thermalData);
                            Log.d(TAG, "追加:"+ i);
                            //         thermalzoneView = "";
                        } catch (IOException e) {
                            Log.d(TAG, "IOException:ThermalZoneが読み取れない");
                            stringBuilder.append(",error");
                            e.printStackTrace();
                        }

                    }
                    break;
                case 2:
                    stopSelf();
                    break;
            }
        }

 */

        return stringBuilder.toString();
    }


    //バッテリーの健康状態を返す関数
    private String health(int h) {

        String return_health = "";

        switch (h) {
            case BatteryManager.BATTERY_HEALTH_COLD:
                return_health = "COLD";
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return_health = "DEAD";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return_health = "GOOD";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return_health = "Over_Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return_health = "Overheat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                return_health = "Unknown";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return_health = "Unspecified_Failure";
                break;
        }
        //バッテリーの健康状態を返す
        return return_health;
    }

    //接続状態を返す関数
    private String plug(int p) {

        String return_plug = "";

        switch (p) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return_plug = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                return_plug = "USB";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return_plug = "Wireless";
                break;
            default:
                return_plug = "Unplugged";
        }
        //接続状態を返す
        return return_plug;
    }

    //充電状態を返す関数
    private String stat(int s) {

        String return_status = "";

        switch (s) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return_status = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                return_status = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return_status = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return_status = "NotCharging";
                break;
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
                return_status = "Unknown";
                break;
        }
        //充電ステータスを返す
        return return_status;
    }

    public void read_5G_status(TelephonyManager telephonyManager){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            telephonyManager.listen(phoneStateListener2, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED);
        }else {
            statusOf5G="Unsupported";
        }
    }

    public final PhoneStateListener phoneStateListener2=new PhoneStateListener() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onDisplayInfoChanged(TelephonyDisplayInfo telephonyDisplayInfo) {

            /* OVERRIDE_NETWORK_TYPE_NONE = 0
             * OVERRIDE_NETWORK_TYPE_LTE_CA = 1
             * OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO = 2
             * OVERRIDE_NETWORK_TYPE_NR_NSA = 3
             * OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE =4*/

            int status =telephonyDisplayInfo.getOverrideNetworkType();
            switch (status) {
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_LTE_ADVANCED_PRO");
                    statusOf5G="LTE_ADVANCED_PRO";
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_LTE_CA");
                    statusOf5G="LTE_CA";
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NONE");
                    statusOf5G="NONE";
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NR_NSA");
                    statusOf5G="Sub-6";
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE");
                    statusOf5G="mmWave";
                    break;
            }
        }
    };
}