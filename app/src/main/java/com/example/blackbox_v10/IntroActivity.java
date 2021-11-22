package com.example.blackbox_v10;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class IntroActivity extends AppCompatActivity {

    // main
    TextView main_name;
    Animation anim_FadeIn;

    // values
    String id, password, name, phone, address, port;
    Send_Message send_message=Send_Message.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        main_name = findViewById(R.id.main_name);
        anim_FadeIn = AnimationUtils.loadAnimation(this,R.anim.anim_splash_fadein);

        // auto-login
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        id = auto.getString("id", null);
        name = auto.getString("name", null);
        password = auto.getString("password", null);
        phone = auto.getString("phone", null);
        address = auto.getString("address", null);
        port = auto.getString("port", null);

        send_message.setId(id);
        send_message.setName(name);
        send_message.setPassword(password);
        send_message.setPhone(phone);
        send_message.setAddress(address);
        send_message.setPort(port);

        anim_FadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!TextUtils.isEmpty(id)) {

                    Intent intent = new Intent(IntroActivity.this, MenuActivity.class);

                    Toast.makeText(IntroActivity.this, name + "님 자동 로그인 했습니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                }
                else if(TextUtils.isEmpty(id)) {
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        main_name.startAnimation(anim_FadeIn);
    }
}