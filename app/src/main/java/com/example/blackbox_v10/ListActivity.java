package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends AppCompatActivity {

    // Toolbar
    private ImageView iv_Menu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    TextView tv_name, tv_phone;
    ImageView iv_picture;
    String item;

    // dDB
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase db;

    // main
    Intent intent;
    DrawerLayout drawer;

    // Values
    String id, password, name, phone, address, port;

    ArrayList<String> item_list = new ArrayList<>();
    ListView lv_list;
    Bitmap bitmap;
    String bitmap_str;

    String img_path;
    Send_Message send_message=Send_Message.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Menu Bar
        iv_Menu = findViewById(R.id.iv_menu);
        iv_picture = findViewById(R.id.iv_picture);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        // Main
        id =send_message.getId();
        password = send_message.getPassword();
        name = send_message.getName();
        phone = send_message.getPhone();
        address = send_message.getAddress();
        port = send_message.getPort();

        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // ???????????????

        // database
        dbHelper=new MemberSQLiteOpenHelper(this);
        db= dbHelper.getWritableDatabase();

        // ????????? ????????????
        item_list = setData();  // ArrayList ?????????
        lv_list = (ListView)findViewById(R.id.lv_list);

        // Adapter set & onClickListener
        CaptureListAdapter adapter = new CaptureListAdapter(this, R.layout.capture_list_row, item_list);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(listener);

        iv_Menu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);
        setSupportActionBar(toolbar);

    }


    // List Item ??????
    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            intent = new Intent(ListActivity.this, CaptureActivity.class);
            intent.putExtra("str", item);

            startActivity(intent);
        }
    };


    // REFRESH ?????? ??????
    public void onClickRefresh(View view) {
        getData();

        item_list = setData();  // ArrayList ?????????
        lv_list = (ListView)findViewById(R.id.lv_list);
    }


    private ArrayList<String> setData(){

        ArrayList<String> item_list = new ArrayList<>();
        Cursor cursor;

        // ????????????????????? ????????????
        cursor=db.query(MemberSQLiteOpenHelper.CAPTURE, null,null,null,null,null,null);

        while(cursor.moveToNext()){
            String date = cursor.getString(0);  // ??????
            String time = cursor.getString(1);  // ??????

            item = date + " " + time;      // ?????? + ??????
            item_list.add(item);
        }


        return item_list;
    }

    private void getData(){

        // ????????????????????? ??????????????? ????????? ????????? ??? ???,
        // ????????? ?????? Thread??? ?????? ????????? ?????? Thread??? ???????????? ???????????? ??????.
        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // ?????? ?????? ??? ??????(?????? ??????)
                    URL url = new URL("http://192.168.0.234:8091/?action=snapshot/test.jpg");

                    // Web?????? ???????????? ????????? ???
                    // ImageView??? ????????? Bitmap??? ?????????
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // ????????? ?????? ?????? ??????
                    conn.connect();

                    InputStream is = conn.getInputStream();  // InputStream ??? ????????????
                    bitmap = BitmapFactory.decodeStream(is); // Bitmap?????? ??????
                    bitmap_str = BitmapToString(bitmap);

                    // ?????? ?????? ?????? ?????????
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);

                    // ?????? & ?????? ????????? ??????
                    SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:ss");

                    String getDate = sdf_date.format(date);
                    String getTime = sdf_time.format(date);

                    Log.i("bb", "Date: "+getDate + ", Time: " + getTime);   // ????????????

                    // ??? ??????
                    ContentValues values=new ContentValues();

                    values.put(MemberSQLiteOpenHelper.CAPTURE_DATE, getDate);
                    values.put(MemberSQLiteOpenHelper.CAPTURE_TIME, getTime);
                    values.put(MemberSQLiteOpenHelper.CAPTURE_PICTURE, bitmap_str);

                    long result=db.insert(MemberSQLiteOpenHelper.CAPTURE,null,values);

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start(); // Thread ??????
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();

        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }


    View.OnClickListener ivMenu_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(Gravity.LEFT);

            tv_name = (TextView) findViewById(R.id.name_text);
            tv_phone = (TextView) findViewById(R.id.tel_text);
            iv_picture = findViewById(R.id.user_img);

            tv_name.setText(send_message.getName() + " ???");
            tv_phone.setText("tel: " + send_message.getPhone());
            iv_picture.setImageURI(Uri.parse(img_path));
        }
    };

    NavigationView.OnNavigationItemSelectedListener navigation_listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.mainpage:
                    intent = new Intent(ListActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    intent = new Intent(ListActivity.this, MypageCheckActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.capture:
                    Toast.makeText(ListActivity.this, "?????? ??????????????? ?????????.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.call_center:
                    intent = new Intent(ListActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.camera:
                    intent = new Intent(ListActivity.this, LiveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:
                    intent = new Intent(ListActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:

                    intent = new Intent(ListActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor autoLogin = auto.edit();

                    autoLogin.clear();
                    autoLogin.commit();

                    return true;
            }

            return true;
        }
    };

    // ????????? ????????????
    public void onBackPressed(){
        DrawerLayout drawer = findViewById(R.id.drawer);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

}
