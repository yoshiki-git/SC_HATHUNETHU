package com.example.sc_hathunethu;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CameraCall {

    private Context context;
    private static final String TAG="CamaraCall";

    CameraCall(Context context){
        this.context=context;
    }
    //端末のデフォルトカメラアプリの起動メソッド
    public void tryCamera() {
        Intent[] intents = new Intent[7];
        Log.d(TAG, "配列定義");
        //カメラのパッケージとクラスはadb shell と外部アプリのクラス名取得するやつとパッケージ名取得するやつで見つけた。GetIntentClassNameってアプリとAplinってアプリ
        //adb shell pm list packages camera →adb shell pm dump パッケージ名 を実行後Intent.action.MAIN を探す

        //Galaxy
        intents[0] = new Intent("android.intent.action.MAIN");
        intents[0].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[0].setClassName("com.sec.android.app.camera", "com.sec.android.app.camera.Camera");
        //Sony SOとか　SanとHijiriで確認
        intents[1] = new Intent("android.intent.action.MAIN");
        intents[1].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[1].setClassName("com.sonyericsson.android.camera", "com.sonyericsson.android.camera.CameraActivity");
        //ASUS 業電で確認
        intents[2] = new Intent("android.intent.action.MAIN");
        intents[2].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[2].setClassName("com.asus.camera", "com.asus.camera.CameraApp");
        //Huawei
        intents[3] = new Intent("android.intent.action.MAIN");
        intents[3].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[3].setClassName("com.huawei.camera", "com.huawei.camera");
        //富士通　AMATERASUで確認
        intents[4] = new Intent("android.intent.action.MAIN");
        intents[4].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[4].setClassName("com.fujitsu.mobile_phone.camera", "com.android.camera.CameraActivity");
        //シャープ　Kingで確認
        intents[5] = new Intent("android.intent.action.MAIN");
        intents[5].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[5].setClassName("jp.co.sharp.android.camera", "jp.co.sharp.android.camera.stillimagecamera.Camera");
        //LG
        intents[6] = new Intent("android.intent.action.MAIN");
        intents[6].setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intents[6].setClassName("com.lge.camera", "com.lge.camera.CameraAppLauncher");

        Log.d(TAG, "setClassName");
        int counter = 0;
        for (Intent i : intents) {
            try {
                context.startActivity(i);
                Log.d(TAG, "カメラ起動おｋ");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                counter++;
                Log.d(TAG, "エラー" + counter + "回目");
            }
        }
        if (counter == intents.length) {
            Log.d(TAG, "全部だめだった" + intents.length + counter);
            Toast.makeText(context, "直接カメラアプリを起動してください", Toast.LENGTH_LONG).show();
        }
        //counterを初期化
        counter = 0;
        Log.d(TAG, "counter初期化" + counter + "になった");
    }
}
