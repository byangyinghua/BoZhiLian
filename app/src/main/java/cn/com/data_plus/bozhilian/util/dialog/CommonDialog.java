package cn.com.data_plus.bozhilian.util.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.com.data_plus.bozhilian.R;


/**
 * 自定义弹出对话框
 *
 * @author ymb
 * Create at 2017/4/27 17:04
 */
public class CommonDialog extends Dialog {

    private Context mContext;
    private ClickCallBack mCallBack;
    private ClickCallBackTow mClickCallBackTow;
    private int mType = 2;
    private String mMsg, mMsgSub, mLeftText, mRightText;
    public static final int TYPE_ONE_KEY = 1;
    public static final int TYPE_TWO_KEY = 2;
    private TextView tv_ok;
    private TextView tv_cancel;
    private TextView tv_content, tv_title;
    private TextView tvSub;

    public CommonDialog(Context context, ClickCallBack callBack, int type, String msg, String leftText, String rightText) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        mCallBack = callBack;
        mType = type;
        mMsg = msg;
        mLeftText = leftText;
        mRightText = rightText;
    }

    public CommonDialog(Context context, ClickCallBackTow callBack, int type, String msg, String leftText, String rightText) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        mClickCallBackTow = callBack;
        mType = type;
        mMsg = msg;
        mLeftText = leftText;
        mRightText = rightText;
    }

    public CommonDialog(Context context, ClickCallBackTow callBack, int type, String msg, String mMsgSub, String leftText, String rightText) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        mClickCallBackTow = callBack;
        mType = type;
        this.mMsgSub = mMsgSub;
        mMsg = msg;
        mLeftText = leftText;
        mRightText = rightText;
    }

    public CommonDialog(Context context, ClickCallBack callBack, int type, String msg, String msgSub, String leftText, String rightText) {
        super(context, R.style.CommonDialogStyle);
        mContext = context;
        mCallBack = callBack;
        mType = type;
        mMsg = msg;
        mMsgSub = msgSub;
        mLeftText = leftText;
        mRightText = rightText;
    }


    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            //    sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_common, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        lp.width = (int) (dm.widthPixels * 0.8); // 把对话框宽度设置为屏幕宽度的0.8
        dialogWindow.setAttributes(lp);

        //设置按钮的隐藏
        if (mType == TYPE_ONE_KEY) {
            ((View) view.findViewById(R.id.dialog_common_btn_divider)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.tv_cancel)).setVisibility(View.GONE);
        }
        // 设置子内容是否隐藏
        if (!TextUtils.isEmpty(mMsgSub)) {
            tvSub = (TextView) view.findViewById(R.id.dialog_common_msg_sub);
            tvSub.setVisibility(View.VISIBLE);
            tvSub.setText(mMsgSub);
        }

        tv_title = ((TextView) view.findViewById(R.id.dialog_common_title));
        //设置显示内容
        ((TextView) view.findViewById(R.id.dialog_common_msg)).setText(mMsg);
        //tvSub =  view.findViewById(R.id.dialog_common_msg_sub)).setText(mMsgSub);
        ((TextView) view.findViewById(R.id.tv_ok)).setText(mRightText);
        ((TextView) view.findViewById(R.id.tv_cancel)).setText(mLeftText);
        //设置显示内容
        tv_content = (TextView) view.findViewById(R.id.dialog_common_msg);

        // 设置按钮监听
        tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCallBack) {
                    mCallBack.onConfirm();
                }
                if (null != mClickCallBackTow) {
                    mClickCallBackTow.onConfirm();
                }
                dismiss();
            }
        });

        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mCallBack) {
                    dismiss();
                }
                if (null != mClickCallBackTow) {
                    mClickCallBackTow.onCancle();
                    dismiss();
                }
            }
        });

    }


    // 回调接口，执行具体的处理逻辑
    public interface ClickCallBackTow {
        void onConfirm();

        void onCancle();
    }

    public interface ClickCallBack {
        void onConfirm();
    }

    /**
     * 设置确认按钮的颜色
     */
    public void setOkTextColor(int color) {
        if (null != tv_ok) {
            tv_ok.setTextColor(color);
        }

    }

    /**
     * 设置内容控件的颜色
     */
    public void setContentTextColor(int color) {
        if (null != tv_ok) {
            tv_ok.setTextColor(color);
        }

    }

    /**
     * setOkTextColor的文字大小
     */
    public void setOkTextSize(int dp) {
        if (null != tv_ok) {
            tv_ok.setTextSize(dp);
        }
    }

    /**
     * 设置内容控件的文字大小
     */
    public void setContentTextSize(int dp) {
        if (null != tv_content) {
            tv_content.setTextSize(dp);
        }

    }

    public void setmTitle(String title) {
        if (null != tv_title) {
            tv_title.setText(title);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置取消按钮的文字大小
     */
    public void setCancelTextSize(int dp) {
        if (null != tv_cancel) {
            tv_cancel.setTextSize(dp);
        }

    }

    /**
     * 设置取消按钮的颜色
     */
    public void setCancelTextColor(int color) {
        if (null != tv_cancel) {
            tv_cancel.setTextColor(color);

        }
    }
}
