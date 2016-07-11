package com.teboz.egg.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;

/**
 * 隐私政策
 */
public class PrivacyActivity extends Activity implements View.OnClickListener{

    private TextView id_tv_center;
    private TextView id_tv_left;
    private ImageView id_iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
    }

    private void initView(){
        id_iv_back = (ImageView) findViewById(R.id.id_iv_back);
        id_tv_center = (TextView) findViewById(R.id.id_tv_center);
        id_tv_left = (TextView) findViewById(R.id.id_tv_left);

        id_iv_back.setVisibility(View.VISIBLE);
        id_tv_center.setVisibility(View.VISIBLE);
        id_tv_left.setVisibility(View.VISIBLE);

        id_iv_back.setOnClickListener(this);
        id_tv_left.setOnClickListener(this);
        id_tv_left.setText(R.string.head_left_back);
        id_tv_center.setText(R.string.more_privacy_text);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.id_tv_left:
            case R.id.id_iv_back:
                finish();
                break;
        }
    }
}
