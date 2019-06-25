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
}
