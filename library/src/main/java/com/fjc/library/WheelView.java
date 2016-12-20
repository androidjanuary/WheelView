package com.fjc.library;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by FJC on 2016/12/14.
 */
public class WheelView extends ViewGroup {

    // 指示器文字
    private String text;

    // 选项数
    private int num;
    // 指示器颜色
    private int color;
    // 菜单背景色
    private int menuColor;

    //颜色渐变动画
    ObjectAnimator colorAnimator;
    // WheelView宽度
    private int witdh;
    // WheelView高度
    private int height;

    // 指示器角度 右边水平为0度 顺时针计算.
    private float angle;

    //偏移量,用于计算产生扇形缝隙
    int delta = 10;
    // 扇形半径系数,设为0.9,是为了给缝隙留一点偏移空间.
    double r1_xishu = 0.9;
    // 指示器半径系数
    double r2_xishu = 0.4;

    // 选择的id
    int checkId = 0;

    OnCheckListener listener;

    //旋转动画时长
    int rotateDuration = 500;
    //颜色渐变时长
    int colorDuration = 500;
    //居中的textview
    TextView centerTextView;

    // 菜单名
    String menuString[];
    // 菜单图标
    int menuImg[];

    //sp
    float menuTextSize;
    float centerTextSize;
    int centerTextColor;
    int menuTextColor;
    int selectedMenuColor;


