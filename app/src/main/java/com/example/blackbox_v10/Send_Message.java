package com.example.blackbox_v10;

import java.io.Serializable;
import java.util.PrimitiveIterator;

public class Send_Message implements Serializable {
    private String send_str="asd";
    private double latitude=36.80766;  //위도
    private double longitude=127.16360;
    private String get_str="1";
    private String id;
    private String password="1";
    private String name;
    private String phone="1";
    private String address="1";
    private String port="1";

    // 생성자는 private
    private static final Send_Message ourInstance=new Send_Message();

    public static Send_Message getOurInstance() {
        return ourInstance;
    }

    public static Send_Message getInstance(){
        return ourInstance;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }


    public void setGet_str(String get_str) {
        this.get_str = get_str;
    }

    public String getGet_str() {
        return get_str;
    }

    public String getSend_str() {
        return send_str;
    }

    public void setSend_str(String send_str) {
        this.send_str = send_str;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
