package com.example.crow.demoproject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{"下载中","已完成"};
    private Context context;

    public DownloadFragment downloadFragment;
    public FinishFragment finishFragment;
    public boolean getDownloadFragment;
    public boolean getFinishFragment;

    public MainFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
        getDownloadFragment = false;
        getFinishFragment = false;

        downloadFragment = DownloadFragment.newInstance(0);
        finishFragment = FinishFragment.newInstance(1);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            //downloadFragment = DownloadFragment.newInstance(position);
            getDownloadFragment = true;
            return downloadFragment;
        }
        else {
            //finishFragment = FinishFragment.newInstance(position);
            getFinishFragment = true;
            return finishFragment;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return super.isViewFromObject(view,object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(position == 0) {
            DownloadFragment fragment = (DownloadFragment) super.instantiateItem(container, position);
            return fragment;
        }
        else{
            FinishFragment fragment = (FinishFragment) super.instantiateItem(container, position);
            return fragment;
        }
    }
}