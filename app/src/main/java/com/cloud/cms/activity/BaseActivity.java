package com.cloud.cms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //黄油刀 view 注解框架
        ButterKnife.bind(this);
    }

    /**
     * show Dailog
     * @param title
     * @param context
     */
    public void showDailog(Context context,String title,String message){
        new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        } ).show();
    }

    /**
     * toast
     * @param context
     * @param message
     */
    public void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}

