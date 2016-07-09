package com.tencent.newtime.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.newtime.util.BitmapUtils;
import com.tencent.newtime.util.DimensionUtils;
import com.tencent.newtime.R;

public class TabItem extends LinearLayout {
    private Context mContext;
    private ImageView mImageView;
    private TextView mTextView;


    Drawable mDrawableEnabled;
    Drawable mDrawableCommon;
    String mText;
    int mColorEnabled;
    int mColorCommon;

    private boolean mEnable = false;
    public TabItem(Context context) {
        this(context, null);
    }

    public TabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.mTabItem);
        mDrawableCommon = a.getDrawable(R.styleable.mTabItem_image);
        mText = a.getString(R.styleable.mTabItem_text);
        a.recycle();

        mColorEnabled = mContext.getResources().getColor(R.color.colorPrimary);
        mColorCommon = mContext.getResources().getColor(R.color.colorGray);

        if(!(mDrawableCommon instanceof BitmapDrawable)){
            throw new RuntimeException("should use BitmapDrawable as TabItem drawable");
        }
        Bitmap b = ((BitmapDrawable)mDrawableCommon).getBitmap();
        Bitmap out = BitmapUtils.changeColor(b,mColorEnabled);
        mDrawableEnabled = new BitmapDrawable(mContext.getResources(), out);
        setOrientation(VERTICAL);

        mImageView = new ImageView(mContext);
        mImageView.setBackgroundDrawable(mDrawableCommon);
        LayoutParams params = new LayoutParams(DimensionUtils.dp2px(20), DimensionUtils.dp2px(20));
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, DimensionUtils.dp2px(2), 0,DimensionUtils.dp2px(4));
        addView(mImageView, params);

        mTextView = new TextView(mContext);
        mTextView.setTextSize(10);
        mTextView.setTextColor(mColorCommon);
        mTextView.setText(mText);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        addView(mTextView,params);
    }

    public void setEnable(boolean enable){
        if(mEnable!=enable) {
            mEnable = enable;
            mImageView.setBackgroundDrawable(enable ? mDrawableEnabled : mDrawableCommon);
            mTextView.setTextColor(enable ? mColorEnabled : mColorCommon);
            invalidate();
        }
    }


}
