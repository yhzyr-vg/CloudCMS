package com.cloud.cms.manager.filelist;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File: FileManager.java
 * Author: Landy
 * Create: 2019/6/21 13:28
 */
public class FileManager {

    /**
     * 复制文件夹
     * @param srcPath 源文件path
     * @param destPath 目标文件 path
     * @return
     */
    public  boolean copyDirectory(String srcPath, String destPath){
        Log.i("=====copyDirectory", srcPath+" to " + destPath);
        File src=new File(srcPath);
        File dest=new File(destPath);
        return copyDirectory(src,dest);
    }

    /**
     * 复制文件夹
     * @param src
     * @param dest
     * @return
     */
      public  boolean copyDirectory(File src, File dest) {
            if (!src.isDirectory()) {
                  return false;
            }
            if (!dest.isDirectory() && !dest.mkdirs()) {
                  return false;
            }
            File[] files = src.listFiles();
            for (File file : files) {
                  File destFile = new File(dest, file.getName());
                  if (file.isFile()) {
                          if (!copyFile(file, destFile)) {
                                return false;
                          }
                  } else if (file.isDirectory()) {
                          if (!copyDirectory(file, destFile)) {
                                return false;
                          }
                  }
            }
            return true;
      }

      /**
        * 复制文件
        */
      public  boolean copyFile(File src, File des) {
            if (!src.exists()) {
                  Log.e("cppyFile", "file not exist:" + src.getAbsolutePath());
                  return false;
            }
            if (!des.getParentFile().isDirectory() && !des.getParentFile().mkdirs()) {
                  Log.e("cppyFile", "mkdir failed:" + des.getParent());
                  return false;
            }
          Log.i("=====cppyFile", src.getAbsolutePath()+" to " + des.getAbsolutePath());
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                  bis = new BufferedInputStream(new FileInputStream(src));
                  bos = new BufferedOutputStream(new FileOutputStream(des));
                  byte[] buffer = new byte[4 * 1024];
                  int count;
                  while ((count = bis.read(buffer, 0, buffer.length)) != -1) {
                          if (count > 0) {
                                bos.write(buffer, 0, count);
                          }
                  }
                  bos.flush();
                  return true;
            } catch (Exception e) {
                  Log.e("copyFile", "exception:", e);
            } finally {
                  if (bis != null) {
                          try {
                                bis.close();
                          } catch (IOException e) {
                                e.printStackTrace();
                          }
                  }
                  if (bos != null) {
                          try {
                                bos.close();
                          } catch (IOException e) {
                                e.printStackTrace();
                          }
                  }
            }
            return false;
      }
}
