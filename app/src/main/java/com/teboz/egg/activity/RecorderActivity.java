package com.teboz.egg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.utils.Cmd;

/**
 * Created by Administrator on 2016/6/7.
 */
public class RecorderActivity extends Activity implements View.OnClickListener {


    private ImageView topleft_ib;
    private TextView topleft_tv, topcenter_tv, countdown_tv;
    private ImageView countdown_iv, starrecord_bg_iv, starrecord_iv,point1_iv,point2_iv,point3_iv; /*rightpoint1_iv, rightpoint2_iv, rightpoint3_iv,
            leftpoint1_iv, leftpoint2_iv, leftpoint3_iv*/;
    private RelativeLayout rightcdprompt_rl, leftcdprompt_rl;
    private String type;
    private FrameLayout countdownFrame, starrecordFrame,timerFrame;

    private MyApplication myApplication;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        type = getIntent().getStringExtra("type");
        myApplication = (MyApplication) getApplication();
        if (type.equals("touch")) {
            myApplication.iscompletedTouch = true;
        } else if (type.equals("shake")) {
            myApplication.iscompletedShake = true;
        } else if (type.equals("upside")) {
            myApplication.iscompletedUpside = true;
        }
        initView();

    }


    /**
     * 初始化视图
     */
    private void initView() {
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topleft_ib = (ImageView) findViewById(R.id.id_iv_back);
//        topleft_ib.setVisibility(View.VISIBLE);
//        topleft_tv.setVisibility(View.VISIBLE);
//        topleft_tv.setText("新建角色");
//        topleft_ib.setOnClickListener(this);
//        topleft_tv.setOnClickListener(this);
        countdown_tv = (TextView) findViewById(R.id.countdown_tv);

        timerFrame = (FrameLayout) findViewById(R.id.countdowntimer_layout);
        countdownFrame = (FrameLayout) findViewById(R.id.countdown_frame);
        starrecordFrame = (FrameLayout) findViewById(R.id.starrecord_frame);
        countdown_iv = (ImageView) findViewById(R.id.countdownprompt_iv);
        starrecord_bg_iv = (ImageView) findViewById(R.id.starrecord_bg_iv);
        starrecord_iv = (ImageView) findViewById(R.id.starrecord_iv);
        rightcdprompt_rl = (RelativeLayout) findViewById(R.id.cd_right_layout);
        leftcdprompt_rl = (RelativeLayout) findViewById(R.id.cd_left_layout);
        point1_iv = (ImageView) findViewById(R.id.point1);
        point2_iv = (ImageView) findViewById(R.id.point2);
        point3_iv = (ImageView) findViewById(R.id.point3);
       /* rightpoint1_iv = (ImageView) findViewById(R.id.right_point1);
        rightpoint2_iv = (ImageView) findViewById(R.id.right_point2);
        rightpoint3_iv = (ImageView) findViewById(R.id.right_point3);
        leftpoint1_iv = (ImageView) findViewById(R.id.left_point1);
        leftpoint2_iv = (ImageView) findViewById(R.id.left_point2);
        leftpoint3_iv = (ImageView) findViewById(R.id.left_point3);
*/

        //因为录制三种声音的倒数界面公用，所以根据声音不同更换界面图片
        if (type.equals("touch")) {
            rightcdprompt_rl.setVisibility(View.VISIBLE);
            leftcdprompt_rl.setVisibility(View.INVISIBLE);
            topcenter_tv.setText("录摸头声音");
            countdown_iv.setImageResource(R.mipmap.add_touch);
//            rightpoint1_iv.setImageResource(R.mipmap.add_three_purple);
//            rightpoint2_iv.setImageResource(R.mipmap.add_three_purple);
//            rightpoint3_iv.setImageResource(R.mipmap.add_three_purple);
            starrecord_bg_iv.setImageResource(R.mipmap.add_touch_back);
            starrecord_iv.setImageResource(R.mipmap.add_action_purple_pic);
        } else if (type.equals("shake")) {
            rightcdprompt_rl.setVisibility(View.INVISIBLE);
            leftcdprompt_rl.setVisibility(View.VISIBLE);
            topcenter_tv.setText("录摇晃声音");
            countdown_iv.setImageResource(R.mipmap.add_shake);
            starrecord_bg_iv.setImageResource(R.mipmap.add_shake_back);
            starrecord_iv.setImageResource(R.mipmap.add_action_green_pic);
        } else if (type.equals("upside")) {
            rightcdprompt_rl.setVisibility(View.VISIBLE);
            leftcdprompt_rl.setVisibility(View.INVISIBLE);
            topcenter_tv.setText("录倒置声音");
            countdown_iv.setImageResource(R.mipmap.add_upside);
//            rightpoint1_iv.setImageResource(R.mipmap.add_three_blue);
//            rightpoint2_iv.setImageResource(R.mipmap.add_three_blue);
//            rightpoint3_iv.setImageResource(R.mipmap.add_three_blue);
            starrecord_bg_iv.setImageResource(R.mipmap.more_diy_back);
            starrecord_iv.setImageResource(R.mipmap.add_action_blue_pic);
        }

    }


    private int countdownnumber = 0;

    /**
     * 圆点倒数计时器
     */
    private CountDownTimer timer = new CountDownTimer(3500, 1000) {

        private int count = 0;

        @Override
        public void onTick(long millisUntilFinished) {
//            if (count == 0) {
//                count++;
//                return;
//            }
            if (point3_iv.getVisibility() == View.VISIBLE) {
                point3_iv.setVisibility(View.INVISIBLE);
            } else if (point2_iv.getVisibility() == View.VISIBLE) {
                point2_iv.setVisibility(View.INVISIBLE);
            } else if (point1_iv.getVisibility() == View.VISIBLE) {
                point1_iv.setVisibility(View.INVISIBLE);
            }
            /*if (type.equals("shake")) {
                if (leftpoint1_iv.getVisibility() == View.VISIBLE) {
                    leftpoint1_iv.setVisibility(View.INVISIBLE);
                } else if (leftpoint2_iv.getVisibility() == View.VISIBLE) {
                    leftpoint2_iv.setVisibility(View.INVISIBLE);
                } else if (leftpoint3_iv.getVisibility() == View.VISIBLE) {
                    leftpoint3_iv.setVisibility(View.INVISIBLE);
                }
            } else {
                if (rightpoint1_iv.getVisibility() == View.VISIBLE) {
                    rightpoint1_iv.setVisibility(View.INVISIBLE);
                } else if (rightpoint2_iv.getVisibility() == View.VISIBLE) {
                    rightpoint2_iv.setVisibility(View.INVISIBLE);
                } else if (rightpoint3_iv.getVisibility() == View.VISIBLE) {
                    rightpoint3_iv.setVisibility(View.INVISIBLE);
                }
            }*/
            count++;
        }

        @Override
        public void onFinish() {//圆点全部消失后开始录制倒数计时器
            point3_iv.setVisibility(View.INVISIBLE);
            point2_iv.setVisibility(View.INVISIBLE);
            point1_iv.setVisibility(View.INVISIBLE);

         /*   countdownFrame.setVisibility(View.INVISIBLE);
            starrecordFrame.setVisibility(View.VISIBLE);*/
            recorderTimer.start();
            timerFrame.setVisibility(View.VISIBLE);
            countdownnumber = 8;

        }
    };

    /**
     * 监听手机返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 录制倒数5秒，5秒后默认录制完，界面返回至上一个界面
     */
    private CountDownTimer recorderTimer = new CountDownTimer(8500, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            countdown_tv.setText(countdownnumber + "");
            countdownnumber--;
        }

        @Override
        public void onFinish() {
           // countdown_tv.setText(countdownnumber + "");
//            countdownnumber--;
            //录制完成后记录状态

            finish();

        }
    };


    @Override
    protected void onResume() {
        super.onResume();


        if(isFirst){
            isFirst = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(100);
                    if(type.equals("touch")){
                        myApplication.sendCommand(Cmd.RECORDER_TOUCHSOUND);
                    }else if(type.equals("shake")){
                        myApplication.sendCommand(Cmd.RECORDER_SHAKESOUND);
                    }else if(type.equals("upside")){
                        myApplication.sendCommand(Cmd.RECORDER_UPSIDESOUND);
                    }
                   SystemClock.sleep(600);
                    timer.start();
                }
            }).start();
        }
//        timer.start();//开机圆点计时器

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_iv_back:
            case R.id.id_tv_left:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //界面销毁时关闭未完成倒数的计时器
        if (timer != null)
            timer.cancel();
        if (recorderTimer != null)
            recorderTimer.cancel();
    }
}
