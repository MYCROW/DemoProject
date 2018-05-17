package com.example.crow.demoproject.download;

import android.content.Context;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadTask {
    private String filename;//任务文件名
    private boolean exited;             //停止下载的标志
    private int downloadedSize = 0;               //已下载的文件长度
    private int fileSize = 0;           //开始的文件长度
    private File saveFile;              //数据保存到本地的文件中
    private Map<Integer, Integer> data = new ConcurrentHashMap<Integer, Integer>();  //缓存每个线程的下载的长度
    private int block;                            //每条线程下载的长度
    private String downloadUrl;                   //下载的路径

    public DownloadTask(String filename,String downloadUrl){
        this.filename =filename;
        this.downloadUrl = downloadUrl;
    }
}
