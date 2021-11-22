package com.example.blackbox_v10;


import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
public class TcpClient implements Serializable {
    private Socket socket;
    private OutputStream socketOutput;
    private BufferedReader socketInput;
    private String message;
    private String ip;
    private String port;
    private ClientCallback listener=null;

    Send_Message send_message=Send_Message.getInstance();
    Handler handler;

    // 생성자
    public TcpClient(){}
    public TcpClient(String ip,String port){
        this.ip = ip;
        this.port = port;
    }
    public TcpClient(String ip,String port,Handler handler){
        this.ip = ip;
        this.port = port;
        this.handler=handler;
    }
    
    // Send Message 객체의 getter, setter
    public void setSend_message(Send_Message send_message) {
        this.send_message = send_message;
    }

    public String getMessage() {
        return message ;
    }

    // 쓰레드 런 : socket연결
    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(ip, Integer.parseInt(port));

                try {
                    socket.connect(socketAddress);
                    socketOutput = socket.getOutputStream();
                    socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new ReceiveThread().start();
                    new check().start();
                    if(listener!=null)
                        listener.onConnect(socket);
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onConnectError(socket, e.getMessage());
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    public class check extends Thread implements Runnable{
        public void run(){
            while(true) {

                if (send_message.getSend_str().equals("left")){
                    send(send_message.getSend_str());
                    send_message.setSend_str("");
                }
                else if (send_message.getSend_str().equals("right")){ ;
                    send(send_message.getSend_str());
                    send_message.setSend_str("");
                }
                else if (send_message.getSend_str().equals("where")){
                    send(send_message.getSend_str());
                    send_message.setSend_str("");
                }

            }

        }
    }

    public void send(final String message){
        new Thread()
        {
            public void run()
            {
                try {
                    socketOutput.write(message.getBytes());
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onDisconnect(socket, e.getMessage());
                }
            }
        }.start();

    }

    // 받는 쓰레드 : 위치값을 받거나, '-1' (차량충돌감지)메시지를 받거나
    public class ReceiveThread extends Thread implements Runnable{
        public void run(){

            String fullString ;
            String[] splitText=new String[3];

            try {
                while((message = socketInput.readLine()) != null) {   // each line must end with a \n to be received
                    send_message.setGet_str(message);
                    if(message.equals("-1")){
                        handler.sendEmptyMessage(0);
                    }
                    fullString=message;
                    splitText=fullString.split("-");
                    if(splitText[0].equals("차량위치")){
                        send_message.setLatitude(Double.parseDouble(splitText[1]));
                        send_message.setLongitude(Double.parseDouble(splitText[2]));
                    }
                }

            } catch (IOException e) {
                if(listener!=null)
                    listener.onDisconnect(socket, e.getMessage());
            }

        }
    }

    public void setClientCallback(ClientCallback listener){
        this.listener=listener;
    }

    public void removeClientCallback(){
        this.listener=null;
    }

    public interface ClientCallback {
        void onMessage(String message);
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError(Socket socket, String message);
    }
}