package com.cloud.cms.http.download;

import android.os.Environment;
import android.util.Log;

import com.cloud.cms.config.Config;
import com.cloud.cms.http.OkHttpManage;
import com.cloud.cms.util.FileUtil;
import com.cloud.cms.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Description:
 * Data：4/19/2018-1:45 PM
 *
 */
public class DownloadRunnable implements Runnable {
    private static final String TAG = "DownloadRunnable";
    private static final int STATUS_DOWNLOADING = 1;
    private static final int STATUS_STOP = 2;
    //线程的状态
    private int mStatus = STATUS_DOWNLOADING;
    //文件下载的url
    private DownloadItem downloadItem;
    //线程id
    private int threadId;
    //每个线程下载开始的位置
    private long start;
    //每个线程下载结束的位置
    private long end;
    //每个线程的下载进度
    private long mProgress;
    //文件的总大小 content-length
    private long mCurrentLength;
    private DownloadCallback downloadCallback;
    private DownloadEntity mDownloadEntity;


    public DownloadRunnable(DownloadItem downloadItem, long currentLength, int threadId, long start, long end,
                            long progress, DownloadEntity downloadEntity, DownloadCallback downloadCallback) {
        this.downloadItem=downloadItem;
        this.mCurrentLength = currentLength;
        this.threadId = threadId;
        this.start = start;
        this.end = end;
        this.mProgress = progress;
        this.mDownloadEntity = downloadEntity;
        this.downloadCallback = downloadCallback;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            String name=downloadItem.getName();
            if(name==null){
                name=FileUtil.getFileNameByUrl(downloadItem.getUrl());
            }
            String path=downloadItem.getPath();
            if(path==null){
                path= Config.RESOURCE_INFO;
            }
            path= path+name;
            String[] command = {"chmod", "777", path};
            ProcessBuilder builder = new ProcessBuilder(command);
            try {
                builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Response response = OkHttpManage.getInstance().syncResponse(downloadItem.getUrl(), start, end);
            Log.i(TAG, "fileName=" + name + " 每个线程负责下载文件大小contentLength=" + response.body().contentLength()
                    + " 开始位置start=" + start + "结束位置end=" + end + " threadId=" + threadId+"  path:"+path);
            inputStream = response.body().byteStream();
            //保存文件的路径
            File file = new File(Config.RESOURCE_INFO, name);
            randomAccessFile = new RandomAccessFile(file, "rwd");
            //seek从哪里开始
            randomAccessFile.seek(start);
            int length;
            byte[] bytes = new byte[10 * 1024];
            while ((length = inputStream.read(bytes)) != -1) {
                if (mStatus == STATUS_STOP) {
                    downloadCallback.onPause(length, mCurrentLength);
                    break;
                }
                //写入
                randomAccessFile.write(bytes, 0, length);
                mProgress = mProgress + length;
                //实时去更新下进度条，将每次写入的length传出去
                downloadCallback.onProgress(length, mCurrentLength);
            }
            downloadCallback.onSuccess(file);
        } catch (IOException e) {
            e.printStackTrace();
            downloadCallback.onFailure(e);
        } finally {
            NetworkUtil.close(inputStream);
            NetworkUtil.close(randomAccessFile);
           // Log.i(TAG, "**************保存到数据库*******************");
            //保存到数据库
            mDownloadEntity.setProgress(mProgress);
            //DaoManagerHelper.getManager().addEntity(mDownloadEntity);

        }
    }

    public void stop() {
        mStatus = STATUS_STOP;
    }
}
