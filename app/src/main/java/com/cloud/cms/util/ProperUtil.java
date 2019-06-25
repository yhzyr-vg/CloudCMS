package com.cloud.cms.util;

import android.content.Context;

import com.cloud.cms.CloudCMSApplication;

import java.io.InputStream;
import java.util.Properties;

/**
 * 读取Properties 文件
 * File: ProperUtil.java
 * Author: Landy
 * Create: 2018/12/21 16:45
 */
public class ProperUtil {

    public static Properties getProperties(String fileName){
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            InputStream in = CloudCMSApplication.getInstance().getAssets().open(fileName);
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return props;
    }
}
