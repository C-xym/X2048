package com.example.x.x2048;

import android.content.Context;

public class Utils {
    public static int px2dip(Context context,float pxValue) {
        final float scale =context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    };

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
