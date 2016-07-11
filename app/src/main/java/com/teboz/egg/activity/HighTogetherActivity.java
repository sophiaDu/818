package com.teboz.egg.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.adapter.MediaAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/8.
 */
public class HighTogetherActivity extends Activity implements View.OnClickListener{

    private ImageView topleft_iv;//顶栏左边图标
    private TextView topleft_tv,topcenter_tv;//顶栏左边、中间文字
    private ListView listView;//一起嗨数据列表
    private MediaAdapter adapter;
    private ImageView point_iv;
    private TextView allplay_tv,allplay_tv2;//全部播放的大字，全部播放的小字
    //private AppCompatCheckBox checkBox;

    private MyApplication myApplication;
    private ScrollView scrollView;
    private RelativeLayout prompt_frame;//页面上方步骤文字提示布局


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highttogether);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myApplication = (MyApplication) getApplication();
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView(){
        topleft_iv = (ImageView) findViewById(R.id.id_iv_back);
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topleft_tv.setText(R.string.more_text);
        topcenter_tv.setText(R.string.multi_eggs_interaction_text);
        topleft_tv.setVisibility(View.VISIBLE);
        topleft_iv.setVisibility(View.VISIBLE);

        topleft_iv.setOnClickListener(this);
        topleft_tv.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.ht_listview);

        //获取列表数据
        Resources res = getResources();
        String[] strs = res.getStringArray(R.array.hightogether);
        List<String> list = new ArrayList<>();
        for (int i = 0; i<strs.length; i++){
            list.add(strs[i]);
        }
        adapter =new MediaAdapter(this,list);
        listView.setAdapter(adapter);

        //列表子项点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if(!checkBox.isChecked())*/
               if(!isSending) {
                    adapter.setCheckedPosition(position);
                    adapter.setIsAnim(true);
                    adapter.notifyDataSetChanged();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(3000);
                            mHandler.sendEmptyMessage(2);
                        }
                    }).start();

                    //发送子项内容相关码值
                    int[] cmd = {10, 0};
                    cmd[1] = position;
                    myApplication.sendCommand(cmd);
                   isSending = true;
                }

            }
        });

        point_iv = (ImageView) findViewById(R.id.point_iv);
        allplay_tv = (TextView) findViewById(R.id.allplay_tv);
        allplay_tv2 = (TextView) findViewById(R.id.allplay_tv2);
        allplay_tv.setOnClickListener(this);
        allplay_tv2.setOnClickListener(this);


        //让listview上面的控件获取焦点，解决scrollview一开始滑入低端的问题
        prompt_frame = (RelativeLayout) findViewById(R.id.prompt_frame);
        prompt_frame.setFocusable(true);
        prompt_frame.setFocusableInTouchMode(true);
        prompt_frame.requestFocus();
        setListViewHeightBasedOnChildren(listView);//解决listview内嵌在scrollview只显示一行数据的问题

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private boolean isSending = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0://全部播放文字前面的红绿灯动画开始
                    point_iv.setVisibility(View.VISIBLE);
                    point_iv.setImageResource(R.drawable.points_anim);
                    AnimationDrawable animationDrawable = (AnimationDrawable) point_iv.getDrawable();
                    animationDrawable.start();
                    myApplication.sendCommand(new int[]{10, 79});
                    break;
                case 1://全部播放文字前面的红绿灯动画结束
                    allplay_tv.setTextColor(getResources().getColor(R.color.content_textcolor));
                    allplay_tv2.setTextColor(getResources().getColor(R.color.content_textcolor));
                    point_iv.clearAnimation();
                    point_iv.setVisibility(View.INVISIBLE);
                    isSending = false;
                    break;
                case 2://列表中item前面的红绿灯动画结束
                    adapter.setIsAnim(false);
                    adapter.notifyDataSetChanged();
                    isSending = false;
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_iv_back://返回
            case R.id.id_tv_left:
                finish();
                break;
            case R.id.allplay_tv:
            case R.id.allplay_tv2://全部播放
                if(!isSending) {
                    //文字置灰
                    allplay_tv.setTextColor(Color.GRAY);
                    allplay_tv2.setTextColor(Color.GRAY);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SystemClock.sleep(50);
                            mHandler.sendEmptyMessage(0);//开始红绿灯动画
                            SystemClock.sleep(3000);
                            mHandler.sendEmptyMessage(1);//结束红绿灯动画
                        }
                    }).start();
                    isSending  = true;
                }




                break;
        }
    }
}
