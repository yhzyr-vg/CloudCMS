package com.cloud.cms.manager.template;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.CloudCMSApplication;
import com.cloud.cms.command.ResultCommand;
import com.cloud.cms.command.TemplateCommand;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.http.WebService;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.util.Validator;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.util.List;

/**发布管理
 * File: PublishManager.java
 * Author: Landy
 * Create: 2019/6/14 17:17
 */
public class PublishManager {

    private static final String tag=PublishManager.class.getSimpleName();
    /**
     * 发布模板
     * @param templateCommand
     * @param response
     */
    public void publishTemplate(TemplateCommand templateCommand, AsyncHttpServerResponse response){
        ResultCommand resultCommand=new ResultCommand();
        //发送广播，展示模板
        if(Validator.isNotNullOrEmpty(templateCommand)){
            Log.i(tag,"=======publishTemplate: "+templateCommand.getName());
            Intent intent1 = new Intent();
            intent1.putExtra("templateCommand",JSON.toJSONString(templateCommand));
            intent1.setAction(ActionConstants.ACTION_PUBLISH_TEMPLATE);
            CloudCMSApplication.context.sendBroadcast(intent1);
            resultCommand.setResult(true);
        }else{
            resultCommand.setResult(false);
        }
        response.send(JSON.toJSONString(resultCommand));
    }

    /**
     * 发布历史
     * @param response
     */
    public void showPublishHistory(AsyncHttpServerResponse response){
        ResultCommand resultCommand=new ResultCommand();
        resultCommand.setResult(false);
        //从xml 中读取文件，并初始化
        String templateCommandStr=PreferenceManager.getInstance().getTemplate();
        List<TemplateCommand> templateCommandList=null;
        if(Validator.isNotNullOrEmpty(templateCommandStr)){
            Log.i(tag,"=======Template history: "+templateCommandStr);
            templateCommandList= JSON.parseArray(templateCommandStr,TemplateCommand.class);
            if(Validator.isNotNullOrEmpty(templateCommandList)){
                resultCommand.setResult(true);
                resultCommand.setReturnObject(templateCommandList);
            }
        }
        response.send(JSON.toJSONString(resultCommand));
    }
}
