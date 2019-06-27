package com.cloud.cms.manager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;
import java.util.Set;

/**
 * File: PreferenceManager.java
 * Author: Landy
 * Create: 2018/12/13 18:46
 */
public class PreferenceManager {
    public static final String PREFERENCE_NAME = "system_setting";
    private static SharedPreferences sharedPreferences;
    private static PreferenceManager preferencemManager;
    private static SharedPreferences.Editor editor;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 初始化
     * @param cxt
     */
    public static synchronized void init(Context cxt) {
        if (preferencemManager == null) {
            preferencemManager = new PreferenceManager(cxt);
        }
    }

    public synchronized static PreferenceManager getInstance() {
        if (preferencemManager == null) {
            throw new RuntimeException("please init PreferenceManager first!");
        }
        return preferencemManager;
    }

    /**
     * 注册状态
     * @param registerState
     */
    public void setRegisterState(int registerState) {
        putInt("registerState", registerState);
    }

    public int getRegisterState() {
       return getInt("registerState");
    }

    /**
     * 第一次登陆
     * @param firstStart
     */
    public void setIsFirstStart(boolean firstStart) {
       putBoolean("firstStart",firstStart);
    }

    public boolean getIsFirstStart() {
        return getBoolean("firstStart",true);
    }

    /**
     * 横竖屏
     * @return
     */
    public int getOrientation() {
        return getInt("orientation");
    }

    public void setOrientation(int orientation) {
        putInt("orientation", orientation);
    }


    /**
     * 模板内容
     * @param template
     */
    public void setTemplate(String template) {
        putString("template",template);
    }

    public String getTemplate() {
        return getString("template");
    }

    /**
     * 商品信息
     * @param programs
     */
    public void setPrograms(String programs) {
        putString("programs",programs);
    }

    public String getPrograms() {
        return getString("programs");
    }

    /**
     * 商品信息
     * @param products
     */
    public void setProducts(String products) {
        putString("products",products);
    }

    public String getProducts() {
        return getString("products");
    }

    /**
     * 下载完成的节目模板
     * @param templateListId
     * @return
     */
    public boolean getIsProgramListCompleteDownload(String templateListId) {
        return getBoolean("programList_"+templateListId,false);
    }

    public void setIsProgramListCompleteDownload(String templateListId,boolean isCompleteDownload) {
        putBoolean("programList_"+templateListId, isCompleteDownload);
    }

    public boolean getIsProgramCompleteDownload(String templateListId,String programId) {
        return getBoolean(templateListId+"_"+programId,false);
    }

    public void setIsProgramCompleteDownload(String templateListId,String programId,boolean isCompleteDownload) {
        putBoolean(templateListId+"_"+programId, isCompleteDownload);
    }

    public void setUserName(String userName) {
        putString("userName",userName);
    }

    public String getUserName() {
        return getString("userName");
    }

    public String getPublishMessageId(String templatesId) {
        return getString(templatesId+"_publishMessageId");
    }

    public void setPublishMessageId(String templatesId,String publishMessageId) {
        putString(templatesId+"_publishMessageId", publishMessageId);
    }

    public String getTemplatesId() {
        return getString("templateListId");
    }

    public void setTemplatesId(String templateListId) {
        putString("templateListId", templateListId);
    }

    /**
     * 下载进度
     * @param str
     */
    public void setResId(String str) {
        putString("ResId", str);
    }

    public String getResId() {
        return getString("ResId");
    }




    /**
     * float
     * @param key
     * @param value
     */
    private void putFloat(String key,float value){
        editor.putFloat(key,value);
        editor.commit();
    }
    public float getFloat(String key) {
        return sharedPreferences.getFloat(key,0);
    }

    /**
     * int
     * @param key
     * @param value
     */
    private void putInt(String key,int value){
        editor.putInt(key,value);
        editor.commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key,0);
    }

    /**
     * boolean
     * @param key
     * @param value
     */
    private void putBoolean(String key,boolean value){
        editor.putBoolean(key,value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }

    public boolean getBoolean(String key,boolean defValue) {
        return sharedPreferences.getBoolean(key,defValue);
    }

    /**
     *
     * @param key
     * @param value
     */
    private void putLong(String key,long value){
        editor.putLong(key,value);
        editor.commit();
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key,0);
    }

    private void putStringSet(String key,Set<String> values){
        editor.putStringSet(key,values);
        editor.commit();
    }

    /**
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return sharedPreferences.getString(key,"");
    }

    private void putString(String key,String values){
        editor.putString(key,values);
        editor.commit();
    }
}
