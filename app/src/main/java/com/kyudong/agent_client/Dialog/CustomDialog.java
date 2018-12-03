package com.kyudong.agent_client.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kyudong.agent_client.Board.BoardMain;
import com.kyudong.agent_client.LoginMain;
import com.kyudong.agent_client.R;
import com.kyudong.agent_client.UserToken.UserToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Kyudong on 2017. 12. 13..
 */

public class CustomDialog extends Dialog implements View.OnClickListener{

    private ImageButton btn_confirm;
    private ImageButton btn_cancel;
    private OkHttpClient client;
    private Context context;

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        Activity owner = (context instanceof Activity) ? (Activity)context : null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //타이틀 바 삭제
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.custom_dialog);

        btn_confirm = (ImageButton) findViewById(R.id.btn_confirm);
        btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);

        setClickListener();
    }

    private void setClickListener() {
        btn_confirm.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                Toast.makeText(getContext(), "확인 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                PostLogout postLogout = new PostLogout();
                try {
                    String token = UserToken.getPreferences(getContext(), "user_seqNo");
                    postLogout.post("http://10.0.2.2:8080/auth/logout/" + token);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_cancel:
                Toast.makeText(getContext(), "취소 버튼이 눌렸습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    // ** 로그아웃 HTTP 요청 ** //
    public class PostLogout {
        String post(String url) throws IOException {

            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                String code = jsonObject.getString("code");
                                if(code.equals("LOGOUT_SUCCESS")) {
                                    UserToken.setPreferences(getContext(), "user_seqNo", "empty");
                                    Toast.makeText(getContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                    Intent intent = new Intent(getContext(), LoginMain.class);
                                    getContext().startActivity(intent);
                                    ((Activity) context).finish();
                                } else {
                                    Toast.makeText(getContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //Toast.makeText(getContext(), "wow", Toast.LENGTH_SHORT).show();
                        }
                    }, 0);
                }
            });

            return null;
        }
    }
}
