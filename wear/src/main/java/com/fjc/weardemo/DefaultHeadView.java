package com.fjc.weardemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by FJC on 2016/10/28.
 */
public class DefaultHeadView extends HeadView {

    TextView tv_refresh;

    public DefaultHeadView(Context context) {
        super(context);
        init();

    }

    public DefaultHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    private void init() {
//        for(int i=0;i<getChildCount();i++){
//            if(getChildAt(i).getId()==R.id.tv_refresh){
//                tv_refresh = (TextView) getChildAt(i);
//                break;
//            }
//        }
        setGravity(Gravity.CENTER_HORIZONTAL);
        tv_refresh = new TextView(context);
        tv_refresh.setText("下拉刷新");
        tv_refresh.setPadding(20, 20, 20, 20);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv_refresh.setLayoutParams(layoutParams);
        addView(tv_refresh, 0);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);



    }

    @Override
    void onPositionChange(int offset) {

    }

    @Override
    void onPercent(float percent) {
        if (percent >= 1.0) {
            tv_refresh.setText("松开刷新");
        } else {
            tv_refresh.setText("下拉刷新");
        }
    }


    @Override
    void onStart() {
        tv_refresh.setText("正在刷新");
    }

    @Override
    void onStop() {
        tv_refresh.setText("刷新完毕");
    }
}
