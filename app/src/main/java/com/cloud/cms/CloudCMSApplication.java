package com.cloud.cms;

import android.app.Application;
import android.content.Context;

import com.cloud.cms.config.Config;

import cn.trinea.android.common.util.PackageUtils;

/**
 * File: CloudCMSApplication.java
 * Author: Landy
 * Create: 2018/12/19 18:02
 */
public class CloudCMSApplication extends Application {

    //整个app的上下文
    public static Context context;

    private static CloudCMSApplication mInstance;

    public static CloudCMSApplication getInstance() {
        return mInstance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        init();
    }

    private void init(){
        Config.INSTALL_APK_PATH=getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        Config.PACKAGE_NAME=PackageUtils.getAppProcessName(context);

    }
}
