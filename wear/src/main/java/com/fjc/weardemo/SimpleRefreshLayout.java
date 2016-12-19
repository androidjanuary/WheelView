package com.fjc.weardemo;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;


/**
 * Created by FJC on 2016/10/27.
 */
public class SimpleRefreshLayout extends ViewGroup {

    int DRAG_MAX_DISTANCE = 64;
    int mTotalDragDistance;

    float DRAG_RATE = 0.5f;

    float pointY;
    int pointerId;
    int pointerIndex;

    int currentOffsetTop;
    private int mInitialOffsetTop;

    HeadView mHeadView;
    Context context;
    View mTarget;

    public int mDurationToStartPosition = 400; // 平移到顶部动画时长，400
    public int mDurationToCorrectPosition = 400; // 平移到刷新点动画时长,400
    private Interpolator mDecelerateInterpolator; // 减速插值器
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

    private float mDragPercent; // 下拉临界值的百分比

    boolean mDispatchTargetTouchDown = false;
    boolean mRefreshing = false;

    boolean mIsBeingDragged = false;

    int marginTop;

    public SimpleRefreshLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SimpleRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SimpleRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mHeadView = new DefaultHeadView(context);
        addView(mHeadView, 0);

        mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE);
        marginTop = -mTotalDragDistance;
    }

    public void setHeadView(HeadView headView) {
        setRefreshing(false);
        removeView(mHeadView);
        mHeadView = headView;
        addView(mHeadView, 0);
        if (android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }

//        mTotalDragDistance = mHeadView.getHeight();
        mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE);
        marginTop = -mTotalDragDistance;
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mHeadView)
                    mTarget = child;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTarget();
        if (mTarget == null)
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        Log.v("info", "widthMeasureSpec=" + widthMeasureSpec);
        Log.v("info", "heightMeasureSpec=" + heightMeasureSpec);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mHeadView.measure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        ensureTarget();
        if (mTarget == null)
            return;

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left,
                top + mTarget.getTop(),
                left + width - right,
                top + height - bottom + mTarget.getTop());

        mHeadView.layout(left,
                top + marginTop,
                left + width - right,
                top + height - bottom + marginTop
        );

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {


        final int action = MotionEventCompat.getActionMasked(event);
        pointerIndex = MotionEventCompat.getActionIndex(event);
        pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mRefreshing) {
                    setTargetOffsetTop(0);
                }
                Log.v("info", "onInterceptTouchEvent--ACTION_DOWN");
                pointY = MotionEventCompat.getY(event, pointerIndex);
                mInitialOffsetTop=currentOffsetTop;
            case MotionEvent.ACTION_MOVE:

//                if (canChildScrollUp()) {
//                    return false;
//                }

                Log.v("info", "onInterceptTouchEvent--ACTION_MOVE");
                float y = MotionEventCompat.getY(event, pointerIndex);

                float delta_y = y - pointY;

                if (delta_y > 10) {
                    return true;
                }

                if (delta_y < -10 && mRefreshing) {
                    return true;
                }

            case MotionEvent.ACTION_UP:
                Log.v("info", "onInterceptTouchEvent--ACTION_UP");
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                break;

        }


        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = MotionEventCompat.getActionMasked(event);

        pointerIndex = MotionEventCompat.getActionIndex(event);
        pointerId = MotionEventCompat.getPointerId(event, pointerIndex);


        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                Log.v("info", "onTouchEvent--ACTION_MOVE");
                int targetY = 0;
                float y = MotionEventCompat.getY(event, pointerIndex);
                float delta_y = y - pointY;
                if (mRefreshing) {

                    targetY = (int) (mInitialOffsetTop + delta_y);
                    if (canChildScrollUp()) {
                        pointY = y;
                        targetY = 0;
                        mInitialOffsetTop=0;
                        if (mDispatchTargetTouchDown) {
                            mTarget.dispatchTouchEvent(event);
                        } else {
                            MotionEvent obtain = MotionEvent.obtain(event);
                            obtain.setAction(MotionEvent.ACTION_DOWN);
                            mDispatchTargetTouchDown = true;
                            mTarget.dispatchTouchEvent(obtain);
                        }
                    } else {
                        if (targetY < 0) {
                            if (mDispatchTargetTouchDown) {
                                mTarget.dispatchTouchEvent(event);
                            } else {
                                MotionEvent obtain = MotionEvent.obtain(event);
                                obtain.setAction(MotionEvent.ACTION_DOWN);
                                mDispatchTargetTouchDown = true;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                            targetY = 0;
                        } else if (targetY > mTotalDragDistance) {
                            targetY = mTotalDragDistance;
                        } else {
                            if (mDispatchTargetTouchDown) {
                                MotionEvent obtain = MotionEvent.obtain(event);
                                obtain.setAction(MotionEvent.ACTION_CANCEL);
                                mDispatchTargetTouchDown = false;
                                mTarget.dispatchTouchEvent(obtain);
                            }
                        }
                    }


                } else {
                    if (canChildScrollUp()) {
                        if (mDispatchTargetTouchDown) {
                            mTarget.dispatchTouchEvent(event);
                        } else {
                            MotionEvent obtain = MotionEvent.obtain(event);
                            obtain.setAction(MotionEvent.ACTION_DOWN);
                            mDispatchTargetTouchDown = true;
                            mTarget.dispatchTouchEvent(obtain);
                        }
                    } else {
                        final float scrollTop = delta_y * DRAG_RATE;
                        float originalDragPercent = scrollTop / mTotalDragDistance;
                        mDragPercent = Math.min(1f, Math.abs(originalDragPercent));

                        targetY = (int) (scrollTop > mTotalDragDistance * 2 ? mTotalDragDistance * 2 : scrollTop);
                        targetY = targetY > 0 ? targetY : 0;
                        if (mHeadView.getVisibility() != View.VISIBLE) {
                            mHeadView.setVisibility(View.VISIBLE);
                        }
                        mHeadView.onPercent(mDragPercent);
                    }

                }
                setTargetOffsetTop(targetY - currentOffsetTop);

                break;
            }
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                Log.v("info", "onTouchEvent--ACTION_UP");
                final float y = MotionEventCompat.getY(event, pointerIndex);
                final float overscrollTop = (y - pointY) * DRAG_RATE;

                if (!mRefreshing) {
                    if (overscrollTop > mTotalDragDistance) {
                        //弹回刷新位置
                        setRefreshing(true);
                    } else {
                        //弹回起始位置
                        smoothScrollToStart();
                    }

                } else {
                    if (mDispatchTargetTouchDown) {
                        mTarget.dispatchTouchEvent(event);
                        mDispatchTargetTouchDown = false;
                    }
                    return false;
                }

                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:

