package com.cloud.cms.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.command.FileUploadHolder;
import com.cloud.cms.command.RegisterCommand;
import com.cloud.cms.manager.ResultCommand;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.body.MultipartFormDataBody;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.async.http.server.AsyncHttpServer;
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

public class WebService  extends Service {
    private static final String TEXT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css;charset=utf-8";
    private static final String BINARY_CONTENT_TYPE = "application/octet-stream";
    private static final String JS_CONTENT_TYPE = "application/javascript";
    private static final String PNG_CONTENT_TYPE = "application/x-png";
    private static final String WOFF_CONTENT_TYPE = "application/x-font-woff";
    private static final String TTF_CONTENT_TYPE = "application/x-font-truetype";
    private static final String SVG_CONTENT_TYPE = "image/svg+xml";
    private static final String EOT_CONTENT_TYPE = "image/vnd.ms-fontobject";
    private static final String MP3_CONTENT_TYPE = "audio/mp3";
    private static final String MP4_CONTENT_TYPE = "video/mpeg4";
    public static final int AUDIO_FILE_DURATION_THRESHOLD_IN_SECOND = 600;
    public static final String MY_IMPORTED_AUDIO = "导入的音频";
    public static final String MY_IMPORTED_MUSIC = "导入的音乐";
    public IBinder onBind(Intent arg0) { return null; }
    private String tag="webservice";
    private AsyncHttpServer server = new AsyncHttpServer();
    private AsyncServer mAsyncServer = new AsyncServer();
    //端口号
    private static final int port=13521;
    private String defaultDirectory;
    private static final String ACTION_UPLOADFILE_COMPLETE="ACTION_UPLOADFILE_COMPLETE";
    Intent _Intent;
    ComponentName _ComponentName ;
    @Override
    public void onCreate() {
        super.onCreate();
        initService();

    }
    private  void initService(){
        Intent intent = new Intent(this, PowerService.class);
        startService(intent);

        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    response.send(getIndexContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(500).end();
                }
            }
        });
        getPage();
        uploadFile();
        httpRequest();
        getResource("/favicon.ico");
        getResource("/images/uploadifive-cancel.png");
        getResource("/images/bg.png");
        server.listen(mAsyncServer, port);
    }
    private void getPage(){
        server.get("/system/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    String fullPath = request.getPath();
                    Log.i(tag,"=============="+fullPath);
                    fullPath = fullPath.replace("%20", " ");
                    String resourceName = fullPath;
                    if (resourceName.startsWith("/")) {
                        resourceName = resourceName.substring(1);
                    }
                    Log.i(tag,resourceName);
                    response.send(getIndexContent(resourceName));
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(500).end();
                }
            }
        });
    }
    private  void httpRequest(){
        server.post("/sys/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                doRequest(request,response);
            }
        });
        getUploadResource();
    }
    private  void doRequest(AsyncHttpServerRequest request,AsyncHttpServerResponse response){
        try{
            String uri=request.getPath();
            Log.d(tag,"onRequest"+uri);
            Object params;
            if (request.getMethod().equals("GET")){
                params=request.getQuery();
            }else if (request.getMethod().equals("POST")){
                String contentType=request.getHeaders().get("Content-Type");
                if (contentType.equals("application/json")){
                    params=((AsyncHttpRequestBody<JSONObject>)request.getBody()).get();
                }else{
                    params=((AsyncHttpRequestBody<Multimap>)request.getBody()).get();
                }

            }else {
                Log.d(tag,"Unsupported Method");
                return;
            }
            if (params!=null){
                Log.d(tag,"params ="+params.toString());
            }
            Log.i(tag,"======================"+uri);
            switch (uri){
                case "/sys/usb":
                    // handleUSBRequest(params,response);
                    Log.i(tag,"======================USB");
                    startapk("com.jrm.localmm","com.jrm.localmm.ui.main.FileBrowserActivity");
                    break;
                case "/sys/dms":
                    Log.i(tag,"======================DMS");
                    PackageManager packageManager = getPackageManager();
                    Intent intent=new Intent();
                    intent =packageManager.getLaunchIntentForPackage("com.victgroup.signup.dmsclient");
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
//                case "/sys/uploadifyFile":
//                    Log.i(tag,"======================uploadfile");
//                    break;

            }
        }catch (Exception e ){
            e.printStackTrace();
            response.code(500).end();
        }

    }
    public void startapk(String pkg,String activity){
        _Intent = new Intent();
        _ComponentName = new ComponentName(pkg,activity);
        _Intent.setFlags(_Intent.FLAG_ACTIVITY_NEW_TASK);
        _Intent.setComponent(_ComponentName);
        startActivity(_Intent);

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private String getIndexContent() throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream= new BufferedInputStream(getAssets().open("index.html"));
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
    private void getResource(String regex){
        server.get(regex, new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    String fullPath = request.getPath();
                    Log.i(tag,"============getResource fullPath:"+fullPath);
                    fullPath = fullPath.replace("%20", " ");
                    String resourceName = fullPath;
                    if (resourceName.startsWith("/")) {
                        resourceName = resourceName.substring(1);
                    }
                    if (resourceName.indexOf("?") > 0) {
                        resourceName = resourceName.substring(0, resourceName.indexOf("?"));
                    }
                    Log.i(tag,"============getResource resourceName "+resourceName);
                    if(!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                        response.setContentType(getContentTypeByResourceName(resourceName));
                    }
                    BufferedInputStream bInputStream = new BufferedInputStream(getAssets().open(resourceName));
                    response.sendStream(bInputStream, bInputStream.available());
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(404).end();
                    return;
                }
            }
        });
    }
    private String getContentTypeByResourceName(String resourceName){
        if(resourceName.endsWith(".css")){
            return CSS_CONTENT_TYPE;
        }else if(resourceName.endsWith(".js")){
            return JS_CONTENT_TYPE;
        }else if(resourceName.endsWith(".swf")){
            return BINARY_CONTENT_TYPE;
        }else if(resourceName.endsWith(".png")){
            return PNG_CONTENT_TYPE;
        }else if(resourceName.endsWith(".woff")){
            return WOFF_CONTENT_TYPE;
        }else if(resourceName.endsWith(".ttf")){
            return TTF_CONTENT_TYPE;
        }else if(resourceName.endsWith(".svg")){
            return SVG_CONTENT_TYPE;
        }else if(resourceName.endsWith(".eot")) {
            return EOT_CONTENT_TYPE;
        }else if(resourceName.endsWith(".mp3")) {
            return MP3_CONTENT_TYPE;
        }else if(resourceName.endsWith(".mp4")) {
            return MP4_CONTENT_TYPE;
        }
        return "";
    }
    private String getIndexContent(String page) throws IOException {
        BufferedInputStream bInputStream = null;
        try {
            bInputStream = new BufferedInputStream(getAssets().open(page));
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
    private void uploadFile(){
        server.post("/uploadifyFile", new HttpServerRequestCallback() {
            @Override
            public void onRequest(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                final MultipartFormDataBody body = (MultipartFormDataBody) request.getBody();
                final FileUploadHolder fileUploadHolder = new FileUploadHolder();
                body.setMultipartCallback(new MultipartFormDataBody.MultipartCallback() {
                    @Override
                    public void onPart(final Part part) {
                        if (part.isFile()){
                            String fileName=getNewFileName(part.getFilename());
                            Log.i(tag,"======newFileName:"+fileName+"  part.isFile():"+part.isFile());
                            if (TextUtils.isEmpty(fileUploadHolder.getFileName()) && !TextUtils.isEmpty(fileName)) {
                                generateReceivedFileAndFileStream(fileUploadHolder, fileName);
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
                                    Intent intent=new Intent(ACTION_UPLOADFILE_COMPLETE);
                                    intent.putExtra("filePath",filePath);
                                    sendBroadcast(intent);
                                    resultCommand.setResult(true);
                                    resultCommand.setMessage(filePath);
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
        });
    }
    private String getFileNameFromHttpByteBufferList(ByteBufferList bb) {
        String s = bb.readString();
        Log.i(tag,"========getFileNameFromHttpByteBufferList:"+s);
        return s;
    }
    private  String getNewFileName(String fileName){
        Log.i(tag,"=======getFileNameFromHttpByteBufferList fileName"+fileName);
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String	newFileName =fileExt+df.format(new Date()) + "." + fileExt;
        return newFileName;
    }
    private void generateReceivedFileAndFileStream(FileUploadHolder fileUploadHolder, String fileName) {
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
     * 访问上传的文件
     */
    private void getUploadResource(){
        server.get(defaultDirectory+".*?", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Log.d(TAG, request.getPath());
                try{
                    String statusLine = ((AsyncHttpServerRequestImpl) (request)).getStatusLine();
                    String[] parts = statusLine.split(" ");
                    String fullPath = parts[1];
                    fullPath = fullPath.replace("%20", " ");
                    Log.i(tag,"============fullPath:"+fullPath);
                    String resourceName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                    if(resourceName.endsWith("?")) {
                        resourceName = resourceName.substring(0, resourceName.length() - 1);
                    }
                    Log.i(tag,"============resourceName:"+resourceName);
                    if(!TextUtils.isEmpty(getContentTypeByResourceName(resourceName))) {
                        response.setContentType(getContentTypeByResourceName(resourceName));
                    }
                    String path = request.getMatcher().replaceAll("");
                    Log.i(tag,"============path "+path);
                    String storagePath = defaultDirectory;//访问的是
                    File file = new File(storagePath, path);
                    FileInputStream ex = new FileInputStream(file);
                    response.sendStream(ex, ex.available());
                } catch (IOException e){
                    e.printStackTrace();
                    return;
                }
            }
        });
    }
}
