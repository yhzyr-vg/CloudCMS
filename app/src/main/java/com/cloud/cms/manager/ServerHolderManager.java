package com.cloud.cms.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.CloudCMSApplication;
import com.cloud.cms.command.FileUploadHolder;
import com.cloud.cms.command.ResultCommand;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.constants.FileConstants;
import com.cloud.cms.util.Validator;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerRequestImpl;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.cloud.cms.activity.DownloadResource.TAG;

/**
 * File: ServerHolderManager.java
 * Author: Landy
 * Create: 2019/6/13 16:31
 */
public class ServerHolderManager {

    private static String tag="ServerHolderManager";


    /**
     * 获取参数
     * @param request
     * @param response
     * @return
     */
    public String getRequestParms(AsyncHttpServerRequest request,AsyncHttpServerResponse response){
        Object params=null;
        if (request.getMethod().equals("GET")){
            params=request.getQuery();
        }else if (request.getMethod().equals("POST")&&Validator.isNotNullOrEmpty(request.getBody())){
            String contentType=request.getHeaders().get("Content-Type");
            if ("application/json".equals(contentType)){
                params=((AsyncHttpRequestBody<JSONObject>)request.getBody()).get();
            }else{
                params=((AsyncHttpRequestBody<Multimap>)request.getBody()).get();
            }
        }
        String paramstr="";
        if (params!=null){
            Log.d(tag,"params ="+params.toString());
            paramstr=params.toString().replaceAll("=",":")
                    .replaceAll("\\[","\"")
                    .replaceAll("\\]","\"");
        }
        Log.i(tag,"======================"+paramstr);
        return paramstr;
    }

