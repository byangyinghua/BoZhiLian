package cn.com.data_plus.bozhilian.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

/**
 * @author wangyu
 * @time 2017/4/19 0019 on 下午 14:16
 * @desc
 */
public class BaseRecyclerHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews;

    public BaseRecyclerHolder(View itemView) {
        super(itemView);
        mViews = new SparseArray<>();
        // AutoLayout
        AutoUtils.autoSize(itemView);
    }

    public View getItemView() {
        return itemView;
    }

    private <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getView(int viewId) {
        return findViewById(viewId);
    }

/*    public RoundedImageView getRoundedImageView(int viewId) {
        return findViewById(viewId);
    }

    public CircleImageView getCircleImageView(int viewId) {
        return findViewById(viewId);
    }*/
    public TextView getTextView(int viewId) {
        return (TextView) getView(viewId);
    }

    public Button getButton(int viewId) {
        return (Button) getView(viewId);
    }

    public ImageView getImageView(int viewId) {
        return (ImageView) getView(viewId);
    }

    public ImageButton getImageButton(int viewId) {
        return (ImageButton) getView(viewId);
    }

    public EditText getEditText(int viewId) {
        return (EditText) getView(viewId);
    }

    public RecyclerView getRecyclerView(int viewId) {
        return (RecyclerView) getView(viewId);
    }

    public CheckBox getCheckBox(int viewId) {
        return (CheckBox) getView(viewId);
    }

    public BaseRecyclerHolder setText(int viewId, String value) {
        TextView view = findViewById(viewId);
        view.setText(value);
        return this;
    }

    public BaseRecyclerHolder setTextSise(int viewId, float value) {
        TextView view = findViewById(viewId);
        view.setTextSize(value);
        return this;
    }

    public BaseRecyclerHolder setBackground(int viewId, int resId) {
        View view = findViewById(viewId);
        view.setBackgroundResource(resId);
        return this;
    }

    public BaseRecyclerHolder setClickListener(int viewId, View.OnClickListener listener) {
        View view = findViewById(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}
