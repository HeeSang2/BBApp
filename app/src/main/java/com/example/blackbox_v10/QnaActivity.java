package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

public class QnaActivity extends AppCompatActivity {

    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    RecyclerVierAdapter adapter;
    ImageView ivMenu;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    DataQuestion data;
    private NavigationView navigationView;
    ImageView iv_picture;
    TextView tv_name, tv_phone;

    Intent intent;
    String id, name, phone;
    String img_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);

        dbHelper=new MemberSQLiteOpenHelper(this);
        db= dbHelper.getWritableDatabase();

        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation);
        tv_name = (TextView) findViewById(R.id.name_text);
        tv_phone = (TextView) findViewById(R.id.tel_text);
        iv_picture = findViewById(R.id.user_img);


        navigationView.setNavigationItemSelectedListener(navigation_listener);
        ivMenu.setOnClickListener(ivMenu_listener);

        setSupportActionBar(toolbar);

        init();
        getData();
    }


    private void init(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // 로그인 상태 정보
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        id = auto.getString("id", null);
        name = auto.getString("name",null);
        phone = auto.getString("phone", null);
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // 프로필사진

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerVierAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void getData(){

        // 데이터베이스를 불러와서
        cursor=db.query(MemberSQLiteOpenHelper.QUESTION, null,null,null,null,null,null);

        String str="";

        while(cursor.moveToNext()){
            String question = cursor.getString(0);
            String answer = cursor.getString(1);

            // 데이터를 클래스에 저장 (그래야 어뎁터 이해가 되서)
            data = new DataQuestion(question, answer);
            adapter.addItem(data);
        }

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
                    intent = new Intent(QnaActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    intent = new Intent(QnaActivity.this, MypageCheckActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.capture:
                    intent = new Intent(QnaActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:
                    Toast.makeText(QnaActivity.this, "이미 사용설명서 페이지 입니다.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.camera:
                    intent = new Intent(QnaActivity.this, LiveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:
                    intent = new Intent(QnaActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:
                    intent = new Intent(QnaActivity.this, MainActivity.class);
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