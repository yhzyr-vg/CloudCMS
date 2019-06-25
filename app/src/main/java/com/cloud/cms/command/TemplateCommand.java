package com.cloud.cms.command;

import java.io.Serializable;
import java.util.List;

/**
 * File: TemplateCommand.java
 * Author: Landy
 * Create: 2019/6/4 18:24
 */
public class TemplateCommand implements Serializable {

    private String name;//模板名称
    private Integer screenType;//选择的屏幕 1,全屏，2：50%，3：%30
    private String imgList;//图片列表
    private String startTime;//起始时间
    private String endTime;//结束时间
    private Integer orientation;//横竖屏 1 横屏 2 竖屏

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    /**
     * 这是直接放到 组件上的资源，就是模板中的组件列表
     */
    List<WidgetCommand> widgetCommandList;

    public List<WidgetCommand> getWidgetCommandList() {
        return widgetCommandList;
    }

    public void setWidgetCommandList(List<WidgetCommand> widgetCommandList) {
        this.widgetCommandList = widgetCommandList;
    }


    public Integer getScreenType() {
        return screenType;
    }

    public void setScreenType(Integer screenType) {
        this.screenType = screenType;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getOrientation() {
        return orientation;
    }

    public void setOrientation(Integer orientation) {
        this.orientation = orientation;
    }
}
