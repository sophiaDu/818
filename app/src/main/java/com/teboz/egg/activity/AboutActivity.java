package com.teboz.egg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;

public class AboutActivity extends Activity implements View.OnClickListener{

    private TextView id_tv_center;//顶栏中间文字
    private TextView id_tv_left;//顶栏左边文字
    private ImageView id_iv_back;//返回按钮
    private TextView weblink;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
    }


    /**
     * 初始化视图
     */
    private void initView(){
        id_iv_back = (ImageView) findViewById(R.id.id_iv_back);
        id_tv_center = (TextView) findViewById(R.id.id_tv_center);
        id_tv_left = (TextView) findViewById(R.id.id_tv_left);

        weblink = (TextView) findViewById(R.id.weblink);
        id_iv_back.setVisibility(View.VISIBLE);
        id_tv_center.setVisibility(View.VISIBLE);
        id_tv_left.setVisibility(View.VISIBLE);

        id_iv_back.setOnClickListener(this);
        id_tv_left.setOnClickListener(this);
        id_tv_left.setText(R.string.head_left_back);
        id_tv_center.setText("关于我们");

        weblink.setText(R.string.company_website);
    }



    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.id_tv_left://返回
            case R.id.id_iv_back:
                finish();
                break;
        }
    }
}
