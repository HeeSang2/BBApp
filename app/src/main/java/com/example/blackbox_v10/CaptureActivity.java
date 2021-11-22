package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

public class CaptureActivity extends AppCompatActivity {

    // MenuBar
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    DrawerLayout drawer;
    TextView tv_name, tv_phone;
    ImageView iv_picture;

    // Database
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    // main
    Intent intent;
    ImageView iv_capture;
    TextView tv_date, tv_time;
    Bitmap bitmap;
    Send_Message send_message=Send_Message.getInstance();

    // values
    String id, password, name, phone, img_path, address, port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        // MenuBar
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        // Database
        dbHelper = new MemberSQLiteOpenHelper(this);
        db = dbHelper.getWritableDatabase();

        // main
        iv_capture = (ImageView) findViewById(R.id.iv_capture);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_time = (TextView) findViewById(R.id.tv_time);
        iv_picture = findViewById(R.id.iv_picture);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        id = auto.getString("id", null);
        name = auto.getString("name", null);
        password = auto.getString("password", null);
        phone = auto.getString("phone", null);
        address = auto.getString("address", null);
        port = auto.getString("port", null);
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // 프로필사진 id받아온 다음

        send_message.setId(id);
        send_message.setName(name);
        send_message.setPassword(password);
        send_message.setPhone(phone);
        send_message.setAddress(address);
        send_message.setPort(port);

        // 화면 setting
        cursor=db.query(MemberSQLiteOpenHelper.CAPTURE, null,null,null,null,null,null);

        while(cursor.moveToNext()){
            String date = cursor.getString(0);  // 날짜
            String time = cursor.getString(1);  // 시간
            String picture = cursor.getString(2);// 사진

            // 인텐트 키가 일치하면
            if(getIntent().getStringExtra("str").equals(date+" "+time))
            {
                // 해당 데이터베이스의 날짜 시간 사진을 불러와서 화면에 출력
                bitmap = StringToBitmap(picture);
                iv_capture.setImageBitmap(bitmap);

                tv_date.setText("일자: " + date);
                tv_time.setText("시간: " + time);
            }
        }

        setSupportActionBar(toolbar);
    }

    View.OnClickListener ivMenu_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(Gravity.LEFT);

            tv_name = findViewById(R.id.name_text);
            tv_phone = findViewById(R.id.tel_text);
            iv_picture = findViewById(R.id.user_img);

            tv_name.setText(name + " 님");
            tv_phone.setText("tel: " + phone);
            iv_picture.setImageURI(Uri.parse(img_path));

        }
    };

    NavigationView.OnNavigationItemSelectedListener navigation_listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.mainpage:
                    intent = new Intent(CaptureActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    intent = new Intent(CaptureActivity.this, MypageCheckActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.capture:

                    intent = new Intent(CaptureActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:
                    intent = new Intent(CaptureActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.camera:
                    intent = new Intent(CaptureActivity.this, LiveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:
                    intent = new Intent(CaptureActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:

                    intent = new Intent(CaptureActivity.this, MainActivity.class);
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


    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    // Back Button Click
    public void onClickBack(View view) {
       intent = new Intent();
       setResult(RESULT_OK,intent);
       finish();
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