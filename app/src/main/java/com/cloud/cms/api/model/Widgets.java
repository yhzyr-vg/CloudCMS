package com.cloud.cms.api.model;

import java.util.List;

/**
 * Widget
 */
public class Widgets {
    private int id;
    private WidgetType type;
    private float location_x;
    private float location_y;

    private float widthPercent;
    private float heightPercent;

    private int fontsize;
    private String fontcolor;
    private String text;
    private String fontbgcolor;
    private String fontfamily;
    private String showtype;
    private String halign;
    private String valign;

    private int playDuration;
    private int style;

    private String htmlurl;
    private List<WidgetResource> resources;

    private int left;
    private int top;
    private int height;
    private int width;
    private String zipPath;
    private String htmlPath;
    private List<String> resourcePathList;
    private List<String> resourceUrlList;

    public List<String> getResourcePathList() {
        return resourcePathList;
    }

    public void setResourcePathList(List<String> resourcePathList) {
        this.resourcePathList = resourcePathList;
    }

    public List<String> getResourceUrlList() {
        return resourceUrlList;
    }

    public void setResourceUrlList(List<String> resourceUrlList) {
        this.resourceUrlList = resourceUrlList;
    }

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

    public String getZipPath() {
        return zipPath;
    }

    public void setZipPath(String zipPath) {
        this.zipPath = zipPath;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public WidgetType getType() {
        return type;
    }

    public void setType(WidgetType type) {
        this.type = type;
    }

    public float getLocation_x() {
        return location_x;
    }

    public void setLocation_x(float location_x) {
        this.location_x = location_x;
    }

    public float getLocation_y() {
        return location_y;
    }

    public void setLocation_y(float location_y) {
        this.location_y = location_y;
    }

    public float getWidthPercent() {
        return widthPercent;
    }

    public void setWidthPercent(float widthPercent) {
        this.widthPercent = widthPercent;
    }

    public float getHeightPercent() {
        return heightPercent;
    }

    public void setHeightPercent(float heightPercent) {
        this.heightPercent = heightPercent;
    }

    public int getFontsize() {
        return fontsize;
    }

    public void setFontsize(int fontsize) {
        this.fontsize = fontsize;
    }

    public String getFontcolor() {
        return fontcolor;
    }

    public void setFontcolor(String fontcolor) {
        this.fontcolor = fontcolor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFontbgcolor() {
        return fontbgcolor;
    }

    public void setFontbgcolor(String fontbgcolor) {
        this.fontbgcolor = fontbgcolor;
    }

    public String getFontfamily() {
        return fontfamily;
    }

    public void setFontfamily(String fontfamily) {
        this.fontfamily = fontfamily;
    }

    public String getShowtype() {
        return showtype;
    }

    public void setShowtype(String showtype) {
        this.showtype = showtype;
    }

    public String getHalign() {
        return halign;
    }

    public void setHalign(String halign) {
        this.halign = halign;
    }

    public String getValign() {
        return valign;
    }

    public void setValign(String valign) {
        this.valign = valign;
    }

    public int getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(int playDuration) {
        this.playDuration = playDuration;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getHtmlurl() {
        return htmlurl;
    }

    public void setHtmlurl(String htmlurl) {
        this.htmlurl = htmlurl;
    }

    public List<WidgetResource> getResources() {
        return resources;
    }

    public void setResources(List<WidgetResource> resources) {
        this.resources = resources;
    }
}
