package com.teboz.egg.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.utils.Cmd;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ListenActivity extends Activity implements View.OnClickListener{


    private ImageView topleft_ib;
    private TextView topleft_tv, topcenter_tv,topright_tv;
    private Button listen_btn,rerecorder_btn;

    private MyApplication myApplication;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myApplication = (MyApplication) getApplication();
        initView();

    }

    private void initView() {
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topright_tv = (TextView) findViewById(R.id.id_tv_right);
        topleft_ib = (ImageView) findViewById(R.id.id_iv_back);
        topleft_ib.setVisibility(View.VISIBLE);
        topleft_tv.setVisibility(View.VISIBLE);
        topright_tv.setVisibility(View.VISIBLE);
        topleft_tv.setText("取消");
        topright_tv.setText("保存");
        topcenter_tv.setText("定制开机声");
        topleft_ib.setOnClickListener(this);
        topleft_tv.setOnClickListener(this);
        topright_tv.setOnClickListener(this);

        listen_btn = (Button) findViewById(R.id.listen_btn);
        rerecorder_btn = (Button) findViewById(R.id.re_recorder_btn);

        listen_btn.setOnClickListener(this);
        rerecorder_btn.setOnClickListener(this);


    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_iv_back://取消
            case R.id.id_tv_left:
                finish();
                break;
            case R.id.id_tv_right://保存
                myApplication.sendCommand(Cmd.CHECKED_DIY_POWERSOUND);
                finish();
                break;
            case R.id.listen_btn://试听
                myApplication.sendCommand(Cmd.DIY_POWERSOUND_LISTEN);

                break;
            case R.id.re_recorder_btn://重录
                Intent intent = new Intent(ListenActivity.this,RecorderPowerActivity.class);
                startActivity(intent);
                myApplication.sendCommand(Cmd.DIY_POWERSOUND);
                finish();
                break;
        }
    }
}