//                Log.v("info", "ACTION_POINTER_DOWN  index=" + pointerIndex + ",pointerId=" + pointerId);

                break;
            case MotionEventCompat.ACTION_POINTER_UP: {
//                Log.v("info", "ACTION_POINTER_UP  index=" + pointerIndex + ",pointerId=" + pointerId);


                break;
            }
//


        }
        return true;
    }

    /**
     * 是否能向上滑动
     *
     * @return
     */
    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) { // listview
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0; // scrollview
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1); // rcview
        }
    }

    private void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;
            ensureTarget();
            if (mRefreshing) {
                mHeadView.onStart();
                //滑到刷新位置

                smoothScrollToRefresh();
            } else {
                //滑到起始位置
                smoothScrollToStart();
            }
        }
    }

    private void smoothScrollToRefresh() {

        Animation animationScrollToRefresh = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float y = (currentOffsetTop - mTotalDragDistance) * (-interpolatedTime);
                setTargetOffsetTop((int) y);
            }
        };
        animationScrollToRefresh.reset();
        animationScrollToRefresh.setInterpolator(mDecelerateInterpolator);
        animationScrollToRefresh.setDuration(mDurationToCorrectPosition);
        animationScrollToRefresh.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (mRefreshing) {
                    mHeadView.onStart();
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                } else {
                    mHeadView.onStop();
                    smoothScrollToStart();
                }

//                mHeadView.onStart();
//                if (mListener != null) {
//                    mListener.onRefresh();
//                }
//                mHeadView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mHeadView.onStop();
//                        smoothScrollToStart();
//                    }
//                }, 8000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHeadView.clearAnimation();
        mHeadView.startAnimation(animationScrollToRefresh);
    }

    private void smoothScrollToStart() {
        Log.v("info", "smoothScrollToStart");

        Animation animationScrollToStart = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                float y = currentOffsetTop * (-interpolatedTime);
                setTargetOffsetTop((int) y);
            }
        };
        animationScrollToStart.reset();
        animationScrollToStart.setInterpolator(mDecelerateInterpolator);
        animationScrollToStart.setDuration(mDurationToStartPosition);
        animationScrollToStart.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mRefreshing = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mHeadView.clearAnimation();
        mHeadView.startAnimation(animationScrollToStart);
    }

    private void setTargetOffsetTop(int offset) {


        mTarget.offsetTopAndBottom(offset);
        currentOffsetTop = mTarget.getTop();
        mHeadView.onPositionChange(offset);

        mHeadView.offsetTopAndBottom(offset);
        marginTop = mHeadView.getTop();
//        Log.v("info", "currentOffsetTop=" + currentOffsetTop + ",marginTop=" + marginTop);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }


    private OnRefreshListener mListener;

    public void setOnRefreshListener(OnRefreshListener mListener) {
        this.mListener = mListener;
    }

    interface OnRefreshListener {
        void onRefresh();
    }


}
