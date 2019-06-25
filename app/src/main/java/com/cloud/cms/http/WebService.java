package com.cloud.cms.http;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.command.ResourceCommand;
import com.cloud.cms.command.ResultCommand;
import com.cloud.cms.command.TemplateCommand;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.constants.FileConstants;
import com.cloud.cms.form.BaseForm;
import com.cloud.cms.form.FileListForm;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.ServerHolderManager;
import com.cloud.cms.manager.filelist.FileListManager;
import com.cloud.cms.manager.template.PublishManager;
import com.cloud.cms.manager.template.TemplateManager;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class WebService  extends Service {
    private static final String tag=WebService.class.getSimpleName();
    private AsyncHttpServer server;
    private AsyncServer mAsyncServer;
    /**静态资源列表 assets 文件夹下的 css,js,html,images,usb 下的静态文件*/
    private String [] assetsResourceList;
    /**手机 TV 内存或者usb 中的文件*/
    private String [] derectoryResourceList;
    private Context context;
    private FileListManager fileListManager;
    private TemplateManager templateManager;
    private ServerHolderManager serverHolderManager;
    private PublishManager publishManager;

    public IBinder onBind(Intent arg0) { return null; }
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        Log.i(tag,"======ip:"+NetworkUtil.getIPAddress(context)+":"+Config.SERVER_PORT);
        init();
        initService();
    }

    /**一些初始化*/
    private void init(){
        server = new AsyncHttpServer();
        mAsyncServer = new AsyncServer();
        fileListManager=new FileListManager();
        templateManager=new TemplateManager();
        serverHolderManager=new ServerHolderManager();
        publishManager=new PublishManager();
        //FileConstants.DEFAULT_DIRECTORY =getDir("upload",Context.MODE_PRIVATE).getPath(); //这里重置默认的上传路径，上生产删掉
        //FileConstants.DEFAULT_USB_PATH ="/data/user/0/com.cloud.cms"; //这里重置默认的U盘路径，上生产删掉
        assetsResourceList=new String []{"/favicon.ico","/css/.*","/js/.*","/system/.*","/images/.*"};
        derectoryResourceList=new String []{FileConstants.DEFAULT_DIRECTORY,FileConstants.DEFAULT_USB_PATH};
    }

    private  void initService(){
//        Intent intent = new Intent(this, PowerService.class);
//        startService(intent);
        server.get("/", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                try {
                    response.send(serverHolderManager.getIndexContent(context));//访问首页
                } catch (IOException e) {
                    e.printStackTrace();
                    response.code(500).end();
                }
            }
        });
        uploadFile(); //上传文件
        httpRequest();//处理 get post 请求
        requestResource();//处理静态资源
        server.listen(mAsyncServer, Config.SERVER_PORT);
    }

    /**
     * 文件上传
     */
    private void uploadFile(){
        server.post("/uploadifyFile", new HttpServerRequestCallback() {
            @Override
            public void onRequest(final AsyncHttpServerRequest request, final AsyncHttpServerResponse response) {
                serverHolderManager.uploadFile(request,response,context,FileConstants.DEFAULT_DIRECTORY);
            }
        });
    }

    /**
     *  返回静态资源文件
     */
    private void requestResource(){
        //1、返回 assets下 html  js  css,图片 文件
        for(String resource:assetsResourceList){
            server.get(resource, new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    serverHolderManager.getAssetsResources(request,response,context);
                }
            });
        }
        //1、返回 u盘，TV 磁盘内的 资源 文件
        for(final String resource:derectoryResourceList){
            server.get(resource+"/.*", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    serverHolderManager.getStorageResource(request,response);
                }
            });
        }
    }

    //处理 get post 请求
    private  void httpRequest(){
        server.post("/sys/.*", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                doRequest(request,response);
            }
        });
    }

    //处理 get post 请求
    private  void doRequest(AsyncHttpServerRequest request,AsyncHttpServerResponse response){
        ResultCommand resultCommand=new ResultCommand();
        try{
            String uri=request.getPath();
            Log.d(tag,"onRequest"+uri);
            Object params;
            if (request.getMethod().equals("GET")){
                params=request.getQuery();
            }else if (request.getMethod().equals("POST")&&Validator.isNotNullOrEmpty(request.getBody())){
                String contentType=request.getHeaders().get("Content-Type");
                if ("application/json".equals(contentType)){
                    params=((AsyncHttpRequestBody<JSONObject>)request.getBody()).get();
                }else{
                    params=((AsyncHttpRequestBody<Multimap>)request.getBody()).get();
                }
            }else {
                Log.d(tag,"Unsupported Method");
                resultCommand.setResult(false);
                response.send(JSON.toJSONString(resultCommand));
                return;
            }
            String paramstr="";
            if (params!=null){
                Log.d(tag,"params ="+params.toString());
                paramstr=params.toString().replaceAll("=",":")
                        .replaceAll("\\[","\"")
                        .replaceAll("\\]","\"");
            }
            Log.i(tag,"======================"+paramstr);

            TemplateCommand templateCommand;
            switch (uri){
                case "/sys/filelist":   //u盘文件列表
                    fileListManager.handleFileListRequest(paramstr,response);
                    Log.i(tag,"======================filelist "+paramstr);
                    break;
                case "/sys/dms":  //广域发布
                    Log.i(tag,"======================DMS");
                    PackageManager packageManager = getPackageManager();
                    Intent intent=new Intent();
                    intent =packageManager.getLaunchIntentForPackage("com.victgroup.signup.dmsclient");
                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    resultCommand.setResult(true);
                    response.send(JSON.toJSONString(resultCommand));
                    break;
                case "/sys/publish":     //单体发布
                    templateCommand=templateManager.getTemplate(paramstr); //构建模板
                    templateManager.saveTemplate(templateCommand);//保存模板
                    publishManager.publishTemplate(templateCommand,response);//发布
                    break;
                case "/sys/publishTemplateById":     //根据模板id 发布
                    if(Validator.isNotNullOrEmpty(paramstr)){
                        BaseForm form= JSON.parseObject(paramstr,BaseForm.class);
                        if(Validator.isNotNullOrEmpty(form)&&Validator.isNotNullOrEmpty(form.getId())){
                            templateCommand=templateManager.getTemplateById(form.getId());//根据id 获取模板
                            publishManager.publishTemplate(templateCommand,response);//发布
                        }else{
                            response.send(JSON.toJSONString(resultCommand));
                        }
                    }else{
                        response.send(JSON.toJSONString(resultCommand));
                    }
                    break;
                case "/sys/publishHistory":     //发布历史
                    publishManager.showPublishHistory(response);
                    break;
                case "/sys/openfile":     //usb文件读取播放

                    break;
            }
        }catch (Exception e ){
            e.printStackTrace();
            response.code(500).end();
        }
    }



    public void startapk(String pkg,String activity){
        Intent _Intent = new Intent();
        ComponentName _ComponentName = new ComponentName(pkg,activity);
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
}
