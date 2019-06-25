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
    /**
     * 登陆的url
     */
    public static final String LOGIN_URL ;

    static{
        Properties properties=ProperUtil.getProperties("mainConfig.properties");
        LOGIN_URL =properties.getProperty("dms.login.url");
    }
}
