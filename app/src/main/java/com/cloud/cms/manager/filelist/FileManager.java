package com.cloud.cms.manager.filelist;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.command.product.ProductCommand;
import com.cloud.cms.constants.ProductConstants;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.ProgramManager;
import com.cloud.cms.manager.product.ProductManager;
import com.cloud.cms.util.Validator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File: FileManager.java
 * Author: Landy
 * Create: 2019/6/21 13:28
 */
public class FileManager {

    ProductManager productManager;
    private static final  String tag="FileManager";

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
            productManager=new ProductManager();
          List<ProductCommand> productCommandList=new ArrayList<ProductCommand>();
          List<ProductCommand> productCommandTempList=new ArrayList<ProductCommand>();
            File[] files = src.listFiles();
            for (File file : files) {
                  if (file.isFile()) {
                          String fileName=file.getName();
                          File destFile = new File(dest, getNewFileName(fileName));
                          if (copyFile(file, destFile)) {
                              ProductCommand productCommand=productManager.getProduct(fileName,destFile.getAbsolutePath());
                              if(Validator.isNotNullOrEmpty(productCommand)){
                                  if(ProductConstants.PRODUCT_RESPURCE_POSITION_TV_RESOURCE.equals(productCommand.getPosition())){
                                      productCommandList.add(productCommand); //TV 上显示的资源
                                  }else{
                                      productCommandTempList.add(productCommand);
                                  }
                              }
                          }else{
                              Log.e(tag, "copyDirectory error:" + file.getName());
                          }
                  } else if (file.isDirectory()) {
                          File destFile = new File(dest, file.getName());
                          if (!copyDirectory(file, destFile)) {
                              Log.e(tag, "copyDirectory error:" + file.getName());
                              return false;
                          }
                  }
            }
            productManager.saveProducts(productCommandList,productCommandTempList);
            return true;
      }

    public   String getNewFileName(String fileName){
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String	newFileName =df.format(new Date()) + "." + fileExt;
        return newFileName;
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
