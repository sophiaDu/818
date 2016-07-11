package com.teboz.egg.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

public class VipActivity extends Activity implements View.OnClickListener {

    private TextView id_tv_center;
    private TextView id_tv_left;
    private ImageView id_iv_back;
    private TextView id_tv_right;
    private EditText id_et_number;
    private MyApplication mApplication;
    private String number;
    private final String httpurl = "http://r1.mytbz.com:8080/J-R1-admin/egg/parentInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        AndroidBug5497Workaround.assistActivity(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        AndroidBug5497Workaround.assistActivity(this);

        mApplication = (MyApplication) getApplication();

        id_iv_back = (ImageView) findViewById(R.id.id_iv_back);
        id_tv_center = (TextView) findViewById(R.id.id_tv_center);
        id_tv_left = (TextView) findViewById(R.id.id_tv_left);


        id_iv_back.setVisibility(View.VISIBLE);
        id_tv_center.setVisibility(View.VISIBLE);
        id_tv_left.setVisibility(View.VISIBLE);

        id_tv_right = (TextView) findViewById(R.id.id_tv_right);
        id_tv_right.setText("提交");
        id_tv_right.setVisibility(View.VISIBLE);
        id_tv_right.setOnClickListener(this);
        id_tv_right.setTextColor(Color.GRAY);
        id_tv_right.setEnabled(false);

        id_iv_back.setOnClickListener(this);
        id_tv_left.setOnClickListener(this);
        id_tv_left.setText(R.string.head_left_back);
//        id_tv_center.setText(R.string.more_vip_text);

        id_et_number = (EditText) findViewById(R.id.id_et_number);
        id_et_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_tv_right:
                postInformation();
                break;
            case R.id.id_tv_left:
            case R.id.id_iv_back:
                finish();
                break;
        }
    }

    private void postInformation() {
//        Log.e("dj", "postinformation");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> map = new HashMap<String, String>();

        number = id_et_number.getText().toString();
        if(number == null || number.equals("")) {
            Utils.Toast(this, "您还没有填写联系方式");
            return;
        }
        if (Utils.isEmail(number)) {
            map.put("email", number);
        } else if (Utils.isMobileNO(number)) {
            map.put("phone", number);
        } else {
            Utils.Toast(this, "请填写正确的联系方式");
            return;
        }

        JSONObject jsonObject = new JSONObject(map);
        JSONObject obj = new JSONObject();
        try {
            obj.put("parent_info", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.e("dj", "vipjson: " + obj);
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(Request.Method.POST, httpurl, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response -> {"rc":200,"msg":"save parent info succeed"}
                        Log.e("dj", "response -> " + response.toString());

                        int rc = 0;
                        try {
                            rc = response.getInt("rc");
                            if (rc == 200) {
//                                Utils.Toast(VipActivity.this, "提交成功");

                            } else if (rc == 3001) {
//                                Utils.Toast(VipActivity.this, "手机号格式错误");
//                                return;
                            } else if (rc == 3002) {
//                                Utils.Toast(VipActivity.this, "Email格式错误");
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
                //Toast.makeText(VipActivity.this,"网络故障，请稍后尝试",Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onPause() {
        super.onPause();
        ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
