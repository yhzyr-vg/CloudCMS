package com.cloud.cms.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class FullScreenVideoView extends VideoView {

    public FullScreenVideoView(Context context) {
        super(context);

    }
    public FullScreenVideoView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);

    }
    public FullScreenVideoView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context,attributeSet,defStyle);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getDefaultSize(0,widthMeasureSpec);
        int height=getDefaultSize(0,heightMeasureSpec);
        setMeasuredDimension(width,height);
    }
}
