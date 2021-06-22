package com.example.sc_hathunethu;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.TOP;

public class MyService extends Service {
    private Context context;
    private String start_date;
    private int filePlace;
    private int thermalNum;
    private String fileName;
    private File file;
    private int bat_interval_time;
    private String viewText;

    private WindowManager WM;
    private TextView textView;
    WindowManager.LayoutParams LP;

    private Timer mTimer = null;
    private Handler mHandler = new Handler();

    private boolean PM_Check;
    private boolean Bat_Check;
    private boolean Thermal_Check;
    private boolean NetWork_Check;
    private boolean SB_Check;
    private boolean Dozelog_Check;
    private boolean LogTime_Check;

    private String brand;
    private String manufacturer;
    private String model;

    private long start_mills;

    private static final String TAG="TestApp";


    @Override
    public void onCreate(){
        Log.d(TAG,"onCreate");
        super.onCreate();
        //コンテキスト取得
        context=getApplicationContext();

        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        //設定値の呼び出し
        bat_interval_time= dataStore.getInt("logInterval",1000);
        PM_Check=dataStore.getBoolean("switch_PM",true);
        Bat_Check=dataStore.getBoolean("switch_Bat",true);
        Thermal_Check=dataStore.getBoolean("switch_Thermal",true);
        NetWork_Check=dataStore.getBoolean("switch_Network",true);
        SB_Check=dataStore.getBoolean("switch_SB",true);
        Dozelog_Check=dataStore.getBoolean("switch_dozelog",false);
        LogTime_Check=dataStore.getBoolean("switch_logTime",false);

        GetLogData gtd=new GetLogData(context);

        //ファイル名を現在時刻に設定
        GetTimeData getTimeData=new GetTimeData();
        String start_date = getTimeData.getFileName();
        //ファイルの拡張子を設定
        String fileName = start_date+"_Log" + ".txt";

        file=gtd.getFileStatus(fileName);

        //開始時刻をlongで取得
        start_mills=System.currentTimeMillis();


        try(FileReader fileReader=new FileReader("sys/devices/virtual/thermal/thermal_zone0/temp")) {
            filePlace =0;
        } catch (Exception e) {
            e.printStackTrace();
            try(FileReader fileReader2=new FileReader("sys/class/thermal/thermal_zone0/temp")){
                filePlace =1;
            }catch (Exception e1){
                e1.printStackTrace();
                Toast.makeText(getApplicationContext(),"このアプリでは端末の温度領域を読み取れません",Toast.LENGTH_SHORT).show();
                filePlace =2;
            }

        }
        //thermalzoneがいくつあるのかを調べる
        switch (filePlace){
            case 0:
                for(int i=0;i<1000;i++){
                    File dir=new File("sys/devices/virtual/thermal/thermal_zone"+i);
                    if(!dir.exists()){
                        thermalNum =i;
                        Log.d(TAG,"thermal_num:"+ thermalNum);
                        break;
                    }
                }
                break;
            case 1:
                for(int i=0;i<1000;i++){
                    File dir2=new File("sys/class/thermal/thermal_zone"+i);
                    if(!dir2.exists()){
                        thermalNum =i;
                        Log.d(TAG,"Thermal_num:"+ thermalNum);
                        break;
                    }
                }
                break;
            case 2:
                break;
        }
        //カラムの作成
        getColumn();

        //端末情報の取得　この後の電流値の補正で用いる
        brand= Build.BRAND;
        Log.d(TAG,"BRAND:"+brand);
        manufacturer=Build.MANUFACTURER;
        Log.d(TAG,"MANUFACTURER:"+manufacturer);
        model=Build.MODEL;
        Log.d(TAG,"MODEL:"+model);
        String product=Build.PRODUCT;
        Log.d(TAG,product);


        Log.d(TAG,"onCreate");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent,int flag,int startId) {
        int requestCode = intent.getIntExtra("REQUEST_CODE", 0);




        Log.d(TAG,"onStartCommland");
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // ForegroundにするためNotificationが必要、Contextを設定
        NotificationManager notificationManager =
                (NotificationManager) context.
                        getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Silent Notification");
        // 通知音を消さないと毎回通知音が出てしまう
        // この辺りの設定はcleanにしてから変更
        channel.setSound(null, null);
        // 通知ランプを消す
        channel.enableLights(false);
        channel.setLightColor(Color.BLUE);
        // 通知バイブレーション無し
        channel.enableVibration(false);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    // android標準アイコンから
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentText(viewText)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            // startForeground
            startForeground(1, notification);

            setBroadcastReceiver();

            if(Dozelog_Check){
            // 毎回Alarmを設定する
            setNextAlarmService(context);
            getThermalLogData();
            }else {
                setTimer();
            }

        }




