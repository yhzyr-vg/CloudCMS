package com.cloud.cms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cloud.cms.R;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.http.intercept.ProgressListener;
import com.cloud.cms.http.intercept.ProgressResponsBody;
import com.cloud.cms.util.FileUtil;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;

import java.io.IOException;

import butterknife.BindView;
import cn.trinea.android.common.util.PackageUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 安装 apk
 */
public class InstallAPKActivity extends BaseActivity {

    private static final String TAG="InstallAPKActivity";

    //程序升级
    public static int TYPE_UPGRADE = 1;
    //安装第三方程序
    public static int TYPE_INSTALL_APK = 2;

    private Context context;

    private String messageId;
    /**
     * 待下载的apk 链接
     */
    private String apkurl;

    /**
     * 描述
     */
    private String description;

    //安装类型
    private int type;

//    @BindView(R.id.txt_message)
//    TextView txt_message;
//    @BindView(R.id.txt_percent)
//    TextView txt_percent;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;

    private TextView txt_message;
    private TextView txt_percent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_apk);
        init();
        context = this;
        if(!NetworkUtil.isNetworkAvailable(context)){
            showToast(context,"网络未连接");
            finish();
            return;
        }
        apkurl = getIntent().getStringExtra("apkurl");
        messageId = getIntent().getStringExtra("messageId");
        description = getIntent().getStringExtra("description");
        type=getIntent().getIntExtra("type",0);

        if(Validator.isNullOrEmpty(apkurl)){
            showToast(context,"找不到安装文件");
            finish();
        }

        if(!apkurl.contains("http")){
            apkurl=URLConstants.BASE_URL+apkurl;
        }
        String fileName=FileUtil.getFileNameByUrl(apkurl);
        //下载apk
        downLoadFile(apkurl,Config.INSTALL_APK_PATH,fileName);
    }

    private void init(){
        txt_message = (TextView) findViewById(R.id.txt_message);
        txt_message.setText(getResources().getString(R.string.download_start) + description);
        txt_percent = (TextView) findViewById(R.id.txt_percent);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    /**
     * 文件下载
     */
    private void downLoadFile(String url, final String path, final String fileName) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new InterNetInterceptor())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MainActivity", e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                FileUtil.writeFile(response,path,fileName);
            }
        });
    }

    /**
     * Interceptor拦截器方法实现
     */
    public class InterNetInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return response.newBuilder().body(new ProgressResponsBody(response.body(), new ProgBar())).build();
        }
    }

    /**
     * 进度条监听
     */
    private class ProgBar implements ProgressListener {

        @Override
        public void onProgress(final int mProgress, final long contentSize) {
           // Log.i("**************下载进度：", String.valueOf(mProgress));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setProgress(mProgress);
                }
            });
        }

        @Override
        public void onDone(long totalSize) {
            Log.i("下载完成********文件大小为：", String.valueOf(totalSize));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txt_message.setText("下载完成，正在安装，请稍候。。。");
                    install();
                }
            });
        }
    }

    /**
     * 安装apk
     */
    private void install(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //安装文件的路径
                String path=Config.INSTALL_APK_PATH+FileUtil.getFileNameByUrl(apkurl);
                //读写权限
                String[] command = {"chmod", "777", path};
                ProcessBuilder builder = new ProcessBuilder(command);
                try {
                    builder.start();
                }catch (IOException e){
                    Log.e(TAG,e.toString());
                }
                if(type==TYPE_UPGRADE){
                    //autoUpgrade(path);
                    //PackageUtils.install(context,path);
                    showToast(context,"下载完毕");
                }else{
                    PackageUtils.install(context,path);
                }
            }
        });
    }

    /**
     * 自动升级
     */
    private void autoUpgrade(String path){
        Intent intentinstall=new Intent("com.vigroup.vgservice.common.INSTALL_APK");
        intentinstall.putExtra("apkUrl", path);
        intentinstall.putExtra("restartFlag", true);//是否重启
        intentinstall.putExtra("packageName", PackageUtils.getAppProcessName(context));
        startActivity(intentinstall);
    }
}
