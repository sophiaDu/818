package com.teboz.egg.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.teboz.egg.R;
import com.teboz.egg.adapter.MyFragmentAdapter;
import com.teboz.egg.utils.Iconst;

import generalplus.com.GPLib.ComAir5Wrapper;


public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private MyFragmentAdapter mVpAdapter;
    private LinearLayout directorLayout, moreLayout;//底栏两按钮的布局
    private TextView directorTv, moreTv;
    private ImageView director_iv, more_iv;//底栏的按钮图片
    private LinearLayout bottomlayout;//整个底栏的布局

    private MyApplication myApplication;

    private ComAir5Wrapper m_ComAir5 = new ComAir5Wrapper();
    private int command = 0;
    private int i32Cmd, i32Count, i32StatusValue;
    private String rstString;
    private int lastcmd = -1;


    // 超声波handler监听
    ComAir5Wrapper.DisplayCommandValueHelper displayHelper = new ComAir5Wrapper.DisplayCommandValueHelper() {

        @Override
        public void getCommand(int count, int cmdValue, int statusValue) {
            i32Cmd = cmdValue;//
            i32Count = count;
            i32StatusValue = statusValue;// 返回的状态值

            if (ComAir5Wrapper.eComAir5Status.eComAir5Status_CalibrationProcessSuccess
                    .getVal() == i32StatusValue) {
                rstString = "ComAir5 Calibration Process is successful. Type: "
                        + m_ComAir5.GetComAir5RecorderType();
                Log.e("mainActivity", rstString);
            } else if (ComAir5Wrapper.eComAir5Status.eComAir5Status_CalibrationProcessFailed
                    .getVal() == i32StatusValue) {
                rstString = "ComAir5 Calibration Process is failed. (All types are failed)";
                Log.e("mainActivity", rstString);
            } else if (ComAir5Wrapper.eComAir5Status.eComAir5Status_RecorderInitializationFailed
                    .getVal() == i32StatusValue) {
                Toast.makeText(MainActivity.this, "麦克风初始化失败",
                        Toast.LENGTH_SHORT).show();
                rstString = "The recorder is not initialized on type "
                        + m_ComAir5.GetComAir5RecorderType();
                Log.e("mainActivity", rstString);
            } else if (ComAir5Wrapper.eComAir5Status.eComAir5Status_CalibrationTypeFailed
                    .getVal() == i32StatusValue) {
                rstString = "Type " + m_ComAir5.GetComAir5RecorderType()
                        + " is failed.";
                Log.e("mainActivity", rstString);
            } else {
                rstString = "receiveCmd: [" + i32Cmd + "]";
//                    Log.e("mainActivity", rstString);

                //判断是否已经记录了发送的最后一个码值标号
                if (myApplication.lastcmd == -1) {//记录手机发送的最后一个码值的标号
                    if (i32Cmd == myApplication.getCurrentCommend()) {//判断码值是不是手机发送的最后一个码值
                        myApplication.receiveCmd = new int[2];
                        myApplication.index = -1;
                        myApplication.lastcmd = i32Count;
                        rstString = "";
                    }
                } else {
                    //判断是否接收了录制开机声的回码
                    if (i32Count == myApplication.lastcmd + 1) {//自己发送码之后收到的第一个码
                        myApplication.receiveCmd[0] = i32Cmd;
                    } else if (i32Count == myApplication.lastcmd + 2) {//自己发送码之后收到的第二个码
                        myApplication.receiveCmd[1] = i32Cmd;
                        rstString = "[receiveCmd: " + myApplication.receiveCmd[0] + ", " + myApplication.receiveCmd[1] + "]";
//                        Log.e("mainActivity", rstString);

                        //判断收到的双码值是不是录制开机声的回码
                        if (myApplication.isRocorderPowersound && myApplication.receiveCmd[0] == 70 && myApplication.receiveCmd[1] == 0) {
                            myApplication.saveBooleanValue("have_diypowersound", true);
                            myApplication.saveSpIntValue("currentpowersound", 1);
                            runOnUiThread(new Runnable() {
                                              @Override
                                              public void run() {
                                                  sendBroadcast(new Intent("action.recorder_powersound_success"));
                                                  // Toast.makeText(MainActivity.this, "录制开机声成功", Toast.LENGTH_SHORT).show();
                                              }

                                          }
                            );
                            myApplication.isRocorderPowersound = false;
                        }
                    }
                }
            }

            runOnUiThread(new Runnable() {

                              @Override
                              public void run() {
//                    Toast.makeText(MainActivity.this, rstString,
//                            Toast.LENGTH_SHORT).show();
                                  // if(eComAir5Status.eComAir5Status_DecodeCommand.getVal()
                                  // == i32StatusValue)
                                  // PlayCmdSound(i32Cmd);
                              }

                          }
            );
        }
    };


    private static final int MY_PERMISSIONS_REQUEST_RECORDER_AUDIO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myApplication = (MyApplication) getApplication();

        //初始化超声波操作对象
        m_ComAir5.SetComAir5Property(ComAir5Wrapper.eComAir5PropertyTarget.eComAir5PropertyTarget_Decode.ordinal(), ComAir5Wrapper.eComAir5Property.eComAir5Property_SampleRate.ordinal(), 48000);
        m_ComAir5.SetComAir5Property(ComAir5Wrapper.eComAir5PropertyTarget.eComAir5PropertyTarget_Encode.ordinal(), ComAir5Wrapper.eComAir5Property.eComAir5Property_SampleRate.ordinal(), 48000);
        m_ComAir5.SetDisplayCommandValueHelper(displayHelper);
        myApplication.setEncodeComAir5(m_ComAir5);//将对象存在application，方便其他页面使用

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_RECORDER_AUDIO);
        } else {
            m_ComAir5.StartComAir5DecodeProcess();//开启超声波进程
        }

        initView();

    }

    /**
     * 初始化视图
     */
    private void initView() {
        //底部2个按钮
        directorLayout = (LinearLayout) findViewById(R.id.director_layout);
        moreLayout = (LinearLayout) findViewById(R.id.more_layout);

        directorLayout.setOnTouchListener(new myTouchLitener());
        moreLayout.setOnTouchListener(new myTouchLitener());

        directorTv = (TextView) findViewById(R.id.director_text);
        moreTv = (TextView) findViewById(R.id.more_text);

        director_iv = (ImageView) findViewById(R.id.director_iv);
        more_iv = (ImageView) findViewById(R.id.more_iv);

        bottomlayout = (LinearLayout) findViewById(R.id.bottom_layout);

        //viewpager初始化
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        FragmentManager fm = getSupportFragmentManager();
        mVpAdapter = new MyFragmentAdapter(fm);
        mViewPager.setAdapter(mVpAdapter);

        //初始界面为导演界面
        mViewPager.setCurrentItem(Iconst.VIEW_DIRECTOR);
        changeBottomTab(Iconst.VIEW_DIRECTOR);
    }


    /**
     * 更多页面控件点击监听onclick的实现（除了顶栏布局的控件）
     *
     * @param v
     */
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.powersound_frame://录制开机声
                intent = new Intent(MainActivity.this, SetPowerSoundActivity.class);
                startActivity(intent);
                break;
           /* case R.id.diy_frame://自定义问答
                intent = new Intent(MainActivity.this, DIYActivity.class);
                startActivity(intent);
                break;*/
            case R.id.hightogether_frame://多机互动
                intent = new Intent(MainActivity.this, HighTogetherActivity.class);
                startActivity(intent);
                break;
            case R.id.customer_iv://客服电话
            case R.id.customer_tv:
            case R.id.customer_layout:
                showTelephonePopupview();
                break;
            case R.id.feedback_iv://文字反馈
            case R.id.feedback_tv:
            case R.id.feedback_layout:
                intent = new Intent(MainActivity.this, TicklingActivity.class);
                startActivity(intent);
                break;
