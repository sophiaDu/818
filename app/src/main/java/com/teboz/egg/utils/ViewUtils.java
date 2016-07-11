package com.teboz.egg.utils;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * View工具类包含动画处理
 * Created by meijian on 2016/5/11.
 */
public class ViewUtils {

    /**
     * 控件缩放动画（真实改变控件宽高）
     *
     * @param view     控件
     * @param duration 动画时间
     * @param wh       改变后的宽高
     */
    public static void animEnlarge(final View view, int duration, final float[] wh) {
        final int width = view.getWidth();
        final int height = view.getHeight();
        ValueAnimator anim_enlarge = new ValueAnimator();
        anim_enlarge.setDuration(duration);
        anim_enlarge.setObjectValues(new float[]{0, 0});
        anim_enlarge.setInterpolator(new LinearInterpolator());
        anim_enlarge.setEvaluator(new TypeEvaluator<float[]>() {
            // fraction = t / duration
            @Override
            public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
                float w = fraction * (wh[0] - width) + width;
                float h = fraction * (wh[1] - height) + height;
                float[] fs = {w, h};
                return fs;
            }
        });

        anim_enlarge.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] fs = (float[]) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = (int) fs[0];
                layoutParams.height = (int) fs[1];
                view.setLayoutParams(layoutParams);
            }
        });

        anim_enlarge.start();
    }

    /**
     * 控件缩放动画（非改变控件真实属性）
     *
     * @param v        控件
     * @param duration 持续时间
     * @param scale    动画结束时缩放比例
     */
    public static void animScale(View v, int duration, float scale) {
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat("scaleX", v.getScaleX(), scale);
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat("scaleY", v.getScaleY(), scale);
        ObjectAnimator.ofPropertyValuesHolder(v, x, y).setDuration(duration).start();
    }
}
