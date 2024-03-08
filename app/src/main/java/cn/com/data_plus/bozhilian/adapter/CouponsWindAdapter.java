package cn.com.data_plus.bozhilian.adapter;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.LayoutHelper;


import java.util.List;

import cn.com.data_plus.bozhilian.R;
import cn.com.data_plus.bozhilian.bean.MedioBean;
import cn.com.data_plus.bozhilian.port.SelectPort;

/*
 * @author  niu
 * @time 2018/8/13 16:43
 * @funtion 本来是有相同的 但是设计的估计回来差异化 预防 所以单独写一个
 *
 */

public class CouponsWindAdapter extends BaseDelegateAdapter<MedioBean> {
    private SelectPort port;

    public CouponsWindAdapter(Activity ctx, List<MedioBean> list, LayoutHelper mLayoutHelper, int mCount, SelectPort port) {
        super(ctx, list, mLayoutHelper, mCount);
        this.port = port;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_coupons_view_wind;
    }

    @Override
    protected void bindData(BaseRecyclerHolder holder, final int position, final MedioBean item) {
        ImageView mImaage = holder.getImageView(R.id.iv_type);
        TextView name = holder.getTextView(R.id.tv_name);
        LinearLayout ll = (LinearLayout) holder.getView(R.id.ll);
        ImageView mSelect = holder.getImageView(R.id.tv_select);
        if (item.getType().equals("mp3")) {
            mImaage.setImageResource(R.mipmap.mp3);
        } else if (item.getType().equals("mkv")) {
            mImaage.setImageResource(R.mipmap.vodio);
        } else {
            mImaage.setImageResource(R.mipmap.image);
        }
        name.setText(item.getName());
        if (item.isSelect()) {
            mSelect.setImageResource(R.mipmap.check_selected_black_exp);
            ll.setBackgroundColor(mContext.getResources().getColor(R.color.text_gray_3));
        } else {
            ll.setBackgroundColor(mContext.getResources().getColor(R.color.c_FFFFFF));
            mSelect.setImageResource(R.mipmap.check_unselected_black);
        }


        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelect(!item.isSelect());
                notifyDataSetChanged();
            }
        });
    }
}
