package com.cloud.cms.http;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.form.BaseForm;
import com.cloud.cms.form.FileListForm;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.ServerHolderManager;
import com.cloud.cms.manager.broadcast.USBBroadcastReceiver;
import com.cloud.cms.manager.filelist.FileListManager;
import com.cloud.cms.manager.filelist.FileManager;
import com.cloud.cms.manager.product.ProductManager;
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
    private AsyncHttpServer  server = new AsyncHttpServer();
    private AsyncServer mAsyncServer= new AsyncServer();
    /**静态资源列表 assets 文件夹下的 css,js,html,images,usb 下的静态文件*/
    private String [] assetsResourceList;
    /**手机 TV 内存或者usb 中的文件*/
    private String [] derectoryResourceList;
    private Context context;
    private FileListManager fileListManager;
    private TemplateManager templateManager;
    private ServerHolderManager serverHolderManager;
    private PublishManager publishManager;
    private ProductManager productManager;

    public IBinder onBind(Intent arg0) { return null; }
    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        init();
        initService();

    }

    /**一些初始化*/
    private void init(){
        fileListManager=new FileListManager();
        templateManager=new TemplateManager();
        serverHolderManager=new ServerHolderManager();
        publishManager=new PublishManager();
        productManager=new ProductManager();
        //FileConstants.DEFAULT_USB_PATH ="/data/user/0/com.cloud.cms"; //这里重置默认的U盘路径，上生产删掉
        //testCopy();//测试电子货架，上生产删掉
        assetsResourceList=new String []{"/favicon.ico","/css/.*","/js/.*","/system/.*","/images/.*"};
        derectoryResourceList=new String []{FileConstants.DEFAULT_RESOURCE_PATH,FileConstants.DEFAULT_USB_PATH};
    }

    /**
     * 测试电子货架，上生产删掉
     */
    private void testCopy(){
        FileConstants.DEFAULT_USB_PRODUCT_PATH=getDir("upload",Context.MODE_PRIVATE).getPath()+"/";//这里重置默认的usb 复制路径，上生产删掉
        String [] paths={"周黑鸭_01.jpg","周黑鸭_02.jpg","热干面_01.jpg","热干面_02.jpg","test_01.jpg","test_02.mp4"}; //01 资源在手机端显示的缩略图，02 资源在电视机上显示
        for(String path:paths){
            File file=new File(FileConstants.DEFAULT_USB_PRODUCT_PATH+path);
            try{
                file.createNewFile();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
        FileManager fileManager=new FileManager();
        fileManager.copyDirectory(FileConstants.DEFAULT_USB_PRODUCT_PATH,FileConstants.DEFAULT_TV_PRODUCT_PATH);//测试复制
    }

    private  void initService(){
        Intent intent = new Intent(this, PowerService.class);
        startService(intent);
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
                serverHolderManager.uploadFile(request,response,context,FileConstants.DEFAULT_UPLOAD_RESOURCE_PATH);
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
        try{
            String uri=request.getPath();
            Log.d(tag,"onRequest"+uri);
           if(!URLConstants.POST_METHOD.equals(request.getMethod())&&!URLConstants.GET_METHOD.equals(request.getMethod())){
               Log.d(tag,"Unsupported Method");
               response.send(JSON.toJSONString(new ResultCommand()));
               return;
           }
           ResultCommand resultCommand=new ResultCommand();
           String paramstr=serverHolderManager.getRequestParms(request,response);
           TemplateCommand templateCommand;
           switch (uri){
                case "/sys/filelist":   //u盘文件列表
                    fileListManager.handleFileListRequest(paramstr,response);
                    break;
                case "/sys/showFile":   //电视机显示需要播放的文件
                    showFile(paramstr,response);
                    break;
                case "/sys/dms":  //广域发布
                    Log.i(tag,"======================DMS");
//                    PackageManager packageManager = getPackageManager();
//                    Intent intent=new Intent();
//                    intent =packageManager.getLaunchIntentForPackage("com.victgroup.signup.dmsclient");
//                    intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    resultCommand.setResult(true);
//                    response.send(JSON.toJSONString(resultCommand));
                    break;
                case "/sys/publish":     //单体发布
                    templateCommand=templateManager.getTemplate(paramstr); //构建模板
                    templateManager.saveTemplate(templateCommand);//保存模板
                    publishManager.publishTemplate(templateCommand,response);//发布
                    break;
                case "/sys/publishTemplateById":     //根据模板id 发布
                    publishTemplateById(paramstr,response);
                    break;
                case "/sys/publishHistory":     //发布历史
                    publishManager.showPublishHistory(response);
                    break;
               case "/sys/productList":     //商品列表
                   FileManager fileManager=new FileManager();
                   fileManager.copyDirectory(FileConstants.DEFAULT_USB_PRODUCT_PATH,FileConstants.DEFAULT_TV_PRODUCT_PATH);//测试复制
                   productManager.getAllProducts(response);
                   break;
               case "/sys/home":     //返回桌面
                   Log.i(tag,"======================HOME");
                   Intent _Intent;

                   ComponentName _ComponentName ;
                   _Intent = new Intent();
                   _ComponentName = new ComponentName("com.android.launcher","com.android.launcher2.Launcher");
                   _Intent.setComponent(_ComponentName);
                   _Intent.setFlags(_Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(_Intent);
                   break;
            }
        }catch (Exception e ){
            e.printStackTrace();
            response.code(500).end();
        }
    }

    /**
     * 根据模板id 发布
     * @param params
     * @param response
     */
    private void publishTemplateById(String params, AsyncHttpServerResponse response){
        BaseForm form= JSON.parseObject(params,BaseForm.class);
        if(Validator.isNotNullOrEmpty(form)&&Validator.isNotNullOrEmpty(form.getId())){
            TemplateCommand templateCommand=templateManager.getTemplateById(form.getId());//根据id 获取模板
            publishManager.publishTemplate(templateCommand,response);//发布
        }else{
            response.send(JSON.toJSONString(new ResultCommand()));
        }
    }

    /**
     * 发布文件，这里是传入文件的路径，然后再页面上显示文件
     * @param params
     * @param response
     */
    private void showFile(String params, AsyncHttpServerResponse response){
        FileListForm fileListForm= JSON.parseObject(params,FileListForm.class);
        if(Validator.isNotNullOrEmpty(fileListForm)&&Validator.isNotNullOrEmpty(fileListForm.getPath())){
            TemplateCommand templateCommand=templateManager.getTemplateByPath(fileListForm.getPath());//根据Path构建模板
            publishManager.publishTemplate(templateCommand,response);//发布
        }else{
            response.send(JSON.toJSONString(new ResultCommand()));
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
