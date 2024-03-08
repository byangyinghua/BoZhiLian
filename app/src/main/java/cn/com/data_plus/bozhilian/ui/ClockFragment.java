package cn.com.data_plus.bozhilian.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.util.TimeUtil;

public class ClockFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clock, container, false);
        TextView tv = (TextView) v.findViewById(R.id.tv_clock_date);
        tv.setText(TimeUtil.getChineseDate());
        return v;
    }

    public static ClockFragment newInstance() {
        return new ClockFragment();
    }
}
