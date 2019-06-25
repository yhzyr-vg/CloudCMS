package com.cloud.cms.manager.template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloud.cms.CloudCMSApplication;
import com.cloud.cms.command.ResourceCommand;
import com.cloud.cms.command.WidgetCommand;
import com.cloud.cms.config.Config;
import com.cloud.cms.util.Validator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**图片管理
 * File: ImageViewManager.java
 * Author: Landy
 * Create: 2019/6/12 10:27
 */
public class ImageViewManager {

    private static String tag="ImageViewManager";
    /**
     *用glide 加载图片
     * @param filePath
     * @param context
     * @param imageView
     * @param width  view 的宽度
     */
    public void showImage(String filePath,Context context,ImageView imageView,int width){
        if(width==RelativeLayout.LayoutParams.MATCH_PARENT){//如果是全屏
            width= Config.SCREEN_WIDTH;//设置为屏幕的宽度
        }
        float percent=0.5f; //缩放比例
        width=(int)(width*percent);
        int[] width_height=getImageWidthHeight(filePath);//图片的原始宽高
        float ratio=(float) ((width_height[0]*1.0)/(width*1.0));  //等比例
        int height=(int) (width_height[1]*1.0/ratio);    //等比缩放高度
        Log.i(tag,"原始宽高："+width_height[0]+"  高："+width_height[1]+"  showImage:width:"+width+"  height:"+height+"  全屏宽："+Config.SCREEN_WIDTH+"  全屏高："+Config.SCREEN_HEIGHT);
        Glide.with(context)
                .load(new File(filePath))
                .override(width, height) //设置宽高
                .skipMemoryCache(true)//禁止缓存
                .crossFade()           //跳过动画
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)  //不设置缓存
                .into(imageView);
    }

    public List<String> getResourcePathList(WidgetCommand widgetCommand){
        if(Validator.isNullOrEmpty(widgetCommand)||Validator.isNullOrEmpty(widgetCommand.getResourceCommandList())){
            return null;
        }
        List<String> list=new ArrayList<String>();
        for(ResourceCommand resourceCommand:widgetCommand.getResourceCommandList()){
            list.add(resourceCommand.getUrl());
        }
        return list;

    }

    //在不加载图片情况下获取图片大小
    public static int[] getImageWidthHeight(String path)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }
}
