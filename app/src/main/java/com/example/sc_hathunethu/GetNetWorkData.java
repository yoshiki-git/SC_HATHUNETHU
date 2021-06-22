package com.example.sc_hathunethu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Method;
import java.util.List;

public class GetNetWorkData {
    private String TAG = "TestApp";

    private Context context;

    GetNetWorkData(Context context) {
        this.context = context;
        Log.d("GetLogData", "コンストラクタ入力");

    }

    //Wifiかセルラーか取得
    public String getConnectionType(NetworkCapabilities capabilities) {
        String con_Type = "";
     /*
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());

      */
        if (capabilities != null) {
            String string = capabilities.toString();
            //    int signalStrength = capabilities.getSignalStrength();
            //    Log.d(TAG, "SignalStrength:" + signalStrength);
            Log.d(TAG, string);
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                con_Type = "Wifi";
                Log.d(TAG, "Wifi");
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                con_Type = "Cellular";
                Log.d(TAG, "Cellular");
            }
        } else {
            con_Type = "Disconnected";
            Log.d(TAG, "Disconnected");
        }
        return con_Type;
    }




    //テザリングステータスを取得する関数
    public String getTetheringStatus(){
        String status = "UNSUPPORTED";
        try {
            WifiManager wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            if("true".equals(method.invoke(wifiManager).toString())){
                status ="ON";
            }else{
                status ="OFF";
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error=e.toString();
            Log.d(TAG,error);
        }
        return status;
    }

 //インターネットに繋がってるか
    public String getInternet(NetworkCapabilities capabilities) {
        String internet_Check = "";
        /*
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());

         */
        if(capabilities!=null) {
            if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                internet_Check = "True";
            } else {
                internet_Check = "False";
            }
        }
        else {
            internet_Check="False";
        }
        return internet_Check;
    }
    //データ通信のネットワーク状態
    public String getDataNetWorkType(TelephonyManager telephonyManager) {

    //    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "READ_PHONE_STATEを許可してください", Toast.LENGTH_LONG).show();
        }
        int networkType = telephonyManager.getDataNetworkType();
        Log.d(TAG, "NetWorkType:" + networkType);
        String string;

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                string = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                string = "NR";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                string = "Unknown";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                string = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                string = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                string = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                string = "EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                string = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                string = "EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                string = "EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                string = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                string = "GSM";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                string = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                string = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                string = "HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                string = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                string = "IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                string = "IWLAN";
                break;
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                string = "SCDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                string = "UMTS";
                break;
            default:
                string = "Error";
                break;
        }
        return string;
    }

    //音声通信のネットワーク状態
    public String getVoiceNetWorkType(TelephonyManager telephonyManager){
     //   TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "READ_PHONE_STATEを許可してください", Toast.LENGTH_LONG).show();
        }
        int networkType = telephonyManager.getVoiceNetworkType();
        Log.d(TAG, "VoiceNetWorkType:" + networkType);
        String string;

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                string = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_NR:
                string = "NR";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                string = "Unknown";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                string = "1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                string = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                string = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                string = "EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                string = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                string = "EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                string = "EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                string = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_GSM:
                string = "GSM";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                string = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                string = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                string = "HSPAP";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                string = "HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                string = "IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_IWLAN:
                string = "IWLAN";
                break;
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                string = "SCDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                string = "UMTS";
                break;
            default:
                string = "Error";
                break;
        }
        return string;
    }

    //Cellパラメータの取得
    public void getCellInformation() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(context, "ACCESS_FINE_LOCATIONを許可してください", Toast.LENGTH_LONG).show();

        }
        List<CellInfo> cellInfo =telephonyManager.getAllCellInfo();


    }

    public void read_5G_status(TelephonyManager telephonyManager){
        String string="";
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            telephonyManager.listen(phoneStateListener2, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED);

        }
    }

    public PhoneStateListener phoneStateListener2=new PhoneStateListener() {
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
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_LTE_CA:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_LTE_CA");
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NONE:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NONE");
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NR_NSA");
                    break;
                case TelephonyDisplayInfo.OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE:
                    Log.d("TestApp","OVERRIDE_NETWORK_TYPE_NR_NSA_MMWAVE");
                    break;
            }
        }
    };

}
