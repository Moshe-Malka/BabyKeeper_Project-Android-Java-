package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Thread splashTimer = new Thread(){
            public void run(){
                try{
                    sleep(10000);
                    this.interrupt();
                }catch(InterruptedException ie){
                    ie.printStackTrace();
                }finally{
                    Intent openApp = new Intent(getApplicationContext(),LoginAcvtivity.class);
                    openApp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    openApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    SplashActivity.this.startActivity(openApp);
                    finish();
                }
            }
        };
        splashTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


}
