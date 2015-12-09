package com.android.lvxin;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @ClassName: ImageScalableHandler
 * @Description: TODO
 * @Author: lvxin
 * @Date: 12/8/15 14:44
 */
public class ImageScalableHandler {
    // 图片是否在缩放状态
    private boolean isZooming;
    // 图片开始缩放时的触摸位置
    private float oriPosition;

    // 图片目前的缩放比例
    private double currentRatio = 1.0d;
    // 图片接下来要缩放的比例
    private double nextRatio;

    // 判断有没有加载过背景图片
    private boolean isInit;

    // 背景图片初始的高度
    private int avatarOriHeight;

    private ImageView backgroundImageView;

    public ImageScalableHandler(int imgHeight) {
        isInit = true;
        avatarOriHeight = imgHeight;
    }

    public void setHeaderImage(ImageView imageView) {
        this.backgroundImageView = imageView;
    }

    public boolean isZooming() {
        return isZooming;
    }

    public void setZooming(boolean zooming) {
        isZooming = zooming;
    }

    public float getOriPosition() {
        return oriPosition;
    }

    public void setOriPosition(float oriPosition) {
        this.oriPosition = oriPosition;
    }

    public int getAvatarOriHeight() {
        return avatarOriHeight;
    }

    public void setAvatarOriHeight(int avatarOriHeight) {
        this.avatarOriHeight = avatarOriHeight;
    }

    public void doWhenTop(boolean isInTop, float rawY) {
        if (!isZooming() && isInTop) {
            setOriPosition(rawY);
            setZooming(true);
        }
    }

    public boolean isOutOfView(View view, MotionEvent ev) {
        // 如果移动过程中触摸点不在recycler view上，比如移动到了屏幕外侧或者系统的虚拟按键等
        // 这时需要将缩放状态重置
        if (!isInView(view, ev.getRawX(), ev.getRawY())) {
            resetZoom();
            setZooming(false);
            return true;
        }

        return false;
    }

    // 用来判断触摸点的坐标(rawX, rawY)是否在view上
    public boolean isInView(View view, float rawX, float rawY) {
        int scrcoords[] = new int[2];
        view.getLocationOnScreen(scrcoords);
        float x = rawX + view.getLeft() - scrcoords[0];
        float y = rawY + view.getTop() - scrcoords[1];
        if (x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom())
            return true;
        return false;
    }

    public boolean handleMove(MotionEvent event) {
        // 计算触摸点与初始点的距离，根据这个距离计算图片缩放的大小
        float diff = event.getRawY() - oriPosition;
        if (diff > 0) {
            // diff大于0表示正在往下拉，此时图片处于缩放状态，
            // 需要拦截触摸事件并缩放图片
//            adapter.setZoomDiff(diff);
            setZoomDiff(diff);
            return true;
        }
        return false;
    }


    // 设置图片的缩放比例
    // zoomDiff是现在的触摸点和初始的触摸点之间的距离
    // 图片最大可以缩放的比例是2倍
    // 图片的缩放比例是：
    // nextRatio = 1.0 + zoomDiff / avatarOriHeight / (1.0 + zoomDiff / avatarOriHeight)
    // 这个计算方法可以实现移动得越远，图片缩放的速度越慢
    public void setZoomDiff(double zoomDiff) {
        double ratio = 1.0 + zoomDiff / avatarOriHeight;
        this.nextRatio = 1.0 + zoomDiff / avatarOriHeight / ratio;
        this.nextRatio = Math.min(this.nextRatio, 2.0);

        // currentRatio不等于zoomRatio表示需要修改图片的高度
        if (Math.abs(currentRatio - nextRatio) > 1e-6) {
            zoomImage(currentRatio, nextRatio, 10);
            currentRatio = nextRatio;
        }
    }

    // 重置缩放状态。图片在200ms内从现在的缩放比例恢复到初始的比例
    // 如果图片处于缩放状态，需要拦截触摸事件的ACTION_UP
    // 返回值是true表示需要拦截触摸事件，false表示不需要拦截
    public boolean resetZoom() {
        if (this.currentRatio != 1.0) {
            zoomImage(currentRatio, 1.0, 200);
            currentRatio = 1.0;
            return true;
        }
        return false;
    }


