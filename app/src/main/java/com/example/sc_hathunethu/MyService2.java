package com.example.sc_hathunethu;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import static android.view.Gravity.BOTTOM;

public class MyService2 extends Service {
    private WindowManager WM;
    private TextView textView;
    private Context context;
    WindowManager.LayoutParams LP;

    public void onCreate(){
        super.onCreate();
        context=getApplicationContext();
        ScreenKeepView();
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
                    .setContentText("Screen Keep ON!!")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    //            .setGroup(GROUP_KEY_WORK_EMAIL)
                    .build();

            // startForeground 第一引数のidで通知を識別
            startForeground(2, notification);
        }
        // 毎回Alarmを設定する
          setNextAlarmService(context);


        //return START_NOT_STICKY;
        //return START_STICKY;
        return START_REDELIVER_INTENT;
    }

    public void onDestroy() {
        super.onDestroy();
        if(WM!=null) {
            WM.removeViewImmediate(textView);
        }
        stopAlarmService();
       // Service終了
        stopSelf();
    }

    // 次のアラームの設定
    private void setNextAlarmService(Context context){

        // 10分毎のアラーム設定
        long repeatPeriod = 10*60*1000;

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

    public void ScreenKeepView(){

        WM = (WindowManager)getSystemService(WINDOW_SERVICE);



        textView = new TextView(MyService2.this);
        textView.setText("SCREEN KEEP ON");



        textView.setTextColor(ContextCompat.getColor(MyService2.this,android.R.color.holo_blue_bright));
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

        LP.gravity = Gravity.LEFT|BOTTOM;
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
