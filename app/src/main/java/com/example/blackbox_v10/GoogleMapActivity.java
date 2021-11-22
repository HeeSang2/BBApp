package com.example.blackbox_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GoogleMapActivity extends AppCompatActivity {

    static final int REQCODE_PERMISSION_CALLPHONE = 1;
    String str_title, str_address, str_time, str_phone;
    String title, address, phone, phone1, phone2, time;

    TextView tv_Main_title, tv_Main_address, tv_Main_phone, tv_Main_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        Bundle extras = getIntent().getExtras();

        if(extras == null){
            title = "error";
        }
        else{
            title = extras.getString("title");
            address = extras.getString("address");
            phone = extras.getString("phone");
            phone1 = extras.getString("phone1");
            phone2 = extras.getString("phone2");
            time = extras.getString("time");
        }

        tv_Main_title = (TextView) findViewById(R.id.tv_Main_title);
        tv_Main_address = (TextView) findViewById(R.id.tv_main_address);
        tv_Main_phone = (TextView) findViewById(R.id.tv_main_phone);
        tv_Main_time = (TextView) findViewById(R.id.tv_main_time);

        str_title = title;
        str_address = address;
        str_phone = phone + "-" + phone1 + "-" + phone2;
        str_time = time;

        tv_Main_title.setText(str_title);
        tv_Main_address.setText(str_address);
        tv_Main_phone.setText(str_phone);
        tv_Main_time.setText(str_time);
    }

    public void onClick_main_call(View view) {
        if(ContextCompat.checkSelfPermission(GoogleMapActivity.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(GoogleMapActivity.this,Manifest.permission.CALL_PHONE)){
                Toast.makeText(this,"권한이 없습니다.",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(GoogleMapActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},REQCODE_PERMISSION_CALLPHONE);
            }
        }
        else {
            if(str_phone.length() > 0) {
                Intent intent_main = new Intent(Intent.ACTION_CALL);
                intent_main.setData(Uri.parse("tel:" + str_phone));
                startActivity(intent_main);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQCODE_PERMISSION_CALLPHONE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                ((Button)findViewById(R.id.bt_main_call)).setEnabled(true);
                Toast.makeText(this, "권한이 없습니다.\n버튼을 다시 눌러 통화를 시도하세요.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClick_main_check(View view) {
        finish();
    }


}