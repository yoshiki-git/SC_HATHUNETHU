package com.example.sc_hathunethu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //ログ保存のために必要なPermission
    private final int REQUEST_CODE = 1000;

    private static final String TAG="TestApp";


    Button btn_start;
    Button btn_stop;

    Button btn_view_start;
    Button btn_view_stop;

    Button btn_camera;
    Button btn_tether;

    Button btn_thermal;

    Button btn_scr_on;
    Button btn_scr_off;

    Button btn_network;

    Button btn_settei;

    Button btn_view_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
        };
        checkPermission(permissions, REQUEST_CODE);
        checkPermission();

        //Android11以降対象
        //直パスを使えるようにするpermissionの付与
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R) {
            checkPermission_R();
        }
        startApplication();
    }

    public void startApplication(){
        Log.d(TAG,"startApplication()");

        //画面点灯維持にWakeLockを使用
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "MyApp::MyWakelockTag");
        Log.d(TAG,wakeLock.toString());


        //ログのスタート
        btn_start=findViewById(R.id.ser_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"onClick");
                Intent intent=new Intent(getApplication(),MyService.class);
                intent.putExtra("REQUEST_CODE", 1);
                startForegroundService(intent);
                Log.d("TestApp","startForegroundService");
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);

            }
        });
        //ログのストップ
        btn_stop=findViewById(R.id.ser_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplication(),MyService.class);
                stopService(intent);
                btn_stop.setEnabled(false);
                btn_start.setEnabled(true);
            }
        });

        //ビューの表示スタート
        btn_view_start=findViewById(R.id.view_start);
        btn_view_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_intent=new Intent(getApplication(),ViewService.class);
                view_intent.putExtra("REQUEST_CODE", 1);
                startForegroundService(view_intent);
                Log.d(TAG,"ViewServiceStart");
                btn_view_start.setEnabled(false);
                btn_view_stop.setEnabled(true);
            }
        });


        //ビューの表示ストップ
        btn_view_stop=findViewById(R.id.view_stop);
        btn_view_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_stop_intent=new Intent(getApplication(),ViewService.class);
                stopService(view_stop_intent);
                btn_view_stop.setEnabled(false);
                btn_view_start.setEnabled(true);
            }
        });

        btn_scr_on=findViewById(R.id.scr_keep_start);
        btn_scr_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(getApplication(),MyService2.class);
                intent2.putExtra("REQUEST_CODE",1);
                startForegroundService(intent2);
                //Wake Lockをオン
                wakeLock.acquire();
                Log.d("TestApp",wakeLock.toString());
                Log.d("TestApp","scrServiceStart");
                btn_scr_on.setEnabled(false);
                btn_scr_off.setEnabled(true);
            }
        });

        btn_scr_off=findViewById(R.id.scr_keep_stop);
        btn_scr_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3=new Intent(getApplication(),MyService2.class);
                stopService(intent3);
                if(wakeLock.isHeld()) {
                    wakeLock.release();
                    Log.d(TAG,wakeLock.toString());
                }
                btn_scr_off.setEnabled(false);
                btn_scr_on.setEnabled(true);
            }
        });




        btn_tether=findViewById(R.id.btn_tether);
        btn_tether.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName("com.android.settings", "com.android.settings.TetherSettings");

                //     startActivity(intent);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "直接テザリングの設定を行ってください", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_camera=findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                CameraCall cameraCall = new CameraCall(context);
                cameraCall.tryCamera();
            }
        });

        btn_thermal=findViewById(R.id.btn_thermal_info);
        btn_thermal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, thermal_info_activity.class);
                startActivity(intent);
            }
        });

        btn_network=findViewById(R.id.btn_network);
        btn_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,NewWorkActivity.class);
                startActivity(intent);
            }
        });


        btn_settei=findViewById(R.id.btn_log_interval);
        btn_settei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Log_Interval_Activity.class);
                startActivity(intent);
            }
        });

        btn_view_setting=findViewById(R.id.btn_view_setting);
        btn_view_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,ViewSettingActivity.class);
                startActivity(intent);
            }
        });
    }



    //Permissionチェックのメソッド
    public void checkPermission(final String[] permissions, final int request_code) {
        // 許可されていないものだけダイアログが表示される
        ActivityCompat.requestPermissions(this, permissions, request_code);
    }

    // requestPermissionsのコールバック
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                   /*     Toast toast = Toast.makeText(this,
                                "Added Permission: " + permissions[i], Toast.LENGTH_SHORT);
                        toast.show(); */
                    } else {
                        Toast toast = Toast.makeText(this,
                                "設定より権限をオンにした後、アプリを再起動してください", Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        //Fragmentの場合はgetContext().getPackageName()
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
    }

    //WindowのほうのPermission
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission() {
        if (!Settings.canDrawOverlays(this)) {

            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    //Windowの方の結果の受け取り
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Log.d(TAG, "SYSTEM_ALERT_WINDOW permission not granted...");
                // SYSTEM_ALERT_WINDOW permission not granted...
                // nothing to do !
            }
        }
    }

    //直パス使えるようにするpermissionの付与
    @TargetApi(Build.VERSION_CODES.R)
    public void checkPermission_R() {

        if (Environment.isExternalStorageManager()) {
            //todo when permission is granted
            Log.d(TAG,"MANAGE_EXTERNAL_STORAGE is granted");
        } else {
            //request for the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }
}