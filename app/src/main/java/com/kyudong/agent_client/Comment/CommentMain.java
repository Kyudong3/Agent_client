package com.kyudong.agent_client.Comment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kyudong.agent_client.Adapter.CommentRvAdapter;
import com.kyudong.agent_client.R;
import com.kyudong.agent_client.RvItem.CommentRvItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kyudong.agent_client.Login.SignUp.JSON;
import static com.kyudong.agent_client.RealMain.url;

public class CommentMain extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView CommentRv;
    private LinearLayoutManager llm;
    private OkHttpClient client = new OkHttpClient();
    private CommentRvAdapter adapter;
    private ArrayList<CommentRvItem> commentRvItemArrayList = new ArrayList<>();
    private EditText writeCommentEdtxt;
    private Button commentRegisterBtn;
    private int board_seqNo;

    private long month1;
    private long day1;
    private long hour1;
    private long minute1;
    private long subtract1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Intent intent = getIntent();
        board_seqNo = intent.getIntExtra("board_seqNo", -1);
        findViewId();

        GetComment getComment = new GetComment();

        try {
            getComment.doGetRequest(url + "/board/getComments/" +
            board_seqNo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void findViewId() {
        writeCommentEdtxt = (EditText) findViewById(R.id.writeComment);
        commentRegisterBtn = (Button) findViewById(R.id.registerCommentBtn);
        commentRegisterBtn.setOnClickListener(this);

        CommentRv = (RecyclerView) findViewById(R.id.commentRv);
        llm = new LinearLayoutManager(getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new CommentRvAdapter(getApplicationContext(), commentRvItemArrayList);

        CommentRv.setHasFixedSize(true);
        CommentRv.setLayoutManager(llm);
        CommentRv.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerCommentBtn:
                String content = writeCommentEdtxt.getText().toString();
                if(content.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        SimpleDateFormat zxcv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        Date dt = new Date();
                        zxcv.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String currentTime = zxcv.format(dt);

                        JSONObject parent = new JSONObject();
                        parent.put("comment_content", content);
                        parent.put("comment_regDate", currentTime);
                        parent.put("board_seqNo", board_seqNo);

                        PostWriteComment postWriteComment = new PostWriteComment();
                        postWriteComment.post(url + "/board/comment/create", parent.toString());
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                finish();
        }
    }

    // 댓글 가져오는 함수 //
    public class GetComment {
        String doGetRequest(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONArray parent = new JSONArray(res);

                                if(parent.length()==0) {
                                    //Toast.makeText(getApplicationContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    for(int i = 0 ; i <parent.length(); i++) {
                                        CommentRvItem item = new CommentRvItem();

                                        JSONObject child = parent.getJSONObject(i);

                                        item.seqNo = child.getInt("comment_seqNo");
                                        item.content = child.getString("comment_content");
                                        //item.time = child.getString("comment_regDate");

                                        final Date zz = new Date();
                                        SimpleDateFormat zxcv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        zxcv.setTimeZone(TimeZone.getTimeZone("UTC"));

                                        try {
                                            Date xxx = zxcv.parse(child.getString("comment_regDate"));
                                            subtract1 = zz.getTime() - xxx.getTime(); // 밀리세컨드 값으로 나옴

                                            month1 = (subtract1/1000/60/60/24/30);
                                            day1 = (subtract1/1000/60/60/24);
                                            hour1 = (subtract1/1000/60/60);
                                            minute1 = (subtract1/1000/60);
                                            if(month1 == 0) {
                                                if(day1 != 0) {
                                                    if(day1 == 1) {
                                                        item.time = "어제";
                                                    } else {
                                                        item.time = day1+"일 전";
                                                    }
                                                } else {
                                                    if(hour1 !=0) {
                                                        item.time = hour1+"시간 전";
                                                    } else {
                                                        item.time = minute1+"분 전";
                                                    }
                                                }
                                            } else if(minute1 <=0) {
                                                item.time = "방금";
                                            } else {
                                                item.time = month1+"달 전";
                                            }

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        commentRvItemArrayList.add(item);
                                    }
                                }
                                adapter.notifyDataSetChanged();

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

    // 댓글 쓰는 함수 //
    public class PostWriteComment {
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
                            Toast.makeText(getApplicationContext(), "댓글이 작성되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
            return null;
        }
    }
}
