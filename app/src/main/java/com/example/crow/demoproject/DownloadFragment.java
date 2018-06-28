package com.example.crow.demoproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crow.demoproject.download.*;
import com.example.crow.demoproject.superview.DragListView;
import com.example.crow.demoproject.superview.DragListViewAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.crow.demoproject.download.DownloadTask.BASIC_PROGRESS;
import static com.example.crow.demoproject.download.DownloadTask.MAX_PROGRESS;

public class DownloadFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    View view;
    private Context mContext;

    private DownloadManager downloadManager;

    public static DownloadFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        DownloadFragment pageFragment = new DownloadFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mContext = getActivity();
        downloadManager = new DownloadManager(mContext);

        id = new ID();

        //强制实现接口
        try {
            mDowFinInterface = (DownloadFinishInterface)getActivity();
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "must implement onDownloadFinish");
        }

        try {
            mDowInitInterface = (DownloadInitInterface)getActivity();
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "must implement onDownloadInit");
        }
    }


    private DownloadInitInterface mDowInitInterface;
    public interface DownloadInitInterface{
        public void onDownloadInit(DownloadTask task);
    }
    @Override
    public void onStart() {
        super.onStart();
        //开启初始化任务列表线程
        InitTaskListTask initTaskList = new InitTaskListTask();
        initTaskList.execute();
    }
    public class InitTaskListTask extends AsyncTask<String,Integer,ArrayList<DownloadTask>>{
        @Override
        protected  ArrayList<DownloadTask> doInBackground(String ...param){
            ArrayList<DownloadTask> taskList = downloadManager.initTaskList();
            return taskList;
        }
        @Override
        protected void onPostExecute(ArrayList<DownloadTask> taskList){
            //清除原有视图
            baseLayout.removeAllViews();
            id.clear();
//            Log.i("TaskList",""+taskList.size());
            for (int i = 0; i < taskList.size(); i++) {
                //加载数据库 未完成任务显示
//                Log.i("TaskList",""+taskList.get(i).getisFinish());
                if(taskList.get(i).getisFinish()==false) {
                    show(taskList.get(i));
                }
                else{
                    //已完成任务让finish fragment显示
                    mDowInitInterface.onDownloadInit(taskList.get(i));
                }
            }
        }
    }


    private LinearLayout baseLayout;
    private DragListView baseList;
    private List<String> dataList = new ArrayList<>();//存储数据
    //private DragListViewAdapter listViewDemoAdapter;//DragListView(ListView)的数据适配器

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);
        baseLayout = view.findViewById(R.id.baseList);

        //实现拖拽的控件
        //listViewDemoAdapter = new DragListViewAdapter(mContext,dataList);
        baseList = new DragListView(mContext);
        //baseList.setAdapter(listViewDemoAdapter);
        baseList.setOnChangeListener(new DragListView.OnChanageListener() {
            @Override
            public void onChange(int form, int to) {
                baseList.getChildAt(form);
                baseList.getChildAt(to);
            }
        });
        baseLayout.addView(baseList);

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStop(){
        //保存未完成任务到数据库
        //应该记录下载任务的下载状态
        StoreTaskListTask storeTaskListTask = new StoreTaskListTask();
        storeTaskListTask.execute();
        super.onStop();
    }
    public class StoreTaskListTask extends AsyncTask<String,Integer,Void>{
        @Override
        protected  Void doInBackground(String ...param){
            downloadManager.storeTaskList();
            return null;
        }
        @Override
        protected void onPostExecute(Void a){
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    /**新增下载任务**/
    //默认不开始下载任务
    public void add_DownloadTask(Editable urlpath){
        File saveDir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            saveDir = Environment.getExternalStorageDirectory();
        }else{
            Toast.makeText(mContext, "sd卡读取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        DownloadTask task = downloadManager.addDownloadTask(urlpath.toString(),saveDir);
        if(task.getFilename()=="") {
            Toast.makeText(mContext, "不是有效下载地址" ,Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(mContext, "下载地址："+urlpath ,Toast.LENGTH_SHORT).show();
        downloadManager.setSaveDir(saveDir);//默认统一本地地址
        show(task);
    }
    /**直接删除下载任务**/
    //也删除已下载的文件？
    public void del_DownloadTask(int taskID){
        String filename;
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        filename = temp.getText().toString();
        downloadManager.delDownloadTask(filename);
        downloadManager.delDownloadFile(filename);
        LinearLayout baseList = (LinearLayout)view.findViewById(R.id.baseList);
        RelativeLayout taskLayout = (RelativeLayout)view.findViewById(taskID*ID_offset+UI_offset.TASK_ID);
        baseList.removeView(taskLayout);
        id.removeID(taskID);
    }

    /**把下载任务移动到已完成列表**/
    /**remove download task which has finished to finish fragment,without removing it from download manager**/
    public void rem_DownloadTask(int taskID){
        String filename;
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        filename = temp.getText().toString();
        LinearLayout baseList = (LinearLayout)view.findViewById(R.id.baseList);
        LinearLayout taskLayout = (LinearLayout)view.findViewById(taskID*ID_offset+UI_offset.TASK_ID);
        baseList.removeView(taskLayout);
        id.removeID(taskID);
    }

    /**Finish fragment删除已完成任务接口引起调用**/
    /**remove downloadtask from download manager **/
    public void del_DownloadTaskByfilename(String filename){
        //询问是否删除本地文件
        final String temp = filename;
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("是否同时删除下载文件？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                downloadManager.delDownloadFile(temp);
                downloadManager.delDownloadTask(temp);
                Toast.makeText(mContext, "删除下载文件和记录!" , Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("只删除下载记录",  new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                downloadManager.delDownloadTask(temp);
                Toast.makeText(mContext, "删除下载记录!" , Toast.LENGTH_SHORT).show();
            }
        });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    /**启动下载任务**/
    public void res_DownloadTask(int taskID){
        Button btn = (Button)view.findViewById(taskID*ID_offset+UI_offset.BEG_PAUSE_BTN);
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        String filename = temp.getText().toString();
        btn.setText(PAUSE_SIGN);
        if(!downloadManager.resDownloadTask(filename)) {
            btn.setText(RESUME_SIGN);
            Toast.makeText(mContext, "下载出错!" , Toast.LENGTH_SHORT).show();
        }
    }
    /**暂停下载任务**/
    public void pau_DownloadTask(int taskID){
        Button btn = (Button)view.findViewById(taskID*ID_offset+UI_offset.BEG_PAUSE_BTN);
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        String filename = temp.getText().toString();
        downloadManager.pauDownloadTask(filename);
        btn.setText(RESUME_SIGN);
    }

    /**下载完成：从下载中页面移除，加入完成页面**/
    //传递信息的接口
    private DownloadFinishInterface mDowFinInterface;
    public interface DownloadFinishInterface{
        void onDownloadFinish(DownloadTask task);
    }
    public void fin_DownloadTask(int taskID){
        String filename;
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        filename = temp.getText().toString();
        DownloadTask task = downloadManager.getTaskbyFilename(filename);
        task.setisFinish(true);
        //调用接口
        mDowFinInterface.onDownloadFinish(task);
        rem_DownloadTask(taskID);
    }

    /**监听进度条**/
    public final int PROCESSING = 1;
    public final int FAILURE = -1;//?
    private Handler handler = new UIHander();
    private final class UIHander extends Handler{
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //下载时
                case PROCESSING:
                    int id = msg.getData().getInt("id");
                    int size = msg.getData().getInt("size");     //从消息中获取已经下载的数据长度
                    int hasFinish = msg.getData().getInt("hasfinish");
                    int hasFileSize = msg.getData().getInt("hasFileSize");
                    ProgressBar progressbar = view.findViewById(id * ID_offset + UI_offset.PRO_BAR);
                    if(progressbar == null)//由于退出的原因而无法获得进度条
                        return;
                    //size代表下载文件大小 speed代表速度（当hasFileSize为0才考虑
                    if (hasFileSize == 1) progressbar.setProgress(size);//设置进度条的进度
                    else{
                        int speed =  msg.getData().getInt("speed");
                        size= progressbar.getProgress()+speed;
                        if(size >= MAX_PROGRESS-BASIC_PROGRESS)
                            size = MAX_PROGRESS-BASIC_PROGRESS;
                        progressbar.setProgress(size);
                    }
                    if (hasFinish == 1) { //下载完成时提示
                        int finishsize = hasFileSize==1?size:MAX_PROGRESS;
                        Toast.makeText(mContext, "文件下载成功", Toast.LENGTH_SHORT).show();
                        progressbar.setProgress(finishsize);
                        fin_DownloadTask(id);
                    }
                    break;
                case FAILURE:    //下载失败时提示
                    int id2 = msg.getData().getInt("id");
                    Toast.makeText(mContext, "文件下载失败", Toast.LENGTH_SHORT).show();
                    //删除已下载文件不删除任务
                    String filename;
                    TextView temp = view.findViewById(id2*ID_offset+UI_offset.TEXT_VIEW);
                    filename = temp.getText().toString();
                    ProgressBar progressbar2 = view.findViewById(id2 * ID_offset + UI_offset.PRO_BAR);
                    progressbar2.setProgress(0);
                    downloadManager.delDownloadFile(filename);
                    break;
            }
        }
    }


    //ID regular:
    //ID = ID_offset*idbase + UI_offset
    private int ID_offset = 5;
    public class UI_offset {
        public final static int BEG_PAUSE_BTN = 1;
        public final static int DEL_BTN = 2;
        public final static int TEXT_VIEW = 3;
        public final static int PRO_BAR = 4;
        public final static int TASK_ID =5;
    }

    public final String PAUSE_SIGN = "暂停";
    public final String RESUME_SIGN = "开始";
    public final String CANCEL_SIGN = "取消";

    private class ID{
        private ArrayList<Integer> id_pool;
        public ID(){
            id_pool = new ArrayList<Integer>();
        }
        public Integer getID(){
            Integer id;
            if(id_pool.contains(-1))
            {
                int d = id_pool.indexOf(-1);
                id_pool.remove(d);
                id_pool.add(d,d);
                id = d;
            }
            else {
                id = id_pool.size();
                id_pool.add(id_pool.size());
            }
            return id;
        }
        public void removeID(int ID){
            id_pool.set(ID,-1);
        }
        public void clear(){
            id_pool.clear();
        }
    }
    private ID id;

    private void show(DownloadTask task){
        int idbase = id.getID();
        task.setId_List(idbase);
        final String filename = task.getFilename();

        /****************************************************/
        //使用预设的任务布局 并获取其id
        LayoutInflater taskInflater = LayoutInflater.from(mContext);
        View taskView = taskInflater.inflate(R.layout.task_layout,null);
        LinearLayout taskLayout = taskView.findViewById(R.id.taskLayout);
        RelativeLayout controlLayout = taskView.findViewById(R.id.controlLayout);
        TextView filename_text = taskView.findViewById(R.id.textView2);
        final Button beg_pau_btn = taskView.findViewById(R.id.button);
        final Button del_btn = taskView.findViewById(R.id.button2);
        ProgressBar taskProgress = taskView.findViewById(R.id.progressBar);
        /****************************************************/

        //1.最外层 RelativeLayout
        //最外层 RelativeLayout ID
        taskLayout.setId(idbase*ID_offset+UI_offset.TASK_ID);

        //1.1 内层 RelativeLayout
//        RelativeLayout controlLayout = new RelativeLayout(mContext);
        //显示文件名 TextView
        //1.1.1 TextView 布局 靠左
        filename_text.setText(filename);
        //TextView ID
        filename_text.setId(idbase*ID_offset+UI_offset.TEXT_VIEW);

        //1.1.2开始暂停按钮 Button按钮布局 靠右
        //默认开始按钮
        beg_pau_btn.setText(RESUME_SIGN);
        //开始暂停按钮ID
        beg_pau_btn.setId(idbase*ID_offset+UI_offset.BEG_PAUSE_BTN);

        //1.1.3取消按钮 Button按钮布局 开始/暂停的左边
        del_btn.setText(CANCEL_SIGN);
        //取消按钮ID
        del_btn.setId(idbase*ID_offset+UI_offset.DEL_BTN);

        //修改ID后更改一下相对布局的参数
        RelativeLayout.LayoutParams btnDelParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDelParam.addRule(RelativeLayout.LEFT_OF, del_btn.getId());
        beg_pau_btn.setLayoutParams(btnDelParam);

        RelativeLayout.LayoutParams tvAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAddParam.addRule(RelativeLayout.LEFT_OF,beg_pau_btn.getId());
        tvAddParam.addRule(RelativeLayout.CENTER_VERTICAL);
        filename_text.setLayoutParams(tvAddParam);

        //开始暂停按钮事件
        beg_pau_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPause = false;
                //boolean isPause = task.isPause;
                String sign = beg_pau_btn.getText().toString();
                int taskID = (beg_pau_btn.getId()-UI_offset.BEG_PAUSE_BTN)/ID_offset;
                if(sign.equals(PAUSE_SIGN)) {
                    pau_DownloadTask(taskID);
                }
                else {
                    res_DownloadTask(taskID);
                    /**fot test**/
                    //del_DownloadTask(taskID);
                }
            }
        });
        //取消按钮事件
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认删除该下载任务？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        //del_btn.getId() = taskNum*ID_offset+UI_offset.DEL_BTN
                        int taskID = (del_btn.getId()-UI_offset.DEL_BTN)/ID_offset;
                        del_DownloadTask(taskID);
                        Toast.makeText(mContext, "删除下载任务!" , Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", null);
                final android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });
        //1.2内层 进度条
        taskProgress.setVisibility(View.VISIBLE);
        taskProgress.setMax(task.getFilesize());
        taskProgress.setProgress(task.getDownsize());
        //ProgressBar ID
        taskProgress.setId(idbase*ID_offset+UI_offset.PRO_BAR);

        baseLayout.addView(taskView);

        //进度条更新
        task.setHandler(handler);
        task.setProgressbar(taskProgress);
    }
}