    // 缩放图片，将图片的缩放比例从startRatio渐变到endRatio
    private void zoomImage(final double startRatio,
                           final double endRatio,
                           long time) {
//        final ImageView image_background = (ImageView)
//                headerView.findViewById(R.id.image_background);
//        final View view_placeholder =
//                headerView.findViewById(R.id.view_placeholder);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    private IntEvaluator mEvaluator = new IntEvaluator();

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        //获得当前动画的进度值，整型，1-100之间
                        int currentValue = (Integer) animator.getAnimatedValue();

                        //计算当前进度占整个动画过程的比例，浮点型，0-1之间
                        float fraction = currentValue / 100f;

                        int imageHeight = mEvaluator.evaluate(
                                fraction,
                                (int) (avatarOriHeight * startRatio),
                                (int) (avatarOriHeight * endRatio));
//                        image_background.getLayoutParams().height = imageHeight;
//                        image_background.requestLayout();
                        backgroundImageView.getLayoutParams().height = imageHeight;
                        backgroundImageView.requestLayout();

                        int viewHeight = mEvaluator.evaluate(
                                fraction,
                                (int) (avatarOriHeight * (startRatio - 1.0)),
                                (int) (avatarOriHeight * (endRatio - 1.0)));

//                        view_placeholder.getLayoutParams().height = viewHeight;
//                        view_placeholder.requestLayout();
                    }
                });
        valueAnimator.setDuration(time).start();
    }

    // 缩放图片，将图片的缩放比例从startRatio渐变到endRatio
    private void zoomImage(View headerView, final double startRatio,
                           final double endRatio,
                           long time) {
        final ImageView image_background = (ImageView)
                headerView.findViewById(R.id.image_background);
        final View view_placeholder =
                headerView.findViewById(R.id.view_placeholder);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    private IntEvaluator mEvaluator = new IntEvaluator();

                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        //获得当前动画的进度值，整型，1-100之间
                        int currentValue = (Integer) animator.getAnimatedValue();

                        //计算当前进度占整个动画过程的比例，浮点型，0-1之间
                        float fraction = currentValue / 100f;

                        int imageHeight = mEvaluator.evaluate(
                                fraction,
                                (int) (avatarOriHeight * startRatio),
                                (int) (avatarOriHeight * endRatio));
                        image_background.getLayoutParams().height = imageHeight;
                        image_background.requestLayout();
                        backgroundImageView.getLayoutParams().height = imageHeight;
                        backgroundImageView.requestLayout();

                        int viewHeight = mEvaluator.evaluate(
                                fraction,
                                (int) (avatarOriHeight * (startRatio - 1.0)),
                                (int) (avatarOriHeight * (endRatio - 1.0)));

                        view_placeholder.getLayoutParams().height = viewHeight;
                        view_placeholder.requestLayout();
                    }
                });
        valueAnimator.setDuration(time).start();
    }

    /*
    private void updateImageBackground(final LinearLayout layout_info,
                                       final View view_placeholder, final ImageView image_background) {
        isInit = true;
        // image_background的宽度和高度要和layout_info保持一致，
        // 所以需要在获取到layout_info高度后设置image_background的高度
        layout_info.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // view_placeholder的高度大于0时表示正处于缩放状态，
                        // 不能使用这个时候layout_info的高度
                        if (view_placeholder.getHeight() > 0) {
                            return;
                        }
                        int layoutWidth = layout_info.getWidth();
                        int layoutHeight = layout_info.getHeight();

                        // 记录背景图片初始的高度
                        avatarOriHeight = layoutHeight;

                        // 设置image_background的宽度和高度
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) image_background.getLayoutParams();
                        params.width = layoutWidth;
                        params.height = layoutHeight;
                        image_background.setLayoutParams(params);

                        // 生成宽高比和image_background一样的图片
                        Bitmap cropAvatarBitmap = getCropAvatarBitmap(
                                avatarBitmap,
                                layoutWidth * 1.0 / layoutHeight);
                        image_background.setImageBitmap(cropAvatarBitmap);

                        avatarBitmap = null;

                        if (android.os.Build.VERSION.SDK_INT >=
                                android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            layout_info.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            layout_info.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }
     */


    /**
     * dp转px
     */
    public int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

}
