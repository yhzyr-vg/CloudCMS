package com.cloud.cms.http;

import android.app.Activity;
import android.util.Log;
import android.widget.ProgressBar;

import com.cloud.cms.http.intercept.ProgressListener;
import com.cloud.cms.http.intercept.ProgressResponsBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * File: OKHttpDownload.java
 * Author: Landy
 * Create: 2018/12/19 15:25
 */
public class OKHttpDownload {

    //下载显示的进度条
    private ProgressBar progressBar;

    //下载文件保存路径
    private String path;

    private Activity activity;

    public OKHttpDownload(Activity activity,ProgressBar progressBar,String path){
        this.progressBar=progressBar;
        this.path=path;
        this.activity=activity;
    }




}
