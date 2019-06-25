package com.cloud.cms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.R;
import com.cloud.cms.command.TemplateCommand;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.http.WebService;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.template.TemplateManager;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;

import java.util.List;

public class CMSActivity extends BaseActivity {

    private String tag="CMSActivity";
    private RelativeLayout main_layout;
    private BroadcastReceiver tvBroadcastReceiver;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_layout = new RelativeLayout(this);
        context=this;
        setContentView(main_layout);
        Intent intent = new Intent(CMSActivity.this, WebService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        context.startService(intent);
        //从xml 中读取文件，并初始化
        String templateCommandStr=PreferenceManager.getInstance().getTemplate();
        List<TemplateCommand> templateCommandList=null;
        if(Validator.isNotNullOrEmpty(templateCommandStr)){
            Log.i(tag,"=======Template history: "+templateCommandStr);
            templateCommandList= JSON.parseArray(templateCommandStr,TemplateCommand.class);
            if(Validator.isNotNullOrEmpty(templateCommandList)){
                //取最新的模板
                TemplateCommand templateCommand=templateCommandList.get(templateCommandList.size()-1);
                //加载模板
                loadTemplate(templateCommand);
            }
        }
        //监听广播
        initBroadcastReceiver();
    }

    /**
     * 广播监听，监听模板的更新
     */
    private void initBroadcastReceiver(){
        //监听广播
        IntentFilter filter=new IntentFilter(ActionConstants.ACTION_PUBLISH_TEMPLATE);
        tvBroadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action=intent.getAction();
                if(action.equals(ActionConstants.ACTION_PUBLISH_TEMPLATE)){
                    String templateCommandStr=intent.getStringExtra("templateCommand");
                    Log.i(tag,"=======publish template action: "+templateCommandStr);
                    if(Validator.isNotNullOrEmpty(templateCommandStr)){
                        TemplateCommand templateCommand= JSON.parseObject(templateCommandStr,TemplateCommand.class);
                        loadTemplate(templateCommand);
                    }
                }
            }
        };
        registerReceiver(tvBroadcastReceiver,filter);
    }

    /**
     * 加载模板
     * @param templateCommand
     */
    private void loadTemplate(TemplateCommand templateCommand){
        TemplateManager templateManager=new TemplateManager();
        templateManager.createView(templateCommand,context,main_layout);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tvBroadcastReceiver!=null){
            unregisterReceiver(tvBroadcastReceiver);
            tvBroadcastReceiver=null;
        }
    }
}
