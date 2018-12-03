package com.kyudong.agent_client.ReadBoard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyudong.agent_client.Comment.CommentMain;
import com.kyudong.agent_client.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class ReadBoard extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();

    private TextView timeTxv;
    private TextView titleTxv;
    private TextView hitCountTxv;
    private TextView likeCountTxv;
    private TextView dislikeCountTxv;
    private TextView contentTxv;
    private LinearLayout commentLL;
    private ImageView readBoard_likeCountIv;

    private long month1;
    private long day1;
    private long hour1;
    private long minute1;
    private long subtract1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_board);

        Intent intent = getIntent();
        final int seqNo = intent.getIntExtra("seqNo", -1);

        findById();

        GetseqNoBoard getseqNoBoard = new GetseqNoBoard();

        try {
            getseqNoBoard.doGetRequest(url + "/board/get/" + seqNo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        commentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentMain.class);
                intent.putExtra("board_seqNo", seqNo);
                view.getContext().startActivity(intent);
            }
        });

    }

    private void findById() {
        timeTxv = (TextView) findViewById(R.id.readBoard_timeTxv);
        titleTxv = (TextView) findViewById(R.id.readBoard_titleTxv);
        hitCountTxv = (TextView) findViewById(R.id.readBoard_hitCountTxv);
        likeCountTxv = (TextView) findViewById(R.id.readBoard_likeCountTxv);
        dislikeCountTxv = (TextView) findViewById(R.id.readBoard_dislikeCountTxv);
        contentTxv = (TextView) findViewById(R.id.readBoard_contentTxv);
        commentLL = (LinearLayout) findViewById(R.id.comment_read_LL);
        readBoard_likeCountIv = (ImageView) findViewById(R.id.readBoard_likeCountIv);

        contentTxv.setMovementMethod(new ScrollingMovementMethod());

    }

    public class GetseqNoBoard {
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
                                JSONObject child = parent.getJSONObject(0);

                                titleTxv.setText(child.getString("board_title"));
                                hitCountTxv.setText(child.getString("board_hitCount"));
                                likeCountTxv.setText(child.getString("board_likeCount"));
                                contentTxv.setText(child.getString("board_content"));
                                dislikeCountTxv.setText(child.getString("comment_cnt"));

                                String likeSeq = child.getString("like_seq");
                                if(likeSeq.equals("null")) {
                                    readBoard_likeCountIv.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                                } else {
                                    readBoard_likeCountIv.setImageResource(R.drawable.heart_red);
                                }

                                final Date zz = new Date();
                                SimpleDateFormat zxcv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                zxcv.setTimeZone(TimeZone.getTimeZone("UTC"));

                                try {
                                    Date xxx = zxcv.parse(child.getString("board_regDate"));
                                    subtract1 = zz.getTime() - xxx.getTime(); // 밀리세컨드 값으로 나옴

                                    month1 = (subtract1/1000/60/60/24/30);
                                    day1 = (subtract1/1000/60/60/24);
                                    hour1 = (subtract1/1000/60/60);
                                    minute1 = (subtract1/1000/60);
                                    if(month1 == 0) {
                                        if(day1 != 0) {
                                            if(day1 == 1) {
                                                timeTxv.setText("어제");
                                            } else {
                                                timeTxv.setText(day1+"일 전");
                                            }
                                        } else {
                                            if(hour1 !=0) {
                                                timeTxv.setText(hour1+"시간 전");
                                            } else {
                                                timeTxv.setText(minute1+"분 전");
                                            }
                                        }
                                    } else if(minute1 <=0) {
                                        timeTxv.setText("방금");
                                    } else {
                                        timeTxv.setText(month1+"달 전");
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
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
