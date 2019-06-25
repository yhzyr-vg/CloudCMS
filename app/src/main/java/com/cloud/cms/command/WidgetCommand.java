package com.cloud.cms.command;

import java.util.List;

/**
 * File: WidgetCommand.java
 * Author: Landy
 * Create: 2019/6/11 15:22
 */
public class WidgetCommand {

    /**
     * 资源列表
     */
    private List<ResourceCommand> resourceCommandList;
    /**
     * 资源位置 目前 1=左，2=右
     */
    private String position;

    /**
     * 宽 高
     */
    private int width;
    private int height;

    /**
     * 边距
     */
    private int left=0;
    private int top=0;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 资源类型
     */
    private String type;

    public List<ResourceCommand> getResourceCommandList() {
        return resourceCommandList;
    }

    public void setResourceCommandList(List<ResourceCommand> resourceCommandList) {
        this.resourceCommandList = resourceCommandList;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
