package com.example.sc_hathunethu;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetTimeData {

    private static final String TAG="TestApp";

    //時刻を取得する関数　表示用日付有り
    public String getNowDate() {
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //時刻を取得する関数　ログデータ用日付無し
    public String getNowTime() {
        final DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //時刻を取得する関数　ファイル名用日付と/と:が無い
    public String getFileName() {
        final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        final Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    //経過時間を取得する関数
    public double getElapsedTime(long startTime){
        long currentTime=System.currentTimeMillis();
        long elapsedTime=currentTime-startTime;
        Log.d(TAG,"経過時間:"+elapsedTime);
        double elapsedTimeSecond =elapsedTime/1000.000;
        return elapsedTimeSecond;

    }

    //経過時間を取得する関数（View用)
    public String getElapsedViewTime(long startTime){
        long currentTime=System.currentTimeMillis();
        long elapsedTime=currentTime-startTime;
        long elapsedTimeSecond=elapsedTime/1000;
        long hour=elapsedTimeSecond/3600;
        long minite=(elapsedTimeSecond%3600)/60;
        long second=(elapsedTimeSecond%60);


        return hour+"時間"+minite+"分"+second+"秒";

    }
}
