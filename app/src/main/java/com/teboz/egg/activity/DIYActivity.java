package com.teboz.egg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;

/**
 * Created by Administrator on 2016/6/8.
 */
public class DIYActivity extends Activity implements View.OnClickListener{

    private ImageView topleft_iv;//顶栏昨天的图标
    private TextView topleft_tv,topcenter_tv;//顶栏左边中间的文字

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        topleft_iv = (ImageView) findViewById(R.id.id_iv_back);
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topleft_tv.setText("更多");
        topcenter_tv.setText("自定义问答");
        topleft_tv.setVisibility(View.VISIBLE);
        topleft_iv.setVisibility(View.VISIBLE);

        topleft_iv.setOnClickListener(this);
        topleft_tv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_iv_back://返回
            case R.id.id_tv_left://
                finish();
                break;
        }
    }
}
