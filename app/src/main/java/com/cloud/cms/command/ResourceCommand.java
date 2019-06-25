package com.cloud.cms.command;

import java.io.Serializable;

/**
 * File: ResourceCommand.java
 * Author: Landy
 * Create: 2019/6/4 18:49
 */
public class ResourceCommand implements Serializable {
    /**
     * 资源链接
     */
   private String  url;
   /**
    * 资源位置 目前 1=左，2=右
    */
   private String position;

   /**
    * 资源类型
    */
   private String type;

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
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
