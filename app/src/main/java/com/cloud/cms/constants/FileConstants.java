package com.cloud.cms.constants;

import java.io.Serializable;

/**
 * File: FileConstants.java
 * Author: Landy
 * Create: 2019/6/10 14:51
 */
public class FileConstants {

    public static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    public static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    public static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String JS_CONTENT_TYPE = "application/javascript";
    public static final String PNG_CONTENT_TYPE = "application/x-png";
    public static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
    public static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
    public static final String SVG_CONTENT_TYPE = "image/svg+xml";
    public static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
    public static final String MP3_CONTENT_TYPE = "audio/mp3";
    public static final String MP4_CONTENT_TYPE = "video/mpeg4";
    public static final int AUDIO_FILE_DURATION_THRESHOLD_IN_SECOND = 600;
    public static final String MY_IMPORTED_AUDIO = "导入的音频";
    public static final String MY_IMPORTED_MUSIC = "导入的音乐";


    /**
     * 默认的文件上传路径
     */
    public static String  DEFAULT_DIRECTORY="/data/local/tmp/vg";

    /**默认的usb 路径*/
    public static String DEFAULT_USB_PATH = "/mnt/usb/sda1";

    /**默认的 usb 复制文件 路径*/
    public static String DEFAULT_USB_PRODUCT_PATH = DEFAULT_USB_PATH+"/commodity";

    /**默认的 tv 保存 复制 文件路径*/
    public static String DEFAULT_TV_PRODUCT_PATH = DEFAULT_DIRECTORY+"/commodity";
}
