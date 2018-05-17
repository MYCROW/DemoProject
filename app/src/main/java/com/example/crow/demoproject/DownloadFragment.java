package com.example.crow.demoproject;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import static android.content.ContentValues.TAG;

public class DownloadFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private LinearLayout baseLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_download, container, false);
        baseLayout = (LinearLayout)view.findViewById(R.id.baseList);
//        TextView a = view.findViewById(R.id.textView3);
//        Log.i(TAG,a.getText().toString());
        return view;
    }

    public void add_DownloadTask(Editable urlpath){
        Toast.makeText(mContext, "下载地址："+urlpath ,Toast.LENGTH_SHORT).show();
        DownloadTask task = downloadManager.addDownloadTask(urlpath.toString());
        show(task);
        //downloadManager.startDownloadTask
    }

    //ID regular
    //ID = ID_offset*taskNum + UI_offset
    private int ID_offset = 3;
    public class UI_offset {
        public final static int BEG_PAUSE_BTN = 0;
        public final static int DEL_BTN = 1;
        public final static int TEXT_VIEW = 2;
        public final static int PRO_BAR = 3;
    }
    /**显示一个下载任务在“下载中”列表**/
    private void show(DownloadTask task){
        String filename = "test.rar";
        //String filename = task.filename;
        int taskNum = downloadManager.getTaskNum();

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
        //1.1 内层 RelativeLayout
        RelativeLayout controlLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams controlLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
        filename_text.setId(taskNum*ID_offset+UI_offset.TEXT_VIEW);

        //1.1.2开始暂停按钮 Button按钮布局 靠右
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 靠右放置
        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final Button beg_pau_btn = new Button(mContext);
        beg_pau_btn.setText("暂停");
        beg_pau_btn.setLayoutParams(btnAddParam);
        //开始暂停按钮ID
        beg_pau_btn.setId(taskNum*ID_offset+UI_offset.BEG_PAUSE_BTN);

        //1.1.3取消按钮 Button按钮布局 开始/暂停的左边
        RelativeLayout.LayoutParams btnDelParam = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDelParam.addRule(RelativeLayout.LEFT_OF, beg_pau_btn.getId());
        btnDelParam.addRule(RelativeLayout.RIGHT_OF, filename_text.getId());
        final Button del_btn = new Button(mContext);
        del_btn.setText("取消");
        del_btn.setLayoutParams(btnDelParam);
        //取消按钮ID
        del_btn.setId(taskNum*ID_offset+UI_offset.DEL_BTN);


        //开始暂停按钮事件
        beg_pau_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPause = false;
                //boolean isPause = task.isPause;
                if(isPause)
                    Log.i(TAG,""+beg_pau_btn.getId());
                    //downloadManager.resumDownload(task);
                else
                    Log.i(TAG,""+beg_pau_btn.getId());
                    //downloadManager.pauseDownload(task);
            }
        });
        //取消按钮事件
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,""+del_btn.getId());
                //downloadManager.cancelDownload(task);
            }
        });

        controlLayout.addView(filename_text);
        controlLayout.addView(beg_pau_btn);
        controlLayout.addView(del_btn);
        taskLayout.addView(controlLayout,0);
        //1.2内层 进度条
        ProgressBar taskProgress = new ProgressBar(mContext,null,android.R.attr.progressBarStyleHorizontal);
        taskProgress.setVisibility(View.VISIBLE);
        taskProgress.setId(taskNum*ID_offset+UI_offset.PRO_BAR);
        taskLayout.addView(taskProgress,1);

        baseLayout.addView(taskLayout,-1);
    }
}
