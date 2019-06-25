package com.cloud.cms;

import android.app.Application;
import android.content.Context;

import com.cloud.cms.config.Config;
import com.cloud.cms.manager.DeviceManager;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.sql.DatabaseHelper;
import com.cloud.cms.util.ScreenUtil;
import com.cloud.cms.util.Validator;

import java.io.File;

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
        mInstance=this;
        init();
    }

    private void init(){
        PreferenceManager.init(context);
        DatabaseHelper.init(context);
        if (Validator.isNullOrEmpty(Config.SERIAL_NUMBER) || "unknown".equals(Config.SERIAL_NUMBER)) {
            Config.SERIAL_NUMBER = "mac" + DeviceManager.getMacId(context);
        }
        if (Validator.isNullOrEmpty(Config.DEVICE_NAME)) {
            if (Config.USER_GROUP_NAME != null && !"".equals(Config.USER_GROUP_NAME)) {
                Config.DEVICE_NAME = Config.USER_GROUP_NAME + "_" + DeviceManager.getSerialNumberRandomStr(Config.SERIAL_NUMBER);
            }
        }
        Config.IP = DeviceManager.getIP(context);
        Config.DIR_NAME = context.getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        Config.INSTALL_APK_PATH=getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        Config.DIR_CACHE=getDir("dms_download",Context.MODE_PRIVATE ).getPath()+File.separator;
        Config.RESOURCE_INFO=getDir("dms_resource",Context.MODE_PRIVATE).getPath()+File.separator;
        Config.DIR_SCREENSHOT=getDir("screenshot",Context.MODE_PRIVATE).getPath()+File.separator;

        Config.PACKAGE_NAME=PackageUtils.getAppProcessName(context);
        Config.SCREEN_HEIGHT = ScreenUtil.getScreenHeight(context);
        Config.SCREEN_WIDTH = ScreenUtil.getScreenWidth(context);

        if(Config.SCREEN_WIDTH>Config.SCREEN_HEIGHT) {
            PreferenceManager.getInstance().setOrientation(0);//横屏
        }else if(Config.SCREEN_WIDTH<Config.SCREEN_HEIGHT){
            PreferenceManager.getInstance().setOrientation(1);//竖屏
        }
    }
}
