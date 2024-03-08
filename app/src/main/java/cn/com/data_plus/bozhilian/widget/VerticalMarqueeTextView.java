package cn.com.data_plus.bozhilian.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.util.LogUtil;

@SuppressWarnings("WeakerAccess")
public class VerticalMarqueeTextView extends ScrollView {
    private static final String TAG = VerticalMarqueeTextView.class.getName();

    private static final int MIN_MARQUEE_SPEED = 1;
    private static final int MAX_MARQUEE_SPEED = 1000;

    private static final double SECOND = 1000;

    private Handler handler;

    private TextView textView;

    private int marqueeSpeed;
    private boolean marqueeStarted;
    private boolean marqueePaused;
    private boolean isAnimating;

    private int unitDisplacement;

    public VerticalMarqueeTextView(final Context context) {
        super(context);

        this.init(context, null);
    }

    public VerticalMarqueeTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        this.init(context, attrs);
    }

    public VerticalMarqueeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        this.init(context, attrs);
    }

    /**
     * Returns the text content of this text view.
     *
     * @return The text content of this text view.
     */
    public CharSequence getText() {
        return this.textView.getText();
    }

    /**
     * Sets a string for this text view.
     *
     * @param text The text to set.
     */
    public void setText(final CharSequence text) {
        LogUtil.debug("text" + text);
        if (text.toString().contains(":")) {
            int i = text.toString().indexOf(";");
        }
        String test = "";
        //_@end标题和内容分开
        //_@n;换行和首行缩进
        //_@m 空格
        test = text.toString().replaceAll("_@end;", "\\\n    \\\u3000");
        test = test.toString().replaceAll("_@n;", "\\\n\\\u3000\\\u3000");
        test = test.toString().replaceAll("_@m;", " ");
        test = test.toString().replaceAll("，", " , ");
        test = test.toString().replaceAll("。", " 。 ");
        test = test.toString().replaceAll("、", " 、 ");
        //test= test.toString().substring(1,text.length()-1);
        LogUtil.debug("test" + test);
        SpannableString spanString = new SpannableString(test);
        //再构造一个改变字体颜色的Span
      //  LogUtil.debug("i" + i);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLUE);
        //将这个Span应用于指定范围的字体
     //   spanString.setSpan(span, 0, i - 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(150);
     //   spanString.setSpan(absoluteSizeSpan, 0, i - 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        this.textView.setText(spanString);
    }

    // 功能：字符串半角转换为全角
