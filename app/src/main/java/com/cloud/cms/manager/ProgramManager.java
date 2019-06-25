package com.cloud.cms.manager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.CloudCMSApplication;
import com.cloud.cms.activity.DownloadResource;
import com.cloud.cms.api.model.Program;
import com.cloud.cms.api.model.ProgramData;
import com.cloud.cms.api.model.ProgramSchedule;
import com.cloud.cms.api.model.Template;
import com.cloud.cms.api.model.WidgetResource;
import com.cloud.cms.api.model.Widgets;
import com.cloud.cms.command.LoginCommand;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.http.BaseCallBack;
import com.cloud.cms.http.BaseOkHttpClient;
import com.cloud.cms.model.LoadItem;
import com.cloud.cms.model.ProgramItem;
import com.cloud.cms.sql.DatabaseHelper;
import com.cloud.cms.sql.SQLiteHelper;
import com.cloud.cms.util.FileUtil;
import com.cloud.cms.util.Validator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * File: ProgramManager.java
 * Author: Landy
 * Create: 2019/1/3 17:29
 */
public class ProgramManager {

    private static final String TAG="ProgramManager";

    public static void sendfinishTask(final String status, final String errorinfo,final String messageId,final String resId){
        BaseOkHttpClient.newBuilder().url(URLConstants.DOWNLOAD_COMPLETED_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
                .addParam("status", status)
                .addParam("errorinfo", errorinfo)
                .addParam("msgId", messageId)
                .addParam("resId", resId)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        if(Validator.isNotNullOrEmpty(o)){
                            String str=o.toString();
                            Log.i(TAG,"========"+o.toString());
                            PreferenceManager.getInstance().setPrograms(str);

                        }else{
                            Log.e(TAG,"return: null");
                        }
                    }
                    @Override
                    public void onError(int code) {
                        Log.e(TAG,"connect error"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,e.toString());
                    }
                });
    }
    /**
     * 获取资源
     */
    public static void getResource(){
        BaseOkHttpClient.newBuilder().url(URLConstants.GET_SCHEDULE_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
                .get()
                .build()
                .enqueue(new BaseCallBack(){

                    @Override
                    public void onSuccess(Object o) {
                        if(Validator.isNotNullOrEmpty(o)){
                            String str=o.toString();
                            Log.i(TAG,"========"+URLConstants.GET_SCHEDULE_URL+o.toString());
                            PreferenceManager.getInstance().setPrograms(str);
                            Intent intent=new Intent(CloudCMSApplication.getInstance().getBaseContext(),DownloadResource.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            CloudCMSApplication.getInstance().startActivity(intent);
                        }else{
                            Log.e(TAG,"return: null");
                        }
                    }
                    @Override
                    public void onError(int code) {
                        Log.e(TAG,"connect error"+code);
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,e.toString());
                    }
                });
    }

    public static List<ProgramItem> getProgramItemList(){
        List<ProgramItem> list=new ArrayList<ProgramItem>();
        Program program=getDownloadTemplate();

        Log.i(TAG,"===============program:"+JSON.toJSONString(program));
        String templatesId = program.getMessageId();
        List<Template> templateList = program.getuITemplateList();

        if(Validator.isNullOrEmpty(templateList)){
            return null;
        }
        for(Template template:templateList){
            List<LoadItem> loadItemList = template.getLoadItemList();
            list.add(new ProgramItem(template.getId(), loadItemList));
        }
        Log.i(TAG,"===============list:"+JSON.toJSONString(list));
        return list;
    };

    /**
     * 获取要下载的模板资源
     * @return
     */
    public static Program getDownloadTemplate() {
        Program programPublish = null;
        String programStr=PreferenceManager.getInstance().getPrograms();
        if(Validator.isNullOrEmpty(programStr)){
            return null;
        }
        try {
            programPublish=JSON.parseObject(programStr,Program.class);

            if (programPublish != null) {

                List<Template> uITemplateList = new ArrayList<Template>();
                List<ProgramData> rows = programPublish.getRows();
                if (rows == null) {
                    return null;
                }
                ProgramData data = rows.get(0);
                if (data == null) {
                    return null;
                }

                List<ProgramSchedule> scheduleoftimes = data.getScheduleoftimes();
                ContentValues values = new ContentValues();
                Cursor c = null;
                for (ProgramSchedule scheduleoftime : scheduleoftimes) {
                    for (Template uITemplate : scheduleoftime.getTemplates()) {

                        List<String> wholeUrlList = new ArrayList<String>();
                        List<String> urlList = new ArrayList<String>();

                        List<LoadItem> loadItemList = new ArrayList<LoadItem>();
                        String background = uITemplate.getBackground();

                        if (Validator.isNotNullOrEmpty(background)) {
                            urlList.add(background);


                            c = DatabaseHelper.getInstance().query(SQLiteHelper.RESOURCE_TB_NAME,
                                    new String[] { "wholeurl", "name", "complete" }, "url=?",
                                    new String[] { background }, null, null, null);

                            String background_path;
                            String background_url;
                            if (c.moveToNext()) {
                                Log.i("mytest", "background c.moveToNext()");
                                background_path = Config.DIR_CACHE + c.getString(c.getColumnIndex("name"));
                                background_url = c.getString(c.getColumnIndex("wholeurl"));
                                // Log.i("connect", "TemplateHelper.getTemplate
                                // background_path="+background_path);
                                if (FileUtil.getIsExists(background_path)) {
                                    int complete = c.getInt(c.getColumnIndex("complete"));
                                    if (complete == 0) {
                                        loadItemList.add(new LoadItem(background_path, background_url));
                                    }
                                } else {
                                    loadItemList.add(new LoadItem(background_path, background_url));
                                }
                                uITemplate.setBackground_path(background_path);
                                uITemplate.setBackground_url(background_url);
                            } else {
                                Log.i("mytest", "background !c.moveToNext()");
                                String endWith = background.substring(background.lastIndexOf("/") + 1);
                                String fileName = null;
                                String timeStr = Calendar.getInstance().getTimeInMillis() + "";
                                if (endWith.contains(".")) {
                                    fileName = timeStr + endWith.substring(endWith.lastIndexOf("."));
                                } else {
                                    fileName = timeStr;
                                }

                                background_path = Config.DIR_CACHE + fileName;

                                if (background.startsWith("http")) {
                                    background_url = background;
                                } else {
                                    background_url = URLConstants.BASE_URL + background;
                                }

                                values.clear();
                                values.put("date", timeStr);
                                values.put("url", background);
                                values.put("wholeurl", background_url);
                                values.put("name", fileName);
                                values.put("complete", 0);
                                DatabaseHelper.getInstance().insert(SQLiteHelper.RESOURCE_TB_NAME, null, values);
                                uITemplate.setBackground_path(background_path);
                                uITemplate.setBackground_url(background_url);

                                loadItemList.add(new LoadItem(Config.DIR_CACHE + fileName, background_url));
                            }
                            wholeUrlList.add(background_url);
                            c.close();
                        }

                        List<Widgets> widgets = uITemplate.getWidgets();
                        for (Widgets widget : widgets) {

                            double location_x = widget.getLocation_x();
                            double location_y = widget.getLocation_y();
                            double widthPercent = widget.getWidthPercent();
                            double heightPercent = widget.getHeightPercent();
                            int left = (int) (location_x * Config.SCREEN_WIDTH / 100);
                            int top = (int) (location_y * Config.SCREEN_HEIGHT / 100);
                            int width = (int) (widthPercent * Config.SCREEN_WIDTH / 100);
                            int height = (int) (heightPercent * Config.SCREEN_HEIGHT / 100);

                            widget.setLeft(left);
                            widget.setTop(top);
                            widget.setWidth(width);
                            widget.setHeight(height);

                            List<WidgetResource> resources = widget.getResources();
                            List<String> resourcePathList = new ArrayList<String>();
                            if (widget.getType().getId() == 5) {
                                String htmlurl = widget.getHtmlurl();
                                if (htmlurl != null && !"".equals(htmlurl) && !"null".equals(htmlurl)) {
                                    urlList.add(htmlurl);
                                    c = DatabaseHelper.getInstance().query(SQLiteHelper.RESOURCE_TB_NAME,
                                            new String[] { "date", "wholeurl", "name", "complete" }, "url=?",
                                            new String[] { htmlurl }, null, null, null);
                                    String zip_path;
                                    String html_path;
                                    String wholeHtmlUrl;
                                    if (c.moveToNext()) {
                                        Log.i("mytest", "h5 c.moveToNext()");
                                        zip_path = Config.DIR_CACHE + c.getString(c.getColumnIndex("name"));
                                        html_path = Config.DIR_CACHE + c.getString(c.getColumnIndex("date"))
                                                + "/index.html";
                                        wholeHtmlUrl = c.getString(c.getColumnIndex("wholeurl"));
                                        if (FileUtil.getIsExists(zip_path)) {
                                            int complete = c.getInt(c.getColumnIndex("complete"));
                                            if (complete == 0) {
                                                loadItemList.add(new LoadItem(zip_path, wholeHtmlUrl));
                                            }
                                        } else {
                                            loadItemList.add(new LoadItem(zip_path, wholeHtmlUrl));
                                        }
                                        widget.setZipPath(zip_path);
                                        widget.setHtmlPath(html_path);
                                        resourcePathList.add(html_path);
                                    } else {
                                        Log.i("mytest", "h5 !c.moveToNext()");
                                        values.clear();
                                        Log.i("mytest", "htmlurl=" + htmlurl);
                                        String endWith = htmlurl.substring(htmlurl.lastIndexOf("/") + 1);
                                        String fileName = null;
                                        String timeStr = Calendar.getInstance().getTimeInMillis() + "";

                                        if (endWith.contains(".")) {
                                            fileName = timeStr + endWith.substring(endWith.lastIndexOf("."));
                                        } else {
                                            fileName = timeStr + ".zip";
                                        }
                                        zip_path = Config.DIR_CACHE + fileName;
                                        html_path = Config.DIR_CACHE + timeStr + "/" + "index.html";
                                        if (htmlurl.startsWith("http")) {
                                            wholeHtmlUrl = htmlurl + ".zip";
                                        } else {
                                            wholeHtmlUrl = URLConstants.BASE_URL + htmlurl + ".zip";
                                        }

                                        values.put("date", timeStr);
                                        values.put("url", htmlurl);
                                        values.put("wholeurl", wholeHtmlUrl);
                                        values.put("name", fileName);
                                        values.put("complete", 0);
                                        DatabaseHelper.getInstance().insert(SQLiteHelper.RESOURCE_TB_NAME, null,
                                                values);
                                        widget.setZipPath(zip_path);
                                        widget.setHtmlPath(html_path);
                                        resourcePathList.add(html_path);
                                        loadItemList.add(new LoadItem(zip_path, wholeHtmlUrl));
                                    }
                                    wholeUrlList.add(wholeHtmlUrl);
                                    widget.setHtmlurl(wholeHtmlUrl);
                                    c.close();
                                }
                            }

                            for (WidgetResource resource : resources) {
                                String resUrl = resource.getResUrl();
                                urlList.add(resUrl);

                                /*
                                 * if (resUrl.startsWith("http")) { wholeResUrl = resUrl; } else { wholeResUrl =
                                 * Config.URL + resUrl;
                                 *
                                 * } wholeUrlList.add(wholeResUrl);
                                 */

                                c = DatabaseHelper.getInstance().query(SQLiteHelper.RESOURCE_TB_NAME,
                                        new String[] { "wholeurl", "name", "complete" }, "url=?",
                                        new String[] { resUrl }, null, null, null);
                                String path;
                                String wholeResUrl;
                                if (c.moveToNext()) {
                                    Log.i("mytest", "resource c.moveToNext()");
                                    path = Config.DIR_CACHE + c.getString(c.getColumnIndex("name"));
                                    wholeResUrl = c.getString(c.getColumnIndex("wholeurl"));
                                    // Log.i("connect", "TemplateHelper.getTemplate path="+path);
                                    if (FileUtil.getIsExists(path)) {

                                        int complete = c.getInt(c.getColumnIndex("complete"));

                                        if (complete == 0) {
                                            loadItemList.add(new LoadItem(path, wholeResUrl));
                                        }
                                    } else {
                                        loadItemList.add(new LoadItem(path, wholeResUrl));
                                    }
                                    resourcePathList.add(path);
                                } else {
                                    Log.i("mytest", "resource !c.moveToNext()");
                                    values.clear();

                                    String endWith = resUrl.substring(resUrl.lastIndexOf("/") + 1);

                                    String fileName = null;
                                    String timeStr = Calendar.getInstance().getTimeInMillis() + "";
                                    if (endWith.contains(".")) {
                                        fileName = timeStr + endWith.substring(endWith.lastIndexOf("."));
                                    } else {
                                        switch (widget.getType().getId()) {

                                            case 1:
                                                fileName = timeStr + ".mp4";
                                                break;
                                            case 2:
                                                fileName = timeStr + ".jpg";
                                                break;
                                            case 3:
                                                fileName = timeStr + ".txt";
                                                break;
                                            case 4:
                                                fileName = timeStr;
                                                break;
                                            case 5:
                                                fileName = timeStr + ".html";
                                                break;
                                            case 6:
                                                fileName = timeStr + ".html";
                                                break;
                                            case 7:
                                                fileName = timeStr + ".ppt";
                                                break;
                                        }

                                    }

                                    path = Config.DIR_CACHE + fileName;
                                    if (resUrl.startsWith("http")) {
                                        wholeResUrl = resUrl;
                                    } else {
                                        wholeResUrl = URLConstants.BASE_URL + resUrl;

                                    }
                                    values.put("date", timeStr);
                                    values.put("url", resUrl);
                                    values.put("wholeurl", wholeResUrl);
                                    values.put("name", fileName);
                                    values.put("complete", 0);
                                    DatabaseHelper.getInstance().insert(SQLiteHelper.RESOURCE_TB_NAME, null, values);

                                    resourcePathList.add(path);
                                    loadItemList.add(new LoadItem(path, wholeResUrl));
                                }
                                wholeUrlList.add(wholeResUrl);
                                c.close();
                            }
                            widget.setResourcePathList(resourcePathList);
                        }
                        uITemplate.setLoadItemList(loadItemList);
                        uITemplate.setWholeUrlList(wholeUrlList);
                        uITemplate.setUrlList(urlList);
                        if (!getIsContain(uITemplateList, uITemplate)) {
                            // Log.i()
                            uITemplateList.add(uITemplate);
                        }
                    }
                }
                programPublish.setuITemplateList(uITemplateList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return programPublish;
    }

    public static boolean getIsContain(List<Template> uITemplates, Template uITemplate) {
        for (Template template : uITemplates) {
            if (template.getId() == uITemplate.getId()) {
                return true;
            }
        }
        return false;
    }




    /**
     * 获取节目列表
     * @return
     */
    public static List<String> getProgramListList(){
        List<String> programListList = null;
        File FILE_CACHE = new File(Config.DIR_CACHE);
        File FILE_RESOURCE = new File(Config.RESOURCE_INFO);
        if (FILE_RESOURCE.exists()&&FILE_CACHE.exists()&&FILE_CACHE.isDirectory()) {
            programListList = new ArrayList<String>();
            String[] fileNameArr = FILE_RESOURCE.list();
            for (String fileName : fileNameArr) {
                if (PreferenceManager.getInstance().getIsProgramListCompleteDownload(fileName)) {
                    Log.i(TAG, "FileUtil fileName = "+fileName);
                    programListList.add(fileName);
                } else {
                    Log.i(TAG, "FileUtil 下载未完成 fileName = "+fileName);
                }
            }
            if (programListList.size() == 0) {
                return null;
            }
        }
        return programListList;
    }
}
