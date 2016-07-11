package com.teboz.egg.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.teboz.egg.R;
import com.teboz.egg.utils.Cmd;
import com.teboz.egg.utils.Utils;

/**
 * Created by Administrator on 2016/6/8.
 */
public class CardHorizontalGridView extends HorizontalGridView {

    private Context mContext;

    public CardHorizontalGridView(Context context) {
        super(context);
        mContext = context;
    }

    public CardHorizontalGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public CardHorizontalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    private float x1, y1;
    private float x2, y2;
    private long time1, time2;


    private boolean isDown = false;
    private boolean isShowDelete = false;

    private View chileview = null, moveView;
    private RelativeLayout deletelayout = null;
    private RelativeLayout movedeletelayout = null;
    private FrameLayout content = null;
    private FrameLayout movecontent = null;
    private int lastmove = 0;


    private boolean ismove = false;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            lastmove = 0;
            x1 = ev.getX();
            y1 = ev.getY();
            time1 = System.currentTimeMillis();
            isDown = true;
            return super.dispatchTouchEvent(ev);
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (getSelectedPosition() < Cmd.DEFAULT_ROLE_COUNT) {//默认角色岁手指上下滑动，up时归原位
                if (getSelectedPosition() == 0) {
                    moveView = getChildAt(getSelectedSubPosition());
                    movedeletelayout = (RelativeLayout) moveView.findViewById(R.id.delete_layout);
                    movecontent = (FrameLayout) moveView.findViewById(R.id.content_layout);
                } else {
                    moveView = getChildAt(getSelectedSubPosition() + 1);
                    movedeletelayout = (RelativeLayout) moveView.findViewById(R.id.delete_layout);
                    movecontent = (FrameLayout) moveView.findViewById(R.id.content_layout);
                }

                int width = getScreenWidth(mContext);
                if (x2 >= (width / 4) && x2 <= (width - (width / 4)) && (Math.abs(ev.getX() - x1) < Math.abs(ev.getY() - y1))) {//手指在居中的卡片范围内
                    if ((int) (ev.getY() - y1) > 10 && (Math.abs(ev.getX() - x1) < Math.abs(ev.getY() - y1))) {//向下滑动

                        ismove = true;

                        ValueAnimator animator = ValueAnimator.ofFloat(lastmove, (int) (ev.getY() - y1)).setDuration(100);
                        animator.start();
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float curVal = (float) animation.getAnimatedValue();
                                ViewHelper.setTranslationY(movecontent, curVal);//注意不要使用btn.setTranslationY
                            }
                        });
                        lastmove = (int) (ev.getY() - y1);
                        if (lastmove > 20)
                            callback.pulldown();

                    } else if ((int) (ev.getY() - y1) < -10 && (Math.abs(ev.getX() - x1) < Math.abs(ev.getY() - y1))) {//向上滑动
                        ismove = true;

                        ValueAnimator animator = ValueAnimator.ofFloat(lastmove, (int) (ev.getY() - y1)).setDuration(100);
                        animator.start();
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                float curVal = (float) animation.getAnimatedValue();
                                ViewHelper.setTranslationY(movecontent, curVal);//注意不要使用btn.setTranslationY
                            }
                        });
                        lastmove = (int) (ev.getY() - y1);
                        if (lastmove < -20)
                            callback.pullup();
                    }
                }
            }

            if(!ismove && !isShowDelete && Math.abs(ev.getX() - x1) >= Math.abs(ev.getY() - y1) &&  Math.abs(ev.getX() - x1)>30){
                return super.dispatchTouchEvent(ev);
            }

        } else if (action == MotionEvent.ACTION_UP) {
            if (getSelectedPosition() < Cmd.DEFAULT_ROLE_COUNT && moveView != null) {//默认角色卡片归原位
                if (getSelectedPosition() < Cmd.DEFAULT_ROLE_COUNT) {
//                    ismove = false;
                    ValueAnimator animator = ValueAnimator.ofFloat(0, 0).setDuration(200);
                    animator.start();
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            float curVal = (float) animation.getAnimatedValue();
                            ViewHelper.setTranslationY(movecontent, curVal);//注意不要使用btn.setTranslationY
                        }
                    });
                    callback.reset();
                    if(ismove){
                        Utils.Toastbottom(mContext,"内置角色不能删除");
//                        Toast.makeText(mContext, "内置角色不能删除", Toast.LENGTH_SHORT).show();
                        ismove = false;
                    }
                }
            }

            isDown = false;
            x2 = ev.getX();
            y2 = ev.getY();
            time2 = System.currentTimeMillis();
            float offsetX = x2 - x1;
            float offsetY = y2 - y1;
            long offtime = time2 - time1;
            int speed = Math.round(Math.abs(offsetX) / offtime);
             Log.e("selectrole", "speed: " + (Math.abs(offsetX) / offtime));
            if (Math.abs(offsetX) >= Math.abs(offsetY) && !isShowDelete) {//横向
               //根据手指不同滑动的速度滑动
                if (offsetX >= 25) {
                    if (offsetX > 60 && speed > 1) {
                        int count = 1;
                        if (speed < 2) {
                            count = 2;
                        } else if (speed < 3) {
                            count =4;
                        } else {
                            count = 5;
                        }
                        new CountDownTimer(count * 50, 50) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                scrollLeft();
                            }

                            @Override
                            public void onFinish() {

                            }
                        }.start();
                    } else {
                        scrollLeft();
                    }


                } else if (offsetX <= -25) {
                    if (offsetX < -60 && speed > 1) {
                        int count = 1;
                        if (speed < 2) {
                            count = 2;
                        } else if (speed < 3) {
                            count = 4;
                        } else {
                            count = 5;
                        }
                        new CountDownTimer((count * 50), 50) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                scrollRight();
                            }

                            @Override
                            public void onFinish() {

                            }
                        }.start();
                    } else {
                        scrollRight();
                    }

                } else {
                    return super.dispatchTouchEvent(ev);
                }
            } else if (Math.abs(offsetX) < Math.abs(offsetY) && getSelectedPosition() >= Cmd.DEFAULT_ROLE_COUNT) {//竖向

                //获取新建角色的卡片的childview
                if (getSelectedPosition() == 0) {
                    chileview = getChildAt(getSelectedSubPosition());
                    deletelayout = (RelativeLayout) chileview.findViewById(R.id.delete_layout);
                    content = (FrameLayout) chileview.findViewById(R.id.content_layout);
                } else {
                    chileview = getChildAt(getSelectedSubPosition() + 1);
                    deletelayout = (RelativeLayout) chileview.findViewById(R.id.delete_layout);
                    content = (FrameLayout) chileview.findViewById(R.id.content_layout);
                }

                if (deletelayout != null) {
                    if (offsetY > 10) {//手指向下滑动
                        if (deletelayout.getVisibility() == View.VISIBLE) {//当前删除提示页面是显示的
                            if (isUp) {//卡片是上移状态，则向下移回原位
                                deletelayout.setVisibility(View.INVISIBLE);
                                isShowDelete = false;
                                ValueAnimator animator = ValueAnimator.ofFloat(0, 0).setDuration(100);
                                animator.start();
                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float curVal = (float) animation.getAnimatedValue();
                                        ViewHelper.setTranslationY(content, curVal);//注意不要使用btn.setTranslationY
                                    }
                                });
                                callback.reset();
                            }
                        } else {//当前删除提示页面是不显示的，则卡片下移，显示删除提示页面
                            isUp = false;
                            deletelayout.setVisibility(View.VISIBLE);
                            isShowDelete = true;
                            ValueAnimator animator = ValueAnimator.ofFloat(0, content.getHeight()).setDuration(100);
                            animator.start();
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float curVal = (float) animation.getAnimatedValue();
                                    ViewHelper.setTranslationY(content, curVal);//注意不要使用btn.setTranslationY
                                }
                            });
                            callback.pulldown();
                        }

                    } else if (offsetY < -10) {//手势上滑
                        if (deletelayout.getVisibility() == View.VISIBLE) {//当前删除提示页面是显示的
                            if (!isUp) {//如果卡片为下移状态，卡片上移回原位，隐藏删除提示页面
                                deletelayout.setVisibility(View.INVISIBLE);
                                isShowDelete = false;
                                ValueAnimator animator = ValueAnimator.ofFloat(0, 0).setDuration(100);
                                animator.start();
                                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float curVal = (float) animation.getAnimatedValue();
                                        ViewHelper.setTranslationY(content, curVal);//注意不要使用btn.setTranslationY
                                    }
                                });
                                callback.reset();
                            }
                        } else {////当前删除提示页面是非显示的，卡片上移，显示删除提示页面
                            isUp = true;
                            deletelayout.setVisibility(View.VISIBLE);
                            isShowDelete = true;
                            ValueAnimator animator = ValueAnimator.ofFloat(0, -content.getHeight()).setDuration(100);
                            animator.start();
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float curVal = (float) animation.getAnimatedValue();
                                    ViewHelper.setTranslationY(content, curVal);//注意不要使用btn.setTranslationY
                                }
                            });
                            callback.pullup();
                        }
                    }
                }
                return true;
            } else {
                return super.dispatchTouchEvent(ev);
            }
        }
        return true;
    }


    private boolean isUp = false;

    public void dissDeletelayout() {
        if (chileview != null) {
            deletelayout = (RelativeLayout) chileview.findViewById(R.id.delete_layout);
            content = (FrameLayout) chileview.findViewById(R.id.content_layout);
        }
        if (deletelayout != null) {
            if (deletelayout.getVisibility() == View.VISIBLE) {
                deletelayout.setVisibility(View.GONE);
                isShowDelete = false;
                ValueAnimator animator = ValueAnimator.ofFloat(0, 0).setDuration(300);
                animator.start();
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float curVal = (float) animation.getAnimatedValue();
                        ViewHelper.setTranslationY(content, curVal);//注意不要使用btn.setTranslationY
                    }
                });
                callback.reset();
            }

        }
    }

    public void setIsDeleteShow(boolean is) {
        isShowDelete = is;
    }
   public boolean isDeleteShow() {
         return  isShowDelete;
    }

    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public void scrollLeft() {
        if (isShowDelete)
            return;
        int current = getSelectedPosition();
        if (current == 0) {
            return;
        }
        current--;
        setSelectedPositionSmooth(current);
    }

    public void scrollRight() {
        if (isShowDelete)
            return;
        int current = getSelectedPosition();
        if (current >= getAdapter().getItemCount() - 1) {
            return;
        }
        current++;
        setSelectedPositionSmooth(current);
    }

    public GestureListener callback;

    public void setGesturelistener(GestureListener listener) {
        callback = listener;
    }

    /**
     * 手势接口回调，方便是有此控件的页面监听动作进行其他控件刷新
     */
    public interface GestureListener {
        void pullup();

        void pulldown();

        void reset();

    }
}
