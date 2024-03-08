package cn.com.data_plus.bozhilian.util.window;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.adapter.CouponsWindAdapter;
import cn.com.data_plus.bozhilian.bean.MedioBean;
import cn.com.data_plus.bozhilian.port.SelectPort;
import cn.com.data_plus.bozhilian.util.toast.ToastUtil;

/*
 * @author  niu
 * @time 2018/8/13 16:29
 * @funtion  视频音频选择框
 *
 */
public class CouponListWindow implements View.OnClickListener, SelectPort {
    //选择规格
    private Activity mContext;

    /**
     * 属性展示列表
     */
    private RecyclerView mRecycleView;

    private TextView shopName;

    private TextView couponsNum;

    /**
     * 扩展数据 适配器集合
     */
    private List<DelegateAdapter.Adapter> mAdapters;
    /**
     * 购物车适配器
     */
    private DelegateAdapter mDelegateAdapter;
    private RecyclerView.RecycledViewPool viewPool;
    private Dialog dialog;

    /**
     * 选择后属性拼接字符串
     */
    private String specifstr = "";
    private View view;
    private List<MedioBean> gnFreeData = new ArrayList<>();
    private SelectPort port;
    private TextView ok;

    public void showBottomWindow() {
        dialog.show();
    }

    public void setData(List<MedioBean> response) {
        gnFreeData.addAll(response);
        setGoodsData();
    }

    private Handler mhandle;

    public CouponListWindow(Activity context, Handler mhandle) {
        dialog = new Dialog(context);
        this.mhandle = mhandle;
        initView(context);
    }

    private void initView(Activity context) {
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.pop_window_select, null);
        mRecycleView = (RecyclerView) view.findViewById(R.id.swipe_target);
        ok = (TextView) view.findViewById(R.id.tv_ok);
        ok.setOnClickListener(this);
        initRecycleView();
        setPopWindowStyle(mContext);
    }

    private void initRecycleView() {
        mAdapters = new LinkedList<>();
    }

    private void setGoodsData() {
        final VirtualLayoutManager layoutManager = new VirtualLayoutManager(mContext);
        final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
        mRecycleView.setRecycledViewPool(viewPool);
        viewPool.setMaxRecycledViews(0, 10);
        mDelegateAdapter = new DelegateAdapter(layoutManager, false);
        mRecycleView.setAdapter(mDelegateAdapter);
        mRecycleView.setLayoutManager(layoutManager);
        refreshAdapterData();
    }

    public void setReflsh() {
        if (mDelegateAdapter != null) {
            mDelegateAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 刷新数据
     */
    private void refreshAdapterData() {
        if (mAdapters.size() > 0) {
            mAdapters.clear();
        }
        LinearLayoutHelper layoutHelper = new LinearLayoutHelper();
        CouponsWindAdapter itemListAdapter = new CouponsWindAdapter(mContext, gnFreeData, layoutHelper, gnFreeData.size(), this);
        mAdapters.add(itemListAdapter);
        mDelegateAdapter.setAdapters(mAdapters);
        mDelegateAdapter.notifyDataSetChanged();
    }

    private void setPopWindowStyle(Context context) {
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialog.setCanceledOnTouchOutside(false);//禁止窗口外点击
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        if (isSelect()) {
            mhandle.post(new Runnable() {
                @Override
                public void run() {
                    Message msg = mhandle.obtainMessage();
                    msg.what = -900;
                    msg.obj = gnFreeData;
                    mhandle.sendMessage(msg);
                }
            });
            dialog.dismiss();
        } else {
            ToastUtil.TextToast(mContext, "请选择需要播放的文件！！");
        }
    }

    private boolean isSelect() {
        for (int i = 0; i < gnFreeData.size(); i++) {
            if (gnFreeData.get(i).isSelect()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void select(MedioBean item) {

    }
}