//            case R.id.vip_iv://vip计划
//            case R.id.vip_tv:
//            case R.id.vip_layout:
//                intent = new Intent(MainActivity.this, VipActivity.class);
//                startActivity(intent);
//                break;
            case R.id.about_iv://关于我们
            case R.id.about_tv:
            case R.id.about_layout:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.privacy_iv://隐私政策
            case R.id.privacy_tv:
            case R.id.privacy_layout:
                intent = new Intent(MainActivity.this, PrivacyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }


    private PopupWindow mPopupWindow;//客服电话的弹窗

    /**
     * 显示客服电话提示弹窗
     */
    private void showTelephonePopupview() {
        View contentView = getLayoutInflater().inflate(R.layout.popupwindow, null);
        Button yes_btn = (Button) contentView.findViewById(R.id.yes_btn);
        Button cancle_btn = (Button) contentView.findViewById(R.id.cancle_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "400-801-5090"));
                //检查是否添加了权限
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
            }
        });

        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAtLocation(mViewPager, Gravity.CENTER, 0, 0);
        mPopupWindow.update();
    }


    /**
     * 底部文字触摸监听
     */
    private class myTouchLitener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.director_layout:
                    mViewPager.setCurrentItem(Iconst.VIEW_DIRECTOR);
                    changeBottomTab(Iconst.VIEW_DIRECTOR);
                    break;
                case R.id.more_layout:
                    mViewPager.setCurrentItem(Iconst.VIEW_MORE);
                    changeBottomTab(Iconst.VIEW_MORE);
                    break;
            }
            return false;
        }

    }

    /**
     * 主页面底部图标变化
     *
     * @param n
     */
    private void changeBottomTab(int n) {

        switch (n) {
            case Iconst.VIEW_DIRECTOR:
                director_iv.setImageResource(R.mipmap.main_role_full);
                more_iv.setImageResource(R.mipmap.main_more);
                break;
            case Iconst.VIEW_MORE:
                director_iv.setImageResource(R.mipmap.main_role);
                more_iv.setImageResource(R.mipmap.main_more_full);
                break;
            default:
                break;

        }
    }

    public void changeView(int n) {
        mViewPager.setCurrentItem(n);
        bottomlayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_ComAir5.StopComAir5DecodeProcess();
        myApplication.resetVolum();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORDER_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                m_ComAir5.StartComAir5DecodeProcess();//开启超声波进程
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private android.os.Handler selectRoleHandler;

    public void setSelectRoleHandler(android.os.Handler handler) {
        selectRoleHandler = handler;
    }
}
