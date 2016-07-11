package com.teboz.egg.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.utils.Cmd;

/**
 * Created by Administrator on 2016/6/8.
 */
public class SetPowerSoundActivity extends Activity implements View.OnClickListener {
    private ImageView topleft_iv;//顶栏左边的返回按钮
    private TextView topleft_tv, topcenter_tv;//顶栏左边的文字，中间的文字
    private RelativeLayout mysoundrlayout, defaultsoundrlayout;//我的开机声布局，默认开机声布局
    private ImageView mysoundchecked_iv, defaultsoundchecked_iv;//勾选图标
    private TextView ms_re_tv,//重录
    /*, ms_listen_tv, ds_listen_tv*///试听
                     mysound_tv;//我的开机声文字
    private ImageView point1_iv, point2_iv, point3_iv, point4_iv, point5_iv, point6_iv;//1,2，3为默认开机声下面的红绿灯，4,5,6是我的得开机声上面的红绿灯

    private ImageView mysound_back, //我的开机声背景条
                      mysound_pic, //我的开机声蛋的图
                      defaultsound_back,//默认开机声背景条
                      defaultsound_pic;//默认开机声蛋的图

    private MyApplication myApplication;
    private boolean haveDiyPowerSound = false;//标记是否录制过开机声

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpowersound);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myApplication = (MyApplication) getApplication();

        initView();
    }

    /**
     * 初始化界面视图
     */
    private void initView() {
        topleft_iv = (ImageView) findViewById(R.id.id_iv_back);
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topleft_tv.setText(R.string.more_text);
        topcenter_tv.setText(R.string.diy_my_sound_text);
        topleft_tv.setVisibility(View.VISIBLE);
        topleft_iv.setVisibility(View.VISIBLE);

        topleft_iv.setOnClickListener(this);
        topleft_tv.setOnClickListener(this);

        point1_iv = (ImageView) findViewById(R.id.point1);
        point2_iv = (ImageView) findViewById(R.id.point2);
        point3_iv = (ImageView) findViewById(R.id.point3);
        point4_iv = (ImageView) findViewById(R.id.point4);
        point5_iv = (ImageView) findViewById(R.id.point5);
        point6_iv = (ImageView) findViewById(R.id.point6);

        mysoundrlayout = (RelativeLayout) findViewById(R.id.mysound_rlayout);
        defaultsoundrlayout = (RelativeLayout) findViewById(R.id.defaultsound_rlayout);
        mysoundchecked_iv = (ImageView) findViewById(R.id.mysound_checked_iv);
        defaultsoundchecked_iv = (ImageView) findViewById(R.id.defaultsound_checked_iv);
        ms_re_tv = (TextView) findViewById(R.id.mysound_re_tv);
        //  ms_listen_tv = (TextView) findViewById(R.id.mysound_listen_tv);
        //  ds_listen_tv = (TextView) findViewById(R.id.defaultsound_listen_tv);
        mysound_tv = (TextView) findViewById(R.id.mysound_tv);
        mysoundrlayout.setOnClickListener(this);
        defaultsoundrlayout.setOnClickListener(this);
        ms_re_tv.setOnClickListener(this);

        defaultsound_back = (ImageView) findViewById(R.id.defaultsound_back);
        defaultsound_pic = (ImageView) findViewById(R.id.defaultsound_pic);
        mysound_back = (ImageView) findViewById(R.id.mysound_back);
        mysound_pic = (ImageView) findViewById(R.id.mysound_pic);
//        ms_listen_tv.setOnClickListener(this);
//        ds_listen_tv.setOnClickListener(this);

        register();//注册广播

    }


    /**
     * 如果原点倒计时没完成前点击了其他按钮发送命令，则将未完成的原点倒计时取消
     */
    private void dismissPoints() {
        point1_iv.setVisibility(View.INVISIBLE);
        point2_iv.setVisibility(View.INVISIBLE);
        point3_iv.setVisibility(View.INVISIBLE);
        point4_iv.setVisibility(View.INVISIBLE);
        point5_iv.setVisibility(View.INVISIBLE);
        point6_iv.setVisibility(View.INVISIBLE);

//        int choose = myApplication.getSpIntValue("currentpowersound");
        /*if(choose == 1)*/
        {
            mysound_back.setImageResource(R.mipmap.more_start_colour);
            mysound_pic.setImageResource(R.mipmap.more_start_colour_pic);
            mysoundrlayout.setEnabled(true);
        }/*else*/
        {
            defaultsound_back.setImageResource(R.mipmap.more_start_default_colour);
            defaultsound_pic.setImageResource(R.mipmap.more_start_default_colour_pic);
            defaultsoundrlayout.setEnabled(true);
        }
        updateUi();

        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * 根据标记的所点击的开机声类型，显示相对应的红绿灯
     *
     * @param cmd
     */
    private void pointsShow(final int[] cmd) {
        if (clicksoundtype == 0) {
            point1_iv.setVisibility(View.VISIBLE);
            point2_iv.setVisibility(View.VISIBLE);
            point3_iv.setVisibility(View.VISIBLE);
        } else if (clicksoundtype == 1) {
            point4_iv.setVisibility(View.VISIBLE);
            point5_iv.setVisibility(View.VISIBLE);
            point6_iv.setVisibility(View.VISIBLE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(100);
                myApplication.sendCommand(cmd);
                myApplication.isSelectReturn = false;
                SystemClock.sleep(600);
                Log.e("addrole", "timer start");
                timer.start();//开机圆点计时器


            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // myApplication.saveBooleanValue("have_diypowersound", true);
        updateUi();
    }

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
            //根据标记的所点击的开机声类型，刷新相对应的红绿灯
            if (clicksoundtype == 0) {//默认开机声
                if (point3_iv.getVisibility() == View.VISIBLE) {
                    point3_iv.setVisibility(View.INVISIBLE);
                } else if (point2_iv.getVisibility() == View.VISIBLE) {
                    point2_iv.setVisibility(View.INVISIBLE);
                } else if (point1_iv.getVisibility() == View.VISIBLE) {
                    point1_iv.setVisibility(View.INVISIBLE);
                }
            } else if (clicksoundtype == 1) {//我的开机声
                if (point6_iv.getVisibility() == View.VISIBLE) {
                    point6_iv.setVisibility(View.INVISIBLE);
                } else if (point5_iv.getVisibility() == View.VISIBLE) {
                    point5_iv.setVisibility(View.INVISIBLE);
                } else if (point4_iv.getVisibility() == View.VISIBLE) {
                    point4_iv.setVisibility(View.INVISIBLE);
                }
            }
            count++;


        }

        @Override
        public void onFinish() {//圆点全部消失后开始录制倒数计时器
            Log.e("addrole", "timer finish");
            int choose = myApplication.getSpIntValue("currentpowersound");
            //根据选项判断当前置灰的是哪个布局，然后将置灰的布局恢复成彩色，且置为有效可点击
            if (choose == 1) {
                mysound_back.setImageResource(R.mipmap.more_start_colour);
                mysound_pic.setImageResource(R.mipmap.more_start_colour_pic);
                mysoundrlayout.setEnabled(true);
                defaultsoundrlayout.setEnabled(true);
//                mysoundchecked_iv.setImageResource(R.mipmap.more_start_checked_red);
            } else {
                defaultsound_back.setImageResource(R.mipmap.more_start_default_colour);
                defaultsound_pic.setImageResource(R.mipmap.more_start_default_colour_pic);
                mysoundrlayout.setEnabled(true);
                defaultsoundrlayout.setEnabled(true);
//                defaultsoundchecked_iv.setImageResource(R.mipmap.more_start_checked_red);
            }

            updateUi();//刷新重录按钮和勾选图标

            //确保留个倒数的红绿灯时隐藏状态
            point6_iv.setVisibility(View.INVISIBLE);
            point5_iv.setVisibility(View.INVISIBLE);
            point4_iv.setVisibility(View.INVISIBLE);
            point3_iv.setVisibility(View.INVISIBLE);
            point2_iv.setVisibility(View.INVISIBLE);
            point1_iv.setVisibility(View.INVISIBLE);

        }
    };

    /**
     * 根据保存的状态数据，刷新重录按钮和勾选图标
     */
    private void updateUi() {
        haveDiyPowerSound = myApplication.getSpBooleanValue("have_diypowersound");
        //判断是否有录制过开机声
        if (!haveDiyPowerSound) {//如果没有录制过，隐藏重录按钮，文字显示为定制开机声
            ms_re_tv.setVisibility(View.INVISIBLE);
            mysound_tv.setText(R.string.diy_my_sound_text);
//            ms_listen_tv.setVisibility(View.INVISIBLE);
        } else {
            mysound_tv.setText(R.string.my_sound_text);//如果有录制过，显示重录按钮，文字显示为我的开机声
            ms_re_tv.setVisibility(View.VISIBLE);
            ms_re_tv.setTextColor(getResources().getColor(R.color.redtext_color));
            ms_re_tv.setBackgroundResource(R.mipmap.more_start_re_red);
//            ms_listen_tv.setVisibility(View.VISIBLE);
        }

        //判断当前开机声的选项刷新勾选图标,1 我的开机声，2 默认开机声
        int choose = myApplication.getSpIntValue("currentpowersound");
        if (choose == 1) {//当前选择的是我的开机声
            mysoundchecked_iv.setImageResource(R.mipmap.more_start_checked_red);
            defaultsoundchecked_iv.setImageResource(R.mipmap.more_start_checked_white);
        } else if (choose == 0) {//当前选择的是默认开机声
            mysoundchecked_iv.setImageResource(R.mipmap.more_start_checked_white);
            defaultsoundchecked_iv.setImageResource(R.mipmap.more_start_checked_red);

        }
    }



    /**
     * 自定义广播接收器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.recorder_powersound_success")) {
                updateUi();
            }
        }
    };

    /**
     * 注册广播
     */
    private void register() {
        IntentFilter filter = new IntentFilter("action.recorder_powersound_success");
        registerReceiver(mReceiver, filter);
    }


    private int clicksoundtype = -1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_iv_back:
            case R.id.id_tv_left:
                finish();
                break;
            //case R.id.mysound_layout:
            case R.id.mysound_rlayout://选择我的开机声
                // myApplication.saveBooleanValue("have_diypowersound",true);
                haveDiyPowerSound = myApplication.getSpBooleanValue("have_diypowersound");

                //判断当前是否已经录制过开机声，如果没有录制过直接跳入到录制开机声的界面
                if (!haveDiyPowerSound) {//没有录制过开机声
                    myApplication.isRocorderPowersound = true;
                    myApplication.saveBooleanValue("have_diypowersound", true);
                    myApplication.saveSpIntValue("currentpowersound", 1);
                    Intent iten = new Intent(SetPowerSoundActivity.this, RecorderPowerActivity.class);
                    startActivity(iten);
//                    myApplication.sendCommand(Cmd.DIY_POWERSOUND);//录制自定义开机声

                } else {//录制开机声
                    clicksoundtype = 1;
                    dismissPoints();//将为完成的倒计时红绿灯页面清掉，置灰的界面恢复原样

                    //我的开机声布局置灰点击无效，开始红绿灯倒计时动画
                    mysoundchecked_iv.setImageResource(R.mipmap.more_start_checked_gray);//勾选控件置灰
                    defaultsoundchecked_iv.setImageResource(R.mipmap.more_start_checked_white);//默认开机声勾选置为空白
                    mysound_back.setImageResource(R.mipmap.more_start_gray);//我的开机声背景置为置灰
                    mysound_pic.setImageResource(R.mipmap.more_start_gray_pic);//我的开机声图标置灰
                    ms_re_tv.setTextColor(Color.GRAY);//重录按钮背景置灰
                    ms_re_tv.setBackgroundResource(R.mipmap.more_start_re_gray);//重录按钮文字置灰

                    //开机声布局置为无效，让倒计时的过程中点击无效
                    mysoundrlayout.setEnabled(false);
                    defaultsoundrlayout.setEnabled(false);

                    pointsShow(Cmd.CHECKED_DIY_POWERSOUND);//发送选择我的开机声的码值，开始红绿灯倒计时

                    myApplication.saveSpIntValue("currentpowersound", 1);//保存当前的开机声选择改为1（我的开机声），0为默认开机声
                }

                break;
//            case R.id.defaultsound_layout:
            case R.id.defaultsound_rlayout://选择默认开机声
                clicksoundtype = 0;
                dismissPoints();//将为完成的倒计时红绿灯页面清掉，置灰的界面恢复原样
                /*if (defaultsoundchecked_iv.getVisibility() == View.INVISIBLE)*/

                //默认开机声布局置灰点击无效，开始红绿灯倒计时动画
                mysoundchecked_iv.setImageResource(R.mipmap.more_start_checked_white);//我的开机声勾选的图标变为空白
                defaultsoundchecked_iv.setImageResource(R.mipmap.more_start_checked_gray);//默认开机声勾选的图标变为灰色勾选
                defaultsound_back.setImageResource(R.mipmap.more_start_default_gray);//默认开机声布局背景置灰
                defaultsound_pic.setImageResource(R.mipmap.more_start_default_gray_pic);//默认开机声布局蛋图标置灰

                //开机声布局置为无效，让倒计时的过程中点击无效
                defaultsoundrlayout.setEnabled(false);
                mysoundrlayout.setEnabled(false);

                pointsShow(Cmd.CHECKED_DEFAULT_POWERSOUND);//发送选择默认开机声的码值，开始红绿灯倒计时

                myApplication.saveSpIntValue("currentpowersound", 0);//保存当前的开机声选择改为1（我的开机声），0为默认开机声
                break;
            case R.id.mysound_re_tv://重录我的开机声
                myApplication.isRocorderPowersound = true;
                myApplication.saveBooleanValue("have_diypowersound", true);
                myApplication.saveSpIntValue("currentpowersound", 1);
                Intent iten = new Intent(SetPowerSoundActivity.this, RecorderPowerActivity.class);
                startActivity(iten);
//                myApplication.sendCommand(Cmd.DIY_POWERSOUND);//录制自定义开机声
                break;
//            case R.id.mysound_listen_tv://我的开机声试听
//                //myApplication.saveBooleanValue("have_diypowersound",true);
//                myApplication.sendCommand(Cmd.DIY_POWERSOUND_LISTEN);//试听自定义开机声
//               /* haveDiyPowerSound = myApplication.getSpBooleanValue("have_diy_powersound");
//                if(haveDiyPowerSound){
//
//                }*/
//                break;
//            case R.id.defaultsound_listen_tv://默认开机声试听
////                int[] cmd = {70,0};
////                myApplication.sendCommand(cmd);//试听默认开机声
//                myApplication.sendCommand(Cmd.DEFAULT_POWERSOUND_LISTEN);//试听默认开机声
//                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }
}
