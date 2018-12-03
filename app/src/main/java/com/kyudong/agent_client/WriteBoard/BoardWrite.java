package com.kyudong.agent_client.WriteBoard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kyudong.agent_client.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kyudong.agent_client.RealMain.url;

public class BoardWrite extends AppCompatActivity implements View.OnClickListener {

    private Spinner selectRegionSpinner;
    private ArrayAdapter spinnerAdapter;
    private int selectedRegionInt;

    private EditText writeBoardTitle_edtxt;
    private EditText writeBoardContent_edtxt;
    private Button writeBoardBtn;
    private OkHttpClient client;

    private String str;
    private int regionNo;
    private String seqNo;

    public MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        Intent getSeqNo = getIntent();
        seqNo = getSeqNo.getStringExtra("user_seqNo");
        findViewId();
        setClickListener();

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.region, R.layout.support_simple_spinner_dropdown_item);
        selectRegionSpinner.setAdapter(spinnerAdapter);
        selectRegionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str = adapterView.getItemAtPosition(i).toString();
                regionNo = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedRegionInt = 99;
            }
        });
    }

    // ** View Id 찾기 ** //
    private void findViewId() {
        writeBoardTitle_edtxt = (EditText) findViewById(R.id.writeboard_title);
        writeBoardContent_edtxt = (EditText) findViewById(R.id.writeboard_content);
        writeBoardBtn = (Button) findViewById(R.id.writeBoardBtn);
        selectRegionSpinner = (Spinner) findViewById(R.id.selectRegion_spinner);
    }

    // ** 클릭 리스너 설정 ** //
    private void setClickListener() {
        writeBoardBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.writeBoardBtn:

                String title = writeBoardTitle_edtxt.getText().toString();
                String content = writeBoardContent_edtxt.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "제목을 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (str.equals("선택")) {
                    Toast.makeText(getApplicationContext(), "지역을 선택하세요", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        SimpleDateFormat zxcv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dt = new Date();
                        zxcv.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String currentTime = zxcv.format(dt);


                        JSONObject parent = new JSONObject();
                        parent.put("board_title", title);
                        parent.put("board_content", content);
                        parent.put("board_regDate", currentTime);
                        //parent.put("board_updateDate", currentTime);
                        parent.put("board_region", regionNo);
                        parent.put("user_seqNo", seqNo);

                        postWriteBoard postWriteBoard = new postWriteBoard();
                        postWriteBoard.post(url + "/board/create", parent.toString());
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
        }
    }

    public class postWriteBoard {
        String post(String url, String json) {
            client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(JSON, json))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String res = response.body().string();

                    if (!response.isSuccessful()) throw new IOException(
                            "Unexpected code " + response
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "작성되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
            return null;
        }
    }
}
