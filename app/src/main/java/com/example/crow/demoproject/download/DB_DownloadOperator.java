package com.example.crow.demoproject.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static com.example.crow.demoproject.download.DB_DownloadTable.*;

//声明数据库管理器
public class DB_DownloadOperator {
    private DB_DownloadTable db_downloadTable;

    public DB_DownloadOperator(Context context){
        db_downloadTable = new DB_DownloadTable(context);
//        SQLiteDatabase db = db_downloadTable.getReadableDatabase();
//        db.execSQL("DROP TABLE "+TABLE_NAME);
//        db.close();
    }

    /**获得未完成文件的文件名和下载路径**/
    public Map<String,String> getAllFilename_unfinish(){
        SQLiteDatabase db = db_downloadTable.getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+VALUE_NAME+  ", "+
                        VALUE_PATH + " from "+
                        TABLE_NAME + " where "  +
                        VALUE_ISFINISH + " = ? ",
                new String[]{"0"});
        Map<String,String> data = new HashMap<String,String>();
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            data.put(cursor.getString(0), cursor.getString(1));
            data.put(cursor.getString(cursor.getColumnIndexOrThrow(VALUE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(VALUE_PATH)));
        }
        return data;
    }

    public Map<String,String> getAllFilename_finish(){
        SQLiteDatabase db = db_downloadTable.getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+VALUE_NAME+  ", "+
                        VALUE_PATH + " from "+
                        TABLE_NAME + " where "  +
                        VALUE_ISFINISH + " = ? ",
                new String[]{"1"});
        Map<String,String> data = new HashMap<String,String>();
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            data.put(cursor.getString(0), cursor.getString(1));
            data.put(cursor.getString(cursor.getColumnIndexOrThrow(VALUE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(VALUE_PATH)));
        }
        return data;
    }


    /**获得指定任务的每条线程已经下载的文件长度**/
    //filename 唯一，其实只要传进filename就能表示表里对应项？
    //Map<thread_id,downlength>
    public Map<Integer,Integer> getLength_Thread(String path,String filename){
        SQLiteDatabase db = db_downloadTable.getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+VALUE_THREADID+ ", "+
                        VALUE_LENGTH+ " from "+
                        TABLE_NAME+" where "+
                        VALUE_PATH+ "=? and "+
                        VALUE_NAME+"=? ",
                new String[]{path,filename});
        Map<Integer,Integer> data = new HashMap<Integer, Integer>();
        cursor.moveToFirst();
        while(cursor.moveToNext())
        {
            //把线程id与该线程已下载的长度存放到data哈希表中
            data.put(cursor.getInt(0), cursor.getInt(1));
            data.put(cursor.getInt(cursor.getColumnIndexOrThrow(VALUE_THREADID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(VALUE_LENGTH)));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**保存指定任务的每条线程已下载的文件长度**/
    public void setLength_Thread(String path,String filename, Map<Integer,Integer> map,String isfinish){
        SQLiteDatabase db = db_downloadTable.getWritableDatabase();
        db.beginTransaction();
        try{
            //使用增强for循环遍历数据集合
            for(Map.Entry<Integer, Integer> entry : map.entrySet())
            {
                //插入特定文件名特定下载路径特定线程ID已经下载的数据
                db.execSQL("insert into "+TABLE_NAME+"("+VALUE_NAME+", "+VALUE_PATH+", "+
                                VALUE_THREADID+", "+VALUE_LENGTH+ ","+VALUE_ISFINISH+") values(?,?,?,?,?)",
                        new Object[]{filename, path, entry.getKey(), entry.getValue(),isfinish});
            }
            //设置一个事务成功的标志,如果成功就提交事务,如果没调用该方法的话那么事务回滚
            //就是上面的数据库操作撤销
            db.setTransactionSuccessful();
        }finally{
            //结束一个事务
            db.endTransaction();
        }
        db.close();
    }

    /**实时更新指定任务的某条线程已经下载的文件长度**/
    public void updateLength_Thread(String path,String filename,int threadId,int pos)
    {
        SQLiteDatabase db = db_downloadTable.getWritableDatabase();
        //更新特定下载路径下特定线程已下载的文件长度
        db.execSQL("update "+TABLE_NAME+" set "+VALUE_LENGTH+"=? where "+VALUE_PATH+
                        "=? and "+VALUE_THREADID+"=? and "+VALUE_NAME+"=?",
                new Object[]{pos, path, threadId, filename});
        db.close();
    }

    /**删除下载记录**/
    public void delete(String path,String filename)
    {
        SQLiteDatabase db = db_downloadTable.getWritableDatabase();
        db.execSQL("delete from "+TABLE_NAME+" where "+VALUE_PATH+
                "=? and "+VALUE_NAME+"=?", new Object[]{path,filename});
        db.close();
    }
}
