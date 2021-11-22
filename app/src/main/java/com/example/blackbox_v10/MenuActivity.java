package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class MenuActivity extends AppCompatActivity {

    long backKeyPressedTime;

    // Toolbar
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView iv_picture;
    private NavigationView navigationView;
    TextView tv_name, tv_phone;

    // values
    String id, password, name, phone, address, port, img_path;


    // Main
    Intent intent;
    Send_Message send_message = Send_Message.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Menu Bar
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation);

        // 내부저장소 values 가져오기
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        id =send_message.getId();
        password = send_message.getPassword();
        name = send_message.getName();
        phone = send_message.getPhone();
        address = send_message.getAddress();
        port = send_message.getPort();
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // 프로필사진 id받아온 다음

        setSupportActionBar(toolbar);
        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        // Service 시작
        intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        //처음에는 SharedPreferences에 아무런 정보도 없으므로 값을 저장할 키들을 생성한다.
        String loginId = auto.getString("id", null);

        if (TextUtils.isEmpty(loginId)) {
            stopService(intent);
        }

        super.onDestroy();
    }


    View.OnClickListener ivMenu_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(Gravity.LEFT);

            tv_name = findViewById(R.id.name_text);
            tv_phone = findViewById(R.id.tel_text);
            iv_picture = findViewById(R.id.user_img);

            tv_name.setText(send_message.getName() + " 님");
            tv_phone.setText("tel: " + send_message.getPhone());
            iv_picture.setImageURI(Uri.parse(img_path)); // 설정한 이미지로 프사 ( 메뉴바 열면 프사 나오게)
        }
    };

    NavigationView.OnNavigationItemSelectedListener navigation_listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.mainpage:
                    Toast.makeText(MenuActivity.this, "이미 메인페이지 입니다.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.account:
                    intent = new Intent(MenuActivity.this, MypageCheckActivity.class);
                    startActivity(intent);

                    return true;

                case R.id.capture:
                    intent = new Intent(MenuActivity.this, ListActivity.class);
                    startActivity(intent);

                    return true;

                case R.id.call_center:
                    intent = new Intent(MenuActivity.this, QnaActivity.class);
                    startActivity(intent);
                    return true;

                case R.id.camera:
                    intent = new Intent(MenuActivity.this, LiveActivity.class);
                    startActivity(intent);

                    return true;

                case R.id.location:
                    intent = new Intent(MenuActivity.this, LocationActivity.class);
                    startActivity(intent);

                    return true;

                case R.id.logout:

                    intent = new Intent(MenuActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();

                    autoLogin.clear(); // 로그인 정보 다 삭제
                    autoLogin.commit();

                    return true;
            }

            return true;
        }
    };


    public void onClickCapture(View view) {
        intent = new Intent(MenuActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void onClickQuestion(View view) {
        intent = new Intent(MenuActivity.this, QnaActivity.class);
        startActivity(intent);
    }

    public void onClickCamera(View view) {
        intent = new Intent(MenuActivity.this, LiveActivity.class);
        startActivity(intent);
    }

    public void onClickWhere(View view) {
        intent = new Intent(MenuActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    // 메뉴바 뒤로가기
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                Toast.makeText(MenuActivity.this, getString(R.string.close), Toast.LENGTH_SHORT).show();
            } else {
                AppFinish();
            }
        }

    }

    public void AppFinish(){
        ActivityCompat.finishAffinity(this);
    }
}