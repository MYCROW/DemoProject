package com.example.crow.demoproject.download;

public interface DownloadProgressListener {
    public void onDownloadSize(int downloadedSize,int downloadSpeed);
}