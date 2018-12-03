package com.kyudong.agent_client.Board;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kyudong.agent_client.Adapter.BoardRvAdapter;
import com.kyudong.agent_client.Interface.LikeCountInterface;
import com.kyudong.agent_client.R;
import com.kyudong.agent_client.RvItem.BoardRvItem;
import com.kyudong.agent_client.SimpleDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Created by Kyudong on 2017. 11. 25..
 */

public class BoardIncheon extends Fragment implements LikeCountInterface {

    private RecyclerView boardIncheonRv;
    private LinearLayoutManager llm;
    private BoardRvAdapter adapter;
    private ArrayList<BoardRvItem> boardRvItemArrayList = new ArrayList<>();
    private SimpleDividerItemDecoration simpleDividerItemDecoration;
    private OkHttpClient client = new OkHttpClient();

    private LikeCountInterface likeCountInterface;

    private String user_seqNo;

    private long month1;
    private long day1;
    private long hour1;
    private long minute1;
    private long subtract1;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 0;
    int firstVisibleItem=0, visibleItemCount=0, totalItemCount=0;

    public BoardIncheon newInstance() {
        BoardIncheon fragment = new BoardIncheon();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_incheon, container, false);

        user_seqNo = getActivity().getIntent().getStringExtra("user_seqNo");

        findViewById(v);
        initiallize();
        boardRvItemArrayList.clear();

        GetIncheonBoard getIncheonBoard = new GetIncheonBoard();

        try {
            JSONObject object = new JSONObject();
            object.put("region", 8);
            object.put("board_seq", 1000);
            getIncheonBoard.doGetRequest(url + "/board/get/userBoard/" + user_seqNo, object.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return v;
    }

    private void findViewById(View v) {
        boardIncheonRv = (RecyclerView) v.findViewById(R.id.incheonRv);
        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new BoardRvAdapter(getContext(), boardRvItemArrayList, user_seqNo, this);

        boardIncheonRv.setHasFixedSize(true);
        boardIncheonRv.setLayoutManager(llm);
        boardIncheonRv.setAdapter(adapter);

    }

    private void initiallize() {
        previousTotal=0;
        visibleThreshold=0;
        firstVisibleItem=0;
        visibleItemCount=0;
        totalItemCount = 0;
    }

    // ** 좋아요 http 요청 ** //
    @Override
    public String doLikeRequest(String url, String json) throws IOException {
        Request  request = new Request.Builder()
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "좋아요가 눌렸습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return null;
    }

    // ** 인천게시판 HTTP 요청 ** //
    public class GetIncheonBoard {
        String doGetRequest(final String url, String json) throws IOException {
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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                JSONArray parent = new JSONArray(res);

                                if(parent.length()==0) {
                                    //Toast.makeText(getActivity(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show();
                                } else {
                                    for(int i = 0 ; i <parent.length(); i++) {
                                        BoardRvItem item = new BoardRvItem();

                                        JSONObject child = parent.getJSONObject(i);

                                        item.seqNo = child.getInt("board_seqNo");
                                        //item.time = child.getString("board_regDate");
                                        item.title = child.getString("board_title");
                                        item.hitCount = child.getInt("board_hitCount");
                                        item.likeCount = child.getInt("board_likeCount");
                                        item.like_seq = child.getString("like_seq");
                                        item.commentCount = child.getInt("comment_cnt");

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

                                        boardRvItemArrayList.add(item);
                                    }
                                }
                                adapter.notifyDataSetChanged();

                                boardIncheonRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);

                                        visibleItemCount = boardIncheonRv.getChildCount();
                                        totalItemCount = llm.getItemCount();
                                        firstVisibleItem = llm.findFirstVisibleItemPosition();

                                        if (loading) {
                                            if (totalItemCount > previousTotal) {
                                                loading = false;
                                                previousTotal = totalItemCount;
                                            }
                                        }
                                        if (!loading && (totalItemCount - visibleItemCount)
                                                <= (firstVisibleItem + visibleThreshold)) {
                                            // End has been reached
                                            try {
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("region", 8);
                                                jsonObject.put("board_seq", boardRvItemArrayList.get(boardRvItemArrayList.size()-1).seqNo);

                                                doGetRequest("http://13.125.46.71:8080/board/get/userBoard/" + user_seqNo, jsonObject.toString());
                                            } catch (JSONException | IOException e) {
                                                e.printStackTrace();
                                            }
                                            // Do something
                                            loading = true;

                                        }
                                    }
                                });

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
