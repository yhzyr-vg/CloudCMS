package com.cloud.cms.model.mqtt;

import java.util.List;

/**
 * File: MqttMessageInfo.java
 * Author: Landy
 * Create: 2018/12/21 21:11
 */
public class MqttMessageInfo {
    private String syncCmd;
    private String topic;
    private String messageId;
    private List<MqttDevice> devicelist;

    public String getSyncCmd() {
        return syncCmd;
    }

    public void setSyncCmd(String syncCmd) {
        this.syncCmd = syncCmd;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<MqttDevice> getDevicelist() {
        return devicelist;
    }

    public void setDevicelist(List<MqttDevice> devicelist) {
        this.devicelist = devicelist;
    }
}
