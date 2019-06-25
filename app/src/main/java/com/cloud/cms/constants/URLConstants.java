package com.cloud.cms.constants;

import com.cloud.cms.config.Config;
import com.cloud.cms.util.ProperUtil;

import java.util.Properties;

/** url 的常量
 * File: URLConstants.java
 * Author: Landy
 * Create: 2018/12/13 17:09
 */
public class URLConstants {

    public static final String POST_METHOD="POST";
    public static final String GET_METHOD="GET";

    /** api domain */
    public static final String BASE_URL ;
    /**登陆的url*/
    public static final String LOGIN_URL ;
    /**注册的URL*/
    public static final String REGISTER_URL ;
    /**获取播放节目的url*/
    public static final String GET_SCHEDULE_URL ;
    /**在下载完成后上传下载进度的url*/
    public static final String DOWNLOAD_COMPLETED_URL ;

    static{
        Properties properties=ProperUtil.getProperties("mainConfig.properties");
        BASE_URL=properties.getProperty("dms.main.url");
        LOGIN_URL =properties.getProperty("dms.login.url");
        REGISTER_URL=properties.getProperty("dms.register.url");
        GET_SCHEDULE_URL=properties.getProperty("dms.get.chedule.url");
        DOWNLOAD_COMPLETED_URL=properties.getProperty("dms.download.completed.url");



    }
}
