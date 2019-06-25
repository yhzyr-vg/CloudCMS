package com.cloud.cms.api.model;

import com.cloud.cms.model.LoadItem;

import java.util.List;

public class Template {
    private int id;
    private String name;
    private String background;
    private int screenWidth;
    private int screenHeight;
    private int version;
    private int fid;
    private String playtype;
    private String times;
    private String duration;
    private List<Widgets> widgets;

    private String background_path;
    private String background_url;

    private List<String> urlList;
    private List<String> wholeUrlList;
    private List<LoadItem> loadItemList;

    public String getBackground_path() {
        return background_path;
    }

    public void setBackground_path(String background_path) {
        this.background_path = background_path;
    }

    public String getBackground_url() {
        return background_url;
    }

    public void setBackground_url(String background_url) {
        this.background_url = background_url;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public List<String> getWholeUrlList() {
        return wholeUrlList;
    }

    public void setWholeUrlList(List<String> wholeUrlList) {
        this.wholeUrlList = wholeUrlList;
    }

    public List<LoadItem> getLoadItemList() {
        return loadItemList;
    }

    public void setLoadItemList(List<LoadItem> loadItemList) {
        this.loadItemList = loadItemList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getPlaytype() {
        return playtype;
    }

    public void setPlaytype(String playtype) {
        this.playtype = playtype;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Widgets> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widgets> widgets) {
        this.widgets = widgets;
    }
}
