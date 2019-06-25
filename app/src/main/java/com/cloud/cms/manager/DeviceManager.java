package com.cloud.cms.manager;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

/**
 * File: DeviceManager.java
 * Author: Landy
 * Create: 2019/1/3 16:53
 */
public class DeviceManager {

    /**
     * 获取内部总的存储空间
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return (totalBlocks * blockSize)/(1024*1024);
    }

    /**
     * 获取内部可用存储空间
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return (availableBlocks * blockSize)/(1024*1024);
    }

    /**
     * 获取随机的序列号
     * @param str
     * @return
     */
    public static String getSerialNumberRandomStr(String str) {
        Log.i("mac", "serialName=" + str);
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(str.getBytes(), 0, str.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {

            int b = (0xFF & p_md5Data[i]);
            // int b= p_md5Data[i]
            if (b <= 0xF)
                m_szUniqueID += "0";
            m_szUniqueID += Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase();

        return m_szUniqueID.substring(0,6);
    }

    /**
     * 获取设备地址
     * @param context
     * @return
     */
    public static String getMacId(Context context) {
        String m_szWLANMAC = getMac(context);

        String m_szLongID = m_szWLANMAC;
        Log.i("mac", "mac=" + m_szWLANMAC);
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = new String();
        for (int i = 0; i < p_md5Data.length; i++) {

            int b = (0xFF & p_md5Data[i]);
            // int b= p_md5Data[i]
            if (b <= 0xF)
                m_szUniqueID += "0";
            m_szUniqueID += Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }

    /**
     * 获取设备ip
     * @param context
     * @return
     */
    public static String getIP(Context context){

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address))
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex){
            ex.printStackTrace();
        }
        return null;
    }

    public static String getMac(Context context) {
        String macSerial = null;
        String str = "";
        InputStreamReader ir = null;
        LineNumberReader input;
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/eth0/address ");
            ir = new InputStreamReader(pp.getInputStream());
            input = new LineNumberReader(ir);

            for (; null != str;) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
            ir.close();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || macSerial.equals("")) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            macSerial = wm.getConnectionInfo().getMacAddress();
        }
        return macSerial;
    }

}
