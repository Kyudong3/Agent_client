package com.kyudong.agent_client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kyudong.agent_client.Board.BoardMain;
import com.kyudong.agent_client.Login.SignUp;
import com.kyudong.agent_client.UserToken.UserToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kyudong.agent_client.RealMain.url;

public class LoginMain extends AppCompatActivity implements View.OnClickListener{

    private EditText login_accountEditText;
    private EditText login_passwordEditText;
    private TextView registerTxv;
    private TextView findIdTxv;
    private Button loginBtn;
    private CheckBox autoLoginCB;
    private OkHttpClient client;
    public  MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private String authorization;
    private SharedPreferences mPref;
    private Boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        findById();
        setClickListener();

        mPref = getSharedPreferences("isFirst", MODE_PRIVATE);
        isFirst = mPref.getBoolean("isFirst", false);
        if(!isFirst) {
            SharedPreferences.Editor editor = mPref.edit();
            editor.putBoolean("isFirst", true);
            editor.apply();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
        Date date = new Date();
        String datee = format.format(date);
    }

    // ** 뷰 아이디 찾는 함수 ** //
    private void findById() {
        login_accountEditText = (EditText) findViewById(R.id.login_account_Etxt);
        login_passwordEditText = (EditText) findViewById(R.id.login_password_Etxt);
        registerTxv = (TextView) findViewById(R.id.RegisterTxv);
        findIdTxv = (TextView) findViewById(R.id.FindIdPwdTxv);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        autoLoginCB = (CheckBox) findViewById(R.id.autoLoginCheckBox);
    }

    // ** 뷰에 클릭 리스너 설정하는 함수 ** //
    private void setClickListener() {
        registerTxv.setOnClickListener(this);
        findIdTxv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
    }

    // ** 클릭 이벤트 ** //
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RegisterTxv:
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
                break;
            case R.id.FindIdPwdTxv:
                Toast.makeText(getApplicationContext(), "서비스를 준비중입니다", Toast.LENGTH_SHORT).show();
                break;
            case R.id.loginBtn:
                String authId = login_accountEditText.getText().toString().trim();
                String authPwd = login_passwordEditText.getText().toString().trim();

                if(authId.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if(authPwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("auth_id", authId);
                        jsonObject.put("auth_password", authPwd);

                        PostLogin authLogin = new PostLogin();
                        authLogin.post(url + "/auth/login", jsonObject.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    // ** 로그인 HTTP 요청 ** //
    public class PostLogin {
        String post(String url, String json) throws IOException {

            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .url(url)
                    .post(RequestBody.create(JSON, json))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("get", " : " + "bad");
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {

                    final String res = response.body().string();

                    authorization = response.headers().get("authorization");

                    if(autoLoginCB.isChecked()) {
                        UserToken.setPreferences(getApplicationContext(),"user_seqNo", authorization);
                    } else {
                        UserToken.setPreferences(getApplicationContext(), "user_seqNo", "empty");
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(res);

                                String responseCode = jsonObject.getString("code");
                                if(responseCode.equals("NO_AUTHORIZATION")) {
                                    Toast.makeText(getApplicationContext(), "아이디와 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                }
                                if(responseCode.equals("LOGIN_SUCCESS")) {
                                    JSONArray data = jsonObject.getJSONArray("data");
                                    JSONObject childObj = data.getJSONObject(0);
                                    String user_seqNo = childObj.getString("user_seqNo");
                                    Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), BoardMain.class);
                                    intent.putExtra("user_seqNo", user_seqNo);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

            return null;
        }
    }

}
