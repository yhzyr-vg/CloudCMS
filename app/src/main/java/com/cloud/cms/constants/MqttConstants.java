package com.cloud.cms.constants;

import com.cloud.cms.util.ProperUtil;

import java.util.Properties;

/**
 * File: MqttConstants.java
 * Author: Landy
 * Create: 2018/12/21 17:00
 */
public class MqttConstants {

    // mqtt host
    public static final String MQTT_HOST;

    //mqtt 用户名
    public static final String MQTT_USER_NAME;

    //mqtt 密码
    public static final String MQTT_PASSWORD;

    //mqtt topic
    public static final String MQTT_TOPIC;

    public final static int MQTT_CMD_RESOURCE_UPDATE_AYN = 0;
    public final static int MQTT_CMD_RESOURCE_UPDATE = 1;// 资源更新
    public final static int MQTT_CMD_SCREENSHOT = 2;// 截图
    public final static int MQTT_CMD_INSTALL_APK = 3;// 安装新应用
    public final static int MQTT_CMD_PUBLISH_IM = 4;// 发布即时消息
    public final static int MQTT_CMD_STOP_MSG = 5;//终止即时消息

    public final static int MQTT_CMD_REBOOT = 6;// 远程重启
    public final static int MQTT_CMD_POWER_PLAN = 7;// 远程开机
    public final static int MQTT_CMD_UPGRADE = 8;// 升级客户端
    public final static int MQTT_CMD_GET_CLENT_STATE = 9;// 获取客户端播放节目ID
    public final static int MQTT_CMD_GET_CLENT_PROGRAM = 10;// 获取客户端节目列表
    public final static int MQTT_CMD_LOOK_DOWNLOAD_STATUS = 11;// 查看下载进度
    public final static int MQTT_CMD_DELETE_PROGRAM = 12;// 删除节目
    public final static int MQTT_CMD_POWER_TIMING = 13;// 定时开关机

    static {
        Properties properties = ProperUtil.getProperties("mainConfig.properties");
        MQTT_HOST = properties.getProperty("mqtt.service.host");
        MQTT_USER_NAME = properties.getProperty("mqtt.service.username");
        MQTT_PASSWORD = properties.getProperty("mqtt.service.password");
        MQTT_TOPIC = properties.getProperty("mqtt.service.topic");
    }
}
