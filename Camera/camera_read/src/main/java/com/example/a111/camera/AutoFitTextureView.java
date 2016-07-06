package com.example.a111.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by 111 on 2016/5/30.
 */
public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspectRatio(int width, int height) {

        mRatioHeight = width;
        mRatioWidth = height;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heigth = MeasureSpec.getSize(heightMeasureSpec);
        if (mRatioWidth == 0 || mRatioHeight == 0) {
            setMeasuredDimension(width, heigth);
        } else {
            if (width < heigth * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(heigth * mRatioWidth / mRatioHeight, heigth);
            }
        }
    }
}
