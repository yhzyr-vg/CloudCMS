package com.cloud.cms.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * File: DownloadService.java
 * Author: Landy
 * Create: 2019/3/11 12:05
 */
public class DownloadService extends Service {

    private DownloadItem DownloadItem = null;

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();//④返回Binder的事例
    }

    public class Binder extends android.os.Binder{
        public void setData(String data){//③写一个公共方法，用来对data数据赋值。
            DownloadService.this.data = data;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
