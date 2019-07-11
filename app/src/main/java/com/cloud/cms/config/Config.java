package com.cloud.cms.config;

public class Config {
    /**
     * 设备序列号
     */
    public static String SERIAL_NUMBER = "test";//android.os.Build.SERIAL;

    /**设备名=USER_GROUP_NAME+SERIAL_NUMBER  MD5 加密取六位*/
    public static String DEVICE_NAME;

    /**设备所属用户组*/
    public static String USER_GROUP_NAME="";

    /**
     * URL
     */
    public static String BASE_URL ="http://signup.victgroup.com/tvdms2/";

    /**
     * 下载待安装apk 存放的路径
     */
    public static String INSTALL_APK_PATH;

    /**
     * 项目包名
     */
    public static String PACKAGE_NAME;

    /**屏幕宽度*/
    public static int SCREEN_WIDTH;
    /**屏幕高度*/
    public static int SCREEN_HEIGHT;

    /**资源文件路径*/
    public static String RESOURCE_INFO;
    /**下载路径*/
    public static String DIR_CACHE;

    /**截图路径*/
    public static String DIR_SCREENSHOT;

    /**
     * 根目录
     */
    public static String DIR_NAME;

    /**设备IP*/
    public static String IP;

    /** 设备型号*/
    public static String DEVICE_MODEL=android.os.Build.MODEL.replaceAll(" ", "").trim();

    /**
     * 服务端口号
     */
    public static final int SERVER_PORT=13521;

    public static final String ORIENTATION_ACTION = "victgroup.action.orientation";
}
