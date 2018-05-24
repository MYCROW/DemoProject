package com.example.crow.demoproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.crow.demoproject.download.DownloadTask;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class MainActivity extends FragmentActivity implements DownloadFragment.DownloadFinishInterface,
        DownloadFragment.DownloadInitInterface,
        FinishFragment.DownloadDeleteInterface{
//        implements DownloadFragment.OnFragmentInteractionListener
    private MainFragmentPagerAdapter pagerAdapter;

    private ViewPager viewPager;

    private TabLayout tabLayout;

    private DownloadFragment downloadFragment;
    private FinishFragment finishFragment;
    public boolean getDownloadFragment;
    public boolean getFinishFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabList);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        getDownloadFragment = false;
        getFinishFragment = false;
        loadFragment();
    }

    void loadFragment(){
        if(getFinishFragment && getFinishFragment)
            return;
        if(pagerAdapter.getDownloadFragment == true) {
            this.downloadFragment = pagerAdapter.downloadFragment;
            getDownloadFragment = true;
        }
        if(pagerAdapter.getFinishFragment == true){
            this.finishFragment = pagerAdapter.finishFragment;
            getFinishFragment = true;
        }
    }

    @Override
    /*加载菜单栏xml*/
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
//        Log.i("ContentValues","Run");
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

    //function 新增下载任务
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
        this.finishFragment.setTask(task);
    }

    @Override
    public void onDownloadDelete(String filename){
        this.downloadFragment = pagerAdapter.downloadFragment;
        downloadFragment.del_DownloadTaskByfilename(filename);}

    @Override
    public void onDownloadInit(DownloadTask task) {
        this.finishFragment = pagerAdapter.finishFragment;
        //Log.i("onDownloadFinish",task.getFilename());
        this.finishFragment.getTask(task);
    }

}
