package com.teboz.egg.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.activity.AddRoleActivity;
import com.teboz.egg.activity.MainActivity;
import com.teboz.egg.activity.MyApplication;
import com.teboz.egg.utils.Cmd;
import com.teboz.egg.utils.Iconst;


/*
 * 导演页面
 * Created by Administrator on 2016/3/23.
 */
public class DirectorFragment extends Fragment implements View.OnClickListener {

    private View mDirectorView;
    private static DirectorFragment mDirectorFragment;
    private ImageButton addrole_btn;
    private TextView topcenter_tv,topright_tv;
    private ImageButton topright_ib;
    private ImageView card_iv;
    private EditText card_tv;
    private ImageView point1_iv,point2_iv,point3_iv;
    private Button changerole_btn;

    private MyApplication myApplication;
    private long lastClick = -1;

    public synchronized static DirectorFragment getInstance() {
        if (mDirectorFragment == null) {
            mDirectorFragment = new DirectorFragment();
        }

        return mDirectorFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDirectorView = inflater.inflate(R.layout.fragment_director, container, false);
        return mDirectorView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApplication = (MyApplication) getActivity().getApplication();

        initView(mDirectorView);
    }


    /**
     * 初始化视图，ps:更换角色按钮的监听在MainActivity类
     *
     * @param v
     */
    private void initView(View v) {

        topcenter_tv = (TextView) v.findViewById(R.id.id_tv_center);
        topright_tv = (TextView) v.findViewById(R.id.id_tv_right);
        topright_ib = (ImageButton) v.findViewById(R.id.id_ib_right);

        topcenter_tv.setText(R.string.director_text);
        topright_ib.setVisibility(View.VISIBLE);
        topcenter_tv.setVisibility(View.VISIBLE);
        topright_ib.setOnClickListener(this);
        topright_tv.setOnClickListener(this);
        topright_tv.setText(R.string.edit_name_save);
        topright_tv.setTextColor(getResources().getColor(R.color.redtext_color));

        changerole_btn = (Button) v.findViewById(R.id.changerole_btn);
        changerole_btn.setOnClickListener(this);

        point1_iv = (ImageView) v.findViewById(R.id.point1);
        point2_iv = (ImageView) v.findViewById(R.id.point2);
        point3_iv = (ImageView) v.findViewById(R.id.point3);

        resetAddBtn();

        card_iv = (ImageView) v.findViewById(R.id.card_iv);
        card_tv = (EditText) v.findViewById(R.id.card_tv);
        card_tv.setOnClickListener(this);


        int num = myApplication.getSpIntValue("current_role");
        if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
            num = 7;

          /*  int[] cmd = {3,7};
            myApplication.sendCommand(cmd);*/
            myApplication.saveSpIntValue("current_role", 7);
        }
        if (myApplication.cardInfos == null || myApplication.cardInfos.size() == 0) {
            myApplication.resetCardData();
        }
        card_tv.setText(myApplication.cardInfos.get(num).getName());
        card_iv.setImageResource(myApplication.cardInfos.get(num).getImageId());
        card_tv.setFocusableInTouchMode(false);
        card_tv.setFocusable(false);
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
            if (point3_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole", "one point dismiss");
                point3_iv.setVisibility(View.INVISIBLE);
            } else if (point2_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole","two point dismiss");
                point2_iv.setVisibility(View.INVISIBLE);
            } else if (point1_iv.getVisibility() == View.VISIBLE) {
                Log.e("addrole","three point dismiss");
                point1_iv.setVisibility(View.INVISIBLE);
            }
            count++;


        }

        @Override
        public void onFinish() {//圆点全部消失后开始录制倒数计时器
            Log.e("addrole", "timer finish");
           /* point3_iv.setVisibility(View.INVISIBLE);
            point2_iv.setVisibility(View.INVISIBLE);
            point1_iv.setVisibility(View.INVISIBLE);*/


        /*    countdownFrame.setVisibility(View.INVISIBLE);
            starrecordFrame.setVisibility(View.VISIBLE);
            recorderTimer.start();
            countdownnumber = 8;*/

        }
    };

    private void resetAddBtn() {
        boolean[] states = new boolean[myApplication.roleShareprefrecekeys.length];
        for (int i = 0; i < myApplication.roleShareprefrecekeys.length; i++) {
            states[i] = myApplication.getSpBooleanValue(myApplication.roleShareprefrecekeys[i]);
        }

        if (states[0] && states[1] && states[2] && states[3] && states[4] && states[5]
                && states[6] && states[7] && states[8] && states[9]) {
//            topright_ib.setClickable(false);
            topright_ib.setEnabled(false);
            topright_ib.setImageResource(R.mipmap.role_add_gray);
        } else {
            topright_ib.setEnabled(true);
            topright_ib.setImageResource(R.mipmap.role_add);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //获取焦点时更新界面，因为其他界面有可能切换了角色
        int num = myApplication.getSpIntValue("current_role");
        if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
            num = 7;

           /* int[] cmd = {3,num};
            myApplication.sendCommand(cmd);*/
            myApplication.saveSpIntValue("current_role", 7);
        }
        card_tv.setText(myApplication.cardInfos.get(num).getName());
        card_iv.setImageResource(myApplication.cardInfos.get(num).getImageId());
        final int[] cmd = {3,myApplication.cardInfos.get(num).getCmd()};
        if(myApplication.isSelectReturn){
            point1_iv.setVisibility(View.VISIBLE);
            point2_iv.setVisibility(View.VISIBLE);
            point3_iv.setVisibility(View.VISIBLE);
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
        resetAddBtn();
    }


    private int num = 0;
    @Override
    public void onClick(View v) {
        num = 0;
        switch (v.getId()) {

            case R.id.changerole_btn:
                fatherActivity.changeView(Iconst.VIEW_SELECTROLE);
                if(timer != null){
                    timer.cancel();
                }
                break;
            case R.id.card_tv:

                num = myApplication.getSpIntValue("current_role");
                if(num >= Cmd.DEFAULT_ROLE_COUNT) {
                    card_tv.setFocusableInTouchMode(true);
                    card_tv.setFocusable(true);
                    card_tv.requestFocus();
                    card_tv.setSelection(card_tv.getText().toString().length());
                    topright_ib.setVisibility(View.GONE);
                    topright_tv.setVisibility(View.VISIBLE);
                    showInput();
                }
                break;
            case R.id.id_ib_right:
                // boolean[] states = new boolean[myApplication.roleShareprefrecekeys.length];
                //防止快速重复点击重复发码

                if (lastClick == -1 || (System.currentTimeMillis() - lastClick > 3000)) {
                    lastClick = System.currentTimeMillis();
                    myApplication.isDirectorIn = true;
                   /* num = 0;
                    for (int i = 0; i < myApplication.roleShareprefrecekeys.length; i++) {
                        Log.e("directorf", "[" + i + "]: " + myApplication.roleShareprefrecekeys[i]);
                        boolean state = myApplication.getSpBooleanValue(myApplication.roleShareprefrecekeys[i]);
                        if (!state) {
                            num = i;
                            myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[i], true);
                            break;
                        }
                    }


                    Log.e("savename", "num: " + num);

                   // Log.e("savename", "cmd: " + cmd[1]);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
//                            SystemClock.sleep(50);
                            int[] cmd = {1, 0};
                            cmd[0] = Cmd.ADD_ROLE[0];
                            cmd[1] = Cmd.ADD_ROLE[1] + num;
                            myApplication.sendCommand(cmd);
                        }
                    }).start();

                    myApplication.saveSpIntValue("newrole", num);*/

                    Intent intent = new Intent(getActivity(), AddRoleActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.id_tv_right:
                hideInput();
                card_tv.setFocusableInTouchMode(false);
                card_tv.setFocusable(false);
                topright_tv.setVisibility(View.GONE);
                topright_ib.setVisibility(View.VISIBLE);

                num = myApplication.getSpIntValue("current_role");
                myApplication.cardInfos.get(num).setName(card_tv.getText().toString());
                myApplication.writeToStorage("cards.json",myApplication.listToJsonArray(myApplication.cardInfos).getBytes());

                break;
        }
    }

    private void hideInput(){
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.hideSoftInputFromWindow(card_tv.getWindowToken(), 0); //强制隐藏键盘
    }

    private void showInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(1, InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.showSoftInput(card_tv, InputMethodManager.SHOW_IMPLICIT);
    }


    private MainActivity fatherActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fatherActivity = (MainActivity) context;
    }
}
