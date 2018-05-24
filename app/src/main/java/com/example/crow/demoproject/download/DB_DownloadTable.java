package com.example.crow.demoproject.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class DB_DownloadTable extends SQLiteOpenHelper {
    //数据库：一张记录线程下载任务信息的表
    // 表表名:DOWNLOADLIST
    /*表名*/
    public static final String TABLE_NAME = "DOWNLOAD_LIST";
    /*字段*/
    //id 自动增长做主码
    //downloadid:下载任务的id
    //downpath:资源路径,
    //threadid:下载的线程id，
    //downlength:线程下载的最后位置？线程下载的文件长度？
    //isginish 是否完成
    public static final String VALUE_ID = "_id";
    public static final String VALUE_NAME = "filename";
    public static final String VALUE_PATH = "filepath";
    public static final String VALUE_THREADID = "thread_id";
    public static final String VALUE_LENGTH = "downlength";
    public static final String VALUE_ISFINISH = "isfinish";

    /*创建表语句 语句对大小写不敏感 create table 表名(字段名 类型，字段名 类型，…)*/
    //同一个下载任务VALUE_NAME和VALUE_PATH相同
    //不同的下载任务VALUE_NAME不同
    private final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "(" +
            VALUE_ID + " integer primary key autoincrement," +
            VALUE_NAME + " varchar(100) ," +
            VALUE_PATH + " varchar(200)," +
            VALUE_THREADID + " integer," +
            VALUE_LENGTH + " integer," +
            VALUE_ISFINISH + " integer default(0)" +
            ")";

    private final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;

    public DB_DownloadTable(Context context) {
        super(context, "master_db", null, 2);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //当版本号发生改变时调用该方法,这里删除数据表,在实际业务中一般是要进行数据备份的
        Log.i("DB","onUpdate");
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
