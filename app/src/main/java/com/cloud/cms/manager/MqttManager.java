package com.cloud.cms.manager;

import android.util.Log;

import com.cloud.cms.model.mqtt.MqttMessageInfo;
import com.cloud.cms.util.Validator;
import com.google.gson.Gson;

/**
 * File: MqttManager.java
 * Author: Landy
 * Create: 2018/12/21 21:03
 */
public class MqttManager {
    private static String TAG="MqttManager";

    /**
     * 消息到达，执行命令
     * @param context
     */
    public void executedCommand(String context){
        if(Validator.isNullOrEmpty(context)){
            Log.e(TAG,"MQTT message is null");
            return;
        }
        Gson mGson=new Gson();
        MqttMessageInfo mqttMessageInfo=mGson.fromJson(context,MqttMessageInfo.class);

        if(Validator.isNullOrEmpty(mqttMessageInfo)){
            Log.e(TAG,"MQTT message object is null");
            return;
        }
        switch (mqttMessageInfo.getSyncCmd()) {

        }


    }
}
