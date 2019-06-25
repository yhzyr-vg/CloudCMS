package com.cloud.cms.manager.template;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloud.cms.command.ResourceCommand;
import com.cloud.cms.command.TemplateCommand;
import com.cloud.cms.command.WidgetCommand;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.TemplateConstants;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.util.FullScreenVideoView;
import com.cloud.cms.util.Validator;
import com.youth.banner.Banner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**模板管理
 * File: TemplateManager.java
 * Author: Landy
 * Create: 2019/6/11 15:09
 */
public class TemplateManager {
    private static String tag="TemplateManager";
    public static final String TYPE_IMAGE="jpg,png,jpeg,bmp";
    public static final String TYPE_VIDEO="mp4,mov";
    public static String [] WIDGET_POSITION={TemplateConstants.WIDGET_POSITION_LEFT,TemplateConstants.WIDGET_POSITION_RIGHT};

    private ImageViewManager imageViewManager=new ImageViewManager();
    int curIndex=0;
    private List<String> mVideoList = new ArrayList<String>();
    /**
     * 保存模板
     * @param templateCommand
     */
    public void saveTemplate(TemplateCommand templateCommand){
        String templateCommandStr=PreferenceManager.getInstance().getTemplate();
        List<TemplateCommand> templateCommandList=null;
        if(Validator.isNotNullOrEmpty(templateCommandStr)){
            Log.i(tag,"=======Template history: "+templateCommandStr);
            templateCommandList= JSON.parseArray(templateCommandStr,TemplateCommand.class);
        }
        if(Validator.isNullOrEmpty(templateCommandList)){
            templateCommandList=new ArrayList<TemplateCommand>();
        }
        templateCommandList.add(templateCommand);
        PreferenceManager.getInstance().setTemplate(JSON.toJSONString(templateCommandList));
    }

