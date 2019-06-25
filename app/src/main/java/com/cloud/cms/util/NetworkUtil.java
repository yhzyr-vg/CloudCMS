package com.cloud.cms.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
/**
 * 网络连接工具类
 * File: NetworkUtil.java
 * Author: Landy
 * Create: 2018/12/19 13:37
 */
public class NetworkUtil {
    /**
     * 网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr == null){
            return false;
        }
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null && info.length > 0) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
