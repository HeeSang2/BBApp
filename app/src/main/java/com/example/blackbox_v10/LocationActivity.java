package com.example.blackbox_v10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.material.navigation.NavigationView;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
ActivityCompat.OnRequestPermissionsResultCallback{
    HashMap<String, String> markerMap = new HashMap<String, String>();

    private GoogleMap mMap;
    private  Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private  static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION}; //외부 저장소

    Send_Message send_message=Send_Message.getInstance();
    double a=send_message.getLatitude();
    double b=send_message.getLongitude();
    Location mCurrentLocation;
    LatLng currentPosition;
    LatLng previousPosition = null;
    LatLng BB_Main_location = new LatLng(36.809949,127.148000);
    LatLng BB_second_location = new LatLng(36.81371,127.13458);
    LatLng BB_third_location = new LatLng(36.80776,127.16388);
    LatLng My_Car_location = new LatLng(a,b);
    LatLng latLng;

    Button bt_mylocate;
    Button bt_BBMain;
    Button bt_BBsecond;
    Button bt_BBthird;
    Button bt_Mycar;

    private  FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;

    int camera_state = 0;

    // Toolbar
    private ImageView ivMenu;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    TextView tv_name, tv_phone;

    Intent intent;
    ImageView iv_picture;
    String id, name, phone;
    String img_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //google map
        bt_mylocate = findViewById(R.id.bt_mylocate);
        bt_BBMain = findViewById(R.id.bt_BBMain);
        bt_BBsecond = findViewById(R.id.bt_BBsecond);
        bt_BBthird = findViewById(R.id.bt_BBthird);
        bt_Mycar = findViewById(R.id.bt_Mycar);
        mLayout = findViewById(R.id.layout_main);

        //메뉴 바
        ivMenu = findViewById(R.id.iv_menu);
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation);

        bt_BBMain.setOnClickListener(BBMain_listener);
        bt_BBsecond.setOnClickListener(BBPrague_listener);
        bt_BBthird.setOnClickListener(BBNewYork_listener);
        bt_Mycar.setOnClickListener(Mycar_listener);
        ivMenu.setOnClickListener(ivMenu_listener);
        navigationView.setNavigationItemSelectedListener(navigation_listener);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        name = send_message.getName();
        phone = send_message.getPhone();
        id = send_message.getId();
        img_path = "/data/user/0/com.example.blackbox_v10/cache/profile" + id + ".jpg";

        // 프로필사진
        setSupportActionBar(toolbar);

        send_message.setSend_str("where");


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
                    intent = new Intent(LocationActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.account:
                    intent = new Intent(LocationActivity.this, MypageCheckActivity.class);
                    startActivity(intent);
                    finish();
                    return true;


                case R.id.capture:
                    intent = new Intent(LocationActivity.this, ListActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.call_center:
                    intent = new Intent(LocationActivity.this, QnaActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.camera:

                    intent = new Intent(LocationActivity.this, LiveActivity.class);
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.location:
                    Toast.makeText(LocationActivity.this, "이미 GPS 페이지 입니다.", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.logout:

                    intent = new Intent(LocationActivity.this, MainActivity.class);
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

    View.OnClickListener BBMain_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BB_Main_location, 18));
        }
    };

    View.OnClickListener BBPrague_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BB_second_location, 18));
        }
    };

    View.OnClickListener BBNewYork_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BB_third_location, 18));
        }
    };

    View.OnClickListener Mycar_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(My_Car_location,18));

        }
    };




    @Override
    public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
        String id = null;

        Marker BB_main = mMap.addMarker(new MarkerOptions().position(BB_Main_location)
                .title("Break Breaker 본사")
                .snippet("충청남도 천안시 동남구 대흥로")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        id = BB_main.getId();
        markerMap.put(id,"BB_main");

        Marker BB_second = mMap.addMarker(new MarkerOptions().position(BB_second_location)
                .title("Break Breaker 성정점")
                .snippet("충청남도 천안시 서북구 성정로")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        id = BB_second.getId();
        markerMap.put(id,"BB_second");

        Marker BB_third = mMap.addMarker(new MarkerOptions().position(BB_third_location)
                .title("Break Breaker 원성점")
                .snippet("충청남도 천안시 서북구 원성동")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        id = BB_third.getId();
        markerMap.put(id,"BB_third");


        MarkerOptions myCar = new MarkerOptions();
        myCar.position(My_Car_location);

        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.car1);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b,200,200,false);
        myCar.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        mMap.addMarker(myCar);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {

                String m = markerMap.get(marker.getId());
                String title;
                String address;

                intent = new Intent(LocationActivity.this,GoogleMapActivity.class);
                title = marker.getTitle();
                address = marker.getSnippet();

                if(m.equals("BB_main")){
                    sendInfo(title, address, "041", "555", "1234", "10:00 ~ 18:00");
                }

                else if(m.equals("BB_second")){
                    sendInfo(title, address, "041", "532", "5555", "10:00 ~ 18:00");
                }

                else if(m.equals("BB_third")){
                    sendInfo(title, address, "041", "555", "1234", "10:00 ~ 18:00");
                }
            }
        });

        setDefaultLocation();

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(LocationActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
            }
        });
    }

    void sendInfo(String title, String address, String phone, String phone1, String phone2, String time){
        intent.putExtra("title",title);
        intent.putExtra("address",address);
        intent.putExtra("phone",phone);
        intent.putExtra("phone1",phone1);
        intent.putExtra("phone2",phone2);
        intent.putExtra("time",time);

        startActivity(intent);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                previousPosition = currentPosition;

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                if(previousPosition == null) previousPosition = currentPosition;

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());


                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;
            }
        }
    };

    private void startLocationUpdates(){

        if(!checkLocationServiceStatus()){
            showDialogForLocationServiceSetting();
        }else{
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if(hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED){
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if(checkPermission())
                mMap.setMyLocationEnabled(true);
        }

    }


    public String getCurrentAddress(LatLng latlng){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try{

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        }catch (IOException ioException){
            Toast.makeText(this,"지오코더 서비스 사용불가",Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        }catch (IllegalArgumentException illegalArgumentException){
            Toast.makeText(this,"잘못된 GPS 좌표",Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if(addresses == null || addresses.size() == 0){
            Toast.makeText(this,"주소 미발견",Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }else{
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    public boolean checkLocationServiceStatus(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String makerTitle, String markerSnippet){
        if(currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);

        if(camera_state == 0) {
            camera_state = 1;
        }

        if(camera_state == 1) {
            mMap.moveCamera(cameraUpdate);
            camera_state = 2;
        }
        bt_mylocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18));
            }
        });

    }

    public void setDefaultLocation(){

        //디폴트 위치,Seoul
        LatLng DEFAULT_LOCATION = new  LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if(currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        CameraUpdateFactory.newLatLng(DEFAULT_LOCATION);
        mMap.moveCamera(cameraUpdate);
    }

    private boolean checkPermission(){
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    @Override

    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                startLocationUpdates();
            }
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])){
                    Snackbar.make(mLayout,"퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해 주세요.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }else{
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    private void showDialogForLocationServiceSetting(){

        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override

    protected  void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case GPS_ENABLE_REQUEST_CODE:

                if(checkLocationServiceStatus()){
                    if(checkLocationServiceStatus()){

                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }

}