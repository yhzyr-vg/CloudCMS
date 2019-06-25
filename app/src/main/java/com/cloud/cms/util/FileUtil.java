package com.cloud.cms.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * File: FileUtil.java
 * Author: Landy
 * Create: 2018/12/19 15:39
 */
public class FileUtil {

    public static String getFileNameByUrl(String url){
        if(Validator.isNullOrEmpty(url)){
            return null;
        }
       return  url.substring(url.lastIndexOf("/") + 1);
    }
    /**
     * 读取文件
     *
     * @param response
     */
    public static void writeFile(Response response,String path,String fileName) {
        InputStream is = null;
        FileOutputStream fos = null;
        is = response.body().byteStream();
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path, fileName);
        try {
            fos = new FileOutputStream(file);
            byte[] bytes = new byte[2048];
            int len = 0;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            is.close();
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
