package com.teboz.egg.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.teboz.egg.R;
import com.teboz.egg.adapter.SimpleRecyclerViewAdapter;
import com.teboz.egg.bean.CardInfo;
import com.teboz.egg.view.CardHorizontalGridView;

import java.util.List;

/**
 * 选择角色页面用的selectroleFragment
 */
@Deprecated
public class SelectRoleActivity extends Activity implements View.OnTouchListener {

    private TextView topleft_tv, topcenter_tv;//顶栏的左边文字，中间文字

    private CardHorizontalGridView hgv_menu;//卡片列表gridview
    private SimpleRecyclerViewAdapter adapter;//gridview适配器
    private TextView select_tv;//选择角色按钮

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

    //动画相关变量
    private ScaleAnimation inScaleAnimation, outScaleAnimation;
    private TranslateAnimation inTranslateanimation, outTranslateanimation;
    private AlphaAnimation inAlphaAnimation, outAlphaAnimation;
    private AnimationSet scaleInSet, scaleOutSet;

    private MyApplication myApplication;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrole);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        myApplication = (MyApplication) getApplication();
        initView();
    }

    //监听touch时间是记录x,y位置变量
    private int y1 = 0;
    private int y2 = 0;

    /**
     * 初始化视图
     */
    private void initView() {
        topleft_tv = (TextView) findViewById(R.id.id_tv_left);
        topcenter_tv = (TextView) findViewById(R.id.id_tv_center);
       // topleft_tv.setText("完成");
        topcenter_tv.setText("选择角色");
       // topleft_tv.setVisibility(View.VISIBLE);
        topleft_tv.setOnTouchListener(this);


        select_tv = (TextView) findViewById(R.id.tv_choose);
        select_tv.setOnTouchListener(this);

        hgv_menu = (CardHorizontalGridView) findViewById(R.id.hgv_menu);
        //设置只有一行
        hgv_menu.setNumRows(1);
        //设置item之间的间距
        hgv_menu.setHorizontalMargin(0);
        //初始化item的宽高
        itemWidth = (getScreenWidth(this) / 3)*2;
        itemHeight = getScreenHeight(this);

        cardInfos = myApplication.getCardInfos();//获取卡片数据列表
       /* for (int i = 0; i < cardInfos.size(); i++) {
            CardInfo info = cardInfos.get(i);
            info.setImageId(images[i % images.length]);
        }*/

        initAnimation();//初始化动画相关变量


        //初始化适配器
        adapter = new SimpleRecyclerViewAdapter<MyViewHolder>(cardInfos, R.layout.item_menu_list, SelectRoleActivity.this) {

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

                //gridview子项视图下滑手势监听
              /*  holder.mview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        if (position == hgv_menu.getSelectedPosition()) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    y1 = (int) event.getY();
                                    Log.e("select", "y1: " + y1);
                                    return true;
                                case MotionEvent.ACTION_UP:
                                    y2 = (int) event.getY();
//                                    Log.e("select","y1: "+y1);
//                                    Log.e("select","y2: "+y2);
                                    int offestY = y2 - y1;
                                    Log.e("select", "offestY:　" + offestY);

                                    if (position >= Cmd.DEFAULT_ROLE_COUNT && offestY > 1) {//当前手势为竖向下滑动，且选中的子项不为默认角色时显示删除提示界面
                                        if (holder.deletelayout.getVisibility() != View.VISIBLE) {//如果删除提示界面未提示时，显示删除提示界面
                                            holder.deletelayout.startAnimation(scaleInSet); //给删除提示界面添加y轴慢慢放大的动画
                                            holder.deletelayout.setVisibility(View.VISIBLE);//显示删除提示界面
                                            select_tv.setVisibility(View.GONE);//因为“就你了”按钮布局在卡片列表之上，所以为了不造成布局重叠，卡片下移时隐藏按钮
                                            holder.contentlayout.startAnimation(outTranslateanimation);//给卡片添加下移动画
                                            hgv_menu.setIsDeleteShow(true);//告诉gridview当前有子项显示删除提示界面,不可以横向滚动
                                        }
                                    } else if (position >= Cmd.DEFAULT_ROLE_COUNT && offestY < -1) {//当前手势为竖向上滑动，且选中的子项不为默认角色时
                                        if (holder.deletelayout.getVisibility() == View.VISIBLE) {//如果当前删除提示界面时显示的，则隐藏删除提示界面
                                            holder.deletelayout.startAnimation(scaleOutSet);//给删除提示界面添加y轴慢慢缩小消失的动画
                                            holder.contentlayout.startAnimation(inTranslateanimation);//给卡片添加慢慢上移的动画
                                            holder.deletelayout.setVisibility(View.GONE);//隐藏删除提示界面
                                            select_tv.setVisibility(View.VISIBLE);//卡片上移后，显示“就你了”按钮
                                            hgv_menu.setIsDeleteShow(false);//告诉gridview当前没有子项显示删除提示界面,可以横向滚动
                                        }else{

                                        }
                                    } else {
                                        holder.deletelayout.clearAnimation();
                                    }
                                    break;
                            }
                        }
                        return onTouchEvent(event);
                    }
                });*/

                //删除提示界面取消按钮监听,点击取消，隐藏删除提示界面
                holder.tv_cancle.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                hgv_menu.dissDeletelayout();
//                                holder.deletelayout.startAnimation(scaleOutSet);
//                                holder.contentlayout.startAnimation(inTranslateanimation);
//                                holder.deletelayout.setVisibility(View.GONE);
//                                select_tv.setVisibility(View.VISIBLE);
//                                hgv_menu.setIsDeleteShow(false);//告诉gridview当前没有子项显示删除提示界面，可以横向滚动
                                return true;
                        }
                        return onTouchEvent(event);
                    }
                });

                //删除提示界面删除按钮监听,点击删除当前选择的角色
                holder.tv_delete.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                hgv_menu.dissDeletelayout();
                                //发送所选择删除的角色码
                               /* int[] cmd = {3, 0};
                                cmd[1] = cardInfos.get(hgv_menu.getSelectedPosition()).getId();
                                myApplication.sendCommand(cmd);*/
                                int num = cardInfos.get(hgv_menu.getSelectedPosition()).getCmd();
                                if(num == 7){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[0],false);
                                }else if (num == 8){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[1],false);
                                }else if (num == 9){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[2],false);
                                }else if(num == 10){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[3],false);
                                }else if(num == 11){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[4],false);
                                }else if(num == 12){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[5],false);
                                }else if(num == 13){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[6],false);
                                }else if(num == 14){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[7],false);
                                }else if(num == 15){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[8],false);
                                }else if(num == 16){
                                    myApplication.saveBooleanValue(myApplication.roleShareprefrecekeys[9],false);
                                }

                                myApplication.cardInfos.remove(hgv_menu.getSelectedPosition());//列表中移除删除的角色
                                //adapter.notifyDataSetChanged();
                                //在删除后的列表转化成json覆盖之前的文件数据
                                myApplication.writeToStorage("cards.json", myApplication.listToJsonArray(myApplication.cardInfos).getBytes());
                                // adapter.remove(hgv_menu.getSelectedPosition());
                                select_tv.setVisibility(View.VISIBLE);//隐藏删除提示界面
                                hgv_menu.setIsDeleteShow(false);//告诉gridview当前没有子项显示删除提示界面，可以横向滚动

                                return true;
                        }
                        return onTouchEvent(event);
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
              /*  if (lastView != null) {
                    lastView.setTransitionName("");
                }*/
                lastView = child.itemView;
               /* if(lastView != null){
                    lastView.setTransitionName("shareView");
                }*/
            }
        });

        //设置select的item始终在window中间
        hgv_menu.setWindowAlignment(HorizontalGridView.WINDOW_ALIGN_NO_EDGE);
        //item垂直居中
        hgv_menu.setGravity(Gravity.CENTER_VERTICAL);
        int num = myApplication.getSpIntValue("current_role");
        if(num<0 || num>=myApplication.cardInfos.size()) {//如果获取的角色值非法，则设为默认值0，即第一个角色
            num = 0;
        }
        hgv_menu.setSelectedPosition(num);
    }


    /**
     * 监听手机返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
         /*   finish();
            overridePendingTransition(R.anim.zoomin2, R.anim.zoomout2);*/
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 初始化动画相关变量
     */
    private void initAnimation() {
        inAlphaAnimation = new AlphaAnimation(0.2f, 1);
        inAlphaAnimation.setDuration(200);
        outAlphaAnimation = new AlphaAnimation(0.8f, 0.0f);
        outAlphaAnimation.setDuration(150);
        inScaleAnimation = new ScaleAnimation(1, 1, 0.5f, 1,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        inScaleAnimation.setDuration(200);
        outScaleAnimation = new ScaleAnimation(1, 1, 1f, 0.5f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f);
        outScaleAnimation.setDuration(200);

        inTranslateanimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        inTranslateanimation.setDuration(200);

        outTranslateanimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        outTranslateanimation.setDuration(200);

        scaleInSet = new AnimationSet(true);
        scaleInSet.addAnimation(inAlphaAnimation);
        scaleInSet.addAnimation(inScaleAnimation);

        scaleOutSet = new AnimationSet(true);
        scaleOutSet.addAnimation(outAlphaAnimation);
        scaleOutSet.addAnimation(outScaleAnimation);

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
                        finish();
                        overridePendingTransition(R.anim.zoomin2, R.anim.zoomout2);
                        break;
                    case R.id.tv_choose:
                        int[] cmd = {3, 0};
                        cmd[1] = cardInfos.get(hgv_menu.getSelectedPosition()).getCmd();
                        myApplication.sendCommand(cmd);

                        myApplication.saveSpIntValue("current_role", hgv_menu.getSelectedPosition());

                        finish();
                        overridePendingTransition(R.anim.zoomin2, R.anim.zoomout2);
                        return true;
                }
        }
        return onTouchEvent(event);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_menu;
        TextView tv_name, tv_delete, tv_cancle;
        RelativeLayout deletelayout;
        FrameLayout contentlayout;
        View mview;


        public MyViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
            iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
            tv_name = (TextView) itemView.findViewById(R.id.name_tv);
            tv_delete = (TextView) itemView.findViewById(R.id.delete_tv);
            tv_cancle = (TextView) itemView.findViewById(R.id.cancle_tv);
            deletelayout = (RelativeLayout) itemView.findViewById(R.id.delete_layout);
            contentlayout = (FrameLayout) itemView.findViewById(R.id.content_layout);
        }
    }

    public int getCurrentPosition() {
        return hgv_menu.getSelectedPosition();
    }


}
