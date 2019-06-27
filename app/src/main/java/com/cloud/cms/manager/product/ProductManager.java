package com.cloud.cms.manager.product;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.CloudCMSApplication;
import com.cloud.cms.command.ProgramFilesCommand;
import com.cloud.cms.command.ResultCommand;
import com.cloud.cms.command.product.ProductCommand;
import com.cloud.cms.constants.ActionConstants;
import com.cloud.cms.constants.FileConstants;
import com.cloud.cms.constants.ProductConstants;
import com.cloud.cms.manager.template.TemplateManager;
import com.cloud.cms.util.Validator;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * File: ProductManager.java
 * Author: Landy
 * Create: 2019/6/25 16:39
 */
public class ProductManager {


    private static final String tag="ProductManager";

    /**
     * 获取所有的商品
     * @param response
     */
    public void getAllProducts(AsyncHttpServerResponse response){
        ResultCommand resultCommand=new ResultCommand();
        List<ProductCommand> productCommandList=getAllProducts();
        if(Validator.isNotNullOrEmpty(productCommandList)){
            resultCommand.setReturnObject(productCommandList);
            resultCommand.setResult(true);
        }else{
            resultCommand.setResult(false);
        }
        response.send(JSON.toJSONString(resultCommand));
    }

    /**
     * 获取所有的商品
     * @return
     */
    public List<ProductCommand> getAllProducts(){
        String path=FileConstants.DEFAULT_TV_PRODUCT_PATH;
        File file=new File(path);
        if(!file.exists()){
            return null;
        }
        String[] fileNames = file.list();
        if (Validator.isNullOrEmpty(fileNames)||fileNames.length<=0){
            return null;
        }
        List<ProductCommand> productCommandList=new ArrayList<ProductCommand>();
        List<ProductCommand> productCommandTempList=new ArrayList<ProductCommand>();
        for (String fileName : fileNames) { //商品_01.jgp  如 周黑鸭_01.jpg
            String products=fileName.substring(0, fileName.lastIndexOf("."));
            String [] info=products.split("_");
            if(info.length==2){
                ProductCommand productCommand=new ProductCommand();
                productCommand.setName(info[0]);//商品名称
                productCommand.setProductResourceType(TemplateManager.getResourceType(TemplateManager.getExp(fileName))); //商品资源类型 视频或图片 1 图片 2  视频
                File sfile = new File(path, fileName);
                if(sfile.exists()){
                    Log.i(tag,"文件存在");
                    if(ProductConstants.PRODUCT_RESPURCE_POSITION_TV_RESOURCE.equals(info[1])){
                        productCommand.setProductResource(sfile.getAbsolutePath());
                        productCommandList.add(productCommand); //TV 上显示的资源
                    }else{
                        productCommand.setThumbnail(sfile.getAbsolutePath());//缩略图
                        productCommandTempList.add(productCommand);
                    }
                }
            }
        }
        if(Validator.isNotNullOrEmpty(productCommandList)&&Validator.isNotNullOrEmpty(productCommandTempList)){
            for(ProductCommand productCommand:productCommandList){
                for(ProductCommand tempproductCommand:productCommandTempList){
                    if(productCommand.equals(tempproductCommand)){//是同一个商品
                        productCommand.setThumbnail(tempproductCommand.getThumbnail());
                    }
                }
            }
        }
        return productCommandList;
    }
}
