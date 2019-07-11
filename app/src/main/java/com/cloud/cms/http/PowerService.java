package com.cloud.cms.http;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.cloud.cms.config.Config;

public class PowerService extends Service {
    PowerManageReceiver powerManageReceiver;
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("cycle", "PowerService.onCreate");
        powerManageReceiver=new PowerManageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ORIENTATION_ACTION);
        registerReceiver(powerManageReceiver, filter);

    }

    class PowerManageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {

            final Intent intent=arg1;
            final Context context=arg0;

            new Thread(new Runnable() {
                public void run() {
                    String action = intent.getAction();
                    Log.i("onReceive", "PowerManageReceiver.intent.action=" + action);
                    if(action.equals(Config.ORIENTATION_ACTION)){
                        int orientation = intent.getIntExtra("orientation",0);
                        handler.sendEmptyMessage(orientation);
                    }
                }

            }).start();
        }

    }

    public boolean getIsContain(String[] strArr, String str) {
        for (String s : strArr) {
            if (Integer.parseInt(s) == Integer.parseInt(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean getIsSameTime(String timing_time, int currentHour, int currentMinute) {
        if(timing_time!=null&&!timing_time.equals("notSelect")){

            try {
                String[] timeArr = timing_time.split(":");
                if (Integer.parseInt(timeArr[0]) == currentHour) {
                    if (Integer.parseInt(timeArr[1]) == currentMinute) {
                        return true;
                    }
                }
            } catch (Exception e) {

            }
        }
        return false;
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            toChangeOrientation(msg.what);

        }
    };


    public void toChangeOrientation(int orientation) {
        Log.i("mytest", "PowerManageReceiver.toChangeOrientation orientation="+orientation);
        WindowManager mWindow = (WindowManager)getSystemService(WINDOW_SERVICE);
        CustomLayout mLayout=new CustomLayout();
        mLayout.screenOrientation = orientation;
        mWindow.addView(new View(PowerService.this), mLayout);
        Log.i("mytest", "PowerManageReceiver   Config.SCREEN_WIDTH="+ Config.SCREEN_WIDTH+",Config.SCREEN_HEIGHT="+ Config.SCREEN_HEIGHT);
    }

    static class CustomLayout extends WindowManager.LayoutParams {

        public CustomLayout() {
            super(0, 0, TYPE_SYSTEM_OVERLAY, FLAG_FULLSCREEN | FLAG_NOT_FOCUSABLE, PixelFormat.RGBX_8888);
            this.gravity = Gravity.TOP;
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("cycle", "PowerService.onDestroy");
        unregisterReceiver(powerManageReceiver);
    }
}
