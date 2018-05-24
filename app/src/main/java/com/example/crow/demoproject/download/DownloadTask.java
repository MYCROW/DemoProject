package com.example.crow.demoproject.download;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

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
    private boolean isFinish = false;

    //进度条更新
    private Handler handler;
    private ProgressBar progressbar;


    public DownloadTask(String filename,String downloadUrl,File saveDir,Context context){
        this.filename =filename;
        this.downloadUrl = downloadUrl;
        this.saveFile = saveDir;
        this.mContext = context;
        this.threadnum =3;
    }

    public void setId_List(int id){this.id_List = id;}

    public void setId_Fini(int id){this.id_Fini = id;}

    public void setHandler(Handler handler){this.handler = handler;}

    public void setProgressbar(ProgressBar progressbar){this.progressbar = progressbar;}

    public void setisFinish(boolean isFinish){this.isFinish = isFinish;}

    public int getId_List(){return id_List;}

    public int getId_Fini(){return id_Fini;}

    public String getFilename(){return filename;}

    public File getSavefile(){return saveFile;}

    public String getDownloadUrl(){return downloadUrl;}

    public int getFilesize(){return filesize;}

    public boolean getisFinish(){return isFinish;}

    public void run() {
        try {
            loader = new FileDownloadered(mContext,filename,downloadUrl, saveFile, threadnum);
            progressbar.setMax(loader.getFileSize());//设置进度条的最大刻度
            loader.download(new com.example.crow.demoproject.download.DownloadProgressListener() {
                public void onDownloadSize(int size) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.getData().putInt("size", size);
                    msg.getData().putInt("id",id_List);
                    handler.sendMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendMessage(handler.obtainMessage(-1));
        }
    }

    public void exit(){
//        if(loader!=null) loader.exit();
    }
}
