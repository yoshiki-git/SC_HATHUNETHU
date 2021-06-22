package com.example.sc_hathunethu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;

public class thermal_info_activity extends AppCompatActivity {
    private int fileplace;
    private int thermalnum;


    private Timer mTimer = null;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermal_info_activity);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //保存ボタンの処理
        Button save_btn=findViewById(R.id.infolog_Button);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String log_data=getThermalLog();
                Context context=getApplicationContext();
                GetLogData getLogData=new GetLogData(context);
                getLogData.getLogAll(log_data);
             //   Toast.makeText(context,"ログ保存完了！",Toast.LENGTH_SHORT).show();
            }
        });

        final TextView textView = findViewById(R.id.thermalzone);
        try (FileReader fileReader = new FileReader("sys/devices/virtual/thermal/thermal_zone0/temp")) {
            fileplace = 0;
        } catch (Exception e) {
            e.printStackTrace();
            try (FileReader fileReader2 = new FileReader("sys/class/thermal/thermal_zone0/temp")) {
                fileplace = 1;
            } catch (Exception e1) {
                e1.printStackTrace();
                Toast.makeText(getApplicationContext(), "このアプリでは端末の温度領域を読み取れません", Toast.LENGTH_SHORT).show();
                fileplace = 2;
            }
        }
        //thermalzoneがいくつあるのかを調べる
        switch (fileplace){
            case 0:
                for(int i=0;i<1000;i++){
                    File dir=new File("sys/devices/virtual/thermal/thermal_zone"+i);
                    if(!dir.exists()){
                        thermalnum=i;
                        Log.d("thermalnum",String.valueOf(thermalnum));
                        break;
                    }
                }
                break;
            case 1:
                for(int i=0;i<1000;i++){
                    File dir2=new File("sys/class/thermal/thermal_zone"+i);
                    if(!dir2.exists()){
                        thermalnum=i;
                        Log.d("thermalnum",String.valueOf(thermalnum));
                        break;
                    }
                }
                break;
            case 2:
                break;
        }



        // タイマーの設定 1秒毎にループ

        mTimer = new Timer(true);

        mTimer.schedule( new TimerTask(){

            @Override

            public void run(){

                mHandler.post( new Runnable(){

                    public void run(){
                        Log.d( "TestService" , "Timer run" );
                        String thermalzoneinfomation=getThermalInfo();
                        textView.setText(thermalzoneinfomation);
                    }

                });

            }

        }, 1, 1000); //1ミリ秒後にintervalミリ秒ごとの繰り返し
    }
    //Activity破棄でタイマー停止
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }



    public String getThermalInfo(){
        ArrayList<String> filenames=new ArrayList<>();
        ArrayList<String> filenames2=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        switch (fileplace) {
            case 0:
                for (int i = 0; i <thermalnum; i++) {
                    filenames.add("sys/devices/virtual/thermal/thermal_zone" + i + "/temp");
                    filenames2.add("sys/devices/virtual/thermal/thermal_zone"+i+"/type");
                    try (
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames2.get(i)))
                    ) {
                        String thermalType = bufferedReader.readLine();
                        stringBuilder.append("thermal_zone"+i+"("+thermalType+"):");
                        Log.d("追加", String.valueOf(i));
                    } catch (IOException e) {
                        Log.d("IOException", "thermalzone"+i+"のtypeがないです");
                        e.printStackTrace();
                    }
                    try(
                            BufferedReader bufferedReader2=new BufferedReader(new FileReader(filenames.get(i)))
                    ){
                        String thermalValue=bufferedReader2.readLine();
                        stringBuilder.append(thermalValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("IOException", "thermalzone"+i+"のtempがないです");
                        stringBuilder.append("読み取り不可");
                    }
                    stringBuilder.append("\n");

                }
                break;
            case 1:
                for (int i = 0; i <thermalnum; i++) {
                    filenames.add("sys/class/thermal/thermal_zone" + i + "/temp");
                    filenames2.add("sys/class/thermal/thermal_zone"+i+"/type");
                    try (
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames2.get(i)))
                    ) {
                        String thermalType = bufferedReader.readLine();
                        stringBuilder.append("thermal_zone"+i+"("+thermalType+"):");
                        Log.d("追加", String.valueOf(i));
                    } catch (IOException e) {
                        Log.d("IOException", "thermalzone"+i+"のtypeがないです");
                        e.printStackTrace();
                    }
                    try(
                            BufferedReader bufferedReader2=new BufferedReader(new FileReader(filenames.get(i)))
                    ){
                        String thermalValue=bufferedReader2.readLine();
                        stringBuilder.append(thermalValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("IOException","thermalzone"+i+"のtempがないです");
                        stringBuilder.append("読み取り不可");
                    }
                    stringBuilder.append("\n");
                }
                break;
            case 2:
                stringBuilder.append("thermal_zoneが見つかりませんでした");
                break;
        }
        String thermalzoneinfomation=stringBuilder.toString();
        return thermalzoneinfomation;
    }

    //ログ保存用の文字列を取得する関数
    public String getThermalLog(){
        ArrayList<String> filenames=new ArrayList<>();
        ArrayList<String> filenames2=new ArrayList<>();
        StringBuilder stringBuilder=new StringBuilder();
        switch (fileplace) {
            case 0:
                for (int i = 0; i <thermalnum; i++) {
                    filenames.add("sys/devices/virtual/thermal/thermal_zone" + i + "/temp");
                    filenames2.add("sys/devices/virtual/thermal/thermal_zone"+i+"/type");
                    try (
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames2.get(i)))
                    ) {
                        String thermalType = bufferedReader.readLine();
                        stringBuilder.append("thermal_zone"+i+","+thermalType+",");
                        Log.d("追加", String.valueOf(i));
                    } catch (IOException e) {
                        Log.d("IOException", "thermalzone"+i+"のtypeがないです");
                        e.printStackTrace();
                    }
                    try(
                            BufferedReader bufferedReader2=new BufferedReader(new FileReader(filenames.get(i)))
                    ){
                        String thermalValue=bufferedReader2.readLine();
                        stringBuilder.append(thermalValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("IOException", "thermalzone"+i+"のtempがないです");
                        stringBuilder.append("読み取り不可");
                    }
                    stringBuilder.append("\n");

                }
                break;
            case 1:
                for (int i = 0; i <thermalnum; i++) {
                    filenames.add("sys/class/thermal/thermal_zone" + i + "/temp");
                    filenames2.add("sys/class/thermal/thermal_zone"+i+"/type");
                    try (
                            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenames2.get(i)))
                    ) {
                        String thermalType = bufferedReader.readLine();
                        stringBuilder.append("thermal_zone"+i+","+thermalType+",");
                        Log.d("追加", String.valueOf(i));
                    } catch (IOException e) {
                        Log.d("IOException", "thermalzone"+i+"のtypeがないです");
                        e.printStackTrace();
                    }
                    try(
                            BufferedReader bufferedReader2=new BufferedReader(new FileReader(filenames.get(i)))
                    ){
                        String thermalValue=bufferedReader2.readLine();
                        stringBuilder.append(thermalValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("IOException","thermalzone"+i+"のtempがないです");
                        stringBuilder.append("読み取り不可");
                    }
                    stringBuilder.append("\n");
                }
                break;
            case 2:
                stringBuilder.append("thermal_zoneが見つかりませんでした");
                break;
        }
        String thermalzoneinfomation=stringBuilder.toString();
        return thermalzoneinfomation;
    }


    //戻るボタンの処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemId=item.getItemId();
        if(itemId==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}