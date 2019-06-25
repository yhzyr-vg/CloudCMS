package com.cloud.cms.http.download;

/**
 * File: DownloadItem.java
 * Author: Landy
 * Create: 2019/3/11 13:03
 */
public class DownloadItem {
    public static final String TPYE_IMAGE_WELCOM="TPYE_IMAGE_WELCOM";
    public static final String TPYE_IMAGE_BOOT="TPYE_IMAGE_BOOT";
    public static final String TPYE_VIDEO_WELCOM="TPYE_VIDEO_WELCOM";

    private String url;
    private String type;
    private String path;
    private String name;

    public DownloadItem(){
    }
    public DownloadItem(String url, String type){
        this.url=url;
        this.type=type;
    }

    public DownloadItem(String url, String type,String path,String name){
        this.url=url;
        this.type=type;
        this.path=path;
        this.name=name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
