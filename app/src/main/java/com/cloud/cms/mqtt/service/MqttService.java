package com.cloud.cms.mqtt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.cloud.cms.config.Config;
import com.cloud.cms.constants.MqttConstants;
import com.cloud.cms.util.NetworkUtil;
import com.cloud.cms.util.Validator;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * File: MqttService.java
 * Author: Landy
 * Create: 2018/12/21 16:19
 */
public class MqttService extends Service {

    public static final String TAG = MqttService.class.getSimpleName();

    private static MqttAndroidClient client;
    private MqttConnectOptions options;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(getClass().getName(), "onCreate");
        init();
    }

    public static void publish(String msg){
        Integer qos = 0;
        Boolean retained = false;
        try {
            if (client != null){
                client.publish(MqttConstants.MQTT_TOPIC, msg.getBytes(), qos.intValue(), retained.booleanValue());
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        // 服务器地址（协议+地址+端口号）
        client = new MqttAndroidClient(this, MqttConstants.MQTT_HOST, Config.SERIAL_NUMBER);
        options = new MqttConnectOptions();
        // 清除缓存
        options.setCleanSession(true);
        // 设置超时时间，单位：秒
        options.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        options.setKeepAliveInterval(20);
        // 用户名
        options.setUserName(MqttConstants.MQTT_USER_NAME);
        // 密码
        options.setPassword(MqttConstants.MQTT_PASSWORD.toCharArray());     //将字符串转换为字符串数组

        // 设置MQTT监听并且接受消息
        client.setCallback(new MqttCallback(){

            @Override
            public void connectionLost(Throwable cause) {
                Log.e(TAG,"connectionLost----------");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String content = new String(message.getPayload(), "utf-8");
                String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
                Log.i(TAG, "===========messageArrived:" + content);
                Log.i(TAG, str2);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.i(TAG,"deliveryComplete---------"+ token.isComplete());
            }
        });

        // last will message
        boolean doConnect = true;
        String message = "{\"terminal_uid\":\"" + Config.SERIAL_NUMBER + "\"}";
        Log.e(getClass().getName(), "message是:" + message);
        Integer qos = 0;
        Boolean retained = false;
        if (Validator.isNotNullOrEmpty(message) && Validator.isNotNullOrEmpty(MqttConstants.MQTT_TOPIC)) {
            // 最后的遗嘱
            // MQTT本身就是为信号不稳定的网络设计的，所以难免一些客户端会无故的和Broker断开连接。
            //当客户端连接到Broker时，可以指定LWT，Broker会定期检测客户端是否有异常。
            //当客户端异常掉线时，Broker就往连接时指定的topic里推送当时指定的LWT消息。
            try {
                options.setWill(MqttConstants.MQTT_TOPIC, message.getBytes(), qos.intValue(), retained.booleanValue());
            } catch (Exception e) {
                Log.i(TAG, "Exception Occured", e);
                doConnect = false;
                iMqttActionListener.onFailure(null, e);
            }
        }
        if (doConnect) {
            doClientConnection();
        }

    }

    @Override
    public void onDestroy() {
        stopSelf();
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /** 连接MQTT服务器 */
    private void doClientConnection() {
        if (!client.isConnected() && NetworkUtil.isNetworkAvailable(this)) {
            try {
                client.connect(options, null, iMqttActionListener);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    // MQTT是否连接成功
    private IMqttActionListener iMqttActionListener = new IMqttActionListener() {
        @Override
        public void onSuccess(IMqttToken arg0) {
            Log.i(TAG, "连接成功 ");
            try {
                // 订阅myTopic话题
                client.subscribe(MqttConstants.MQTT_TOPIC,1);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(IMqttToken arg0, Throwable arg1) {
            arg1.printStackTrace();
            // 连接失败，重连
            Log.e(TAG,"");
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Log.e(getClass().getName(), "onBind");
        return new MqttService.CustomBinder();
    }

    public class CustomBinder extends Binder {
        public MqttService getService(){
            return MqttService.this;
        }
    }


}

