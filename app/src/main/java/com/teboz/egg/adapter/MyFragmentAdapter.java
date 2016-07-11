package com.teboz.egg.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.teboz.egg.fragment.DirectorFragment;
import com.teboz.egg.fragment.MoreFragment;
import com.teboz.egg.fragment.SelectRoleFragment;
import com.teboz.egg.utils.Iconst;

/**
 * Created by Administrator on 2016/3/25.
 */
public class MyFragmentAdapter extends FragmentPagerAdapter {


    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Iconst.VIEW_DIRECTOR:
                return DirectorFragment.getInstance();
           /* case Iconst.VIEW_DEMAND:
                return DemandFragment.getInstance();*/
            case Iconst.VIEW_MORE:
                return MoreFragment.getInstance();
            case Iconst.VIEW_SELECTROLE:
                return SelectRoleFragment.getInstance();
            /*case Iconst.VIEW_DEMANDSUB:
                return DemandSubFragment.getInstance();
            case Iconst.VIEW_THEME:
                return ThemeFragment.getInstance();
            case Iconst.VIEW_POWERSOUND:
                return PowerSoundFragment.getInstance();*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return Iconst.PAGE_NUM;
    }
}
