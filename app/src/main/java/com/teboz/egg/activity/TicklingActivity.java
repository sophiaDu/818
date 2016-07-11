package com.teboz.egg.activity;

import android.app.Activity;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.teboz.egg.R;
import com.teboz.egg.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TicklingActivity extends Activity {

    private TextView id_tv_center;
    private TextView id_tv_right;
    private TextView id_tv_left;
    private TextView textcount_tv;
    private EditText id_et_content;
    private EditText id_et_number;
    private LocationManager lm;//gps定位相关

    private final String httpurl = "http://r1.mytbz.com:8080/J-R1-admin/egg/feedback";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickling);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initView() {
        id_tv_center = (TextView) findViewById(R.id.id_tv_center);
        id_tv_center.setText(R.string.more_feedback_text);

        id_tv_right = (TextView) findViewById(R.id.id_tv_right);
        id_tv_right.setText(R.string.text_send);
        id_tv_right.setVisibility(View.VISIBLE);
        id_tv_right.setOnClickListener(new TextClickLisntener());
        id_tv_right.setTextColor(Color.GRAY);
        id_tv_right.setEnabled(false);
        id_tv_left = (TextView) findViewById(R.id.id_tv_left);
        id_tv_left.setText(R.string.bt_cancel);
        id_tv_left.setVisibility(View.VISIBLE);
        id_tv_left.setOnClickListener(new TextClickLisntener());
        id_tv_left.setPadding(Utils.dip2px(this, 20), 0, 0, 0);

        id_et_content = (EditText) findViewById(R.id.id_et_content);
        id_et_number = (EditText) findViewById(R.id.id_et_number);

        textcount_tv = (TextView) findViewById(R.id.textcount_tv);
        id_et_content.addTextChangedListener(mEditText);
        textcount_tv.setText("0/140");
    }

    class TextClickLisntener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_tv_right:
                    postFeedback();
                    break;
                case R.id.id_tv_left:
                    finish();
                    break;
            }
        }
    }

    private void postFeedback() {
        Log.e("dj", "postinformation");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> map = new HashMap<String, String>();

        String number = id_et_number.getText().toString();
        String content = id_et_content.getText().toString();

        if (content == null || content.equals("")) {
            Utils.Toast(this, "您还没有填写反馈内容");
            return;
        }

        if (number != null && !number.equals("")) {
            if (Utils.isEmail(number)) {
                map.put("email", number);
            } else if (Utils.isMobileNO(number)) {
                map.put("phone", number);
            } else {
                Utils.Toast(TicklingActivity.this, "请填写正确的联系方式");
                return;
            }
        }else{
            map.put("email","");
            map.put("phone","");
        }


        map.put("os_version", "android_" + Utils.getSystemVersion());
        map.put("device", Utils.getPhoneModel());
        map.put("content", content);
        map.put("city", "");

        JSONObject jsonObject = new JSONObject(map);
        JSONObject obj = new JSONObject();
        try {
            obj.put("feedback", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.e("dj", "feedbackjson: " + obj);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, httpurl, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response -> {"rc":200,"msg":"save parent info succeed"}
//                        Log.e("dj", "response -> " + response.toString());

                        int rc = 0;
                        try {
                            rc = response.getInt("rc");
                            if (rc == 200) {
//                                Utils.Toast(TicklingActivity.this, "提交成功");

                            } else if (rc == 3001) {
//                                Utils.Toast(TicklingActivity.this, "手机号格式错误");
//                                return;
                            } else if (rc == 3002) {
//                                Utils.Toast(TicklingActivity.this, "Email格式错误");
//                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("dj", error.getMessage(), error);
                finish();
                return;
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");

                return headers;
            }
        };
        requestQueue.add(jsonRequest);
    }


    TextWatcher mEditText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            textcount_tv.setText(s.length() + "/140");

            if(s == null || s.length() == 0){
                id_tv_right.setTextColor(Color.GRAY);
                id_tv_right.setEnabled(false);
            }else{
                id_tv_right.setTextColor(getResources().getColor(R.color.redtext_color));
                id_tv_right.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };




    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
