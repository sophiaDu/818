package com.teboz.egg.utils;

import android.content.Context;

/**
 * 屏幕工具类
 * Created by meijian on 2016/5/9.
 */
public class DisplayUtils {

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
