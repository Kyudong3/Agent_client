package com.kyudong.agent_client.Splash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kyudong.agent_client.R;
import com.kyudong.agent_client.RealMain;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1500);
                    Intent splashIntent = new Intent(getApplicationContext(), RealMain.class);
                    startActivity(splashIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();
    }
}
