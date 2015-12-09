package com.android.lvxin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: RecyclerAdapter
 * @Description: TODO
 * @Author: lvxin
 * @Date: 12/8/15 15:33
 */
public class RecyclerAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private List<String> mData = new ArrayList<>();

    private LayoutInflater mInflater;
    private ImageScalableHandler imageScalableHandler;

    public RecyclerAdapter(Context context, ImageScalableHandler imageScalableHandler) {
        mInflater = LayoutInflater.from(context);
        this.imageScalableHandler = imageScalableHandler;
    }

    public void update(List<String> data) {
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (VIEW_TYPE_HEADER == viewType) {
            View view = mInflater.inflate(R.layout.recycler_header, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            imageScalableHandler.setHeaderImage(holder.imageView);
            return holder;
        } else {
            View view = mInflater.inflate(R.layout.recycler_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).textView.setText(mData.get(position - 1));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (VIEW_TYPE_HEADER == position) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.header_bg);

        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }
    }
}
