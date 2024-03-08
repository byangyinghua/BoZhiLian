package cn.com.data_plus.bozhilian.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class StateImageView extends ImageView {

    private RectF mRectF;
    private Paint mPaint;
    private int rightColor;
    private int leftColor;

    public StateImageView(Context context) {
        this(context, null, 0);
    }

    public StateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mRectF = new RectF();

        rightColor = Color.RED;
        leftColor = Color.RED;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(leftColor);
        canvas.drawArc(mRectF, 90, 180, true, mPaint);

        mPaint.setColor(rightColor);
        canvas.drawArc(mRectF, 270, 180, true, mPaint);
    }

    public void setRightColor(int rightColor) {
        this.rightColor = rightColor;
        postInvalidate();
    }

    public void setLeftColor(int leftColor) {
        this.leftColor = leftColor;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        mRectF.set(0, 0, size, size);
    }
}