     //   return START_NOT_STICKY;
        //return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if(Dozelog_Check) {
            stopAlarmService();
        }
        //   WM.removeViewImmediate(textView);
        // Service終了
        stopSelf();
    }


    // 次のアラームの設定
    private void setNextAlarmService(Context context){

        // 15分毎のアラーム設定
        long repeatPeriod = bat_interval_time;

        Intent intent = new Intent(context,MyService.class);

        long startMillis = System.currentTimeMillis() + repeatPeriod;

        PendingIntent pendingIntent
                = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(alarmManager != null){
            // Android Oreo 以上を想定
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    startMillis, pendingIntent);
        }
    }

    private void stopAlarmService(){
        Intent indent = new Intent(context, MyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, indent, 0);

        // アラームを解除する
        AlarmManager alarmManager
                = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }
    }

    //Timerで定期処理
    public void setTimer(){
        // 設定した間隔おきにログを取得する

        mTimer = new Timer(true);

        mTimer.schedule( new TimerTask(){

            @Override

            public void run(){

                mHandler.post( new Runnable(){

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    public void run(){
                        Log.d( TAG , "Timer run" );
                        getThermalLogData();
                    }

                });

            }

        }, 1, bat_interval_time); //1ミリ秒後にintervalミリ秒ごとの繰り返し
    }

    //カラムの取得
    public void getColumn(){
        GetLogData column_Write =new GetLogData(context);
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("Time");
        if(PM_Check){
            stringBuilder.append(",PowerManager");
        }
        /*
        //カメラオンオフ
        stringBuilder.append(",Camera");

         */

        if(NetWork_Check){
            stringBuilder.append(",").append("Connection")
                         .append(",").append("Internet")
                         .append(",").append("DataType")
                         .append(",").append("VoiceType")
                         .append(",").append("Tethering")
                         .append(",").append("DL_BandWidth[Mbps]")
                         .append(",").append("UL_BandWidth[Mbps]");


        }
        if(SB_Check){
            stringBuilder.append(",").append("ScreenBrightness");
        }
        if(Bat_Check){
            stringBuilder.append(",").append("Level").append("[%],").append("Plug").append(",")
                    .append("Health").append(",").append("Status").append(",")
                    .append("Voltage").append("[V],").append("Current").append("[mA],").append("Temp").append("[℃]");
        }
        if(Thermal_Check){
            ArrayList<String> filenames = new ArrayList<>();
            switch (filePlace) {
                case 0:
                    for (int i = 0; i < thermalNum; i++) {
                        filenames.add("sys/devices/virtual/thermal/thermal_zone" + i + "/type");
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
                        filenames.add("sys/class/thermal/thermal_zone" + i + "/type");
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

        stringBuilder.append("\n");
        String column_Data=stringBuilder.toString();
        column_Write.getLog(file,column_Data);
    }

//ログデータの取得
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getThermalLogData(){
        //Timerごとにインスタンス生成したくなかったからfinalつけたけど、まずかったらTimerの中に入れてfinal消す
       // thermal_context =getApplicationContext();
        final GetLogData getLogData=new GetLogData(context);
        StringBuilder stringBuilder=new StringBuilder();
        GetTimeData gtd=new GetTimeData();
        if(LogTime_Check){
            stringBuilder.append(gtd.getElapsedTime(start_mills));
        }else {
            stringBuilder.append(gtd.getNowTime());
        }



        if(PM_Check) {
           // Android10以上対象
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                //Thermal Statusを取得しString化
                PowerManager PM = (PowerManager) getSystemService(Context.POWER_SERVICE);
            int currentStat = PM.getCurrentThermalStatus();
            final String CS = String.valueOf(currentStat);

            stringBuilder.append(",").append(CS);
         }else {
                stringBuilder.append(",Unsupported");
            }
        }
/*
        //カメラのオンオフ
        String cameraActive=CameraForeground();
        stringBuilder.append(",").append(cameraActive);

 */


        if(NetWork_Check){
            GetNetWorkData gtn=new GetNetWorkData(context);
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String con_type=gtn.getConnectionType(capabilities);
            String has_internet=gtn.getInternet(capabilities);
            String data_type=gtn.getDataNetWorkType(telephonyManager);
            String voice_type=gtn.getVoiceNetWorkType(telephonyManager);
            String tether_status=gtn.getTetheringStatus();
            stringBuilder
                    .append(",").append(con_type)
                    .append(",").append(has_internet)
                    .append(",").append(data_type)
                    .append(",").append(voice_type)
                    .append(",").append(tether_status)
                    //帯域幅らしい
                    .append(",").append(capabilities.getLinkDownstreamBandwidthKbps()/1000)
                    .append(",").append(capabilities.getLinkUpstreamBandwidthKbps()/1000);
        }

        if(SB_Check){
            // 端末画面の明るさを取得(0～255)
            String value = Settings.System.getString(this.getContentResolver(), "screen_brightness");
            stringBuilder.append(",").append(value);
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
            stringBuilder.append(",").append(level).append(",").append(plug(plugged)).append(",")
                    .append(health(health)).append(",").append(stat(batterystat)).append(",")
                    .append(voltage / 1000).append(",").append(ampere).append(",").append(temp);
        }
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
            stringBuilder.append("\n");

        String log_data = stringBuilder.toString();
   //     String view_string=view_text.toString();


        getLogData.getLog(file, log_data);
    }

    public void setBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SHUTDOWN);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onReceive(Context context, Intent intent) {

            GetLogData getLogData=new GetLogData(context);
            GetTimeData getTimeData=new GetTimeData();
            if(LogTime_Check){
                double nowTime=getTimeData.getElapsedTime(start_mills);
                getLogData.getLog(file,nowTime+",ShutDown\n");
                Log.d(TAG,nowTime+",ShutDown");
            }else {
                String nowTime=getTimeData.getNowTime();
                getLogData.getLog(file,nowTime+",ShutDown\n");
                Log.d(TAG,nowTime+",ShutDown");
            }
            getThermalLogData();
        }
    };

    //カメラがForegroundか確認する関数
    private String CameraForeground(){
        String isCamera="OFF";

        ArrayList<String> appname = new ArrayList<String>();

        //アクティビティマネージャーの起動
        ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        //動いてるアプリ一覧を取得
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos=activityManager.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appinfo:appProcessInfos){
            Log.d(TAG,"PROCESS:"+appinfo.processName);
            Log.d(TAG,"PROCESS:"+appinfo.importance);
            //
            if(appinfo.processName.equals("com.huawei.camera")){
                if(appinfo.importance== ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    isCamera="TRUE";
                }
            }
        }

        return isCamera;
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
    @SuppressLint("RtlHardcoded")
    public void ScreenKeepView(String string){
        if(WM!=null){
            WM.removeViewImmediate(textView);
        }
        WM = (WindowManager)getSystemService(WINDOW_SERVICE);
        Log.d(TAG,string);


        textView = new TextView(MyService.this);
        textView.setText(string);



        textView.setTextColor(ContextCompat.getColor(MyService.this,android.R.color.holo_blue_light));
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
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
