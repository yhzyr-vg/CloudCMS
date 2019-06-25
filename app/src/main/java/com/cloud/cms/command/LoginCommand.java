package com.cloud.cms.command;

import com.cloud.cms.model.Device;

import java.util.List;

/**
 * File: LoginCommand.java
 * Author: Landy
 * Create: 2018/12/13 17:51
 */
public class LoginCommand {
    private int total;
    private String apkurl;
    private String versionName;
    private String versionCode;
    private String topicName;
    private Integer version;
    private List <Device> rows;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Device> getRows() {
        return rows;
    }

    public void setRows(List<Device> rows) {
        this.rows = rows;
    }
}
