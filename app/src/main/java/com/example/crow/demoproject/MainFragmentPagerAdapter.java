package com.example.crow.demoproject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

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
    }

    @Override
    public Fragment getItem(int position) {
//        Log.i("ContentValues","getItem");
        if(position == 0) {
            downloadFragment = DownloadFragment.newInstance(position + 1);
            getDownloadFragment = true;
            return downloadFragment;
        }
        else {
            finishFragment = FinishFragment.newInstance(position+1);
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
}