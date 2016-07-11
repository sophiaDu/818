package com.teboz.egg.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.activity.MainActivity;
import com.teboz.egg.activity.MyApplication;
import com.teboz.egg.adapter.SimpleRecyclerViewAdapter;
import com.teboz.egg.bean.CardInfo;
import com.teboz.egg.utils.Cmd;
import com.teboz.egg.utils.Iconst;
import com.teboz.egg.utils.Utils;
import com.teboz.egg.view.CardHorizontalGridView;

import java.util.List;

/**
 * Created by Administrator on 2016/6/25.
 */
public class SelectRoleFragment extends Fragment implements View.OnTouchListener {
    private View mSelectRoleView;
    private static SelectRoleFragment mSelectRoleFragment;
    private TextView topcenter_tv, topleft_tv;
    private ImageView topleft_iv;

    private CardHorizontalGridView hgv_menu;//卡片列表gridview
    private SimpleRecyclerViewAdapter adapter;//gridview适配器
    //private TextView select_tv;//选择角色按钮
    private TextView prompt_tv;
    /**
     * 上一个选中的item
     */
    private View lastView = null;

    private int itemWidth, itemHeight;//gridview的子项视图的宽高

    /**
     * 卡片背景图片资源，放列表方便随机选择
     */
    private int[] images = {R.mipmap.card_blue_small, R.mipmap.card_green_small, R.mipmap.card_orange_small, R.mipmap.card_purple_small, R.mipmap.card_red_small, R.mipmap.card_yellow_small};

    private List<CardInfo> cardInfos;//卡片数据列表

    private MyApplication myApplication;


    public synchronized static SelectRoleFragment getInstance() {
        if (mSelectRoleFragment == null) {
            mSelectRoleFragment = new SelectRoleFragment();
        }
        return mSelectRoleFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mSelectRoleView = inflater.inflate(R.layout.activity_selectrole, container, false);
        return mSelectRoleView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myApplication = (MyApplication) getActivity().getApplication();

        initView(mSelectRoleView);
    }


