package com.teboz.egg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.teboz.egg.R;

/**
 * 更多页面
 * Created by Administrator on 2016/3/23.
 */
public class MoreFragment extends Fragment{

    private View mMoreView;
    private static MoreFragment mMoreFragment;
    private TextView topcenter_tv;

    public synchronized  static MoreFragment getInstance(){
        if(mMoreFragment == null){
            mMoreFragment = new MoreFragment();
        }
        return mMoreFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

       mMoreView = inflater.inflate(R.layout.fragment_more,container,false);

        initView();
        return mMoreView;
    }

    /**
     * 初始化视图，更多页面的按钮监听在MainActivity的Onclick里面
     */
    private void initView(){
        topcenter_tv = (TextView) mMoreView.findViewById(R.id.id_tv_center);
        topcenter_tv.setText(R.string.more_text);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
