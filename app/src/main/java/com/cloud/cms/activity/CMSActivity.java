package com.cloud.cms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.R;
import com.cloud.cms.command.TemplateCommand;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.http.WebService;
import com.cloud.cms.manager.ApManager;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.manager.broadcast.USBBroadcastReceiver;
import com.cloud.cms.manager.template.TemplateManager;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;

import java.util.List;

public class CMSActivity extends BaseActivity {

    private String tag="CMSActivity";
    private RelativeLayout main_layout;
    private BroadcastReceiver tvBroadcastReceiver;
    private BroadcastReceiver usbBroadcastReceiver;
    private Context context;
    int keycount=0;
    TemplateManager templateManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main_layout = new RelativeLayout(this);
        context=this;
        Log.i(tag,"热点状态"+ApManager.isApOn(context));
        if(ApManager.isApOn(context)){
        }else {
            Log.i(tag,"开热点");
            ApManager.openHotspot(context,"CMS信息发布","12345678");
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    //String ap=ApManager.getHotspotLocalIpAddress(context);

                    Toast.makeText(getApplicationContext(),"发布地址:192.168.43.1:"+Config.SERVER_PORT,Toast.LENGTH_LONG).show();

                    Log.i(tag,"======ip:"+NetworkUtil.getIPAddress(context)+":"+Config.SERVER_PORT);
                }
            }, 7000);
        }



        templateManager=new TemplateManager();
        //Log.i(tag,"======ip:"+NetworkUtil.getIPAddress(context)+":"+Config.SERVER_PORT);
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

        //U盘监听
//        usbBroadcastReceiver=new USBBroadcastReceiver();
//        IntentFilter usbfilter=new IntentFilter(ActionConstants.ACTION_MEDIA_MOUNTED);//u 盘插入
//        filter.addAction(ActionConstants.ACTION_MEDIA_REMOVED);
//        registerReceiver(usbBroadcastReceiver,filter);
    }



    /**
     * 加载模板
     * @param templateCommand
     */
    private void loadTemplate(TemplateCommand templateCommand){
        templateManager.createView(templateCommand,context,main_layout);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tvBroadcastReceiver!=null){
            unregisterReceiver(tvBroadcastReceiver);
            tvBroadcastReceiver=null;
        }
        if(usbBroadcastReceiver!=null){
            unregisterReceiver(usbBroadcastReceiver);
            usbBroadcastReceiver=null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_1){
              keycount++;
              if (keycount%3==0){

            }

        }
        return super.onKeyDown(keyCode, event);
    }




}
