package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MypageActivity extends AppCompatActivity {
    Send_Message send_message=Send_Message.getInstance();
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase mdb;
    Bitmap bitmap;

    // Toolbar
    TextView tv_name, tv_phone;
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageView img;
    private NavigationView navigationView;
    DrawerLayout drawer;

    // main
    EditText et_id, et_password, et_name, et_phone, et_address, et_port;
    ImageView iv_picture;

    Intent intent;
    String id, password, name, phone, address, port;

    //이미지 관리에 필요한것들
    Uri uri;
    File tempFile;
    String img_path;            //이미지의 경로를 저장하는 변수

    // 다이얼로그에서 선택할 수 있는 배열
    final CharSequence[] items = {"갤러리", "취소"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        // DB
        dbHelper=new MemberSQLiteOpenHelper(this);
        mdb= dbHelper.getWritableDatabase();

        // Main
        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        et_name = findViewById(R.id.et_name);
        et_phone = findViewById(R.id.et_phone);
        et_address = findViewById(R.id.et_address);
        et_port = findViewById(R.id.et_port);

        // Menu Bar
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        setSupportActionBar(toolbar);
        

        init();

    }

    // 초기 세팅
    void init(){

        // 메뉴로 이동
        id = send_message.getId();
        password = send_message.getPassword();
        name = send_message.getName();
        phone = send_message.getPhone();
        address = send_message.getAddress();
        port = send_message.getPort();

        et_id.setText(id);
        et_name.setText(name);
        et_phone.setText(phone);
        et_password.setText(password);
        et_address.setText(address);
        et_port.setText(port);

        iv_picture = findViewById(R.id.iv_picture);
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg"; // 프로필사진 경로
        iv_picture.setImageURI(Uri.parse(img_path));

        et_id.setFocusable(false); // 아이디는 변경 불가
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // 선택한 이미지에서 비트맵 생성
                uri = data.getData();
                iv_picture.setImageURI(uri);

                BitmapDrawable drawable = (BitmapDrawable)iv_picture.getDrawable();
                bitmap = drawable.getBitmap();

                File storage = getCacheDir();
                String img_profile = "profile" + id + ".jpg";
                tempFile = new File(storage, img_profile);
                img_path = String.valueOf(tempFile);



                }
            }
        }


    public void onClickUPDATE(View view) {

        dbHelper=new MemberSQLiteOpenHelper(this);
        mdb= dbHelper.getWritableDatabase();

        id = et_id.getText().toString();
        password = et_password.getText().toString();
        name = et_name.getText().toString();
        phone = et_phone.getText().toString();
        address = et_address.getText().toString();
        port = et_port.getText().toString();

        ContentValues values=new ContentValues();

        values.put(MemberSQLiteOpenHelper.MEMBER_ID, id);
        values.put(MemberSQLiteOpenHelper.MEMBER_PASSWORD, password);
        values.put(MemberSQLiteOpenHelper.MEMBER_NAME, name);
        values.put(MemberSQLiteOpenHelper.MEMBER_PHONE, phone);
        values.put(MemberSQLiteOpenHelper.MEMBER_ADDRESS, address);
        values.put(MemberSQLiteOpenHelper.MEMBER_PORT, port);
        

        mdb.update(MemberSQLiteOpenHelper.MEMBER, values, "id=?", new String[] {id});
        Toast.makeText(MypageActivity.this, "정보 수정을 완료했습니다..", Toast.LENGTH_SHORT).show();

        // 내부 저장소 값 변경
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLogin = auto.edit();

        // 접속 정보를 null로 변경 후 Destroy
        autoLogin.putString("id", id);
        autoLogin.putString("password", password);
        autoLogin.putString("name", name);
        autoLogin.putString("phone", phone);
        autoLogin.putString("address", address);
        autoLogin.putString("port", port);

        send_message.setId(id);
        send_message.setPassword(password);
        send_message.setName(name);
        send_message.setPhone(phone);
        send_message.setAddress(address);
        send_message.setPort(port);

        
        //설정 버튼 클릭 시 이미지 내장저장소에 저장  --정지성
        try{
            tempFile.createNewFile();
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        }catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        autoLogin.clear();
        autoLogin.commit();

        intent = new Intent(MypageActivity.this, MenuActivity.class);
        startActivity(intent);
    }


    View.OnClickListener ivMenu_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            drawerLayout.openDrawer(Gravity.LEFT);

            tv_name = findViewById(R.id.name_text);
            tv_phone = findViewById(R.id.tel_text);
            img = findViewById(R.id.user_img);

            tv_name.setText(name + " 님");
            tv_phone.setText("tel: " + phone);
            img.setImageURI(Uri.parse(img_path)); // 설정한 이미지로 프사
        }
    };


    NavigationView.OnNavigationItemSelectedListener navigation_listener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.mainpage:
                    intent = new Intent(MypageActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    Toast.makeText(MypageActivity.this, "이미 마이페이지 입니다.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.capture:
                    intent = new Intent(MypageActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:
                    intent = new Intent(MypageActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:
                    intent = new Intent(MypageActivity.this, LocationActivity.class);;
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.logout:

                    intent = new Intent(MypageActivity.this, MainActivity.class);
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

    public void onClickChangePic(View view) {
        AlertDialog.Builder selectDialog  = new AlertDialog.Builder(this);
        selectDialog .setTitle("사진 선택");
        // Dialog가 아닌 바깥을 터치했을 때 Dialog가 사라지지는 기능을 활성화 시키지 않겠다는 코드입니다.
        selectDialog.setCancelable(false);

        // 생성한 Dialog에 목록과 클릭이벤트를 수신할 리스너를 연결하여줍니다.
        selectDialog.setItems(items, new DialogInterface.OnClickListener() {
            //dialog : 사용자가 보고있는 dialog
            // which : 사용자가 클릭한 목록의 index값
            @Override
            public void onClick(DialogInterface dialog, int index) {
                switch (index) {
                    //index에 따라 실행되어야 하는 기능을 이곳에서 작성합니다.
                    case 0: // 사진 선택
                        //실행문 작성
                        intent = new Intent();
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 1);
                        break;

                    case 1: // 취소
                        //실행문 작성 필요 업슴
                        break;
                }
            }
        });

//사용자에게 실제로 Dialog를 볼수 있게 띄워주는 메서드. 이 부분을 작성하지 않으면 Dialog는 사용자에게 보여지지 않음.
        selectDialog.show();

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