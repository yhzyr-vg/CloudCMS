package com.cloud.cms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.cloud.license.config.TVConfig;
import com.cloud.license.manager.LicenseManager;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.trinea.android.common.util.PackageUtils;
import okhttp3.Call;

public class MainActivity extends BaseActivity {

    private static String tag="MainActivity";
    private Context context;
    private BroadcastReceiver tvBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;
        init();
    }

    private void init(){
        // 广播监听license 校验的结果
        initBroadcastReceiver();
        //校验license
        LicenseManager licenseManager=new LicenseManager(context);
        licenseManager.setDelayTime(1*1000);//设置延迟时间。可自行调整 ，不设置默认15 秒
       // licenseManager.setUserId(122);    //设置用户id  默认是131  请根据需要设置
        licenseManager.checkLicense();
    }

    /**
     * 广播
     */
    private void initBroadcastReceiver(){
        IntentFilter filter=new IntentFilter(TVConfig.ACTION_CHECK_LICENSE_SUCCESS);
        filter.addAction(TVConfig.ACTION_CHECK_LICENSE_FAILED);
        tvBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(action.equals(TVConfig.ACTION_CHECK_LICENSE_SUCCESS)){ //校验 license 成功
                    Log.i(tag,"==========ACTION_CHECK_LICENSE_SUCCESS");
                    //成功启动cms
                    Intent cmsntent = new Intent(context, CMSActivity.class);
                    context.startActivity(cmsntent);
                } else if(action.equals(TVConfig.ACTION_CHECK_LICENSE_FAILED)){  ////校验 license 失败
                    Log.i(tag,"==========ACTION_CHECK_LICENSE_SUCCESS");
                    //失败后要做的事,这里直接关闭窗口
                    finish();
                }
            }
        };
        registerReceiver(tvBroadcastReceiver,filter);
    }

    protected void onDestroy() {
        super.onDestroy();
        if(tvBroadcastReceiver!=null){
            unregisterReceiver(tvBroadcastReceiver);
            tvBroadcastReceiver=null;
        }
    }

}

