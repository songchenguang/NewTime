package com.tencent.newtime.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Guang on 2016/7/10.
 */
public class IconTextView extends TextView {
    public IconTextView(Context context, AttributeSet attrs){
        super(context,attrs);
        Typeface typeface=Typeface.createFromAsset(context.getAssets(),"font/iconfont.ttf");
        setTypeface(typeface);
    }
}



