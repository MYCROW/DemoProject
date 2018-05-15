package com.example.crow.demoproject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{"下载中","已完成"};
    private Context context;

    public MainFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return DownloadFragment.newInstance(position + 1);
        else
            return FinishFragment.newInstance(position+1);
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}