    /**
     * 首页
     * @param context
     * @return
     * @throws IOException
     */
    public String getIndexContent(Context context) throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream= new BufferedInputStream(CloudCMSApplication.context.getAssets().open("index.html"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len = 0;
            byte[] tmp = new byte[10240];
            while ((len = bInputStream.read(tmp)) > 0) {
                baos.write(tmp, 0, len);
            }
            return new String(baos.toByteArray(), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (bInputStream != null) {
                try {
                    bInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 处理上传文件
     * @param request
     * @param response
     * @param context
     * @param storagePath 默认的上传路径
     */
    public void uploadFile(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response,final Context context,final String storagePath ){
        final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
        final FileUploadHolder fileUploadHolder = new FileUploadHolder();
        body.setMultipartCallback(new MultipartFormDataBody.MultipartCallback() {
            @Override
            public void onPart(final Part part) {
                if (part.isFile()){
                    String fileName=getNewFileName(part.getFilename());
                    //Log.i(tag,"======newFileName:"+fileName+"  part.isFile():"+part.isFile());
                    if (TextUtils.isEmpty(fileUploadHolder.getFileName()) && !TextUtils.isEmpty(fileName)) {
                        generateReceivedFileAndFileStream(fileUploadHolder, fileName,storagePath);
                    }
                    body.setDataCallback(new DataCallback() {
                        @Override
                        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                            if (fileUploadHolder.getFileOutPutStream() != null)
                            //已经开始传输文件
                            {
                                try {
                                    fileUploadHolder.getFileOutPutStream().write(bb.getAllByteArray());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                bb.recycle();
                            }
                        }
                    });
                }  else {
                    Log.i(tag,"======nnnnnnnnnnnnnn:"+part.getName()+"  part.isFile():"+part.isFile());
                    if (body.getDataCallback() == null) {
                        body.setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                String fileName=getFileNameFromHttpByteBufferList(bb);
                                if (TextUtils.isEmpty(fileUploadHolder.getFileName()) && !TextUtils.isEmpty(fileName)) {
                                    Log.i(tag,"======xxxx:"+fileName);
                                }
                            }
                        });
                    }
                }
            }
        });
        request.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                ResultCommand resultCommand=new ResultCommand();
                if (fileUploadHolder.getFileOutPutStream() != null) {
                    try {
                        fileUploadHolder.getFileOutPutStream().close();
                        if (fileUploadHolder.getRecievedFile() != null && fileUploadHolder.getRecievedFile().exists()) {
                            String filePath = fileUploadHolder.getRecievedFile().getPath();
                            Log.i(tag,"=======filePath "+filePath);
                            Intent intent=new Intent(ActionConstants.ACTION_UPLOADFILE_COMPLETE);
                            intent.putExtra("filePath",filePath);
                            context.sendBroadcast(intent);
                            resultCommand.setResult(true);
                            resultCommand.setMessage(filePath);
                            if(filePath.endsWith(".mp4")||filePath.endsWith(".mov")){
                                resultCommand.setReturnObject("/images/player.png");//如果是视频，设置默认的图片
                            }
                            response.send(JSON.toJSONString(resultCommand));
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                resultCommand.setResult(false);
                response.send(JSON.toJSONString(resultCommand));
            }
        });
    }

    /**
     * 获取存储文件夹下的文件
     */
    public void getStorageResource(AsyncHttpServerRequest request, AsyncHttpServerResponse response){
        Log.d(TAG, request.getPath());
        try{
            String resourceName=getResourcePath(request,response);
            File file = new File(resourceName);
            FileInputStream ex = new FileInputStream(file);
            response.sendStream(ex, ex.available());
        } catch (IOException e){
            e.printStackTrace();
            return;
        }
    }

    /**
     * 返回 assets 目录下资源文件
     * @param request
     * @param response
     */
    public void getAssetsResources(AsyncHttpServerRequest request, AsyncHttpServerResponse response,Context context){
        try {
            String resourceName=getResourcePath(request,response);
            BufferedInputStream bInputStream = new BufferedInputStream(context.getAssets().open(resourceName));
            response.sendStream(bInputStream, bInputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
            response.code(404).end();
        }
    }

    private String getResourcePath(AsyncHttpServerRequest request, AsyncHttpServerResponse response){
        String resourceName = request.getPath().replace("%20", " ");
        if (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }
        if (resourceName.indexOf("?") > 0) {
            resourceName = resourceName.substring(0, resourceName.indexOf("?"));
        }
        Log.i(tag,"============getResource:"+resourceName);
        if(!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
            response.setContentType(getContentTypeByResourceName(resourceName));
        }
        return resourceName;
    }

    /**
     * 生成文件名
     * @param bb
     * @return
     */
    private String getFileNameFromHttpByteBufferList(ByteBufferList bb) {
        String s = bb.readString();
        // Log.i(tag,"========getFileNameFromHttpByteBufferList:"+s);
        return s;
    }

    /**
     * 生成文件名
     * @param fileName
     * @return
     */
    public   String getNewFileName(String fileName){
       // Log.i(tag,"=======getNewFileName fileName"+fileName);
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String	newFileName =fileExt+df.format(new Date()) + "." + fileExt;
        return newFileName;
    }

    /**
     * 生成文件
     * @param fileUploadHolder
     * @param fileName
     * @param defaultDirectory
     */
    public void generateReceivedFileAndFileStream(FileUploadHolder fileUploadHolder, String fileName,String defaultDirectory) {
        //取到文件名，生成目标文件和路径
        fileUploadHolder.setFileName(fileName);
        File exStorage = Environment.getExternalStorageDirectory();
        StringBuilder dir = new StringBuilder();
        dir.append(defaultDirectory);
        if (!dir.toString().contains(exStorage.getPath()))
            dir.insert(0, exStorage.getPath());
        File dirFile;
        dirFile = new File(dir.toString()); // convert spaces appropriately
        if (!dirFile.exists() || !dirFile.isDirectory()) // catch issues in the directory path
        {
            dir.replace(0, dir.length(), defaultDirectory); // replace it with defaultDirectory if invalid
            dirFile = new File(dir.toString());
        }
        File recievedFile = new File(dirFile, fileUploadHolder.getFileName());
        final String file_path=recievedFile.getAbsolutePath();
        try {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    String[] command = {"chmod", "777", file_path};
                    ProcessBuilder builder = new ProcessBuilder(command);
                    try {
                        builder.start();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e){

        }
        fileUploadHolder.setRecievedFile(recievedFile);
        BufferedOutputStream fs = null;
        try {
            fs = new BufferedOutputStream(new FileOutputStream(recievedFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileUploadHolder.setFileOutPutStream(fs);
    }

    /**
     * 根据资源名称获取类型
     * @param resourceName
     * @return
     */
    public String getContentTypeByResourceName(String resourceName){
        if(resourceName.endsWith(".css")){
            return FileConstants.CSS_CONTENT_TYPE;
        }else if(resourceName.endsWith(".js")){
            return FileConstants.JS_CONTENT_TYPE;
        }else if(resourceName.endsWith(".swf")){
            return FileConstants.BINARY_CONTENT_TYPE;
        }else if(resourceName.endsWith(".png")){
            return FileConstants.PNG_CONTENT_TYPE;
        }else if(resourceName.endsWith(".woff")){
            return FileConstants.WOFF_CONTENT_TYPE;
        }else if(resourceName.endsWith(".ttf")){
            return FileConstants.TTF_CONTENT_TYPE;
        }else if(resourceName.endsWith(".svg")){
            return FileConstants.SVG_CONTENT_TYPE;
        }else if(resourceName.endsWith(".eot")) {
            return FileConstants.EOT_CONTENT_TYPE;
        }else if(resourceName.endsWith(".mp3")) {
            return FileConstants.MP3_CONTENT_TYPE;
        }else if(resourceName.endsWith(".mp4")) {
            return FileConstants.MP4_CONTENT_TYPE;
        }
        return "";
    }
}
