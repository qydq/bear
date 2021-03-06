package com.sunsta.bear.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsta.bear.AnApplication;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.faster.StringUtils;
import com.sunsta.bear.faster.ToastUtils;
import com.sunsta.bear.listener.OnSmartClickListener;
import com.sunsta.bear.presenter.net.InternetClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public abstract class SmartRecyclerAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private LayoutInflater mInflater;
    protected List<T> data = new ArrayList<>();
    private int[] layoutIds;
    protected Context mContext;
    private RecyclerView.Adapter mAdapter;
    private OnSmartClickListener<T> mSmartItemClickListener;


    public SmartRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public SmartRecyclerAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
    }

    public SmartRecyclerAdapter(Context context, List<T> data, int layoutId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.layoutIds = new int[]{layoutId};
    }

    public SmartRecyclerAdapter(Context context, List<T> data, int[] layoutIds) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.layoutIds = layoutIds;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public LayoutInflater getInflater() {
        return mInflater;
    }

    protected View getInflaterView(@LayoutRes int resource, @NonNull ViewGroup parent) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        return mInflater.inflate(resource, parent, false);
    }


    /**
     * ????????????????????????????????????????????????|| ??????????????????????????????????????????
     */
    @Override
    public int getItemCount() {
        if (null == data || data.size() < 1) {
            return 1;
        } else {
            return data.size();
        }
//        return data.size();
    }

    protected void showToast(String msg) {
        if (mContext == null) {
            mContext = AnApplication.getApplication();
        }
        ToastUtils.s(mContext, msg);
    }

    protected String getString(@StringRes int resId) {
        return StringUtils.getString(resId);
    }

    protected void addDispose(Disposable disposable) {
        InternetClient.getInstance().addDispose(disposable);
    }

    protected void enableItemClick(@NonNull View itemView, int position) {
        itemView.setOnClickListener(view -> {
            if (mSmartItemClickListener != null && data != null) {
                mSmartItemClickListener.onSmartClick(data.get(position));
            }
        });
    }

    /**
     * ???????????????????????????????????????????????????baseAdapter????????????getInflaterView
     */
    public void setOnItemClickListener(OnSmartClickListener<T> smartItemClickListener) {
        this.mSmartItemClickListener = smartItemClickListener;
    }


    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????getItemCount????????????????????????1???????????????1
     * ????????????type???????????????????????????-???????????????????????????
     */
    @Override
    public int getItemViewType(int position) {
//????????????adapter????????????????????????
        if (null == data || data.size() < 1) {
            return AnConstants.ERROR_VIEW_TYPE;
        }
        return 1;
    }
//    protected abstract void onBindData(RecyclerView.ViewHolder baseHolder, T t, int postion);

    public abstract void notifyDataSetChanged(List<T> dataList);
}