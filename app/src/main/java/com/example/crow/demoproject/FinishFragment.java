package com.example.crow.demoproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
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

import com.example.crow.demoproject.download.DownloadTask;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //FinishFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FinishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    View view;
    private Context mContext;
    private ArrayList<DownloadTask>  taskList;

    public static FinishFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FinishFragment pageFragment = new FinishFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        mContext = getActivity();
        id = new ID();
        taskList = new ArrayList<>();

        //强制实现接口
        try {
            mDowFinInterface = (DownloadDeleteInterface)getActivity();
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + "must implement onDownloadDelete");
        }
    }

    private LinearLayout finishList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish, container, false);
        this.view = view;
        finishList = (LinearLayout)view.findViewById(R.id.finishList);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        taskList.clear();
        super.onStop();
    }

    public void receiveTask(DownloadTask task){
        taskList.add(task);
        //更新UI
        ShowFinishTaskTask showFinishTaskTask = new ShowFinishTaskTask();
        showFinishTaskTask.execute();
    }
    public class ShowFinishTaskTask extends AsyncTask<String,Integer,ArrayList<DownloadTask>> {
        @Override
        protected  ArrayList<DownloadTask> doInBackground(String ...param){
            return taskList;
        }
        @Override
        protected void onPostExecute(ArrayList<DownloadTask> taskList){
            finishList.removeAllViews();
            id.clear();
            for(int i =0;i<taskList.size();i++)
                show(taskList.get(i));
        }
    }



    public void del_Task(int task_ID){
        TextView temp = (TextView)view.findViewById(task_ID*ID_offset+UI_offset.TEXT_VIEW);
        String filename = temp.getText().toString();
        for(int i =0;i<taskList.size();i++){
            if(taskList.get(i).getFilename().equals(filename)) {
                taskList.remove(i);
                break;
            }
        }
        LinearLayout taskLayout = (LinearLayout)view.findViewById(task_ID*ID_offset+UI_offset.TASK_ID);
        finishList.removeView(taskLayout);
        id.removeID(task_ID);
    }

    public void check_Task(int task_ID){

    }

    //ID regular
    //ID = ID_offset*idbase + UI_offset
    private int ID_offset = 4;
    public class UI_offset {
        public final static int CHECK_BTN = 0;
        public final static int DEL_BTN = 1;
        public final static int TEXT_VIEW = 2;
        public final static int TASK_ID =3;
    }

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
    public final String CHECK_SIGN = "查看细节";
    public final String DELETE_SIGN = "删除记录";

    /**把task显示在finishfragment中 动态布局**/
    public void show(DownloadTask task){
        //Log.i("show",task.getFilename());
        final int idbase = id.getID();
        task.setId_Fini(idbase);
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
        taskLayout.setId(idbase*ID_offset+ UI_offset.TASK_ID);
        //1.1 内层 RelativeLayout
        RelativeLayout controlLayout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams controlLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        controlLayout.setLayoutParams(controlLayoutParams);
        //显示文件名 TextView
        //1.1.1 TextView布局 靠左
        RelativeLayout.LayoutParams tvAddParam = new RelativeLayout.LayoutParams(
                600,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        tvAddParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        TextView filename_text = new TextView(mContext);
        filename_text.setText(filename);
        filename_text.setLayoutParams(tvAddParam);
        //TextView ID
        filename_text.setId(idbase*ID_offset+ UI_offset.TEXT_VIEW);

        //1.1.2查看信息按钮 Button按钮布局 靠右
        RelativeLayout.LayoutParams btnAddParam = new RelativeLayout.LayoutParams(
                240,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 靠右放置
        btnAddParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        final Button beg_pau_btn = new Button(mContext);
        beg_pau_btn.setText(CHECK_SIGN);
        beg_pau_btn.setLayoutParams(btnAddParam);
        //查看信息按钮ID
        beg_pau_btn.setId(idbase*ID_offset+ UI_offset.CHECK_BTN);

        //1.1.3删除按钮 Button按钮布局 查看信息的左边
        RelativeLayout.LayoutParams btnDelParam = new RelativeLayout.LayoutParams(
                240,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnDelParam.addRule(RelativeLayout.LEFT_OF, beg_pau_btn.getId());
        btnDelParam.addRule(RelativeLayout.RIGHT_OF, filename_text.getId());
        final Button del_btn = new Button(mContext);
        del_btn.setText(DELETE_SIGN);
        del_btn.setLayoutParams(btnDelParam);
        //删除按钮ID
        del_btn.setId(idbase*ID_offset+ UI_offset.DEL_BTN);

        final String savefile = task.getSavefile().toString();
        final int filesize = task.getFilesize();
        //查看信息按钮事件
        beg_pau_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("下载任务信息");
                TextView vv = new TextView(mContext);
                vv.setText("文件名："+filename+"\n"
                        +"文件路径："+savefile+"\n"
                        +"文件大小："+filesize+" bytes");
                builder.setView(vv);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(mContext, "完成查看" , Toast.LENGTH_SHORT).show();
                    }
                });
                final android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });
        //删除按钮事件
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //移除视图
                del_Task(idbase);
                //调用接口
                mDowFinInterface.onDownloadDelete(filename);
            }
        });

        controlLayout.addView(filename_text);
        controlLayout.addView(beg_pau_btn);
        controlLayout.addView(del_btn);
        taskLayout.addView(controlLayout,0);

        finishList.addView(taskLayout,-1);

    }
    //传递信息的接口
    private DownloadDeleteInterface mDowFinInterface;
    public interface DownloadDeleteInterface{
        public void onDownloadDelete(String filename);
    }
}

