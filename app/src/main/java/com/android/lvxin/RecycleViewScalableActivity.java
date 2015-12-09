package com.android.lvxin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

public class RecycleViewScalableActivity extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private List<String> mData = new ArrayList<>(50);
    private ImageScalableHandler imageScalableHandler;
    private int backgroundImageHeight;

    public static void start(Context context) {
        Intent intent = new Intent(context, RecycleViewScalableActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        initView();
    }

    private void initView() {
        backgroundImageHeight = Utils.dp2px(this, 200);
        imageScalableHandler = new ImageScalableHandler(backgroundImageHeight);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new RecyclerAdapter(this, imageScalableHandler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        buildData();
        mAdapter.update(mData);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 如果移动过程中触摸点不在recycler view上，比如移动到了屏幕外侧或者系统的虚拟按键等
        // 这时需要将缩放状态重置
        if (imageScalableHandler.isOutOfView(mRecyclerView, ev)) {
            return false;
        }

        boolean isInTop = 0 == mRecyclerView.getChildAt(0).getTop();
        imageScalableHandler.doWhenTop(isInTop, ev.getRawY());

        if (imageScalableHandler.isZooming()) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    // 计算触摸点与初始点的距离，根据这个距离计算图片缩放的大小
                    if (imageScalableHandler.handleMove(ev)) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    imageScalableHandler.setZooming(false);
                    return imageScalableHandler.resetZoom();
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void buildData() {
        for (int i = 0; i < 50; i++) {
            mData.add("text" + i);
        }
    }

}
