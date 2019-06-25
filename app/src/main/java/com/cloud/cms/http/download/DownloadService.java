package com.cloud.cms.http.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * File: DownloadService.java
 * Author: Landy
 * Create: 2019/3/11 12:05
 */
public class DownloadService extends Service {

    private static final String TAG="DownloadService";
    private List<DownloadItem> downloadItemList = null;

    public  static final String ACTION_DOWNLOAND_SUCCESS="ACTION_DOWNLOAND_SUCCESS";
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();//④返回Binder的事例
    }

    public class Binder extends android.os.Binder{
        public void startDownload(List<DownloadItem> downloadItem){//③写一个公共方法，用来对数据赋值。

        }
    }

    private void downloadResource(List<DownloadItem> list){
        for(int i=0;i<list.size();i++){
            Log.i(TAG, "多线程下载====start:   " + list.get(i));
            final DownloadItem downloadItem=list.get(i);
            DownloadFacade.getFacade().startDownload(downloadItem, new DownloadCallback() {
                @Override
                public void onFailure(Exception e) {
                    Log.i(TAG, "onFailure: 多线程下载失败");
                }

                @Override
                public void onSuccess(File file) {
                    Log.i(TAG, "onSuccess:多线程下载成功 " + file.getAbsolutePath());
                    Intent intent=new Intent(ACTION_DOWNLOAND_SUCCESS);
                    intent.putExtra("path",file.getAbsolutePath());
                    intent.putExtra("type",downloadItem.getType());
                    sendBroadcast(intent);
                }

                @Override
                public void onProgress(final long totalProgress, final long currentLength) {
                    Log.i(TAG, "多线程下载====   " + FileUtil.keepTwoBit(( float ) totalProgress / currentLength));
                }

                @Override
                public void onPause(long progress, long currentLength) {
                    Log.i(TAG, "多线程下载==== 暂停=== ");
                }
            });

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String data = intent.getStringExtra("data");
        downloadItemList=JSON.parseArray(data,DownloadItem.class);
        if(downloadItemList!=null&&downloadItemList.size()>0){
            downloadResource(downloadItemList);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