    public void setText(String text) {
        this.text = text;
        centerTextView.setText(text);
    }

    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        centerTextView.setTextColor(centerTextColor);
    }

    public void setSelectedMenuColor(int selectedMenuColor) {
        this.selectedMenuColor = selectedMenuColor;
    }

    public void setCenterTextSize(float centerTextSize) {
        this.centerTextSize = centerTextSize;
        centerTextView.setTextSize(centerTextSize);
    }


    public void setMenuTextColor(int menuTextColor) {
        this.menuTextColor = menuTextColor;
        for (int i = 0; i < num; i++) {
            setMenuTextType(getChildAt(i + 1));
        }
    }

    public void setMenuTextSize(float menuTextSize) {
        this.menuTextSize = menuTextSize;
        for (int i = 0; i < num; i++) {
            setMenuTextType(getChildAt(i + 1));
        }
    }

    public WheelView(Context context) {
        super(context, null);

    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }


    private void initView(AttributeSet attrs) {
        Log.v("info", "initView");

        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.WheelView);
        text = arr.getString(R.styleable.WheelView_text);
        num = arr.getInteger(R.styleable.WheelView_num, 4);
        color = arr.getColor(R.styleable.WheelView_color, Color.RED);
        menuColor = arr.getColor(R.styleable.WheelView_menuColor, Color.WHITE);
        menuTextColor = arr.getColor(R.styleable.WheelView_menuTextColor, Color.BLACK);
        selectedMenuColor = arr.getColor(R.styleable.WheelView_selectMenuColor, Color.GREEN);
        centerTextColor = arr.getColor(R.styleable.WheelView_centerTextColor, Color.BLACK);
        menuTextSize = DisplayUtil.px2sp(getContext(), arr.getDimension(R.styleable.WheelView_menuTextSize, DisplayUtil.sp2px(getContext(), 12)));
        centerTextSize = DisplayUtil.px2sp(getContext(), arr.getDimension(R.styleable.WheelView_centerTextSize, DisplayUtil.sp2px(getContext(), 20)));
        centerTextView = new TextView(getContext());
        centerTextView.setText(text);
        centerTextView.setTextSize(centerTextSize);
        centerTextView.setGravity(Gravity.CENTER);
        addView(centerTextView);
        setWillNotDraw(false);


        for (int i = 0; i < num; i++) {
            //建立菜单布局
            LinearLayout layout = new LinearLayout(getContext());
            ViewGroup.LayoutParams layoutParams1 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);
            //添加imageview
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            layout.addView(imageView);
            //添加textview
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView, layoutParams1);
            //将菜单添加到父布局
            addView(layout);
        }

    }

    /**
     * 设置菜单内容
     *
     * @param texts
     * @param imgs
     */
    public void setMenu(String[] texts, int[] imgs) {
        this.menuString = texts;
        this.menuImg = imgs;

        for (int i = 0; i < num; i++) {
            View child = getChildAt(i + 1);
            setMenuContent(child, i);
        }

    }

    private void setMenuContent(View child, int i) {
        Log.v("info", "setMenuContent");
        if (child != null) {
            int imgResource = (menuImg != null && menuImg.length > i) ? menuImg[i] : com.fjc.library.R.drawable.menu_defult;
            ((ImageView) ((LinearLayout) child).getChildAt(0)).setImageResource(imgResource);
            String defultText = (menuString != null && menuString.length > i) ? menuString[i] : "菜单" + (i + 1);
            ((TextView) ((LinearLayout) child).getChildAt(1)).setText(defultText);

        }
    }

    private void setMenuTextType(View child) {
        if (child != null) {
            ((TextView) ((LinearLayout) child).getChildAt(1)).setTextColor(menuTextColor);
            ((TextView) ((LinearLayout) child).getChildAt(1)).setTextSize(menuTextSize);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.v("info", "onLayout");
        View child = getChildAt(0);
        int childWidth = child.getMeasuredWidth();
        int childHeight = child.getMeasuredHeight();
        //设置指示器文本位置
        child.layout((witdh - childWidth) / 2, (height - childHeight) / 2, (witdh + childWidth) / 2, (height + childHeight) / 2);
        // 菜单中心距离父布局中心的距离,菜单中心所在圆半径
        int R = (int) ((witdh > height ? height / 2 : witdh / 2) * 0.7);
        int x = witdh > height ? height : witdh;
        for (int i = 0; i < num; i++) {
            child = getChildAt(i + 1);
            if (child != null) {
                //设置菜单图片大小 ,系数0.1;
                ImageView imageView = (ImageView) ((LinearLayout) child).getChildAt(0);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                layoutParams.width = (int) (x * 0.1);
                layoutParams.height = (int) (x * 0.1);
                imageView.setLayoutParams(layoutParams);
                // 设置菜单内容
                setMenuContent(child, i);
                // 设置菜单字体样式
                setMenuTextType(child);
                // 计算菜单宽高
                childWidth = child.getMeasuredWidth();
                childHeight = child.getMeasuredHeight();
                // 计算菜单中心位于父布局的坐标
                int potX = (int) (witdh / 2 + Math.cos(Math.PI * i / num * 2) * R);
                int potY = (int) (height / 2 + Math.sin(Math.PI * i / num * 2) * R);
                // 设置菜单位置
                child.layout(potX - childWidth / 2, potY - childHeight / 2, potX + childWidth / 2, potY + childHeight / 2);
                final int index = i;
            }
        }
    }

    private void check(final int index) {
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
        AnimatorSet set = new AnimatorSet();

        // 计算目标角度
        float angle2 = index / (float) num * 360;
        // 使得每次旋转选取最近方向
        if (Math.abs(angle2 - angle) > 180) {
            if (angle2 > angle) {
                angle += 360;

            } else {
                angle2 += 360;
            }
        }
        // 旋转动画
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(WheelView.this, "angle", angle, angle2);
        set.play(animator1);
        set.setDuration(rotateDuration);
        set.start();
        checkId = index;
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (listener != null) {
                    listener.onCheck(index);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    int touchId = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchId = getCheckId(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移出扇形时取消
                if (getCheckId(x, y) != touchId) {
                    touchId = -1;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (getCheckId(x, y) == touchId && touchId != -1) {
                    check(touchId);
                }
                touchId = -1;
                break;
        }

        invalidate();

        return true;
    }

    /**
     * 获取选项id
     *
     * @param x
     * @param y
     * @return
     */
    private int getCheckId(float x, float y) {

        int index = -1;
        // 中心点坐标
        int potX = witdh / 2;
        int potY = height / 2;

        int r = potX > potY ? potY : potX;
        // r*r1_xishu是扇形的半径,r1是扇形拼成的大圆的半径,两者之间有delta偏移量
        int r1 = (int) (r * r1_xishu + delta);
        // 指示器半径
        int r2 = (int) (r * r2_xishu);

        if (Math.pow(x - potX, 2) + Math.pow(y - potY, 2) < Math.pow(r1, 2)
                && Math.pow(x - potX, 2) + Math.pow(y - potY, 2) > Math.pow(r2, 2)) {
            double delta_x = x - potX;
            double delta_y = y - potY;
            double r3 = Math.sqrt(Math.pow(delta_x, 2) + Math.pow(delta_y, 2));
            double sin = delta_y / r3;
            double cos = delta_x / r3;
            //求arcsin反三角 0-2pi 会有两值
            double angel1 = (Math.asin(sin) + 2 * Math.PI) % (2 * Math.PI);
            double angel2 = (3 * Math.PI - angel1) % (2 * Math.PI);
            double selectAngel = 0;
            // 利用cos确认 正确的角度值
            if (Math.cos(angel2) * cos > 0) {
                selectAngel = angel2;
            } else if (Math.cos(angel1) * cos > 0) {
                selectAngel = angel1;
            }
            // 弧度转角度
            double selectAngel2 = (selectAngel * 180 / Math.PI);
            // 根据角度算id
            index = (int) (selectAngel2 / (360 / (double) num) + 0.5) % num;

        }

        return index;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v("info", "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        witdh = getMeasuredWidth();
        height = getMeasuredHeight();

    }


    public int getCheckId() {
        return this.checkId;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int r = witdh > height ? height / 2 : witdh / 2;
        // 扇形半径
        int r1 = (int) (r * r1_xishu);
        // 指示器半径
        int r2 = (int) (r * r2_xishu);
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        // 画扇形
        for (int i = 0; i < num; i++) {
            // 默认颜色
            paint.setColor(menuColor);
            // 按下的menu背景变色
            if (touchId == i) {
                paint.setColor(selectedMenuColor);
            }
            // 根据id计算弧度
            double pencent = (Math.PI * i / num * 2);
            // 扇形圆心与控件中心的偏移量
            int deltaX = (int) (delta * Math.cos(pencent));
            int deltaY = (int) (delta * Math.sin(pencent));

            RectF oval = new RectF();                     //RectF对象
            oval.left = witdh / 2 - r1 + deltaX;
            oval.top = height / 2 - r1 + deltaY;
            oval.right = witdh / 2 + r1 + deltaX;
            oval.bottom = height / 2 + r1 + deltaY;
            canvas.drawArc(oval, (float) ((i - 0.5) / (float) num * 360), 1 / (float) num * 360, true, paint);
        }
        paint.setStyle(Paint.Style.FILL);//设置实心
        // 设置为指示器颜色
        paint.setColor(color);
        // 画圆
        canvas.drawCircle(witdh / 2, height / 2, (float) (r2), paint);
        // 画三角形
        canvas.drawPath(getTrianglePath(angle, (int) (r2 / 0.8)), paint);
    }


    /**
     * 居中绘制文字
     *
     * @param canvas
     * @param text
     */
    private void paintText(Canvas canvas, String text) {
        //绘制文字
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        // FontMetrics对象
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        // 计算每一个坐标
        float baseX = witdh / 2;
        float baseY = height / 2;
        // 算出基准线,使得文字居中
        int baseline = (int) (height / 2 - (fontMetrics.bottom + fontMetrics.top) / 2);
        // 绘制文本
        canvas.drawText(text, baseX, baseline, textPaint);
    }

    /**
     * 计算三角形
     *
     * @param i 当前指示器指向的角度
     * @param r 指示器圆的半径
     * @return 三角形path
     */
    private Path getTrianglePath(float i, int r) {

        //角度转弧度
        double angel = Math.PI * i / 180;
        int pointX = witdh / 2;
        int pointY = height / 2;

        //第一个点,指针尖角
        double x1 = Math.cos(angel) * r + pointX;
        double y1 = Math.sin(angel) * r + pointY;

        //默认指针尖角60度
        double a1 = Math.PI * (i + 180 + 30) / 180;
        double a2 = Math.PI * (i + 180 - 30) / 180;

        //三角形边长,边长不能太小,否则不能被圆覆盖住两角
        double aa = 0.3 * r;

        //第二个点
        double x2 = Math.cos(a1) * aa + x1;
        double y2 = Math.sin(a1) * aa + y1;

        //第三个点
        double x3 = Math.cos(a2) * aa + x1;
        double y3 = Math.sin(a2) * aa + y1;

        Path path = new Path();
        path.moveTo((float) x1, (float) y1);
        path.lineTo((float) x2, (float) y2);
        path.lineTo((float) x3, (float) y3);
        path.close();

        return path;
    }


    /**
     * 从当前颜色变化到 color2
     *
     * @param color2
     */
    public void startColorAnimation(int color2) {
        startColorAnimation(color, color2);
    }

    /**
     * 从color1变化到 color2
     *
     * @param color1
     * @param color2
     */
    public void startColorAnimation(int color1, int color2) {
        int[] colors = new int[2];
        colors[0] = color1;
        colors[1] = color2;
        startColorAnimation(colors);

    }

    /**
     * 按颜色序列依次变化
     *
     * @param colors
     */
    public void startColorAnimation(int[] colors) {
        if (colorAnimator != null) {
            colorAnimator.cancel();
        }
        colorAnimator =
                ObjectAnimator.ofInt(WheelView.this, "color", colors);
        colorAnimator.setEvaluator(new ArgbEvaluator());
        colorAnimator.setDuration(colorDuration);
        colorAnimator.start();
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle % 360;
        invalidate();
    }

    public interface OnCheckListener {
        public void onCheck(int position);
    }

    public void setRotateDuration(int rotateDuration) {
        this.rotateDuration = rotateDuration;
    }

    public void setMenuBackgroundColor(int color) {
        this.menuColor = color;
    }

    public void setColorDuration(int colorDuration) {
        this.colorDuration = colorDuration;
    }

    public void setOnCheckListener(OnCheckListener listener) {
        this.listener = listener;
    }
}
