package com.cloud.cms.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cloud.cms.R;

public class TemplateActivity extends BaseActivity {

    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);
        context=this;
        showDailog(context,"test","1111");
    }
}
