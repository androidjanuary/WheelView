package com.fjc.weardemo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by FJC on 2016/10/28.
 */
public abstract class HeadView extends LinearLayout {

    Context context;

    public HeadView(Context context) {
        super(context);
        this.context = context;
    }

    public HeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public HeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }


    /**
     * 下拉偏移量
     *
     * @param offset
     */
    abstract void onPositionChange(int offset);

    /**
     * 下拉的百分比进度
     *
     * @param percent
     */
    abstract void onPercent(float percent);

    /**
     * 开始刷新
     */
    abstract void onStart();


    /**
     * 刷新完毕
     */
    abstract void onStop();

}
