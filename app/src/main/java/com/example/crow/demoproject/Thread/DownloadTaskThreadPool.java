package com.example.crow.demoproject.Thread;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DownloadTaskThreadPool extends ThreadPoolExecutor {
    private int corePoolSize;//核心线程数
    private int maxPoolSize;//最大线程数
    private long keepAliveTime = 10;
    private TimeUnit unit = TimeUnit.SECONDS;
    //private BlockingQueue<Runnable> workQueue;

    public DownloadTaskThreadPool(int corePoolSize,int maxPoolSize){
//        this.workQueue = new ArrayBlockingQueue<Runnable>(maxPoolSize);
        super(corePoolSize,maxPoolSize,10,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(maxPoolSize),sThreadFactory);
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }

    @Override
    public void execute(Runnable runnable) {
//        try {
//            super.execute(runnable);
//            isSuccess = true;
//        }
//        catch(Exception e){
//            isSuccess = false;
//            Log.i("ThreadPool excute",e.toString());
//        }
        super.execute(runnable);
    }

    //private final AtomicInteger mCount = new AtomicInteger(1);
    /**线程工厂**/
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "download#" + mCount.getAndIncrement());
        }
    };
}
