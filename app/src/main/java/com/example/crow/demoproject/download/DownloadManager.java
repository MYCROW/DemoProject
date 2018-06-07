package com.example.crow.demoproject.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.crow.demoproject.Thread.DownloadTaskThreadPool;
import com.example.crow.demoproject.Thread.DownloadThread;

public class DownloadManager {
    private int MAX_NUM_DOWN = 3;//最大同时下载文件数
    private int MAX_NUM_THRE = 10;//最大线程数
    private Context context;
    private DB_DownloadOperator db_downloadOperator;//数据库操作
    private DownloadTaskThreadPool threadpool;//根据线程数设置下载的线程池
    private ArrayList<DownloadTask> taskList;//任务池
    private File saveDir;

    Context mContext;

    public DownloadManager(Context context) {
        db_downloadOperator = new DB_DownloadOperator(context);
        mContext = context;
        taskList = new ArrayList<>();
        threadpool = new DownloadTaskThreadPool(MAX_NUM_DOWN,MAX_NUM_THRE);
    }

    protected void finalize( )
    {
        threadpool.shutdown();
    }

    /**读取数据库获得所有下载任务（已完成/未完成）**/
    public ArrayList<DownloadTask> initTaskList(){
        if(saveDir == null)
            saveDir = Environment.getExternalStorageDirectory();
        taskList.clear();
        Map<String,String> data = db_downloadOperator.getAllFilename_unfinish();
        for(Map.Entry<String, String> entry:data.entrySet()){
            DownloadTask temp = new DownloadTask(entry.getKey(),entry.getValue(),saveDir,mContext);
            temp.setisFinish(false);
            taskList.add(temp);
        }
        data.clear();
        //set task process
        data = db_downloadOperator.getAllFilename_finish();
        for(Map.Entry<String, String> entry:data.entrySet()){
            DownloadTask temp = new DownloadTask(entry.getKey(),entry.getValue(),saveDir,mContext);
            temp.setisFinish(true);
            taskList.add(temp);
        }
        return taskList;
    }

    /**保存当前任务信息到数据库**/
    public void storeTaskList() {
        Map<String, String> data1 = db_downloadOperator.getAllFilename_finish();
        Map<String, String> data2 = db_downloadOperator.getAllFilename_unfinish();
        Log.i("DownloadManager", "store");
        for (int i = 0; i < taskList.size(); i++) {
            DownloadTask da = taskList.get(i);
            if (da.getisFinish() == false) {//未完成任务
                boolean flag = false;
                for (String key : data2.keySet()) {
                    String value = data2.get(key);
                    if (key == da.getFilename()) {
                        flag = true;
                        break;
                    }
                }
                if (flag == true)//数据库已存在当前任务信息
                    continue;
                else {
                    Map<Integer, Integer> t = new HashMap<Integer, Integer>();
                    t.put(0, 0);
                    t.put(1, da.getDownsize());
                    db_downloadOperator.setLength_Thread(da.getDownloadUrl(), da.getFilename(), t, "0");
                }

            }
            else {//已完成任务
                boolean flag = false;
                for (String key : data1.keySet()) {
                    String value = data1.get(key);
                    if (key == da.getFilename()) {
                        flag = true;
                        break;
                    }
                }
                if (flag == true)//数据库已存在当前任务信息
                    continue;
                else {
                    Map<Integer, Integer> t = new HashMap<Integer, Integer>();
                    t.put(0, 0);
                    t.put(1, da.getDownsize());
                    db_downloadOperator.setLength_Thread(da.getDownloadUrl(), da.getFilename(), t, "1");
                }
            }
        }
    }

    public DownloadTask getTaskbyFilename(String filename){
        for(int i=0;i<taskList.size();i++){
            if(filename.equals(taskList.get(i).getFilename()))
                return taskList.get(i);
        }
        Log.i("getTaskbyFilename","filename");
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
        //数据库操作在结束时同步
        return taskList.get(taskList.size()-1);
    }
    /**开启下载任务**/
    //downtask的线程管理
    public void startDownloadTask(DownloadTask task){
        if(task.getFilename()== null
                || task.getDownloadUrl() == null
                || task.getSavefile() == null){
            return;
        }
        /**to be finish**/
        threadpool.execute(task);
    }
    //挂起下载任务
    //public void

    /**根据文件名删除下载任务**/
    public void delDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        //本地文件下载 to be finish
        //task.delete();
        if(task !=null) {
            task.setisExit(true);
            threadpool.remove(task);
        }
        db_downloadOperator.delete(task.getDownloadUrl(),filename);
        for(int i =0;i<taskList.size();i++) {
            if(taskList.get(i).getFilename().equals(filename))
                taskList.remove(i);
        }
        //Log.i("DownloadManager","数据库中删除");
    }
    /**to be finished**/
    public void resDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        //if(task ==null)
        //to be finished
        task.setisExit(false);
        startDownloadTask(task);
    }
    /**to be finished**/
    public void pauDownloadTask(String filename){
        DownloadTask task = getTaskbyFilename(filename);
        //to be finished
        if(task !=null) {
            task.setisExit(true);
            threadpool.remove(task);
        }
    }

    public void setSaveDir(File saveDir){this.saveDir = saveDir;}

    public int getTaskSize(){
        return 0;
    }

    public int getTaskNum(){return taskList.size();}

    private void pause(){

    }

    private void exit(){

    }

}
