package cn.com.data_plus.bozhilian.util.dialog;

import android.content.Context;
import cn.com.data_plus.bozhilian.R;


/**
 * 对话框工具类
 *
 * @author ymb
 * Create at 2017/4/27 17:25
 */
public class DialogUtils {

    /**
     * 展示删除地址的对话框
     *
     * @param callBack
     */
    public static void showHintBindDialog(Context context, CommonDialog.ClickCallBack callBack, String title, String msg, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, leftText, rightText);
        dialog.show();
        dialog.setmTitle(title);
        dialog.setCancelTextColor(context.getResources().getColor(R.color.black3));
        dialog.setOkTextColor(context.getResources().getColor(R.color.black3));
        dialog.setContentTextColor(context.getResources().getColor(R.color.title_black_color));
        dialog.setCancelTextSize(14);
        dialog.setOkTextSize(14);
        dialog.setContentTextSize(17);
    }

    /**
     * 单个按钮
     *
     * @param callBack
     */
    public static void showOneKeyDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_ONE_KEY, msg, "", rightText);
        dialog.show();
    }

    /**
     * 单个按钮  回车键是否可点击
     *
     * @param callBack
     */
    public static void showOneKeyDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String rightText, Boolean isCancle) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_ONE_KEY, msg, "", rightText);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * 两个按钮
     *
     * @param callBack
     */
    public static void showTwoKeyDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, leftText, rightText);
        dialog.show();
    }

    public static void showTwoKeyDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String sub, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, sub, leftText, rightText);
        dialog.show();
    }

    /**
     * 取消按钮可以点击处理
     *
     * @param context
     * @param callBack
     * @param msg
     * @param leftText
     * @param rightText
     */
    public static void showTwoKeyDialogCancle(Context context, CommonDialog.ClickCallBackTow callBack, String msg, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, leftText, rightText);
        dialog.show();
    }

    public static void showTwoKeyDialogCancle(Context context, CommonDialog.ClickCallBackTow callBack, String msg, String sub, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, sub, leftText, rightText);
        dialog.show();
    }

    /**
     * 展示客服电话的对话框
     *
     * @param callBack
     */
    public static void showCustomServiceDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, leftText, rightText);
        dialog.setCancelable(false);
        // dialog.setTitle();
        dialog.show();
        dialog.setCancelTextColor(context.getResources().getColor(R.color.title_black_color));
        dialog.setOkTextColor(context.getResources().getColor(R.color.title_black_color));
    }

    /**
     * 展示删除地址的对话框
     *
     * @param callBack
     */
    public static void showDeleteDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, leftText, rightText);

        // dialog.setTitle();
        dialog.show();
        dialog.setCancelTextColor(context.getResources().getColor(R.color.black3));
        dialog.setOkTextColor(context.getResources().getColor(R.color.black3));
        dialog.setContentTextColor(context.getResources().getColor(R.color.title_black_color));
        dialog.setCancelTextSize(14);
        dialog.setOkTextSize(14);
        dialog.setContentTextSize(17);
    }

    /**
     * 完整对话框
     *
     * @param context
     * @param callBack
     * @param msg
     * @param leftText
     * @param rightText
     */
    public static void showCompleteDialog(Context context, CommonDialog.ClickCallBack callBack, String msg, String msgSub, String leftText, String rightText) {
        CommonDialog dialog = new CommonDialog(context, callBack, CommonDialog.TYPE_TWO_KEY, msg, msgSub, leftText, rightText);
        dialog.show();
        dialog.setCancelTextColor(context.getResources().getColor(R.color.title_black_color));
        dialog.setOkTextColor(context.getResources().getColor(R.color.theme));
    }
}
