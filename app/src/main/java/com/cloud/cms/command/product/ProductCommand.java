package com.cloud.cms.command.product;

import com.cloud.cms.command.ResourceCommand;

/**
 * File: ProductCommand.java
 * Author: Landy
 * Create: 2019/6/25 16:40
 */
public class ProductCommand {

    private int id;
    /**
     * 商品名
     */
    private String name;
    /**
     * 商品资源
     */
    private String productResource;
    /**
     * 商品资源缩略图  在手机端显示
     */
    private String thumbnail;
    /**
     * 商品资源类型 视频或图片  1 图片 2  视频
     */
    private String productResourceType;

    /**
     * 显示位置  01  手机  02  TV
     */
    private String position;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getProductResource() {
        return productResource;
    }

    public void setProductResource(String productResource) {
        this.productResource = productResource;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProductResourceType() {
        return productResourceType;
    }

    public void setProductResourceType(String productResourceType) {
        this.productResourceType = productResourceType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProductCommand) {
            ProductCommand productCommand = (ProductCommand) obj;
            return (name.equals(productCommand.name));
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        ProductCommand productCommand = (ProductCommand) this;
        return name.hashCode();

    }
}
