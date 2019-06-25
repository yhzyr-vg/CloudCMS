package com.cloud.cms.model;

/**
 * File: LoadItem.java
 * Author: Landy
 * Create: 2019/1/7 16:00
 */
public class LoadItem {
    public String path;
    public String url;

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public LoadItem(String path, String url) {
        this.path = path;
        this.url = url;
    }
}
