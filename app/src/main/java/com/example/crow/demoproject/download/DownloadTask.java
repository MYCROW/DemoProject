package com.example.crow.demoproject.download;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.crow.demoproject.DownloadFragment;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadTask implements Runnable{
    private int id_List;//表示在下载管理器的位置
    private int id_Fini;//表示在已完成页面的位置
    private String filename;//任务文件名
    private File saveFile;              //数据保存到本地的文件中
    private String downloadUrl;         //下载的路径
    private FileDownloadered loader;
    private int threadnum;
    private Context mContext;
    private int filesize = 0;
    private int downsize = 0;
    private boolean isExit = false;
    private boolean isFinish = false;
    private boolean isDownloading = false;

    //进度条更新
    private Handler handler;
    private ProgressBar progressbar;

    final int THREAD_NUM = 1;

    public DownloadTask(String filename,String downloadUrl,File saveDir,Context context){
        this.filename =filename;
        this.downloadUrl = downloadUrl;
        this.saveFile = saveDir;
        this.mContext = context;
        this.threadnum = THREAD_NUM;
    }

    public void setId_List(int id){this.id_List = id;}

    public void setId_Fini(int id){this.id_Fini = id;}

    public void setHandler(Handler handler){this.handler = handler;}

    public void setProgressbar(ProgressBar progressbar){this.progressbar = progressbar;}

    public void setisFinish(boolean isFinish){this.isFinish = isFinish;}

    public void setisExit(boolean isExit){this.isExit = isExit;}

    public void setDownsize(int size){this.downsize = size;}

    public int getId_List(){return id_List;}

    public int getId_Fini(){return id_Fini;}

    public String getFilename(){return filename;}

    public File getSavefile(){return saveFile;}

    public String getDownloadUrl(){return downloadUrl;}

    public int getFilesize(){return filesize;}

    public int getDownsize(){return downsize;}

    public boolean getisFinish(){return isFinish;}

    public boolean getisExit(){return isExit;}

    public void run() {
        try {
            Log.i("DownloadTask",filename+" thread"+Thread.currentThread().toString()+" run");
            //Thread.sleep(10000);
            loader = new FileDownloadered(mContext,filename,downloadUrl, saveFile, threadnum);
            if(loader.getFileSize()>0)
                progressbar.setMax(loader.getFileSize());//设置进度条的最大刻度
            else {
                progressbar.setMax(1);
            }
            loader.download(new com.example.crow.demoproject.download.DownloadProgressListener() {
                public void onDownloadSize(int size) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.getData().putInt("size", size);
                    msg.getData().putInt("id",id_List);
                    msg.getData().putInt("hasfinish",loader.getFinsih()?1:0);
                    if(loader.getFileSize()>0)
                        msg.getData().putInt("hasFileSize",1);
                    else
                        msg.getData().putInt("hasFileSize",0);
                    handler.sendMessage(msg);
                    if(getisExit())
                        loader.exit();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendMessage(handler.obtainMessage(-1));
        }
    }

//    public void exit(){
//        Log.i("DownloadTask",filename+" thread "+Thread.currentThread().toString()+"exit");
////        if(loader!=null) loader.exit();
//    }
}