// 说明：半角空格为32,全角空格为12288.
// 		 其他字符半角(33-126)与全角(65281-65374)的对应关系是：均相差65248
// 输入参数：input -- 需要转换的字符串
// 输出参数：无：
// 返回值: 转换后的字符串
    public static String halfToFull(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }

            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i] > 32 && c[i] < 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }


    public static String ToDBC(String input) {
        // 导致TextView异常换行的原因：安卓默认数字、字母不能为第一行以后每行的开头字符，因为数字、字母为半角字符
        // 所以我们只需要将半角字符转换为全角字符即可
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

    /**
     * Returns the speed of the marquee effect animation.
     *
     * @return The speed of the marquee effect animation.
     */
    public int getMarqueeSpeed() {
        return this.marqueeSpeed;
    }

    /**
     * Sets the speed of the marquee effect animation. Valid range is [1, 1000].
     *
     * @param marqueeSpeed The speed of the marquee effect animation to set. Valid range is [1, 1000].
     */
    public void setMarqueeSpeed(final int marqueeSpeed) {
        this.marqueeSpeed = Math.min(VerticalMarqueeTextView.MAX_MARQUEE_SPEED, Math.max(VerticalMarqueeTextView.MIN_MARQUEE_SPEED, marqueeSpeed));
    }

    /**
     * Starts the marquee effect animation.
     */
    public void startMarquee() {
        this.marqueeStarted = true;
        this.marqueePaused = false;

        if (!this.isAnimating) {
            this.isAnimating = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    VerticalMarqueeTextView.this.animateTextView();
                }
            }).start();
        }
    }

    /**
     * Stops the marquee effect animation.
     */
    public void stopMarquee() {
        this.marqueeStarted = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (this.marqueeStarted) {
            this.startMarquee();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.marqueePaused = true;
    }

    @Override
    protected float getTopFadingEdgeStrength() {
        if (this.getChildCount() == 0) {
            return 0;
        }

        final int length = this.getVerticalFadingEdgeLength();
        final int scrollY = this.textView.getScrollY();

        if (scrollY < length) {
            return scrollY / (float) length;
        }

        return 1;
    }

    private void init(final Context context, final AttributeSet attrs) {
        this.handler = new Handler(Looper.getMainLooper());

        // 1dp per cycle
        this.unitDisplacement = Math.round(this.getResources().getDisplayMetrics().density);

        this.textView = new TextView(context);
        this.textView.setGravity(Gravity.LEFT);
        this.addView(this.textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        this.textView.scrollTo(0, -this.getHeight());

        this.setHorizontalScrollBarEnabled(false);
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalFadingEdgeEnabled(false);
        this.setVerticalFadingEdgeEnabled(true);
        this.setFadingEdgeLength(30);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalMarqueeTextView, 0, 0);


            this.textView.setText(array.getText(R.styleable.VerticalMarqueeTextView_text));

            final int textRes = array.getResourceId(R.styleable.VerticalMarqueeTextView_text, 0);
            if (textRes > 0) {
                this.textView.setText(array.getText(textRes));
            }

            this.textView.setTextColor(array.getColor(R.styleable.VerticalMarqueeTextView_textColor, ContextCompat.getColor(context, android.R.color.primary_text_light)));

            final int textColorRes = array.getResourceId(R.styleable.VerticalMarqueeTextView_textColor, 0);
            if (textColorRes > 0) {
                this.textView.setTextColor(ContextCompat.getColor(context, textColorRes));
            }

            final float textSize = array.getDimension(R.styleable.VerticalMarqueeTextView_textSize, 0);
            if (textSize > 0) {
                this.textView.setTextSize(textSize);
            }

            final int textSizeRes = array.getResourceId(R.styleable.VerticalMarqueeTextView_textSize, 0);
            if (textSizeRes > 0) {
                this.textView.setTextSize(context.getResources().getDimension(textSizeRes));
            }

            final int typeface = array.getInt(R.styleable.VerticalMarqueeTextView_typeface, 0);
            this.textView.setTypeface(typeface == 1 ? Typeface.SANS_SERIF : typeface == 2 ? Typeface.SERIF : typeface == 3 ? Typeface.MONOSPACE : Typeface.DEFAULT, array.getInt(R.styleable.VerticalMarqueeTextView_textStyle, Typeface.NORMAL));

            final int textAppearance = array.getResourceId(R.styleable.VerticalMarqueeTextView_textAppearance, 0);
            if (textAppearance > 0) {
                //noinspection deprecation
                this.textView.setTextAppearance(context, textAppearance);
            }

            this.setMarqueeSpeed(array.getInt(R.styleable.VerticalMarqueeTextView_marqueeSpeed, 0));

            final int marqueeSpeedRes = array.getResourceId(R.styleable.VerticalMarqueeTextView_marqueeSpeed, 0);
            if (marqueeSpeedRes > 0) {
                this.setMarqueeSpeed(context.getResources().getInteger(marqueeSpeedRes));
            }

            final boolean autoStartMarquee = array.getBoolean(R.styleable.VerticalMarqueeTextView_autoStartMarquee, true);

            if (autoStartMarquee) {
                this.marqueeStarted = true;
            }

            array.recycle();
        }
    }

    private void animateTextView() {
        final Runnable runnable = new VerticalMarqueeTextView.MarqueeRunnable(this.textView);

        long previousMillis = 0;

        while (VerticalMarqueeTextView.this.marqueeStarted && !VerticalMarqueeTextView.this.marqueePaused) {
            final long currentMillis = System.currentTimeMillis();

            if (currentMillis >= previousMillis) {
                VerticalMarqueeTextView.this.handler.post(runnable);

                previousMillis = currentMillis + (long) (VerticalMarqueeTextView.SECOND / VerticalMarqueeTextView.this.marqueeSpeed);
            }

            try {
                Thread.sleep((long) (VerticalMarqueeTextView.SECOND / VerticalMarqueeTextView.this.marqueeSpeed));
            } catch (final InterruptedException e) {
                Log.v(VerticalMarqueeTextView.TAG, e.getMessage(), e);
            }
        }

        this.isAnimating = false;
    }

    private final class MarqueeRunnable implements Runnable {
        private final ViewGroup parent;
        private final TextView textView;

        /**
         * Creates a new instance of {@link VerticalMarqueeTextView.MarqueeRunnable}.
         *
         * @param textView The {@link TextView} to apply marquee effect.
         */
        public MarqueeRunnable(final TextView textView) {
            this.parent = (ViewGroup) textView.getParent();
            this.textView = textView;
        }

        @Override
        public void run() {
            final int height = this.heightOf(this.textView);
            final int parentHeight = this.parent.getHeight();

            if (height > 0 && parentHeight > 0 && height > parentHeight) {
                if (this.textView.getScrollY() >= height) {
                    this.textView.scrollTo(0, -parentHeight);
                } else {
                    this.textView.scrollBy(0, VerticalMarqueeTextView.this.unitDisplacement);
                }

                this.textView.invalidate();
            }
        }

        /**
         * Returns the standard height (i.e. without text effects such as shadow) of all the text of
         * the given {@link TextView}.
         *
         * @param textView The {@link TextView} to determine the height of all its text content.
         * @return The standard height of all the text content of the given {@link TextView}.
         */
        private int heightOf(final TextView textView) {
            return textView.getLineCount() > 0 ? textView.getLineHeight() * textView.getLineCount() : 0;
        }
    }
}