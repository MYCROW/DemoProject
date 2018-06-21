package com.example.crow.demoproject.superview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.crow.demoproject.R;

import java.util.List;

public class DragListViewAdapter extends BaseAdapter {
    private Context context;//上下文对象
    private List<String> dataList;//ListView显示的数据

    /**
     * 构造器
     *
     * @param context  上下文对象
     * @param dataList 数据
     */
    public DragListViewAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecyclerView.ViewHolder viewHolder;
        //判断是否有缓存
//        if (convertView == null) {
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_listview_demo, null);
//            viewHolder = new RecyclerView.ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            //得到缓存的布局
//            viewHolder = (RecyclerView.ViewHolder) convertView.getTag();
//        }
        View e = new View(context);
        return e;
    }
}