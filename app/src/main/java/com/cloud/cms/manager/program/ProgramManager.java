package com.cloud.cms.manager.program;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cloud.cms.api.model.Program;
import com.cloud.cms.config.Config;
import com.cloud.cms.constants.URLConstants;
import com.cloud.cms.http.BaseCallBack;
import com.cloud.cms.http.BaseOkHttpClient;
import com.cloud.cms.manager.PreferenceManager;
import com.cloud.cms.util.Validator;

import java.io.IOException;

import okhttp3.Call;

/**
 * File: ProgramManager.java
 * Author: Landy
 * Create: 2019/1/7 13:49
 */
public class ProgramManager {

    private static final String TAG="ProgramManager";

    /**
     * 获取资源
     */
    private void getResource(){
        BaseOkHttpClient.newBuilder().url(URLConstants.GET_SCHEDULE_URL)
                .addParam("deviceid",Config.SERIAL_NUMBER)
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
}
