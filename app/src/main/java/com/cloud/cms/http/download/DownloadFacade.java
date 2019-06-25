package com.cloud.cms.http.download;

import android.content.Context;

import com.cloud.cms.manager.FileManager;


public class DownloadFacade {
    private static final DownloadFacade sFacade = new DownloadFacade();

    private DownloadFacade() {
    }

    public static DownloadFacade getFacade() {
        return sFacade;
    }

    public void init(Context context) {
        FileManager.getInstance().init(context);
        //DaoManagerHelper.getManager().init(context);
    }

    public void startDownload(DownloadItem downloadItem, DownloadCallback callback) {
        DownloadDispatcher.getInstance().startDownload(downloadItem, callback);
    }

    public void startDownload(String url) {
      //  DownloadDispatcher.getInstance().startDownload(url);
    }
}
