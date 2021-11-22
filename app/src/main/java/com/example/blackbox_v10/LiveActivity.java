package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.net.Socket;

public class LiveActivity extends AppCompatActivity {

    WebView webView;
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    TextView tv_name, tv_phone;

    // Main
    Intent intent;
    ImageView iv_picture;

    // values
    String id, password, name, phone, address, port;
    String img_path;

    Send_Message send_message=Send_Message.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        // Menu Bar
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation);

        tv_name = (TextView) findViewById(R.id.name_text);
        tv_phone = (TextView) findViewById(R.id.tel_text);
        iv_picture = findViewById(R.id.user_img);

        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        // Main
        id =send_message.getId();
        password = send_message.getPassword();
        name = send_message.getName();
        phone = send_message.getPhone();
        address = send_message.getAddress();
        port = send_message.getPort();

        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // 프로필사진

        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.setBackgroundColor(255);

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("http://"+address+":8091/?action=stream");

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
            iv_picture.setImageURI(Uri.parse(img_path)); // 설정한 이미지로 프사

        }
    };

    NavigationView.OnNavigationItemSelectedListener navigation_listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.mainpage:
                    intent = new Intent(LiveActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    intent = new Intent(LiveActivity.this, MypageCheckActivity.class);;
                    startActivity(intent);
                    finish();
                    return true;


                case R.id.capture:
                    intent = new Intent(LiveActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:
                    intent = new Intent(LiveActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.camera:
                    Toast.makeText(LiveActivity.this, "이미 라이브캠 페이지 입니다.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.location:
                    intent = new Intent(LiveActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:
                    intent = new Intent(LiveActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

    public void onClickLeft(View view) {
        if(!send_message.getGet_str().equals("-1")){
            send_message.setSend_str("left");
        }


    }

    public void onClickRight(View view) {
        if(!send_message.getGet_str().equals("-1")) {
            send_message.setSend_str("right");
        }
    }
    // 메뉴바 뒤로가기
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }


}