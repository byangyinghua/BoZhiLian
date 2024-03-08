package cn.com.data_plus.bozhilian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

import cn.com.data_plus.bozhilian.R;

public class ClockView extends View {

    private float mRadius; //外圆半径
    private float mPadding; //边距
    private float mTextSize; //文字大小
    private float mHourPointWidth; //时针宽度
    private float mMinutePointWidth; //分针宽度
    private float mSecondPointWidth; //秒针宽度
    private int mPointRadius; // 指针圆角
    private float mPointEndLength; //指针末尾的长度

    private int mColorLong; //长线的颜色
    private int mColorShort; //短线的颜色
    private int mHourPointColor; //时针的颜色
    private int mMinutePointColor; //分针的颜色
    private int mSecondPointColor; //秒针的颜色

    private Paint mPaint; //画笔

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        obtainStyledAttrs(attrs); //获取自定义的属性
        init(); //初始化画笔
    }

    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray array = null;
        try {
            array = getContext().obtainStyledAttributes(attrs, R.styleable.ClockView);
            mPadding = array.getDimension(R.styleable.ClockView_cv_padding, dp2px(10));
            mTextSize = array.getDimension(R.styleable.ClockView_cv_text_size, sp2px(16));
            mHourPointWidth = array.getDimension(R.styleable.ClockView_cv_hour_pointer_width, dp2px(5));
            mMinutePointWidth = array.getDimension(R.styleable.ClockView_cv_minute_pointer_width, dp2px(3));
            mSecondPointWidth = array.getDimension(R.styleable.ClockView_cv_second_pointer_width, dp2px(2));
            mPointRadius = (int) array.getDimension(R.styleable.ClockView_cv_pointer_corner_radius, dp2px(10));
            mPointEndLength = array.getDimension(R.styleable.ClockView_cv_pointer_end_length, dp2px(10));

            mColorLong = array.getColor(R.styleable.ClockView_cv_scale_long_color, Color.argb(225, 0, 0, 0));
            mColorShort = array.getColor(R.styleable.ClockView_cv_scale_short_color, Color.argb(125, 0, 0, 0));
            mHourPointColor = array.getColor(R.styleable.ClockView_cv_hour_pointer_color, Color.BLACK);
            mMinutePointColor = array.getColor(R.styleable.ClockView_cv_minute_pointer_color, Color.BLACK);
            mSecondPointColor = array.getColor(R.styleable.ClockView_cv_second_pointer_color, Color.RED);
        } catch (Exception e) {
            //一旦出现错误全部使用默认值
            mPadding = dp2px(10);
            mTextSize = sp2px(16);
            mHourPointWidth = dp2px(5);
            mMinutePointWidth = dp2px(3);
            mSecondPointWidth = dp2px(2);
            mPointRadius = (int) dp2px(10);
            mPointEndLength = dp2px(10);

            mColorLong = Color.argb(225, 0, 0, 0);
            mColorShort = Color.argb(125, 0, 0, 0);
            mMinutePointColor = Color.BLACK;
            mSecondPointColor = Color.RED;
        } finally {
            if (array != null) {
                array.recycle();
            }
        }

    }

    private float dp2px(float value) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return value * density + 0.5f;
    }

    private float sp2px(int value) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return value * fontScale + 0.5f;
    }

    //画笔初始化
    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = 1000; //设定一个最小值

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED || heightMeasureSpec == MeasureSpec.AT_MOST || heightMeasureSpec == MeasureSpec.UNSPECIFIED) {
            try {
                throw new NoDetermineSizeException("宽度高度至少有一个确定的值,不能同时为wrap_content");
            } catch (NoDetermineSizeException e) {
                e.printStackTrace();
            }
        } else { //至少有一个为确定值,要获取其中的最小值
            if (widthMode == MeasureSpec.EXACTLY) {
                size = Math.min(widthSize, size);
            }
            if (heightMode == MeasureSpec.EXACTLY) {
                size = Math.min(heightSize, size);
            }
        }
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mRadius = (Math.min(w, h) - getPaddingLeft() - getPaddingRight()) / 2;
        mPointEndLength = mRadius / 6; //尾部指针默认为半径的六分之一
    }

    //绘制外圆背景
    public void paintCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(0, 0, mRadius, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        //绘制外圆背景
        paintCircle(canvas);
        //绘制刻度
        paintScale(canvas);
        //绘制指针
        paintPointer(canvas);
        canvas.restore();
        //刷新
        postInvalidateDelayed(333);
    }

    private void paintPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); //时
        int minute = calendar.get(Calendar.MINUTE); //分
        int second = calendar.get(Calendar.SECOND); //秒
        float angleSecond = second * 6; //秒针转过的角度
        float angleMinute = (minute + second / 60f) * 6; //分针转过的角度
        float angleHour = (hour % 12 + minute / 60f + second / 3600f) * 30; //时针转过的角度
        //绘制时针
        canvas.save();
        canvas.rotate(angleHour); //旋转到时针的角度
        RectF rectFHour = new RectF(-mHourPointWidth / 2, -mRadius * 3 / 5, mHourPointWidth / 2, mPointEndLength);
        mPaint.setColor(mHourPointColor); //设置指针颜色
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(mHourPointWidth); //设置边界宽度
        canvas.drawRoundRect(rectFHour, mPointRadius, mPointRadius, mPaint); //绘制时针
        canvas.restore();
        //绘制分针
        canvas.save();
        canvas.rotate(angleMinute);
        RectF rectFMinute = new RectF(-mMinutePointWidth / 2, -mRadius * 3.5f / 5, mMinutePointWidth / 2, mPointEndLength);
        mPaint.setColor(mMinutePointColor);
        mPaint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectFMinute, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制秒针
        canvas.save();
        canvas.rotate(angleSecond);
        RectF rectFSecond = new RectF(-mSecondPointWidth / 2, -mRadius + 15, mSecondPointWidth / 2, mPointEndLength);
        mPaint.setColor(mSecondPointColor);
        mPaint.setStrokeWidth(mSecondPointWidth);
        canvas.drawRoundRect(rectFSecond, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        //绘制中心小圆
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mSecondPointColor);
        canvas.drawCircle(0, 0, mSecondPointWidth * 4, mPaint);
    }

    //绘制刻度
    private void paintScale(Canvas canvas) {
        mPaint.setStrokeWidth(dp2px(1));
        int lineWidth;
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) { //整点
                mPaint.setStrokeWidth(dp2px(4.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
                mPaint.setTextSize(mTextSize);
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                Rect textBound = new Rect();
                mPaint.getTextBounds(text, 0, text.length(), textBound);
                mPaint.setColor(Color.BLACK);
                canvas.save();
                canvas.translate(0, -mRadius + dp2px(5) + lineWidth + mPadding + (textBound.bottom - textBound.top) / 2);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.rotate(-6 * i);
                canvas.drawText(text, -(textBound.right + textBound.left) / 2, -(textBound.bottom + textBound.top) / 2, mPaint);
                canvas.restore();
            } else { //非整点
                lineWidth = 30;
                mPaint.setColor(mColorShort);
                mPaint.setStrokeWidth(dp2px(3));
            }
            canvas.drawLine(0, -mRadius + mPadding, 0, -mRadius + mPadding + lineWidth, mPaint);
            canvas.rotate(6);
        }
    }

    class NoDetermineSizeException extends Exception {
        NoDetermineSizeException(String message) {
            super(message);
        }
    }
}