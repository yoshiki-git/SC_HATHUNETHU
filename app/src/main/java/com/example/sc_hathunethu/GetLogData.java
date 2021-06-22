package com.example.sc_hathunethu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import static android.content.Context.MODE_PRIVATE;

public class GetLogData {
    private Context context;
    private static final String TAG="GetLogData";
    private boolean isSavedOldFormat;

    GetLogData(Context context){
        this.context=context;
        Log.d(TAG,"コンストラクタ入力");

    }

    //ファイル名とパスも同時に保存
    public void getLogAll(String log_data){
        GetTimeData getTimeData=new GetTimeData();
        //名前を現在時刻に設定
        String fileName=getTimeData.getFileName();
        //拡張子の設定
        fileName = fileName+".txt";

        SharedPreferences datastore=context.getSharedPreferences("DataStore", MODE_PRIVATE);
        isSavedOldFormat=datastore.getBoolean("switch_fileSave",false);
        if(isSavedOldFormat){
            //アプリ固有のファイルに保存する
            File path = context.getExternalFilesDir(null);
            File file=new File(path,fileName);
            getLog(file,log_data);
        }else {
            //直下→Documentsディレクトリを作成して保存
            File dir_myApp =new File("/sdcard/発熱アプリ");
            if(dir_myApp.exists()){
                Log.d(TAG,"あるらしい");
            }else {
                dir_myApp.mkdir();
                Log.d(TAG,"ないから作ったよ");
            }
            File file=new File(dir_myApp,fileName);
            getLog(file,log_data);
        }

        //直下に生データを保存
        //File file=new File("/sdcard",fileName);

    }

    //ファイル名とパスだけ取得
    public File getFileStatus(String fileName){

        File file;
        SharedPreferences datastore=context.getSharedPreferences("DataStore", MODE_PRIVATE);
        isSavedOldFormat=datastore.getBoolean("switch_fileSave",false);
        if(isSavedOldFormat){
            //アプリ固有のファイルにパスを指定
            File path = context.getExternalFilesDir(null);
            file = new File(path, fileName);
        }else {
            //直下→Documentsにディレクトリを作成して保存
            File dir_myApp =new File("/sdcard/発熱アプリ");
            if(dir_myApp.exists()){
                Log.d(TAG,"あるらしい");
            }else {
                dir_myApp.mkdir();
                Log.d(TAG,"ないから作った");
            }
           file=new File(dir_myApp,fileName);
        }
        return file;
    }

    public void getLog(File filepath, String log_data){
        //ログを外部ストレージに保存する
        if (isExternalStorageWritable() ) {
            try (FileOutputStream fileOutputStream =
                         new FileOutputStream(filepath, true);
                 OutputStreamWriter outputStreamWriter =
                         new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);
                 BufferedWriter bw =
                         new BufferedWriter(outputStreamWriter);
            ) {

                bw.write(log_data);
                bw.flush();
            } catch (Exception e) {
                Log.d(TAG,"Exception");
                e.printStackTrace();
                Toast.makeText(context,"ストレージが見つからないためログを保存できません"+"\n"+"設定より旧保存場所を利用するをオンにして試してみてください。",Toast.LENGTH_LONG).show();
                Log.d(TAG,"トースト表示");
            }
        }
    }


    //ログ取得の許可に必要な関数
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
}
