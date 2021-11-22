package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MypageCheckActivity extends AppCompatActivity {
    Send_Message send_message=Send_Message.getInstance();
    // Toolbar
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    DrawerLayout drawer;

    ImageView iv_picture;
    TextView tv_name, tv_phone;

    Intent intent;
    String id, password, name, phone;
    String img_path;

    EditText et_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_check);


        // MenuBar
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        id = send_message.getId();
        phone = send_message.getPhone();
        password = send_message.getPassword();
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg";

        setSupportActionBar(toolbar);
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
                    intent = new Intent(MypageCheckActivity.this, MenuActivity.class);
                    finish();
                    return true;

                case R.id.account:

                    Toast.makeText(MypageCheckActivity.this, "비밀 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    et_pw.setText("");

                    return true;


                case R.id.capture:

                    intent = new Intent(MypageCheckActivity.this, CaptureActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:

                    intent = new Intent(MypageCheckActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.camera:

                    intent = new Intent(MypageCheckActivity.this, LiveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:

                    intent = new Intent(MypageCheckActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:

                    intent = new Intent(MypageCheckActivity.this, MainActivity.class);
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

    public void onClickPWCheck(View view) {

        et_pw = findViewById(R.id.et_pw_check);
        String pw_check = et_pw.getText().toString();

        if(password.equals(pw_check)) {
            Toast.makeText(MypageCheckActivity.this, "비밀번호가 일치합니다.\n정보 수정 페이지로 넘어갑니다.", Toast.LENGTH_SHORT).show();

            intent = new Intent(MypageCheckActivity.this, MypageActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MypageCheckActivity.this, "비밀번호가 다릅니다.\n다시 입력 해 주세요.", Toast.LENGTH_SHORT).show();
            et_pw.setText("");
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