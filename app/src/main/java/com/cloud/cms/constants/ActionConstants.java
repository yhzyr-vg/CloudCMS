package com.cloud.cms.constants;

import android.content.Intent;

/**
 * File: ActionConstants.java
 * Author: Landy
 * Create: 2019/6/10 15:58
 */
public class ActionConstants {
    /**
     * 发布成功action
     */
    public static final String ACTION_PUBLISH_TEMPLATE="com.cloud.cms.http.ACTION_PUBLISH_TEMPLATE";
    /**
     * 上传成功action
     */
    public static final String ACTION_UPLOADFILE_COMPLETE="com.cloud.cms.http.ACTION_UPLOADFILE_COMPLETE";

    /**
     * u盘 拔出
     */
    public static final String ACTION_MEDIA_REMOVED=Intent.ACTION_MEDIA_REMOVED;

    /**
     * u盘插入
     */
    public static final String ACTION_MEDIA_MOUNTED=Intent.ACTION_MEDIA_MOUNTED;
}
