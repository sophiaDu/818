package com.teboz.egg.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.bean.CardInfo;
import com.teboz.egg.utils.Cmd;
import com.teboz.egg.utils.Utils;

/**
 * Created by Administrator on 2016/6/8.
 */
public class SaveNameActivity extends Activity implements View.OnClickListener {
    private TextView topright_tv, topcenter_tv;
    private ImageView card_iv;
    private EditText name_et;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savename);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myApplication = (MyApplication) getApplication();
        initView();
    }


    private int imageId;
    private void initView() {
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topright_tv = (TextView) findViewById(R.id.id_tv_right);
        topcenter_tv.setText("角色扮演");
        topright_tv.setText("保存");
        topright_tv.setVisibility(View.VISIBLE);
        topright_tv.setOnClickListener(this);

        name_et = (EditText) findViewById(R.id.cardname_et);
        card_iv = (ImageView) findViewById(R.id.card_iv);
        imageId = Utils.getRandom(6);

        num = myApplication.getSpIntValue("newrole");
        card_iv.setImageResource(myApplication.cardsmallImageIds.get(num%myApplication.cardbigImageIds.size()));
    }


    public void setEditText(String sequence) {
        name_et.setText(sequence);
        name_et.setSelection(sequence.length());
    }

    private   int num;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_right:
                //判断是否有填写新建角色的名字，没有则给出提醒
                String name = name_et.getText().toString();
                if(name == null || name.equals("")){
                    Utils.Toast(SaveNameActivity.this,"请填写角色名称");
                    return;
                }

                //保存新建角色信息到列表
                CardInfo info = new CardInfo();
                info.setId( Cmd.DEFAULT_ROLE_COUNT +num);
                info.setName(name);
                info.setCmd(Cmd.DEFAULT_ROLE_COUNT - 1 + num);
                info.setImageindex(num%myApplication.cardbigImageIds.size());
                info.setImageId(myApplication.cardbigImageIds.get(num%myApplication.cardbigImageIds.size()));
                info.setImageId2(myApplication.cardsmallImageIds.get(num%myApplication.cardbigImageIds.size()));
                myApplication.cardInfos.add(info);

                //新建角色后，当前角色默认为新建角色
                myApplication.saveSpIntValue("current_role", myApplication.cardInfos.size() - 1);
                //将最新的列表数据更新到文件，方便app退出后进入获取到最新的角色数据
                myApplication.writeToStorage("cards.json", myApplication.listToJsonArray(myApplication.cardInfos).getBytes());
                myApplication.isAddRoleSuccess = true;

                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showInput();

    }

    @Override
    protected void onPause() {
        super.onPause();
        hideInput();
    }

    private void hideInput(){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.hideSoftInputFromWindow(name_et.getWindowToken(), 0); //强制隐藏键盘
    }
    private void showInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(1, InputMethodManager.HIDE_NOT_ALWAYS);
        inputMethodManager.showSoftInput(name_et, InputMethodManager.SHOW_FORCED);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        int num = myApplication.getSpIntValue("newrole");
        if(myApplication.cardInfos.get(myApplication.cardInfos.size()-1).getCmd() != (Cmd.DEFAULT_ROLE_COUNT-1 +num)){
            myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[num], false);

        }

    }
}
