package com.example.crow.demoproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.crow.demoproject.download.DownloadTask;

public class MainActivity extends FragmentActivity implements DownloadFragment.DownloadFinishInterface,
        DownloadFragment.DownloadInitInterface,
        FinishFragment.DownloadDeleteInterface{

    //ViewPager + TabLayout + PagerAdapter 实现标签切换fragment
    private MainFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //从pagerAdapter处获得Fragment
    private DownloadFragment downloadFragment;
    private FinishFragment finishFragment;
    private boolean nullflag;

    /**动态要求赋予权限**/
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //LinearLayout linearLayout = findViewById(R.id.main_line);

        //linearLayout.setBackground();

        setContentView(R.layout.activity_main);
        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        //fragment切换事件侦听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //Log.i("ViewPager",pagerAdapter.getPageTitle(position).toString());
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tabLayout = findViewById(R.id.tabList);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        verifyStoragePermissions(this);

        nullflag =true;
        loadFragment();
    }

    /**确保DownloadFragment和FinishFragment不为null**/
    void loadFragment(){
//        if(getFinishFragment && getFinishFragment)
//            return;
//        if(pagerAdapter.getDownloadFragment == true) {
//            this.downloadFragment = pagerAdapter.downloadFragment;
//            getDownloadFragment = true;
//        }
//        if(pagerAdapter.getFinishFragment == true){
//            this.finishFragment = pagerAdapter.finishFragment;
//            getFinishFragment = true;
//        }
        if(pagerAdapter.downloadFragment != null) {
            downloadFragment = pagerAdapter.downloadFragment;
            nullflag = false;
        }
        else
            nullflag = true;
        if(pagerAdapter.finishFragment != null)
            finishFragment = pagerAdapter.finishFragment;
        else
            nullflag = true;
    }

    @Override
    /**加载菜单栏xml**/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    /**菜单栏点击响应**/
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_Item:
                add_DownloadMission();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    /**处理按返回键后重新进入会有启动界面的情况**/
    public void onBackPressed() {
        // super.onBackPressed(); 	不要调用父类的方法
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    /**新增下载任务**/
    public void add_DownloadMission(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //    设置Title的图标
//        builder.setIcon(R.drawable.add_icon);
        //    设置Title的内容
        builder.setTitle("下载地址输入");
        //    设置自定义的弹出框
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.addmission_dialog, null);
        builder.setView(view);
        final EditText downloadAddr = (EditText)view.findViewById(R.id.downloadAddr);
        builder.setPositiveButton("确定", null);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(downloadAddr.getText().length()!=0)
                    Toast.makeText(MainActivity.this, "取消下载" , Toast.LENGTH_SHORT).show();
            }
        });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
        Button positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //判断下载地址是否为空
                if(downloadAddr.getText().length()==0){
                    Toast.makeText(MainActivity.this, "请输入下载地址", Toast.LENGTH_SHORT).show();
                    return;
                }
                //开始下载
                else{
                    if(nullflag)
                        loadFragment();
                    downloadFragment.add_DownloadTask(downloadAddr.getText());
                }
                alert.dismiss();
            }
        });
    }

    //实现接口DownloadFinishInterface
    @Override
    public void onDownloadFinish(DownloadTask task) {
        this.finishFragment.receiveTask(task);
    }

    @Override
    public void onDownloadDelete(String filename){
        this.downloadFragment = pagerAdapter.downloadFragment;
        downloadFragment.del_DownloadTaskByfilename(filename);}

    @Override
    public void onDownloadInit(DownloadTask task) {
        this.finishFragment = pagerAdapter.finishFragment;
        //Log.i("onDownloadFinish",task.getFilename());
        this.finishFragment.receiveTask(task);
    }

}
