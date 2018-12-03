package com.kyudong.agent_client.Login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kyudong.agent_client.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kyudong.agent_client.RealMain.url;

public class SignUp extends AppCompatActivity {

    private EditText signupId;
    private EditText signupPwd;
    private EditText signupPhone;
    private EditText signupAddr;
    private Button signupBtn;

    private OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findById();

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = signupId.getText().toString().trim();
                String pwd = signupPwd.getText().toString().trim();
                String phone = signupPhone.getText().toString().trim();
                String addr = signupAddr.getText().toString().trim();

                if(id.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "아이디를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if(pwd.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if(phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "전화번호를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else if(addr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "주소를 입력하세요!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_id",id);
                        jsonObject.put("user_password",pwd);
                        jsonObject.put("user_phoneNumber",phone);
                        jsonObject.put("user_address",addr);

                        PostUser postUser = new PostUser();
                        //postUser.post("http://10.0.2.2:8080/user/create", jsonObject.toString());
                        postUser.post(url+ "/user/create", jsonObject.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    // ** 뷰 Id 찾기 ** //
    private void findById() {
        signupId = (EditText) findViewById(R.id.signupIdEtxt);
        signupPwd = (EditText) findViewById(R.id.signupPwdEtxt);
        signupPhone = (EditText) findViewById(R.id.signupPhoneEtxt);
        signupAddr = (EditText) findViewById(R.id.signupAddrEtxt);
        signupBtn = (Button) findViewById(R.id.signupBtn);
    }

    // ** 회원 가입 HTTP 요청 ** //
    public class PostUser {

        String post(String url, String json) throws IOException {
            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("get", " : " + "bad");
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();

                    if (!response.isSuccessful()) throw new IOException(
                            "Unexpected code " + response
                    );

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(res);

                                int responseCode = jsonObject.getInt("responseCode");

                                if(responseCode==3) {
                                    Toast.makeText(getApplicationContext(), "회원가입 되었습니다!" , Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if(responseCode==4) {
                                    Toast.makeText(getApplicationContext(), "중복된 아이디가 있습니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
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
