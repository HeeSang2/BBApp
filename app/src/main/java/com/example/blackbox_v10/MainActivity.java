package com.example.blackbox_v10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    long backKeyPressedTime;
    Send_Message send_message=Send_Message.getInstance();
    // DB
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    // Main
    Intent intent;
    EditText et_id, et_password;
    Button bt_login;
    CheckBox cb_auto;   // 자동로그인 체크박스(체크 해야 자동로그인 가능)

    // values
    String id, password, name, phone, address, port;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // database
        dbHelper = new MemberSQLiteOpenHelper(this);
        db= dbHelper.getWritableDatabase();

        //main
        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        cb_auto = findViewById(R.id.cb_auto);

    }

    // 로그인 버튼 onClick
    public void onClickLogin(View view) {

        int result = 0; // 로그인 성공 여부 저장

        String id_input = et_id.getText().toString();
        String password_input = et_password.getText().toString();

        cursor=db.query(MemberSQLiteOpenHelper.MEMBER, null,null,null,null,null,null);

        while(cursor.moveToNext()){

            id = cursor.getString(0);
            password =cursor.getString(1);
            name = cursor.getString(2);
            phone = cursor.getString(3);
            address = cursor.getString(4);
            port = cursor.getString(5);

            // 공백 에러 처리
            if(id_input.equals("")  || password_input.equals(""))
            {
                Toast.makeText(MainActivity.this, "아이디와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                break;
            }

            // 아이디와 비밀번호가 일치하면
            if(id.equals(id_input) && password.equals(password_input)){
                result = 1;

                // 메뉴로 이동
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();

                autoLogin.putString("id", id);
                autoLogin.putString("password", password);
                autoLogin.putString("name", name);
                autoLogin.putString("phone", phone);
                autoLogin.putString("address", address);
                autoLogin.putString("port", port);

                if(!cb_auto.isChecked()) {
                    autoLogin.clear(); // 로그인 정보 다 삭제
                }

                send_message.setId(id);
                send_message.setPassword(password);
                send_message.setName(name);
                send_message.setPhone(phone);
                send_message.setAddress(address);
                send_message.setPort(port);

                autoLogin.commit();

                intent = new Intent(MainActivity.this, MenuActivity.class);
                Toast.makeText(MainActivity.this, "[System] "+name+"님이 접속했습니다..", Toast.LENGTH_SHORT).show();
                startActivity(intent);

                et_id.setText("");
                et_password.setText("");
            }
        }

        // 반복문을 다 돌아도 조건문 일치 ID가 없으면
        if (result == 0) {
            Toast.makeText(MainActivity.this, "[System] 회원정보가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            et_password.setText("");
        }
    }

//    @Override
//    protected void onDestroy() {
//        // 자동로그인을 체크하지 않으면
//        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor autoLogin = auto.edit();
//        if(!cb_auto.isChecked()) {
//            // 접속 정보를 null로 변경 후 Destroy
//            autoLogin.putString("id", null);
//            autoLogin.putString("password", null);
//            autoLogin.putString("name", null);
//            autoLogin.putString("phone", null);
//            autoLogin.putString("address", null);
//            autoLogin.putString("port", null);
//
//            autoLogin.commit();
//        }
//        super.onDestroy();
//    }

    // Join onClick
    public void onClickJoin(View view) {
        Intent intent = new Intent(MainActivity.this, JoinActivity.class);
        startActivity(intent);
    }

    // 메뉴바 뒤로가기
    public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, getString(R.string.close), Toast.LENGTH_SHORT).show();
            } else {
                AppFinish();
            }
    }

    public void AppFinish(){
        ActivityCompat.finishAffinity(this);
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }


}