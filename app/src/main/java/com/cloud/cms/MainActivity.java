package com.cloud.cms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cloud.cms.activity.CMSActivity;
import com.cloud.cms.activity.InstallAPKActivity;
import com.cloud.cms.command.LoginCommand;
import com.cloud.cms.constants.LoginContants;
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.http.BaseCallBack;
import com.cloud.cms.activity.BaseActivity;
import com.cloud.cms.config.Config;
import com.cloud.cms.model.Device;
import com.cloud.cms.util.Validator;
import com.cloud.cms.http.BaseOkHttpClient;

import java.io.IOException;
import java.util.List;

import cn.trinea.android.common.util.PackageUtils;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private Context context;
    private static final String TAG="MainActivity";

    /**
     * 是否有节目
     */
    private boolean isHasProgram;

    /**
     * 节目列表
     */
    private List<String> programList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        showToast(context,"start");
        autoLogin();
    }

    /**
     *
     */
    private void register(){

    }

    /**
     * 注册弹窗
     */
    private void showRegisterDialog(){

    }

    private void login(LoginCommand loginCommand){
        if(Validator.isNotNullOrEmpty(loginCommand)){
            //step1 检查app 版本，比对本地版本号看是否需要更新
            String version=PackageUtils.getAppVersionCode(context)+"";
            //更新
            if(Validator.isNotNullOrEmpty(loginCommand.getVersionCode())
                    &&loginCommand.getVersionCode().compareTo(version)>0){
                Log.i(TAG,"更新 current version:"+version+" 服务器版本："+loginCommand.getVersionCode());
                String apkUrl=loginCommand.getApkurl();
                Intent intent=new Intent(MainActivity.this,InstallAPKActivity.class);
                intent.putExtra("apkurl",apkUrl);
                intent.putExtra("type",InstallAPKActivity.TYPE_UPGRADE);
                startActivity(intent);
                return;
            }

            //step2 读取设备信息，设备是否已注册
            if(Validator.isNotNullOrEmpty(loginCommand.getRows())){
                Device device=loginCommand.getRows().get(0);
                String registerState=device.getRegisterState();
                if(LoginContants.REGISTER_STATE_YES.equals(registerState)){//已注册
                    //节目列表不为空
                    if(Validator.isNotNullOrEmpty(programList)){
                        startActivity(new Intent(MainActivity.this,CMSActivity.class));
                    }


                }else{//未注册，要求注册
                    showRegisterDialog();
                }
            }
        }else{
            showToast(context,"获取数据失败");
        }
    }

    /**
     * 自动登录
     */
    private void autoLogin(){
        BaseOkHttpClient.newBuilder().url(URLConstants.LOGIN_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
                .addParam("memory_size",30)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        showToast(context,o.toString());
                        LoginCommand loginCommand= (LoginCommand) o;
                        login(loginCommand);
                        Log.i(TAG,"访问成功"+o.toString());
                    }

                    @Override
                    public void onError(int code) {
                        showToast(context,"编码失败"+code);
                        Log.e(TAG,"编码失败"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast(context,"失败");
                        Log.e(TAG,e.toString());
                    }
                });
    }

    /**
     *启动
     */
    private void startTV(){
        if(Validator.isNotNullOrEmpty(Config.SERIAL_NUMBER)){
            autoLogin();
        }else{
            showDailog(context,"提示","设备无法登陆！");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