    /**
     * 根据id 获取模板
     * @param id
     * @return
     */
    public TemplateCommand getTemplateById(Long id){
        try{
            TemplateCommand templateCommand=null;
            String templateCommandStr=PreferenceManager.getInstance().getTemplate();
            List<TemplateCommand> templateCommandList=null;
            if(Validator.isNotNullOrEmpty(templateCommandStr)){
                templateCommandList= JSON.parseArray(templateCommandStr,TemplateCommand.class);
            }else{
                return null;
            }
            if(Validator.isNotNullOrEmpty(templateCommandList)){
                int tid=Integer.parseInt(id+"");
                templateCommand=templateCommandList.get(tid);
            }
            return templateCommand;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 前端传入的数据转化为模板
     * @param paramstr
     * @return
     */
    public TemplateCommand getTemplate(String paramstr){
        TemplateCommand templateCommand= JSON.parseObject(paramstr,TemplateCommand.class); //解析json
        if(Validator.isNotNullOrEmpty(templateCommand)&&Validator.isNotNullOrEmpty(templateCommand.getImgList())){
            String str=templateCommand.getImgList().substring(0,templateCommand.getImgList().length()-1);
            if(Validator.isNotNullOrEmpty(str)){  ///data/user/0/com.cloud.cms/files/jpg20190606192136.jpg_1;/data/user/0/com.cloud.cms/files/jpg20190606192147.jpg_1;
                String [] res=str.split(";");
                List<ResourceCommand> resourceCommandList=new ArrayList<ResourceCommand>();
                for(String resStr:res){
                    String [] resource=resStr.split(",");
                    if(Validator.isNotNullOrEmpty(resource)&&resource.length==2){
                        ResourceCommand resourceCommand=new ResourceCommand();
                        resourceCommand.setUrl(resource[0]);
                        resourceCommand.setPosition(resource[1]);
                        resourceCommand.setType(resource[0].substring(resource[0].lastIndexOf(".") + 1).toLowerCase());
                        resourceCommandList.add(resourceCommand);
                    }
                }

                Log.i(tag, "resourceCommandList:"+JSON.toJSONString(resourceCommandList));
                if(Validator.isNotNullOrEmpty(resourceCommandList)){
                    List<WidgetCommand> widgetCommandList=new ArrayList<WidgetCommand>();
                    //对资源按位置分类,前端输入的数据必须限定 一个widget 里只能输入一种类型的资源，要不就是图片，要不就是视频,也就是一个位置只允许输入一种类型的资源
                    //这样按照位置区分资源的时候，能确保按位置分类的资源是正确的
                    for(String position:WIDGET_POSITION){
                        List<ResourceCommand> resourceList=getResourceByPosition(position,resourceCommandList);
                        if(Validator.isNotNullOrEmpty(resourceList)){
                            WidgetCommand widgetCommand=new WidgetCommand();
                            String type=null;
                            if(isImage(resourceList.get(0).getType())){//图片
                                type=TemplateConstants.WIDGET_TYPE_IMAGE;
                            }else if(isVideo(resourceList.get(0).getType())){
                                type=TemplateConstants.WIDGET_TYPE_VIDEO;
                            }
                            widgetCommand.setType(type);
                            widgetCommand.setResourceCommandList(resourceList);
                            widgetCommand.setPosition(position);
                            //计算widget的宽高 位置
                            widgetCommand=getWidgetHeight(widgetCommand,templateCommand);
                            widgetCommandList.add(widgetCommand);
                        }
                    }
                    templateCommand.setWidgetCommandList(widgetCommandList);
                }
            }
        }
        return templateCommand;
    }

    /**
     * 计算宽高
     * @param widgetCommand
     * @param templateCommand
     * @return
     */
    private WidgetCommand getWidgetHeight(WidgetCommand widgetCommand,TemplateCommand templateCommand){ //目前模板分 1.全屏，2:50%，3:30% ,再根据横竖屏划分
        if(templateCommand.getScreenType().intValue()==1){//全屏
            widgetCommand.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
            widgetCommand.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
            return widgetCommand;
        }
        int width=0;
        int height=0;
        int left=0;
        int top=0;
        int percent=100; //百分比
        if(templateCommand.getScreenType().intValue()==2){
            percent=50;
        } else if(templateCommand.getScreenType().intValue()==3){
            percent=30;
        }
        if(TemplateConstants.TEMPLATE_SCREEN_ORIENTATION_LANDSCAPE.equals(templateCommand.getOrientation())){//横屏 高度100%
            if(TemplateConstants.WIDGET_POSITION_LEFT.equals(widgetCommand.getPosition())){//左
                width=Config.SCREEN_WIDTH * percent /100;
            }else{//右
                width=Config.SCREEN_WIDTH * (100 -percent )/100;
                left=Config.SCREEN_WIDTH * percent /100;
            }
            height=Config.SCREEN_HEIGHT;
        }else{//竖屏 宽度100%
            if(TemplateConstants.WIDGET_POSITION_LEFT.equals(widgetCommand.getPosition())){//上
                height=Config.SCREEN_HEIGHT * percent /100;
            }else{//下
                height=Config.SCREEN_HEIGHT *(100-percent)/100;
                top=Config.SCREEN_HEIGHT * percent /100;
            }
            width=Config.SCREEN_WIDTH;
        }
        Log.i(tag,Config.SCREEN_WIDTH+" widget width:"+width+"  height:"+height+"  left:"+left+"  top:"+top);
        widgetCommand.setWidth(width);
        widgetCommand.setHeight(height);
        widgetCommand.setLeft(left);
        widgetCommand.setTop(top);
        return widgetCommand;
    }

    /***
     * 创建页面
     * @param templateCommand
     */
    public void createView(TemplateCommand templateCommand,Context context,RelativeLayout main_layout){
        if(Validator.isNullOrEmpty(templateCommand)||Validator.isNullOrEmpty(templateCommand.getWidgetCommandList())){
            return;
        }
        //先移除所有的view
        main_layout.removeAllViews();
        //根据模板的widget 循环加载资源
        for(WidgetCommand widgetCommand:templateCommand.getWidgetCommandList()){
            widgetCommand=getWidgetHeight(widgetCommand,templateCommand);
            createWidget(widgetCommand,context,main_layout);
        }
    }

    /**
     * 创建widget 根据资源类型加载 视频或者图片
     * @param widgetCommand
     * @param context
     * @param main_layout
     */
    public void createWidget(WidgetCommand widgetCommand,Context context,RelativeLayout main_layout){
        if(Validator.isNullOrEmpty(widgetCommand)||Validator.isNullOrEmpty(widgetCommand.getResourceCommandList())){
            Log.e(tag,"resource is null");
            return;
        }
        if(Validator.isNullOrEmpty(main_layout)){
            Log.e(tag,"main_layout is null");
            return;
        }
        //定为widget，设置width,height,left,top
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widgetCommand.getWidth(), widgetCommand.getHeight());
        params.setMargins(widgetCommand.getLeft(), widgetCommand.getTop(), 0, 0);

        switch (widgetCommand.getType()){//类型： 目前只有图片和视频
            case TemplateConstants.WIDGET_TYPE_IMAGE://处理图片
                if(widgetCommand.getResourceCommandList().size()==1){//只有一张图片，就创建一个imageView
                    ImageView imageView = new ImageView(context);//创建imageview
                    imageView.setLayoutParams(params);   //设置位置
                    main_layout.addView(imageView);//imageview  加载到layout上
                    //显示图片
                    ResourceCommand resourceCommand=widgetCommand.getResourceCommandList().get(0);
                    imageViewManager.showImage(resourceCommand.getUrl(),context,imageView);
                }else  if(widgetCommand.getResourceCommandList().size() > 1){//多张图片，以轮播的方式展示图片，轮播动画有多重，淡入淡出，左右滑动轮播等
                    //这里使用一个第三方的轮播框架 Banner
                    Banner banner=new Banner(context);
                    banner.setLayoutParams(params);
                    main_layout.addView(banner);
                    //设置图片加载器
                    banner.setImageLoader(new GlideImageLoader());
                    //设置图片集合
                    banner.setImages(imageViewManager.getResourcePathList(widgetCommand));
                    //banner设置方法全部调用完毕时最后调用
                    banner.start();
                }
                break;
            case TemplateConstants.WIDGET_TYPE_VIDEO://处理视频，视频只有一个
                if(widgetCommand.getResourceCommandList().size()==1){
                    FullScreenVideoView videoView=new FullScreenVideoView(context);
                    videoView.setLayoutParams(params);
                    main_layout.addView(videoView);
                    ResourceCommand resourceCommand=widgetCommand.getResourceCommandList().get(0);
                    videoView.setVideoPath(resourceCommand.getUrl());
                    videoView.start();
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.start();
                            mp.setLooping(true);
                        }
                    });
                }else if(widgetCommand.getResourceCommandList().size()>1) {
                    final FullScreenVideoView videoView = new FullScreenVideoView(context);
                    videoView.setLayoutParams(params);
                    main_layout.addView(videoView);
                    ResourceCommand resourceCommand = widgetCommand.getResourceCommandList().get(curIndex);
                }
                break;

            default:break;
        }
    }

    /**
     * 加载视频
     * @param filePath
     * @param context
     * @param videoView
     */
    private void showVideo(String filePath, Context context, VideoView videoView){
        //播放视频的代码

    }


    /**
     * 前端输入的数据必须限定 一个widget 里只能输入一种类型的资源，要不就是图片，要不就是视频
     * @param position
     */
    private  List<ResourceCommand> getResourceByPosition(String position,List<ResourceCommand> resourceList){
        List<ResourceCommand> resourceCommandList=new ArrayList<ResourceCommand>();//目前只有两种资源，图片或者视频,这里确定资源的位置
        for(ResourceCommand resourceCommand :resourceList){
            if(position.equals(resourceCommand.getPosition())){
                resourceCommandList.add(resourceCommand);
            }
        }
        return resourceCommandList;
    }

    /**
     * 图片
     * @param type
     * @return
     */
    public static boolean isImage(String type){
        return TYPE_IMAGE.contains(type.toLowerCase());
    }

    /**
     * 视频
     * @param type
     * @return
     */
    public static boolean isVideo(String type){
        return TYPE_VIDEO.contains(type.toLowerCase());
    }

}
