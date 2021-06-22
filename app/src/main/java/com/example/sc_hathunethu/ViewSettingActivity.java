package com.example.sc_hathunethu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ViewSettingActivity extends AppCompatActivity {
    private final Switch[] switches=new Switch[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_setting);

        //Actionバーと戻るボタンの実装
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        SharedPreferences dataStore = getSharedPreferences("DataStore", MODE_PRIVATE);
        final SharedPreferences.Editor editor =dataStore.edit();
        int viewInterval=dataStore.getInt("viewInterval",1000);
        final EditText editText=(EditText)findViewById(R.id.viewEdit);
        editText.setText(String.valueOf(viewInterval));

        //ビューのカラーの処理
        //ラジオグループの呼び出し
        RadioGroup radioGroup_viewColor=findViewById(R.id.radioGroup_view_color);
        int radioValue=dataStore.getInt("viewColor",R.id.view_Blue);
        //指定したIDのラジオボタンをチェック
        radioGroup_viewColor.check(radioValue);
        //チェックされているラジオボタンのIDを取得する
        RadioButton radioButton=findViewById(radioGroup_viewColor.getCheckedRadioButtonId());
        // ラジオグループのチェック状態が変更された時に呼び出されるコールバックリスナーを登録します
        radioGroup_viewColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // ラジオグループのチェック状態が変更された時に呼び出されます
            // チェック状態が変更されたラジオボタンのIDが渡されます
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                switch (checkedId){
                    case R.id.view_Red:
                        editor.putInt("viewColor",R.id.view_Red);
                        editor.commit();
                        break;
                    case R.id.view_Blue:
                        editor.putInt("viewColor",R.id.view_Blue);
                        editor.commit();
                        break;
                    case R.id.view_Green:
                        editor.putInt("viewColor",R.id.view_Green);
                        editor.commit();
                        break;
                    case R.id.view_White:
                        editor.putInt("viewColor",R.id.view_White);
                        editor.commit();
                        break;
                    case R.id.view_Black:
                        editor.putInt("viewColor",R.id.view_Black);
                        editor.commit();
                        break;
                }
            }
        });


        Button chButton=(Button)findViewById(R.id.viewChangeButton);

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
                editor.putInt("viewInterval",editData2);
                editor.commit();
                Toast.makeText(getApplicationContext(),"VIEWの表示間隔が変更されました",Toast.LENGTH_LONG).show();
            }
        });

        switches[1]=findViewById(R.id.view_PM);
        switches[2]=findViewById(R.id.view_NW);
        switches[3]=findViewById(R.id.view_SB);
        switches[4]=findViewById(R.id.view_Bat);
        switches[5]=findViewById(R.id.view_Thermal);
        switches[6]=findViewById(R.id.sw_view_Time);

        boolean switch_PM=dataStore.getBoolean("view_PM",true);
        switches[1].setChecked(switch_PM);
        boolean switch_NW=dataStore.getBoolean("view_NW",true);
        switches[2].setChecked(switch_NW);
        boolean switch_SB=dataStore.getBoolean("view_SB",true);
        switches[3].setChecked(switch_SB);
        boolean switch_Bat=dataStore.getBoolean("view_Bat",true);
        switches[4].setChecked(switch_Bat);
        boolean switch_Thermal=dataStore.getBoolean("view_Thermal",true);
        switches[5].setChecked(switch_Thermal);
        boolean switch_Time=dataStore.getBoolean("view_Time_Setting",false);
        switches[6].setChecked(switch_Time);


        //スイッチのリスナー軍団
        switches[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_PM", switches[1].isChecked());
                editor.commit();
            }
        });
        switches[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_NW", switches[2].isChecked());
                editor.commit();
            }
        });

        //スイッチのリスナー軍団
        switches[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_SB", switches[3].isChecked());
                editor.commit();
            }
        });

        //スイッチのリスナー軍団
        switches[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_Bat", switches[4].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_Thermal", switches[5].isChecked());
                editor.commit();
            }
        });
        //スイッチのリスナー軍団
        switches[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //switchの設定値をtrueにしてpreferenceに保存
                editor.putBoolean("view_Time_Setting", switches[6].isChecked());
                editor.commit();
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