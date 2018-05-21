package com.example.crow.demoproject.download;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class DownloadThread extends Thread{
    private static final String TAG = "下载线程类";
    private File saveFile;              //下载的数据保存到的文件
    private URL downUrl;              //下载的URL
    private int block;                //每条线程下载的大小
    private int threadId = -1;            //初始化线程id设置
    private int downLength;             //该线程已下载的数据长度
    private boolean finish = false;         //该线程是否完成下载的标志
    private DownloadManager downloadManager;      //文件下载器

    public DownloadThread(DownloadManager downloader, URL downUrl, File saveFile, int block, int downLength, int threadId) {
        this.downUrl = downUrl;
        this.saveFile = saveFile;
        this.block = block;
        this.downloadManager = downloader;
        this.threadId = threadId;
        this.downLength = downLength;
    }

    @Override
    public void run(){

    }

}
