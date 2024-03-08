package cn.com.data_plus.bozhilian.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangyu
 * @time 2017/7/7 0007 on 上午 10:40
 * @desc 协议Adapter
 */
public abstract class BaseDelegateAdapter<T> extends DelegateAdapter.Adapter<BaseRecyclerHolder>{

    protected List<T> mItems;
    protected Context mContext;

    protected LayoutInflater mInflater;
    private LayoutHelper mLayoutHelper;
    protected OnItemClickListener mClickListener;
    protected DecimalFormat mFormat;
    protected int mCount;
    protected int mScreeWidth = 0;
    private WindowManager mWindowManager;

    public BaseDelegateAdapter(Context ctx, List<T> list, LayoutHelper mLayoutHelper, int mCount) {
        this.mItems = (list != null) ? list : new ArrayList<T>();
        this.mContext = ctx;
        this.mLayoutHelper = mLayoutHelper;
        this.mInflater = LayoutInflater.from(ctx);
        this.mFormat = new DecimalFormat("0.00");
        this.mCount = mCount;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreeWidth = mWindowManager.getDefaultDisplay().getWidth();
    }

    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mLayoutHelper;
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        BaseRecyclerHolder holder = null;
        try {
            holder = new BaseRecyclerHolder(LayoutInflater.from(mContext).inflate(getItemLayoutId(viewType), parent, false));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OOM","内存溢出" + e.getMessage());
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final BaseRecyclerHolder holder, final int position) {
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItems.size() > 0 && null != mItems.get(position)){
                        mClickListener.onItemClick(position, mItems.get(position));
                    }
                }
            });
        }
        if(mItems.size() > 0){
            if(null != mItems.get(position)){
                bindData(holder, position, mItems.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void setItemCount(int count){
        this.mCount = count;
        notifyDataSetChanged();
    }

    public List<T> getmItems(){
        if(mItems != null){
            return mItems;
        }
        return null;
    }

    public void add(int pos, T item) {
        mItems.add(pos, item);
        notifyItemInserted(pos);
    }

    public void setData(List<T> list){
        mItems = (list != null) ? list : new ArrayList<T>();
        notifyDataSetChanged();
    }

    public void delete(int pos) {
        mItems.remove(pos);
        notifyItemRemoved(pos);
    }

    public void swap(int fromPosition, int toPosition) {
        Collections.swap(mItems, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    final public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }


    /**
     * 重写该方法，根据viewType设置item的layout
     *
     * @param viewType 通过重写getItemViewType（）设置，默认item是0
     * @return
     */
    abstract protected int getItemLayoutId(int viewType);

    /**
     * 重写该方法进行item数据项视图的数据绑定
     *
     * @param holder   通过holder获得item中的子View，进行数据绑定
     * @param position 该item的position
     * @param item     映射到该item的数据
     */
    abstract protected void bindData(BaseRecyclerHolder holder, int position, T item);

    public interface OnItemClickListener<T> {
        void onItemClick(int position, T item);
    }


    /**
     * 判断view不为空并显示
     * @param view
     */
    public void emptyVisibity(View view){
        if(view != null){
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断view不为空并隐藏
     * @param view
     */
    public void emptyGone(View view){
        if(view != null){
            view.setVisibility(View.GONE);
        }
    }

    /**
     * 判断view不为空且是否获取控件
     * @param view
     * @param isEnabled
     */
    public void emptyEnabled(View view,boolean isEnabled){
        if(null != view){
            view.setEnabled(isEnabled);
        }
    }

    /**
     * 设置TextView值
     * @param tv
     * @param str
     */
    public void emptyTvTitle(TextView tv, String str){
        if(null != tv && null != str){
            tv.setText(str);
        }
    }

    /**
     * 设置TextView值
     * @param tv
     * @param str
     */
    public void emptyTvTitle(TextView tv,int str){
        if(null != tv){
            tv.setText(str);
        }
    }

    /**
     * 设置View背景颜色
     * @param view
     * @param color
     */
    public void emptyViewBackColor(View view ,int color){
        if(null != view){
            view.setBackgroundColor(color);
        }
    }
}
