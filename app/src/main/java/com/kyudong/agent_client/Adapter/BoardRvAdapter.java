package com.kyudong.agent_client.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kyudong.agent_client.Board.BoardMain;
import com.kyudong.agent_client.Board.BoardSeoul;
import com.kyudong.agent_client.Comment.CommentMain;
import com.kyudong.agent_client.Interface.LikeCountInterface;
import com.kyudong.agent_client.R;
import com.kyudong.agent_client.ReadBoard.ReadBoard;
import com.kyudong.agent_client.RvItem.BoardRvItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.kyudong.agent_client.RealMain.url;

/**
 * Created by Kyudong on 2017. 11. 25..
 */

public class BoardRvAdapter extends RecyclerView.Adapter<BoardRvAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BoardRvItem> boardRvItemArrayList;
    private String user_seqNo;

    private LikeCountInterface likeCountInterface;

    public BoardRvAdapter(Context context, ArrayList<BoardRvItem> boardRvItemArrayList, String user_seqNo, LikeCountInterface likeCountInterface) {
        this.context = context;
        this.boardRvItemArrayList = boardRvItemArrayList;
        this.user_seqNo = user_seqNo;
        this.likeCountInterface = likeCountInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View BoardLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_rv_item,
                parent, false);
        return new ViewHolder(BoardLayout);
    }

    @Override
    public int getItemCount() {
        return boardRvItemArrayList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final BoardRvItem item = boardRvItemArrayList.get(position);

        holder.rvTimeTxv.setText(item.time);
        holder.rvTitleTxv.setText(item.title);
        holder.rvHitCountTxv.setText(String.valueOf(item.hitCount));
        holder.rvLikeCountTxv.setText(String.valueOf(item.likeCount));
        holder.rvCommentTxv.setText(String.valueOf(item.commentCount));

        if(item.like_seq.equals("null")) {
            holder.rvLikeCountIv.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        } else {
            holder.rvLikeCountIv.setImageResource(R.drawable.heart_red);
        }

        holder.rv_entityLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int board_seqNo = boardRvItemArrayList.get(holder.getAdapterPosition()).seqNo;
                Intent intent = new Intent(view.getContext(), ReadBoard.class);
                intent.putExtra("seqNo", board_seqNo);
                view.getContext().startActivity(intent);
            }
        });

        // ** 좋아요, 조회수, 싫어요 클릭 이벤트 ** //
        holder.likeCountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.like_seq.equals("null")) {
                    int likeCount = (item.likeCount) + 1;
                    String lC = likeCount + "";
                    holder.rvLikeCountTxv.setText(lC);
                    holder.rvLikeCountIv.setImageResource(R.drawable.heart_red);
                    item.like_seq = "a";
                    item.likeCount = likeCount;

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_seqNo", user_seqNo);
                        likeCountInterface.doLikeRequest(url +"/board/like/" +
                                        item.seqNo, jsonObject.toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    int likeCount = (item.likeCount) - 1;
                    String lC = likeCount + "";
                    holder.rvLikeCountTxv.setText(lC);
                    holder.rvLikeCountIv.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    item.like_seq = "null";
                    item.likeCount = likeCount;

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("user_seqNo", user_seqNo);
                        likeCountInterface.doLikeRequest(url +"/board/like/" +
                                item.seqNo, jsonObject.toString());
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        holder.CommentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CommentMain.class);
                intent.putExtra("board_seqNo", boardRvItemArrayList.get(holder.getAdapterPosition()).seqNo);
                view.getContext().startActivity(intent);
            }
        });
    }

    // ** 뷰 홀더 ** //
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView rvTimeTxv;
        TextView rvTitleTxv;
        TextView rvHitCountTxv;
        TextView rvLikeCountTxv;
        TextView rvCommentTxv;

        ImageView rvHitCountIv;
        ImageView rvLikeCountIv;
        ImageView rvCommentIv;

        LinearLayout rv_entityLL;
        LinearLayout hitCountLL;
        LinearLayout likeCountLL;
        LinearLayout CommentLL;

        public ViewHolder(View itemView) {
            super(itemView);

            rvTimeTxv = (TextView) itemView.findViewById(R.id.rv_timeTxv);
            rvTitleTxv = (TextView) itemView.findViewById(R.id.rv_titleTxv);
            rvHitCountTxv = (TextView) itemView.findViewById(R.id.rv_hitCountTxv);
            rvLikeCountTxv = (TextView) itemView.findViewById(R.id.rv_likeCountTxv);
            rvCommentTxv = (TextView) itemView.findViewById(R.id.rv_CommentTxv);

            rvHitCountIv = (ImageView) itemView.findViewById(R.id.rv_hitCountIv);
            rvLikeCountIv = (ImageView) itemView.findViewById(R.id.rv_likeCountIv);
            rvCommentIv = (ImageView) itemView.findViewById(R.id.rv_commentIv);

            rv_entityLL = (LinearLayout) itemView.findViewById(R.id.rv_entity_LL);
            hitCountLL = (LinearLayout) itemView.findViewById(R.id.hitCountLL);
            likeCountLL = (LinearLayout) itemView.findViewById(R.id.likeCountLL);
            CommentLL = (LinearLayout) itemView.findViewById(R.id.commentLL);
        }
    }
}
