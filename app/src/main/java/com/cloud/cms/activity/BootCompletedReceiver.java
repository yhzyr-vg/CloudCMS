package com.cloud.cms.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver  extends BroadcastReceiver {
    private static final String TAG=BootCompletedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(TAG, "BOOT_COMPLETED,start CMSActivity……");
            Intent cmsntent = new Intent(context, CMSActivity.class);
            cmsntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(cmsntent);
        }
    }
}
