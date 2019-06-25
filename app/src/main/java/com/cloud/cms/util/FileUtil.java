package com.cloud.cms.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.Response;

/**
 * File: FileUtil.java
 * Author: Landy
 * Create: 2018/12/19 15:39
 */
public class FileUtil {

    /**
     * 文件是否存在
     * @param path
     * @return
     */
    public static boolean getIsExists(String path) {
        File file = new File(path);
        return file.exists();
    }
    /**
     * 创建文件
     * @param file
     * @return
     */
    public static boolean createFile(File file) {
        String filePath = file.getAbsolutePath();
        String[] filePathArr = filePath.split("/");
        String newFilePath = "";
        File f;
        for (String str : filePathArr) {
            newFilePath = newFilePath + "/" + str;
            f = new File(newFilePath);
            if (f.getAbsolutePath().equals(file.getAbsolutePath())) {
                if (f.exists()) {
                    if (f.isDirectory()) {
                        deleteAll(f);
                        try {
                            if (f.createNewFile()) {
                                return true;
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        if (f.delete()) {
                            try {
                                if (f.createNewFile()) {
                                    return true;
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                } else {
                    try {
                        if (f.createNewFile()) {
                            return true;
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } else {
                if (f.exists()) {
                    if (f.isDirectory()) {

                    } else {
                        if (f.delete()) {
                            if (file.getParentFile().mkdirs()) {
                                try {
                                    if (file.createNewFile()) {
                                        return true;
                                    }
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    if (file.getParentFile().mkdirs()) {
                        try {
                            if (file.createNewFile()) {
                                return true;
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 删除所有文件
     * @param file
     * @return
     */
    public static boolean deleteAll(File file) {
        if (file.exists()) {
            while (!file.delete()) {
                if (file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        if (!deleteAll(f)) {

                            return false;
                        }
                    }
                    try {
                        new Thread().sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } else {
            return true;
        }

    }

    /**
     * 删除文件以及子文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }


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

    public static float keepTwoBit(float value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Float.parseFloat(df.format(value));
    }
}
