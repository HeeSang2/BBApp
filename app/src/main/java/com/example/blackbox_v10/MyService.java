package com.example.blackbox_v10;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyService extends Service {
    TcpClient mClient;
    MemberSQLiteOpenHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    ArrayList<String> item_list = new ArrayList<>();
    Bitmap bitmap;
    String bitmap_str;

    Intent intent;
    String getDate, getTime;
    String id, password, name, phone, address, port;

    SharedPreferences auto;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onRebind(Intent intent) {

        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 로그인 정보 불러오기
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        address = auto.getString("address", null);
        port = auto.getString("port", null);

        // database
        dbHelper = new MemberSQLiteOpenHelper(this);
        db= dbHelper.getWritableDatabase();

        myServiceHandler handler = new myServiceHandler();
        mClient = new TcpClient(address, port, handler);

        mClient.setClientCallback(new TcpClient.ClientCallback() {
            @Override
            public void onMessage(String message) {
            }

            @Override
            public void onConnect(Socket socket) {
                mClient.send("on create\n");
            }

            @Override
            public void onDisconnect(Socket socket, String message) {
            }

            @Override
            public void onConnectError(Socket socket, String message) {
            }
        });
        mClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        id = auto.getString("id", null);
        password = auto.getString("password", null);
        name = auto.getString("name", null);
        phone = auto.getString("phone", null);
        address = auto.getString("address", null);
        port = auto.getString("port", null);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override// 서비스가 종료될 때 실행
    public void onDestroy() {
        super.onDestroy();
    }

    // Service Handler
    class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            getData();
            item_list = setData();  // ArrayList 초기화
        }
    };


    private void getData(){

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {
                    // 파일 이름 값 변경(날짜 시간)
                    URL url = new URL("http://192.168.0.234:8091/?action=snapshot/test.jpg");

                    // Web에서 이미지를 가져온 뒤
                    // ImageView에 지정할 Bitmap을 만든다
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // 서버로 부터 응답 수신
                    conn.connect();

                    InputStream is = conn.getInputStream();  // InputStream 값 가져오기
                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                    bitmap_str = BitmapToString(bitmap);

                    // 현재 시간 날짜 초기화
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);

                    // 날짜 & 시간 데이터 받기
                    SimpleDateFormat sdf_date = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:ss");

                    getDate = sdf_date.format(date);
                    getTime = sdf_time.format(date);

                    // 값 추가
                    ContentValues values=new ContentValues();

                    values.put(MemberSQLiteOpenHelper.CAPTURE_DATE, getDate);
                    values.put(MemberSQLiteOpenHelper.CAPTURE_TIME, getTime);
                    values.put(MemberSQLiteOpenHelper.CAPTURE_PICTURE, bitmap_str);

                    long result=db.insert(MemberSQLiteOpenHelper.CAPTURE,null,values);
                    createNotification();

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start(); // Thread 실행
    }

    private ArrayList<String> setData(){

        ArrayList<String> item_list = new ArrayList<>();
        Cursor cursor;

        // 데이터베이스를 불러와서
        cursor = db.query(MemberSQLiteOpenHelper.CAPTURE, null,null,null,null,null,null);

        while(cursor.moveToNext()){
            String date = cursor.getString(0);
            String time = cursor.getString(1);

            String item = date + " " + time;      // 날짜 + 시간
            item_list.add(item);
        }
        return item_list;
    }

    public static String BitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();

        String temp = Base64.encodeToString(bytes, Base64.DEFAULT);
        return temp;
    }
    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("차량 충돌");
        builder.setContentText("차량에 출돌이 감지되었습니다.");

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        intent = new Intent(this, CaptureActivity.class);
        intent.putExtra("str", getDate + " " + getTime);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }


}