    /**
     * 初始化视图
     */
    private void initView(View v) {
        topcenter_tv = (TextView) v.findViewById(R.id.id_tv_center);
        topcenter_tv.setText(R.string.select_role_text);
        topcenter_tv.setVisibility(View.VISIBLE);
        topleft_tv = (TextView) v.findViewById(R.id.id_tv_left);
        topleft_iv = (ImageView) v.findViewById(R.id.id_iv_back);
        topleft_iv.setVisibility(View.VISIBLE);
        topleft_tv.setVisibility(View.VISIBLE);
        topleft_tv.setText(R.string.cancle_select_role_text);
        topleft_iv.setOnTouchListener(this);
        topleft_tv.setOnTouchListener(this);

        prompt_tv = (TextView) v.findViewById(R.id.prompt_tv);
//        select_tv = (TextView) v.findViewById(R.id.tv_choose);
//        select_tv.setOnTouchListener(this);

        hgv_menu = (CardHorizontalGridView) v.findViewById(R.id.hgv_menu);
        //设置只有一行
        hgv_menu.setNumRows(1);
        //设置item之间的间距
        hgv_menu.setHorizontalMargin(0);
        //初始化item的宽高
        itemWidth = (getScreenWidth(getActivity()) / 3) * 2;
        itemHeight = getScreenHeight(getActivity()) - Utils.dip2px(getActivity(), 70);

        cardInfos = myApplication.getCardInfos();//获取卡片数据列表
        //初始化适配器
        adapter = new SimpleRecyclerViewAdapter<MyViewHolder>(cardInfos, R.layout.item_menu_list, getActivity()) {
            @Override
            public MyViewHolder buildHolder(View view) {
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(itemWidth, itemHeight);
                view.setLayoutParams(layoutParams);

                return new MyViewHolder(view);
            }

            @Override
            public void bindData(final MyViewHolder holder, final int position) {
                holder.iv_menu.setImageResource(cardInfos.get(position).getImageId2());
                holder.tv_name.setText(cardInfos.get(position).getName());

                if (cardInfos.get(position).getIsShowDelet()) {//是否显示删除提示界面
                    holder.deletelayout.setVisibility(View.VISIBLE);
                } else {
                    holder.deletelayout.setVisibility(View.GONE);
                }
                //删除提示界面取消按钮监听,点击取消，隐藏删除提示界面
                holder.tv_cancle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                hgv_menu.dissDeletelayout();
                                return true;
                        }
                        return getActivity().onTouchEvent(event);
                    }
                });

                //删除提示界面删除按钮监听,点击删除当前选择的角色
                holder.tv_delete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (!hgv_menu.isDeleteShow())
                                    break;

                                int c = myApplication.getSpIntValue("current_role");
                                if (c == hgv_menu.getSelectedPosition()) {
                                    myApplication.isSelectReturn = true;
                                    myApplication.saveSpIntValue("current_role", Cmd.DEFAULT_ROLE_COUNT - 1);
                                    myApplication.isAddRoleSuccess = true;
                                }

                                hgv_menu.dissDeletelayout();


                                //发送所选择删除的角色码
                                int num = cardInfos.get(hgv_menu.getSelectedPosition()).getCmd();
                                if (num == 7) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[0], false);
                                } else if (num == 8) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[1], false);
                                } else if (num == 9) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[2], false);
                                } else if (num == 10) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[3], false);
                                } else if (num == 11) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[4], false);
                                } else if (num == 12) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[5], false);
                                } else if (num == 13) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[6], false);
                                } else if (num == 14) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[7], false);
                                } else if (num == 15) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[8], false);
                                } else if (num == 16) {
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[9], false);
                                }

                                myApplication.cardInfos.remove(hgv_menu.getSelectedPosition());//列表中移除删除的角色
                                adapter.notifyDataSetChanged();
                                //在删除后的列表转化成json覆盖之前的文件数据
                                myApplication.writeToStorage("cards.json", myApplication.listToJsonArray(myApplication.cardInfos).getBytes());
                                // adapter.remove(hgv_menu.getSelectedPosition());
//                                select_tv.setVisibility(View.VISIBLE);//隐藏删除提示界面
                                hgv_menu.setIsDeleteShow(false);//告诉gridview当前没有子项显示删除提示界面，可以横向滚动

                                return true;
                        }
                        return getActivity().onTouchEvent(event);
                    }
                });

            }
        };
        //设置适配器
        hgv_menu.setAdapter(adapter);

        //设置选中监听器
        hgv_menu.setOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
            @Override
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                //选中的做放大动画，上一个做缩小动画
               /* ViewUtils.animScale(child.itemView, 500, 1.08f);
                if (lastView != null) {
                    ViewUtils.animScale(lastView, 500, 1.0f);
                }*/
                //Toast.makeText(SelectRoleActivity.this,"position: "+position,Toast.LENGTH_SHORT).show();
                if (child != null)
                    lastView = child.itemView;
            }
        });

        hgv_menu.setGesturelistener(new CardHorizontalGridView.GestureListener() {
            @Override
            public void pullup() {
                prompt_tv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void pulldown() {
//                select_tv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void reset() {
//                select_tv.setVisibility(View.VISIBLE);
                prompt_tv.setVisibility(View.VISIBLE);
            }
        });

        //设置select的item始终在window中间
        hgv_menu.setWindowAlignment(HorizontalGridView.WINDOW_ALIGN_NO_EDGE);
        //item垂直居中
        hgv_menu.setGravity(Gravity.CENTER_VERTICAL);
        int num = myApplication.getSpIntValue("current_role");
        if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
            num = 7;
        }
        hgv_menu.setSelectedPosition(num);


        hgv_menu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    x1 = (int) event.getX();
                    y1 = (int) event.getY();

                } else if (action == MotionEvent.ACTION_UP) {
                    x2 = (int) event.getX();
                    y2 = (int) event.getY();

                    if (!hgv_menu.isDeleteShow() && Math.abs(x2 - x1) < 10 && Math.abs(y2 - y1) < 10 && (x2 > getScreenWidth(getActivity()) / 4 && x2 < getScreenWidth(getActivity()) - getScreenWidth(getActivity()) / 4)) {
                        myApplication.isSelectReturn = true;
                       /* int[] cmd = {3, 0};
                        cmd[1] = cardInfos.get(hgv_menu.getSelectedPosition()).getCmd();
                        myApplication.sendCommand(cmd);*/
                        myApplication.saveSpIntValue("current_role", hgv_menu.getSelectedPosition());
                        fatherActivity.changeView(Iconst.VIEW_DIRECTOR);
                        return true;
                    }

                }
                return getActivity().onTouchEvent(event);
            }
        });
        regist();
        cardInfos = myApplication.getCardInfos();
        adapter.notifyDataSetChanged();
        num = myApplication.getSpIntValue("current_role");
        if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
            num = 7;
        }
        hgv_menu.setSelectedPosition(num);

    }


    private int x1, x2, y1, y2;

    @Override
    public void onResume() {
        super.onResume();
        if (myApplication.isAddRoleSuccess){
            myApplication.isAddRoleSuccess = false;
            cardInfos = myApplication.getCardInfos();
            adapter.notifyDataSetChanged();
           int num = myApplication.getSpIntValue("current_role");
            if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
                num = 7;
            }
            hgv_menu.setSelectedPosition(num);
        }

