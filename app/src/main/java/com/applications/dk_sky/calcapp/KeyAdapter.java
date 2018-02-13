package com.applications.dk_sky.calcapp;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by D.Karabetskiy on 1/18/18.
 */

public class KeyAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public KeyAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BasicFragment();
            case 1:
                return new AdvancedFragment();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.category_basic);
            case 1:
                return mContext.getString(R.string.category_advanced);
            default:
                return null;
        }
    }
}

