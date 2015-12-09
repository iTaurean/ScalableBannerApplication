package com.android.lvxin;

import android.content.Context;
import android.util.TypedValue;

/**
 * @ClassName: Utils
 * @Description: TODO
 * @Author: lvxin
 * @Date: 12/9/15 11:09
 */
public class Utils {

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
}
