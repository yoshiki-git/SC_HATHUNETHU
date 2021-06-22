package com.example.sc_hathunethu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NewWorkActivity extends AppCompatActivity {

    private TelephonyManager telephonyManager;
    private String celltext = "";
    private String celltext2 = "";
    private String showtext = "";
    private String network_Type="";
    private int lteNumber;
    private Timer mTimer = null;
    Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_work);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        final TextView textView = findViewById(R.id.info);
        final TextView textView1=findViewById(R.id.tv_nettype);

        //保存ボタンの処理
        Button save_btn = findViewById(R.id.infolog_Button);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String log_data = cellInfoLog();
                Context context = getApplicationContext();
                GetLogData getLogData = new GetLogData(context);
                getLogData.getLogAll(log_data);
                Toast.makeText(context, "ログ保存完了！", Toast.LENGTH_SHORT).show();
            }
        });
        // タイマーの設定 1秒毎にループ

        mTimer = new Timer(true);

        mTimer.schedule(new TimerTask() {

            @Override

            public void run() {

                mHandler.post(new Runnable() {

                    public void run() {
                        showtext = "";
                        network_Type="";
                        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        int networkType=telephonyManager.getDataNetworkType();
                        Log.d("TestApp","NetWorkType:"+networkType);
                        switch (networkType) {
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                Log.d("TestApp", "LTE");
                                network_Type+="Network Type:LTE";
                                Log.d("TestApp",network_Type);
                                break;
                            case TelephonyManager.NETWORK_TYPE_NR:
                                Log.d("TestApp","NR");
                                network_Type+="Network Type:NR";

                                break;
                            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                                Log.d("TestApp","Unknown");
                                network_Type+="Network Type:Unknown";
                                break;
                            default:
                                Log.d("TestApp","Other_NetWork");
                                network_Type+="Network Type:Other_NetWork";
                                return;
                        }
                        network_Type+="\n";

                        Log.d("TestApp", "Timer run");
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        List<CellInfo> cellList = telephonyManager.getAllCellInfo();
                        Log.d("getAllCellInfo", String.valueOf(cellList));
                        Log.d("TestApp", "テスト");
                        Log.d("TestApp", "cellListの個数:" + cellList.size());
                        String cellsize = "取得したCELLの個数：" + cellList.size() + "\n";
                        for (CellInfo cellInfo : cellList) {
                            if (cellInfo instanceof CellInfoLte) {
                                StringBuilder stringBuilderlte = new StringBuilder();
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                                CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                                CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

                                int ci = cellIdentityLte.getCi();
                                int pci = cellIdentityLte.getPci();
                                int tac = cellIdentityLte.getTac();
                                int bandwidth = cellIdentityLte.getBandwidth();
                                int earfcn = cellIdentityLte.getEarfcn();
                                int rsrplte = cellSignalStrengthLte.getRsrp();
                                int rsrqlte = cellSignalStrengthLte.getRsrq();
                                int signalLevel = cellSignalStrengthLte.getLevel();
                                int asuLevel = cellSignalStrengthLte.getAsuLevel();
                                int dbm = cellSignalStrengthLte.getDbm();
                                String mccMnc = cellIdentityLte.getMobileNetworkOperator();
                                int rssnr = cellSignalStrengthLte.getRssnr();
                                //     int rssi=cellSignalStrengthLte.getRssi();
             /*   int careerNum= Integer.parseInt(mccMnc);
                Log.d("TestApp",String.valueOf(careerNum));
                switch (careerNum){
                    case 44010:
                        celltext+="キャリア:NTT DOCOMO\n";
                        break;
                    case 44020:
                        celltext+="キャリア:KDDI\n";
                        break;
                    case 44051:
                        celltext+="キャリア:ソフトバンク\n";
                        break;
                    default:
                        celltext+="キャリア:3大キャリア以外\n";

                } */
                                stringBuilderlte.append("CELL:LTE" + "\n" +
                                        "Cell ID:" + ci + "\n" +
                                        "RSRP:" + rsrplte + "dBm" + "\n" +
                                        "RSRQ:" + rsrqlte + "dBm" + "\n" +
                                        "RSSNR:" + rssnr + "\n" +
                                        //          "RSSI:"+rssi+"\n"+
                                        "PCI:" + pci + "\n" +
                                        "TAC:" + tac + "\n" +
                                        "BandWidth:" + bandwidth + "\n" +
                                        "Earfcn:" + earfcn + "\n" +
                                        "Mcc + Mnc:" + mccMnc + "\n" +
                                        "Signal Level:" + signalLevel + "\n" +
                                        "ASU Level:" + asuLevel + "\n" +
                                        "-----------\n");

                                celltext = stringBuilderlte.toString();
                                //  textView.setText(celltext);

                                Log.d("TestApp", "Cell ID is " + ci);
                                Log.d("TestApp", "取得したCellの個数:" + cellList.size());


                            } else if (cellInfo instanceof CellInfoNr) {
                                StringBuilder stringBuilderNr = new StringBuilder();
                                CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
                                CellIdentityNr cellIdentityNr = (CellIdentityNr) cellInfoNr.getCellIdentity();
                                CellSignalStrengthNr cellSignalStrengthNr = (CellSignalStrengthNr) cellInfoNr.getCellSignalStrength();
                                int pci = cellIdentityNr.getPci();
                                long nci = cellIdentityNr.getNci();
                                int csiRsrp = cellSignalStrengthNr.getCsiRsrp();
                                int csiRsrq = cellSignalStrengthNr.getCsiRsrq();
                                int csiSinr = cellSignalStrengthNr.getCsiSinr();
                                int ssRsrp = cellSignalStrengthNr.getSsRsrp();
                                int ssRsrq = cellSignalStrengthNr.getSsRsrq();
                                int ssSinr = cellSignalStrengthNr.getSsSinr();
                                int asuLevel = cellSignalStrengthNr.getAsuLevel();
                                int signalLevel = cellSignalStrengthNr.getLevel();
                                int nrtac = cellIdentityNr.getTac();
                                int nrarfcn = cellIdentityNr.getNrarfcn();
                                String mcc = cellIdentityNr.getMccString();
                                String mnc = cellIdentityNr.getMncString();

                                stringBuilderNr.append("CELL:NR" + "\n" +
                                        "CELL ID:" + nci + "\n" +
                                        "CSI-RSRP:" + csiRsrp + "dBm\n" +
                                        "CSI-RSRQ:" + csiRsrq + "dBm\n" +
                                        "CSI-SINR:" + csiSinr + "\n" +
                                        "SS-RSRP:" + ssRsrp + "dBm\n" +
                                        "SS-RSRQ:" + ssRsrq + "dBm\n" +
                                        "SS-SINR:" + ssSinr + "\n" +
                                        "Nrarfcn:" + nrarfcn + "\n" +
                                        "TAC:" + nrtac + "\n" +
                                        "Signal Level:" + signalLevel + "\n" +
                                        "ASU Level:" + asuLevel + "\n" +

                                        "PCI:" + pci + "\n" +
                                        "MCC:" + mcc + "\n" +
                                        "MNC:" + mnc + "\n" +
                                        "------------\n");
                                celltext2 = stringBuilderNr.toString();
                                //      textView.setText(celltext2);


                            }
                            showtext += network_Type+cellsize + celltext + celltext2;

                        }
                        textView.setText("元データ:" + String.valueOf(cellList) + "\n" + showtext);
                    }

                });

            }

        }, 1, 1000); //1ミリ秒後にintervalミリ秒ごとの繰り返し
    }

    public String cellInfoLog() {
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        Log.d("TestService", "Timer run");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        List<CellInfo> cellList = telephonyManager.getAllCellInfo();
        String log_data=String.valueOf(cellList);
        return log_data;
    }

    //Activity破棄でタイマー停止
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
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