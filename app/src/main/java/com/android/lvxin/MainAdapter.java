package com.android.lvxin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MainAdapter
 * @Description: TODO
 * @Author: lvxin
 * @Date: 12/9/15 14:03
 */
public class MainAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<String> mData = new ArrayList<>();
    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public MainAdapter(Context context, List<String> data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.main_recycler_item, parent, false);
        return new ItemViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ItemViewHolder) holder).textView.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemViewHolder(final View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.main_item_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getLayoutPosition());
                }
            });
        }
    }
}
