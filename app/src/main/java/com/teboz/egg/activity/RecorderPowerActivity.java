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
public class RecorderPowerActivity extends Activity implements View.OnClickListener{


    private ImageView topleft_ib;
    private TextView topleft_tv, topcenter_tv,countdown_tv;
    private ImageView countdown_iv, starrecord_iv, point1_iv, point2_iv, point3_iv;
    private RelativeLayout rightcdprompt_rl;
    private String type;
    private FrameLayout countdownFrame,starrecordFrame,timerFrame;

    private MyApplication myApplication;
    private boolean isFirst = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorderpower);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        type = getIntent().getStringExtra("type");
        myApplication = (MyApplication) getApplication();

        initView();

    }

    private void initView() {
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topleft_ib = (ImageView) findViewById(R.id.id_iv_back);
//        topleft_ib.setVisibility(View.VISIBLE);
//        topleft_tv.setVisibility(View.VISIBLE);
//        topleft_tv.setText("取消");
        topcenter_tv.setText(R.string.diy_my_sound_text);
//        topleft_ib.setOnClickListener(this);
//        topleft_tv.setOnClickListener(this);
        countdown_tv = (TextView) findViewById(R.id.countdown_tv);

        timerFrame  = (FrameLayout) findViewById(R.id.countdowntimer_layout);
        timerFrame.setVisibility(View.INVISIBLE);
        countdownFrame = (FrameLayout) findViewById(R.id.countdown_frame);
        starrecordFrame = (FrameLayout) findViewById(R.id.starrecord_frame);
        countdown_iv = (ImageView) findViewById(R.id.countdownprompt_iv);
        starrecord_iv = (ImageView) findViewById(R.id.starrecord_iv);
        rightcdprompt_rl = (RelativeLayout) findViewById(R.id.cd_right_layout);
        point1_iv = (ImageView) findViewById(R.id.point1);
        point2_iv = (ImageView) findViewById(R.id.point2);
        point3_iv = (ImageView) findViewById(R.id.point3);

    }


    private CountDownTimer timer = new CountDownTimer(3500, 1000) {

        private int count = 0;
        @Override
        public void onTick(long millisUntilFinished) {
           /* if(count == 0) {
                count++;
                return;
            }*/
          {
                if(point3_iv.getVisibility() == View.VISIBLE){
                    point3_iv.setVisibility(View.INVISIBLE);
                }else if(point2_iv.getVisibility() == View.VISIBLE){
                    point2_iv.setVisibility(View.INVISIBLE);
                }else if(point1_iv.getVisibility() == View.VISIBLE){
                    point1_iv.setVisibility(View.INVISIBLE);
                }
            }
            count++;
        }

        @Override
        public void onFinish() {
            point3_iv.setVisibility(View.INVISIBLE);
            point2_iv.setVisibility(View.INVISIBLE);
            point1_iv.setVisibility(View.INVISIBLE);

         /*   countdownFrame.setVisibility(View.INVISIBLE);
            starrecordFrame.setVisibility(View.VISIBLE);*/
            timerFrame.setVisibility(View.VISIBLE);
            countdownnum = 8;
            recorderTimer.start();
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

    private int countdownnum = 0;
    private CountDownTimer recorderTimer = new CountDownTimer(9000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

            countdown_tv.setText(countdownnum+"");
            countdownnum--;
        }

        @Override
        public void onFinish() {

          /*  Intent itent = new Intent(RecorderPowerActivity.this,ListenActivity.class);
            startActivity(itent);*/
            //myApplication.isRocorderPowersound = false;
            finish();

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //如果是第一次进入此页面，则发送录制自定义开机声码值，开始倒计时
        if(isFirst) {
            isFirst = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(100);
                    myApplication.sendCommand(Cmd.DIY_POWERSOUND);//录制自定义开机声
                    SystemClock.sleep(600);
                    timer.start();
                }
            }).start();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_iv_back:
            case R.id.id_tv_left:
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null)
            timer.cancel();
        if(recorderTimer != null)
            recorderTimer.cancel();
    }
}
