package com.cloud.cms;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.activity.CMSActivity;
import com.cloud.cms.activity.DownloadResource;
import com.cloud.cms.activity.InstallAPKActivity;
import com.cloud.cms.api.model.Program;
import com.cloud.cms.command.LoginCommand;
import com.cloud.cms.command.RegisterCommand;
import com.cloud.cms.constants.DeviceConstants;
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.http.BaseCallBack;
import com.cloud.cms.activity.BaseActivity;
import com.cloud.cms.config.Config;
import com.cloud.cms.manager.DeviceManager;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.ProgramManager;
import com.cloud.cms.model.Device;
import com.cloud.cms.mqtt.service.MqttService;
import com.cloud.cms.util.FileUtil;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;
import com.cloud.cms.http.BaseOkHttpClient;
import com.cloud.cms.widget.CustomDialog;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.PackageUtils;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private Context context;
    private static final String TAG="MainActivity";

//    @BindView(R.id.tv_deviceName)
//    TextView tv_deviceName;
//    @BindView(R.id.progressBar)
//    ProgressBar progressBar;
//    @BindView(R.id.tv_prompt_isRegister)
//    TextView tv_prompt_isRegister;
//    @BindView(R.id.tv_prompt_noRegister)
//    TextView tv_prompt_noRegister;
//
//    @BindView(R.id.main_layout)
//    RelativeLayout main_layout;
//
//    @BindView(R.id.layout_progress)
//    LinearLayout layout_progress;

    private RelativeLayout main_layout;
    private LinearLayout layout_progress, qr_layout;
    private ImageView iv_QRcode;
    private EditText et_code1;
    private TextView tv_deviceName, tv_prompt_isRegister, tv_prompt_noRegister;
    private ProgressBar progressBar;
    private Button btn_commit;

    /**
     * 节目列表
     */
    private List<String> programList;

    private String REGISTER_URL;

    /**设备注册状态*/
    private int registerState;

    private CustomDialog dialog;

    public static boolean isCheckedUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        init();
    }

    private void checkIsFirstStart(){
        if (PreferenceManager.getInstance().getIsFirstStart()) {
            PreferenceManager.getInstance().setIsFirstStart(false);
            final File fileDir_cache = new File(Config.DIR_CACHE);
            final File fileResource_info = new File(Config.RESOURCE_INFO);
            if (fileDir_cache.exists() || fileResource_info.exists()) {
                if (fileDir_cache.isFile() || fileResource_info.isFile()) {
                    FileUtil.deleteFile(fileDir_cache);
                    FileUtil.deleteFile(fileResource_info);
                    init();
                }
            }
        }else{
            init();
        }
    }

    /**
     * 初始化
     */
    private void init(){

        main_layout=findViewById(R.id.main_layout);
        layout_progress = (LinearLayout) findViewById(R.id.layout_progress);
        tv_deviceName = (TextView) findViewById(R.id.tv_deviceName);
        tv_deviceName.setText("deviceName:" + Config.DEVICE_NAME + "\ndeviceId:" + Config.SERIAL_NUMBER);
        tv_prompt_isRegister = (TextView) findViewById(R.id.tv_prompt_isRegister);
        tv_prompt_noRegister = (TextView) findViewById(R.id.tv_prompt_noRegister);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        REGISTER_URL = "http://weixin.qq.com/r/2ygbAxjESkU1rVxg933J?macid=" + Config.SERIAL_NUMBER + "&memory_size="
                + DeviceManager.getTotalInternalMemorySize() + "&memory_used="
                + DeviceManager.getAvailableInternalMemorySize() + "&model=" + Config.DEVICE_MODEL;

        programList = ProgramManager.getProgramListList();
        if (Validator.isNullOrEmpty(programList)) {
            progressBar.setVisibility(View.GONE);
            tv_prompt_isRegister.setText(getResources().getString(R.string.tv_hasnotlocalresource));
        } else {
            progressBar.setVisibility(View.VISIBLE);
            tv_prompt_isRegister.setText(getResources().getString(R.string.tv_startload));
        }
        if (isLand()) {
            main_layout.setBackgroundResource(R.drawable.background_land);
        } else {
            main_layout.setBackgroundResource(R.drawable.background);
        }
        registerState = PreferenceManager.getInstance().getRegisterState();
        if (registerState == DeviceConstants.REGISTER_STATE_YES) {//已注册
            startTV();
        }else{//未注册
            if (NetworkUtil.isNetworkAvailable(context)){//有网络
                checkRegisterState();//检查注册状态
            }else{//无网络
                showRegisterDialog();
            }
        }
    }

    private boolean isLand(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            return true;
        }
        return false;
    }

    private void login(LoginCommand loginCommand){
        if(Validator.isNotNullOrEmpty(loginCommand)){
            //step1 检查app 版本，比对本地版本号看是否需要更新
            String version=PackageUtils.getAppVersionCode(context)+"";
            Log.i(TAG,"========== current version:"+version);
            //更新
            if(Validator.isNotNullOrEmpty(loginCommand.getVersionCode())
                    &&loginCommand.getVersionCode().compareTo(version)>0){
                Log.i(TAG,"更新 current version:"+version+" 服务器版本："+loginCommand.getVersionCode());
                String apkUrl=loginCommand.getApkurl();
                Intent intent=new Intent(MainActivity.this,InstallAPKActivity.class);
                intent.putExtra("apkurl",apkUrl);
                intent.putExtra("description", loginCommand.getDescription());
                intent.putExtra("type",InstallAPKActivity.TYPE_UPGRADE);
               // startActivity(intent);
               // return;
            }

            //step2 读取设备信息，设备是否已注册
            if(Validator.isNotNullOrEmpty(loginCommand.getRows())){
                Device device=loginCommand.getRows().get(0);
                String registerState=device.getRegisterState();
                if((DeviceConstants.REGISTER_STATE_YES+"").equals(registerState)){//已注册
                    //节目列表不为空
                    if(Validator.isNotNullOrEmpty(programList)){
                        startActivity(new Intent(MainActivity.this,CMSActivity.class));
                    }else{
                        //startService(new Intent(MainActivity.this, MqttService.class));
                        //getResource();
                        ProgramManager.getResource();
                    }
                }else{//未注册，要求注册
                    showRegisterDialog();
                }
            }
        }else{
            showToast(context,"获取数据失败");
        }
    }






    private void downloadResource(){
        startActivity(new Intent(context,DownloadResource.class));
    }

    /**
     * 获取资源
     */
    public  void getResource(){
        BaseOkHttpClient.newBuilder().url(URLConstants.GET_SCHEDULE_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        if(Validator.isNotNullOrEmpty(o)){
                            String str=o.toString();
                            Log.i(TAG,"========"+o.toString());
                            PreferenceManager.getInstance().setPrograms(str);
                            downloadResource();
                        }else{
                            Log.e(TAG,"return: null");
                        }
                    }
                    @Override
                    public void onError(int code) {
                        Log.e(TAG,"connect error"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,e.toString());
                    }
                });
    }

    /**
     * 自动登录
     */
    private void autoLogin(){
        BaseOkHttpClient.newBuilder().url(URLConstants.LOGIN_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
                .addParam("memory_size",DeviceManager.getTotalInternalMemorySize())
                .addParam("memory_used",DeviceManager.getAvailableInternalMemorySize())
                .addParam("model",Config.DEVICE_MODEL)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        showToast(context,o.toString());
                        if(Validator.isNotNullOrEmpty(o)){
                            String str=o.toString();
                            LoginCommand loginCommand= JSON.parseObject(str,LoginCommand.class);
                            login(loginCommand);
                            Log.i(TAG,"=======connect:"+o.toString());
                        }else{
                            Log.e(TAG,"login: null");
                            showRegisterDialog();
                        }
                    }

                    @Override
                    public void onError(int code) {
                        showToast(context,"connect error:"+code);
                        Log.e(TAG,"connect error"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast(context,"connect failure");
                        Log.e(TAG,e.toString());
                    }
                });
    }

    /**
     * 检查注册状态
     */
    private void checkRegisterState(){
        BaseOkHttpClient.newBuilder().url(URLConstants.REGISTER_URL)
                .addParam("sn",Config.SERIAL_NUMBER)
                .addParam("memory_size",DeviceManager.getTotalInternalMemorySize())
                .addParam("memory_used",DeviceManager.getAvailableInternalMemorySize())
                .addParam("model",Config.DEVICE_MODEL)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        Log.i(TAG,"register str:"+o);
                        if(Validator.isNotNullOrEmpty(o)){
                            String str=o.toString();
                            RegisterCommand registerCommand= JSON.parseObject(str,RegisterCommand.class);
                            checkRegisterState(registerCommand);
                        }else{
                            Log.e(TAG,"register: null");
                            showRegisterDialog();
                        }
                    }

                    @Override
                    public void onError(int code) {
                        showToast(context,"connect error:"+code);
                        Log.e(TAG,"connect error"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        showToast(context,"connect failure");
                        Log.e(TAG,e.toString());
                    }
                });
    }

    /**
     * 注册
     * @param registerCommand
     */
    private void checkRegisterState(RegisterCommand registerCommand){
        if(Validator.isNotNullOrEmpty(registerCommand)){//返回数据
            if(registerCommand.getCustomerId()!=0&&Validator.isNotNullOrEmpty(registerCommand.getCustomerName())){//已注册
                registerState=DeviceConstants.REGISTER_STATE_YES;
                PreferenceManager.getInstance().setRegisterState(registerState);
                Config.USER_GROUP_NAME = registerCommand.getCustomerName();
                PreferenceManager.getInstance().setUserName(registerCommand.getCustomerName());
                showToast(MainActivity.this, getResources().getString(R.string.tip_registersuccess));
                startTV();
            }else{//未注册
                showRegisterDialog();
            }
        }else{
            showRegisterDialog();
        }
    }

    /**
     * 显示注册，显示二维码
     */
    private void showRegisterDialog(){
        if (isLand()) {
            main_layout.setBackgroundResource(R.drawable.background_land_qr);
        } else {
            main_layout.setBackgroundResource(R.drawable.background_qr);
        }
        showToast(context, getResources().getString(R.string.tip_registerfailbynotscan));
        layout_progress.setVisibility(View.GONE);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (isLand()) {
            window.setGravity(Gravity.LEFT);
            lp.x = Config.SCREEN_WIDTH / 5;
        } else {
            window.setGravity(Gravity.BOTTOM);
            lp.y = Config.SCREEN_HEIGHT * 2 / 5;
        }
        lp.dimAmount = 0f;
        window.setAttributes(lp);
        dialog.show();
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

