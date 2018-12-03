package com.kyudong.agent_client;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.kyudong.agent_client.Board.BoardMain;
import com.kyudong.agent_client.UserToken.UserToken;

public class RealMain extends AppCompatActivity {

    public static String url = "http://13.125.46.71:8080";
    public static String localurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        String token = UserToken.getPreferences(getApplicationContext(), "user_seqNo");
        if(token.equals("empty")) {
            Intent loginIntent = new Intent(getApplicationContext(), LoginMain.class);
            startActivity(loginIntent);
            finish();
        } else {
            Intent homeIntent = new Intent(getApplicationContext(), BoardMain.class);
            homeIntent.putExtra("user_seqNo", token);
            startActivity(homeIntent);
            finish();
        }
    }
}
