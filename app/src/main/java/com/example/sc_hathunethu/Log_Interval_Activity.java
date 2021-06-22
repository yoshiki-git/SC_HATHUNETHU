package com.example.sc_hathunethu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class Log_Interval_Activity extends AppCompatActivity {
    private final Switch[] switches=new Switch[9];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log__interval_);

        //Actionバーと戻るボタンの実装
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        final SharedPreferences.Editor editor =dataStore.edit();
        int logInterval=dataStore.getInt("logInterval",1000);
        final EditText editText=(EditText)findViewById(R.id.editTextNumber);
        editText.setText(String.valueOf(logInterval));


        Button chButton=(Button)findViewById(R.id.changeButton);

        chButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editData=editText.getText().toString();
                //空欄の時にはじく
                if(editData.length()==0){
                    Toast.makeText(getApplicationContext(),"値が不正です",Toast.LENGTH_LONG).show();
                    return;
                }
                //Int型に変える
                int editData2= Integer.parseInt(editData);
                //0以下の時はじく
                if(editData2<=0){
                    Toast.makeText(getApplicationContext(),"値が不正です",Toast.LENGTH_LONG).show();
                    return;
                }
                editor.putInt("logInterval",editData2);
                editor.commit();
                Toast.makeText(getApplicationContext(),"測定間隔が変更されました",Toast.LENGTH_LONG).show();
            }
        });

        switches[1]=findViewById(R.id.switch_PM);
        switches[2]=findViewById(R.id.switch_Bat);
        switches[3]=findViewById(R.id.switch_Thermal);
        switches[4]=findViewById(R.id.switch_NW);
        switches[5]=findViewById(R.id.switch_SB);
        switches[6]=findViewById(R.id.switch_dozelog);
        switches[7]=findViewById(R.id.switch_fileSave);
        switches[8]=findViewById(R.id.switch_logTime);

        boolean switch_PM=dataStore.getBoolean("switch_PM",true);
        switches[1].setChecked(switch_PM);
        boolean switch_Bat=dataStore.getBoolean("switch_Bat",true);
        switches[2].setChecked(switch_Bat);
        boolean switch_Thermal=dataStore.getBoolean("switch_Thermal",true);
        switches[3].setChecked(switch_Thermal);
        boolean switch_Network=dataStore.getBoolean("switch_Network",true);
        switches[4].setChecked(switch_Network);
        boolean switch_SB=dataStore.getBoolean("switch_SB",true);
        switches[5].setChecked(switch_SB);
        boolean switch_dozelog=dataStore.getBoolean("switch_dozelog",false);
        switches[6].setChecked(switch_dozelog);
        boolean switch_fileSave=dataStore.getBoolean("switch_fileSave",false);
        switches[7].setChecked(switch_fileSave);
        boolean switch_logTime=dataStore.getBoolean("switch_logTime",false);
        switches[8].setChecked(switch_logTime);


        //スイッチのリスナー軍団
        switches[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_PM", switches[1].isChecked());
                editor.commit();
            }
        });
        switches[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_Bat", switches[2].isChecked());
                editor.commit();
            }
        });

        //スイッチのリスナー軍団
        switches[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_Thermal", switches[3].isChecked());
                editor.commit();
            }
        });

        //スイッチのリスナー軍団
        switches[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_Network", switches[4].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_SB", switches[5].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_dozelog", switches[6].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[7].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_fileSave", switches[7].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[8].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("switch_logTime", switches[8].isChecked());
                editor.commit();
            }
        });

        //バッテリーの最適化設定を開く
        Button btn_optimisation=(Button)findViewById(R.id.button_optimisation);
        btn_optimisation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        });

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