package cn.com.data_plus.bozhilian.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.global.Const;
import cn.com.data_plus.bozhilian.widget.VerticalMarqueeTextView;

public class TextFragment extends BaseFragment {

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_TWO_TEXT = 1;
    private static final int TYPE_LARGE_TEXT = 2;

    private static final String KEY_TYPE = "type";
    private static final String KEY_TEXT = "text";
    private static final String KEY_LEFT_TEXT = "left_text";
    private static final String KEY_RIGHT_TEXT = "right_text";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        View view;
        int type = bundle.getInt(KEY_TYPE);
        if (type == TYPE_TEXT) {
            view = inflater.inflate(R.layout.fragment_text, container, false);
            ((TextView) view).setMovementMethod(ScrollingMovementMethod.getInstance());
            ((TextView) view).setText(bundle.getCharSequence(KEY_TEXT));
        } else if (type == TYPE_TWO_TEXT) {
            view = inflater.inflate(R.layout.fragment_two_text, container, false);
            TextView textView1 = (TextView) view.findViewById(R.id.text_view1);
            TextView textView2 = (TextView) view.findViewById(R.id.text_view2);
            textView1.setMovementMethod(ScrollingMovementMethod.getInstance());
            textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
            textView1.setText(bundle.getCharSequence(KEY_LEFT_TEXT));
            textView2.setText(bundle.getCharSequence(KEY_RIGHT_TEXT));
        } else {
            view = inflater.inflate(R.layout.fragment_text_large, container, false);
            VerticalMarqueeTextView verticalMarqueeTextView = (VerticalMarqueeTextView) view.findViewById(R.id.tv_frag);
            verticalMarqueeTextView.setText(bundle.getCharSequence(KEY_TEXT));
        }
        return view;
    }

    public static TextFragment newInstanceLarge(CharSequence text) {
        TextFragment textFragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, TYPE_LARGE_TEXT);
        bundle.putCharSequence(KEY_TEXT, text);
        textFragment.setArguments(bundle);
        return textFragment;
    }

    public static TextFragment newInstance(CharSequence text) {
        TextFragment textFragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, TYPE_TEXT);
        bundle.putCharSequence(KEY_TEXT, text);
        textFragment.setArguments(bundle);
        return textFragment;
    }

    public static TextFragment newInstance(CharSequence left, CharSequence right) {
        TextFragment textFragment = new TextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, TYPE_TWO_TEXT);
        bundle.putCharSequence(KEY_LEFT_TEXT, left);
        bundle.putCharSequence(KEY_RIGHT_TEXT, right);
        textFragment.setArguments(bundle);
        return textFragment;
    }
}
