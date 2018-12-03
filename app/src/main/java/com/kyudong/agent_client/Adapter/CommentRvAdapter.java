package com.kyudong.agent_client.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyudong.agent_client.R;
import com.kyudong.agent_client.RvItem.CommentRvItem;

import java.util.ArrayList;

/**
 * Created by Kyudong on 2018. 1. 22..
 */

public class CommentRvAdapter extends RecyclerView.Adapter<CommentRvAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommentRvItem> commentRvItemArrayList;

    public CommentRvAdapter(Context context, ArrayList<CommentRvItem> commentRvItemArrayList) {
        this.context = context;
        this.commentRvItemArrayList = commentRvItemArrayList;
    }

    @Override
    public CommentRvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View CommentLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item_rv,
                parent, false);
        return new ViewHolder(CommentLayout);
    }

    @Override
    public void onBindViewHolder(CommentRvAdapter.ViewHolder holder, int position) {
        CommentRvItem item = commentRvItemArrayList.get(position);

        holder.commentContentTxv.setText(item.content);
        holder.commentTimeTxv.setText(item.time);
    }

    @Override
    public int getItemCount() {
        return commentRvItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView commentTimeTxv;
        TextView commentContentTxv;

        public ViewHolder(View itemView) {
            super(itemView);

            commentTimeTxv = itemView.findViewById(R.id.commentTimeTxv);
            commentContentTxv = itemView.findViewById(R.id.commentContextTxv);
        }
    }
}
