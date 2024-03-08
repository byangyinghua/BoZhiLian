package cn.com.data_plus.bozhilian.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import cn.com.data_plus.bozhilian.global.Config;

public class BaseFragment extends Fragment {
    protected MainActivity mActivity;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this instanceof VideoFragment || this instanceof TextFragment ||this instanceof picFragment) {
            Config.getInstance().saveShowFrag(true);
            mActivity.setTextClockVisibility(View.VISIBLE);
        } else {
            Config.getInstance().saveShowFrag(false);
            mActivity.setTextClockVisibility(View.GONE);
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Config.getInstance().saveShowFrag(false);
    }
}
