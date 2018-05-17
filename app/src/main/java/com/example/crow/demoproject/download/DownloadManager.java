package com.example.crow.demoproject.download;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.util.Log;

public class DownloadManager {
    private int MAX_NUM_DOWN = 3;//最大同时下载文件数
    private int MAX_NUM_THRE = 10;//最大线程数
    private Context context;
    private DB_DownloadOperator db_downloadOperator;//数据库操作
    private ArrayList<DownloadThread> threads;//根据线程数设置下载的线程池
    private ArrayList<DownloadTask> taskList;//任务池

    public DownloadManager(Context context)
    {
        db_downloadOperator = new DB_DownloadOperator(context);
        taskList = new ArrayList<>();
    }

    public DB_DownloadOperator getDb_downloadOperator(){return db_downloadOperator;}

    public DownloadTask addDownloadTask(String path){
        /**应该根据路径和已有任务名情况得到一个新的任务名**/
        String filename = path;
        //to be finish
        DownloadTask task = new DownloadTask(filename,path);
        taskList.add(task);
        if(taskList.size()<=MAX_NUM_DOWN)
        {

        }
        return taskList.get(taskList.size()-1);
    }

    public int getTaskSize(){
        return 0;
    }

    public int getTaskNum(){return taskList.size();}

    private void download(){

    }

    private void pause(){

    }

    private void exit(){

    }

}
