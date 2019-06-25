package com.cloud.cms.model;

/**
 * File: Device.java
 * Author: Landy
 * Create: 2018/12/13 17:47
 */
public class Device {
    private Long id;
    private String device_id;
    private String device_name;
    private String registerState;
    private String ip;

    public void setId(Long id) {
        this.id = id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public void setRegisterState(String registerState) {
        this.registerState = registerState;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getRegisterState() {
        return registerState;
    }

    public String getIp() {
        return ip;
    }
}
