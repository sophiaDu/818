package com.teboz.egg.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.utils.Cmd;
import com.teboz.egg.utils.Utils;

/**
 * Created by Administrator on 2016/6/7.
 */
public class AddRoleActivity extends Activity implements View.OnClickListener {

    private TextView topleft_tv, topcenter_tv, topright_tv;//顶栏左边、中间、右边文字
    private FrameLayout touchhead_frame, shake_frame, upside_frame;//录制摸头声音、录制摇晃声音、录制倒置声音的布局
    private MyApplication myApplication;
    private long lastClick = -1;
    private ImageView point1_iv, point2_iv, point3_iv;
    private ImageView touchback, shakeback, upsideback;
    private ImageView touchpic, shakepic, upsidepic;
    private ImageView touchpass, shakepass, upsidepass;
    private boolean isFirst = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addrole);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myApplication = (MyApplication) getApplication();
        myApplication.iscompletedShake = false;
        myApplication.iscompletedTouch = false;
        myApplication.iscompletedUpside = false;

        initView();

    }

    /**
     * 初始化视图
     */
    private void initView() {
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topright_tv = (TextView) findViewById(R.id.id_tv_right);
        topleft_tv.setPadding(Utils.dip2px(this, 20), 0, 0, 0);
        topleft_tv.setText(R.string.addrole_cancle);
        topcenter_tv.setText(R.string.director_text);
        topright_tv.setText(R.string.addrole_continue);
        topright_tv.setTextColor(getResources().getColor(R.color.redtext_color));
        topleft_tv.setVisibility(View.VISIBLE);
        topleft_tv.setOnClickListener(this);
        topright_tv.setOnClickListener(this);
        topright_tv.setVisibility(View.VISIBLE);

        touchpass = (ImageView) findViewById(R.id.touch_pass);
        shakepass = (ImageView) findViewById(R.id.shake_pass);
        upsidepass = (ImageView) findViewById(R.id.upside_pass);

        point1_iv = (ImageView) findViewById(R.id.point1);
        point2_iv = (ImageView) findViewById(R.id.point2);
        point3_iv = (ImageView) findViewById(R.id.point3);

        shakeback = (ImageView) findViewById(R.id.shake_back);
        touchback = (ImageView) findViewById(R.id.touch_back);
        upsideback = (ImageView) findViewById(R.id.upside_back);
        shakepic = (ImageView) findViewById(R.id.shake_iv);
        touchpic = (ImageView) findViewById(R.id.touch_iv);
        upsidepic = (ImageView) findViewById(R.id.upside_iv);

        touchhead_frame = (FrameLayout) findViewById(R.id.touch_layout);
        shake_frame = (FrameLayout) findViewById(R.id.shake_layout);
        upside_frame = (FrameLayout) findViewById(R.id.upside_layout);
        touchhead_frame.setOnClickListener(this);
        shake_frame.setOnClickListener(this);
        upside_frame.setOnClickListener(this);

        shakeback.setImageResource(R.mipmap.add_gray);
        touchback.setImageResource(R.mipmap.add_gray);
        upsideback.setImageResource(R.mipmap.add_gray);
        shakepic.setImageResource(R.mipmap.add_shake_pic_gray);
        touchpic.setImageResource(R.mipmap.add_touch_pic_gray);
        upsidepic.setImageResource(R.mipmap.add_upside_pic_gray);
        touchhead_frame.setEnabled(false);
        shake_frame.setEnabled(false);
        upside_frame.setEnabled(false);

    }


    private int countdownnumber = 0;

    /**
     * 圆点倒数计时器
     */
    private CountDownTimer timer = new CountDownTimer(4000, 1000) {

        private int count = 0;

        @Override
        public void onTick(long millisUntilFinished) {
           /* if (count == 0) {
                count++;
                return;
            }*/
            if (point3_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole", "one point dismiss");
                point3_iv.setVisibility(View.INVISIBLE);
            } else if (point2_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole", "two point dismiss");
                point2_iv.setVisibility(View.INVISIBLE);
            } else if (point1_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole", "three point dismiss");
                point1_iv.setVisibility(View.INVISIBLE);
            }
            count++;

            if (count == 3) {

                point3_iv.setVisibility(View.INVISIBLE);
                point2_iv.setVisibility(View.INVISIBLE);
                point1_iv.setVisibility(View.INVISIBLE);

                shakeback.setImageResource(R.mipmap.add_shake_back);
                touchback.setImageResource(R.mipmap.add_touch_back);
                upsideback.setImageResource(R.mipmap.more_diy_back);
                shakepic.setImageResource(R.mipmap.add_shake_pic);
                touchpic.setImageResource(R.mipmap.add_touch_pic);
                upsidepic.setImageResource(R.mipmap.add_upside_pic);

                touchhead_frame.setEnabled(true);
                shake_frame.setEnabled(true);
                upside_frame.setEnabled(true);
            }
        }

        @Override
        public void onFinish() {//圆点全部消失后开始录制倒数计时器
            Log.e("addrole", "timer finish");
            //以免超声导致画面卡住没更新过来，所以在定时器结束时再重新更新次
            point3_iv.setVisibility(View.INVISIBLE);
            point2_iv.setVisibility(View.INVISIBLE);
            point1_iv.setVisibility(View.INVISIBLE);

            shakeback.setImageResource(R.mipmap.add_shake_back);
            touchback.setImageResource(R.mipmap.add_touch_back);
            upsideback.setImageResource(R.mipmap.more_diy_back);
            shakepic.setImageResource(R.mipmap.add_shake_pic);
            touchpic.setImageResource(R.mipmap.add_touch_pic);
            upsidepic.setImageResource(R.mipmap.add_upside_pic);

            touchhead_frame.setEnabled(true);
            shake_frame.setEnabled(true);
            upside_frame.setEnabled(true);

        }
    };


    private void sendAddCmd() {
        //判断十个新建角色名额哪个是空置的
        if (myApplication.isDirectorIn) {
            int num = 0;
            for (int i = 0; i < myApplication.roleShareprefrecekeys.length; i++) {
                Log.e("directorf", "[" + i + "]: " + myApplication.roleShareprefrecekeys[i]);
                boolean state = myApplication.getSpBooleanValue(myApplication.roleShareprefrecekeys[i]);
                if (!state) {
                    num = i;
                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[i], true);
                    break;
                }
            }

            //发送空置角色的码值
            Log.e("savename", "num: " + num);
            int[] cmd = {1, 0};
            cmd[0] = Cmd.ADD_ROLE[0];
            cmd[1] = Cmd.ADD_ROLE[1] + num;
            Log.e("savename", "cmd: " + cmd[1]);
            myApplication.sendCommand(cmd);
            myApplication.saveSpIntValue("newrole", num);
            myApplication.isDirectorIn = false;
        }
    }

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
            //发送放弃新建角色码值
            myApplication.sendCommand(Cmd.DELETE_ROLE);
            int num = myApplication.getSpIntValue("newrole");
            myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[num], false);

            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //判断三种声音是否录制，刷新继续按钮
        if (myApplication.iscompletedShake && myApplication.iscompletedTouch && myApplication.iscompletedUpside) {
            topright_tv.setTextColor(getResources().getColor(R.color.redtext_color));
            topright_tv.setEnabled(true);
        } else {
            topright_tv.setTextColor(Color.GRAY);
            topright_tv.setEnabled(false);
        }

        //如果是第一次显示此页面，则发送新建角色的码值，开始倒计时
        if (isFirst) {
            isFirst = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(100);
                    sendAddCmd();
                    SystemClock.sleep(600);
                    Log.e("addrole", "timer start");
                    timer.start();//开机圆点计时器


                }
            }).start();
            ;
        }

        //判断三种声音是否录制，刷新pass图标的显示
        if (myApplication.iscompletedUpside) {
            upsidepass.setVisibility(View.VISIBLE);
        }
        if (myApplication.iscompletedTouch) {
            touchpass.setVisibility(View.VISIBLE);
        }
        if (myApplication.iscompletedShake) {
            shakepass.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 点击监听
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.id_tv_left://点击返回，发送删除新添加角色码值
                myApplication.sendCommand(Cmd.DELETE_ROLE);
                int num = myApplication.getSpIntValue("newrole");
                myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[num], false);

                finish();
                break;
            case R.id.id_tv_right://点击完成，如果没有录制完则给出提示，且界面不跳转，否则跳转界面
                if (myApplication.iscompletedShake && myApplication.iscompletedTouch && myApplication.iscompletedUpside) {//判断是否三个声音都录制完毕
                    intent = new Intent(AddRoleActivity.this, SaveNameActivity.class);
                    startActivity(intent);

                    finish();
                } else {
                    Utils.Toast(this, "主人，您还未录完呢");
                }
                break;
            case R.id.touch_layout://点击录制摸头布局，跳转到录制提示页面，且发送录制摸头声音码值
                intent = new Intent(AddRoleActivity.this, RecorderActivity.class);
                intent.putExtra("type", "touch");
                startActivity(intent);

//               myApplication.sendCommand(Cmd.RECORDER_TOUCHSOUND);
                break;
            case R.id.shake_layout://点击录制摇晃布局，跳转到录制提示页面，且发送录制摇晃声音码值
                intent = new Intent(AddRoleActivity.this, RecorderActivity.class);
                intent.putExtra("type", "shake");
                startActivity(intent);

//                myApplication.sendCommand( Cmd.RECORDER_SHAKESOUND);
                break;
            case R.id.upside_layout://点击录制倒置布局，跳转到录制提示页面，且发送录制倒置声音码值
                intent = new Intent(AddRoleActivity.this, RecorderActivity.class);
                intent.putExtra("type", "upside");
                startActivity(intent);

//                myApplication.sendCommand( Cmd.RECORDER_UPSIDESOUND);
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果在没有录制完的情况下退出了这个页面，则将这个新建角色名额状态设置为false;
        if (!myApplication.iscompletedUpside || !myApplication.iscompletedShake || !myApplication.iscompletedTouch) {
            int num = myApplication.getSpIntValue("newrole");
            myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[num], false);
        }
    }
}
