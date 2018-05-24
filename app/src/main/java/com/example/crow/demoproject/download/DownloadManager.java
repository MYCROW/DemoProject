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

    Context mContext;

    public DownloadManager(Context context) {
        db_downloadOperator = new DB_DownloadOperator(context);
        mContext = context;
        taskList = new ArrayList<>();
    }

    //public DB_DownloadOperator getDb_downloadOperator(){return db_downloadOperator;}

    public DownloadTask getTaskbyFilename(String filename){
        for(int i=0;i<taskList.size();i++){
            if(filename.equals(taskList.get(i).getFilename()))
                return taskList.get(i);
        }
        return null;
    }

    /**为新添下载任务创建唯一文件名**/
    //文件名格式：path+时间戳
    private final int MOD = 10000;//控制时间戳
    private String createFilename(String path){
        String filename = "";
        try {
            Urlpath pathcheck = new Urlpath(path);
            if(pathcheck.getPath()=="/")
                return "";
            String []tp = pathcheck.getPath().split("/");
            filename = tp[tp.length-1];
        }catch (Exception e){
            return "";
        }
        //url地址合法 生成文件名
        long currentTime=System.currentTimeMillis()%10000;
        String timestamp = "_"+currentTime;
        String []temp = filename.split("\\.");
        String tempname = "";
        if(temp.length>1){
            tempname = tempname+temp[0]+timestamp;
            for(int i = 1;i<temp.length;i++)
                tempname = tempname+"."+temp[i];
            filename = tempname;
        }
        else{
            filename = filename+timestamp;
        }
        return filename;
    }

    /**添加下载任务**/
    public DownloadTask addDownloadTask(String path,File saveDir){
        /**应该根据路径和已有任务名情况得到一个新的任务名**/
        String filename = createFilename(path);
        DownloadTask task = new DownloadTask(filename,path,saveDir,mContext);
        if(filename == "")//创建下载任务失败
            return task;
        taskList.add(task);
        return taskList.get(taskList.size()-1);
    }
    //开启下载任务
    public void startDownloadTask(DownloadTask task){
        new Thread(task).start();
    }
    //挂起下载任务
    //public void

    /**根据文件名删除下载任务**/
    public void delDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        if(task !=null)
            task.exit();
        //to be finished
        taskList.remove(task.getId_List());
        Log.i("DownloadManager","数据库中删除");
    }

    public void resDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        //if(task ==null)
        //to be finished
        startDownloadTask(task);
    }

    public void pauDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        //to be finished
        if(task !=null)
            task.exit();
    }

    public int getTaskSize(){
        return 0;
    }

    public int getTaskNum(){return taskList.size();}

    private void pause(){

    }

    private void exit(){

    }

}
