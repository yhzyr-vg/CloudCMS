package com.cloud.cms.manager.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.constants.FileConstants;
import com.cloud.cms.manager.filelist.FileManager;
import com.cloud.cms.util.Validator;

/**
 * File: USBBroadcastReceiver.java
 * Author: Landy
 * Create: 2019/6/21 11:34
 */
public class USBBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = USBBroadcastReceiver.class.getSimpleName();
    FileManager fileManager=new FileManager();
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String path = intent.getData().getPath();
        if (Validator.isNotNullOrEmpty(path)) {
            if (ActionConstants.ACTION_MEDIA_REMOVED.equals(action)) {
                Log.e(TAG, "onReceive: ---------------usb拨出-------------");
            }
            if (ActionConstants.ACTION_MEDIA_MOUNTED.equals(action)) {//U盘插入  执行拷贝
                Log.e(TAG, "插入 u 盘 onReceive: --------usb路径-------"+ path);

                fileManager.copyDirectory(FileConstants.DEFAULT_USB_VIDEO_PATH,FileConstants.DEFAULT_TV_VIDEO_PATH);
            }
        }

    }
}