//        Log.e("selectfragment", "resume");
    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e("selectfragment", "pause");
    }

    //获取屏幕的宽度
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    //获取屏幕的高度
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.id_tv_left:
                    case R.id.id_iv_back:
                        fatherActivity.changeView(Iconst.VIEW_DIRECTOR);
                       /* finish();
                        overridePendingTransition(R.anim.zoomin2, R.anim.zoomout2);*/
                        break;
                    case R.id.tv_choose:
                        myApplication.isSelectReturn = true;
                       /* int[] cmd = {3, 0};
                        cmd[1] = cardInfos.get(hgv_menu.getSelectedPosition()).getCmd();
                        myApplication.sendCommand(cmd);*/
                        myApplication.saveSpIntValue("current_role", hgv_menu.getSelectedPosition());
                        fatherActivity.changeView(Iconst.VIEW_DIRECTOR);
                        return true;
                }
        }
        return getActivity().onTouchEvent(event);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_menu;
        TextView tv_name, tv_delete, tv_cancle;
        RelativeLayout deletelayout;
        //        FrameLayout contentlayout;
        View mview;

        public MyViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
            tv_name = (TextView) itemView.findViewById(R.id.name_tv);
            tv_delete = (TextView) itemView.findViewById(R.id.delete_tv);
            tv_cancle = (TextView) itemView.findViewById(R.id.cancle_tv);
            deletelayout = (RelativeLayout) itemView.findViewById(R.id.delete_layout);
//            contentlayout = (FrameLayout) itemView.findViewById(R.id.content_layout);
        }
    }

    private MainActivity fatherActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fatherActivity = (MainActivity) context;
        fatherActivity.setSelectRoleHandler(mHandler);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("action.add_role_success")) {

                cardInfos = myApplication.getCardInfos();
                adapter.notifyDataSetChanged();
                int num = myApplication.getSpIntValue("current_role");

//                Log.e("selecfragment","broadcast-->cuurrent role: "+num);
                if (num < 0 || num >= myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
                    num = 7;
                }
                hgv_menu.setSelectedPosition(num);
            }
        }
    };

    private void regist() {
        IntentFilter filter = new IntentFilter("action.add_role_success");
        getActivity().registerReceiver(mReceiver, filter);
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    break;
            }
        }
    };


}
