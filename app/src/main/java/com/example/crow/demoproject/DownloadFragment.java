package com.example.crow.demoproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.crow.demoproject.download.*;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DownloadFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    View view;
    private Context mContext;

    private DownloadManager downloadManager;

    public final int PROCESSING = 1;
    public final int FAILURE = 0;//?

    private Handler handler = new UIHander();
    private final class UIHander extends Handler{
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //下载时
                case PROCESSING:
                    int id = msg.getData().getInt("id");
                    int size = msg.getData().getInt("size");     //从消息中获取已经下载的数据长度
                    ProgressBar progressbar = view.findViewById(id*ID_offset+UI_offset.PRO_BAR);
                    progressbar.setProgress(size);         //设置进度条的进度
                    if(progressbar.getProgress() == progressbar.getMax()){ //下载完成时提示
                        Toast.makeText(mContext, "文件下载成功", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case FAILURE:    //下载失败时提示
                    Toast.makeText(mContext, "文件下载失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private LinearLayout baseLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);
        baseLayout = (LinearLayout)view.findViewById(R.id.baseList);
        return view;
    }

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
        show(task);
        downloadManager.startDownloadTask(task);
    }

    public void del_DownloadTask(int taskID){
        String filename;
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        filename = temp.getText().toString();
        downloadManager.delDownloadTask(filename);
        LinearLayout baseList = (LinearLayout)view.findViewById(R.id.baseList);
        LinearLayout taskLayout = (LinearLayout)view.findViewById(taskID*ID_offset+UI_offset.TASK_ID);
        baseList.removeView(taskLayout);
        id.removeID(taskID);
    }

    public void res_DownloadTask(int taskID){
        Button btn = (Button)view.findViewById(taskID*ID_offset+UI_offset.BEG_PAUSE_BTN);
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        String filename = temp.getText().toString();
        downloadManager.resDownloadTask(filename);
        btn.setText(RESUME_SIGN);
    }

    public void pau_DownloadTask(int taskID){
        Button btn = (Button)view.findViewById(taskID*ID_offset+UI_offset.BEG_PAUSE_BTN);
        TextView temp = (TextView)view.findViewById(taskID*ID_offset+UI_offset.TEXT_VIEW);
        String filename = temp.getText().toString();
        downloadManager.pauDownloadTask(filename);
        btn.setText(PAUSE_SIGN);
    }
    //ID regular
    //ID = ID_offset*idbase + UI_offset
    private int ID_offset = 5;
    public class UI_offset {
        public final static int BEG_PAUSE_BTN = 0;
        public final static int DEL_BTN = 1;
        public final static int TEXT_VIEW = 2;
        public final static int PRO_BAR = 3;
        public final static int TASK_ID =4;
    }

    public final String PAUSE_SIGN = "暂停";
    public final String RESUME_SIGN = "开始";
    public final String CANCEL_SIGN = "取消";
    /**显示一个下载任务在“下载中”列表**/
    int index = 0;

    public class ID{
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
    }
    ID id;

    private void show(DownloadTask task){
        //int taskNum = downloadManager.getTaskNum();
        int idbase = id.getID();
        //final String filename = "test"+(idbase)+".rar";
        final String filename = task.getFilename();
        // 显示DownloadTask的控件并添加到自定义布局中
        //1.最外层LinearLayout
        LinearLayout taskLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams taskLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        taskLayout.setLayoutParams(taskLayoutParams);
        //透明背景
        taskLayout.setBackgroundColor(Color.argb(0, 255, 255, 0));
        taskLayout.setOrientation(LinearLayout.VERTICAL);
        //最外层LinearLayout ID
        taskLayout.setId(idbase*ID_offset+UI_offset.TASK_ID);
        //1.1 内层 RelativeLayout
        RelativeLayout controlLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams controlLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //controlLayout.setPadding(0, (int) (fDimRatio * 5), 0, 0);
        controlLayout.setLayoutParams(controlLayoutParams);

        //显示文件名 TextView
        //1.1.1 TextView布局 靠左
        RelativeLayout.LayoutParams tvAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAddParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        TextView filename_text = new TextView(mContext);
        filename_text.setText(filename);
        filename_text.setLayoutParams(tvAddParam);
        //TextView ID
        filename_text.setId(idbase*ID_offset+UI_offset.TEXT_VIEW);

        //1.1.2开始暂停按钮 Button按钮布局 靠右
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 靠右放置
        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //btnAddParam.addRule(RelativeLayout.RIGHT_OF, filename_text.getId());
        final Button beg_pau_btn = new Button(mContext);
        beg_pau_btn.setText(PAUSE_SIGN);
        beg_pau_btn.setLayoutParams(btnAddParam);
        //开始暂停按钮ID
        beg_pau_btn.setId(idbase*ID_offset+UI_offset.BEG_PAUSE_BTN);

        //1.1.3取消按钮 Button按钮布局 开始/暂停的左边
        RelativeLayout.LayoutParams btnDelParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDelParam.addRule(RelativeLayout.LEFT_OF, beg_pau_btn.getId());
        btnDelParam.addRule(RelativeLayout.RIGHT_OF, filename_text.getId());
        final Button del_btn = new Button(mContext);
        del_btn.setText(CANCEL_SIGN);
        del_btn.setLayoutParams(btnDelParam);
        //取消按钮ID
        del_btn.setId(idbase*ID_offset+UI_offset.DEL_BTN);


        //开始暂停按钮事件
        beg_pau_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPause = false;
                //boolean isPause = task.isPause;
                String sign = beg_pau_btn.getText().toString();
                int taskID = (beg_pau_btn.getId()-UI_offset.BEG_PAUSE_BTN)/ID_offset;
                if(sign.equals(PAUSE_SIGN)) {
                    res_DownloadTask(taskID);
                }
                else {
                    pau_DownloadTask(taskID);
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

        controlLayout.addView(filename_text);
        controlLayout.addView(beg_pau_btn);
        controlLayout.addView(del_btn);
        taskLayout.addView(controlLayout,0);
        //1.2内层 进度条
        ProgressBar taskProgress = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
        taskProgress.setVisibility(View.VISIBLE);
        taskProgress.setId(idbase*ID_offset+UI_offset.PRO_BAR);
        taskLayout.addView(taskProgress,1);

        baseLayout.addView(taskLayout,-1);

        task.setId_List(idbase);
        task.setHandler(handler);
        task.setProgressbar(taskProgress);
    }
}
