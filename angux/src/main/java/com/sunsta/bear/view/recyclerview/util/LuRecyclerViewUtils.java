package com.sunsta.bear.view.recyclerview.util;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.sunsta.bear.view.recyclerview.recyclerview.LuRecyclerViewAdapter;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：备份REcyclerViewUtils
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 3.0（可选） |   2019/11/30          |   适配androidx重构
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LuRecyclerViewUtils {

    /**
     * 设置HeaderView
     *
     * @param recyclerView
     * @param view
     */
    @Deprecated
    public static void setHeaderView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof LuRecyclerViewAdapter)) {
            return;
        }

        LuRecyclerViewAdapter luRecyclerViewAdapter = (LuRecyclerViewAdapter) outerAdapter;
        luRecyclerViewAdapter.addHeaderView(view);
    }

    /**
     * 设置FooterView
     *
     * @param recyclerView
     * @param view
     */
    @Deprecated
    public static void setFooterView(RecyclerView recyclerView, View view) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof LuRecyclerViewAdapter)) {
            return;
        }

        LuRecyclerViewAdapter luRecyclerViewAdapter = (LuRecyclerViewAdapter) outerAdapter;

        if (luRecyclerViewAdapter.getFooterViewsCount() > 0) {
            luRecyclerViewAdapter.removeFooterView();
        }
        luRecyclerViewAdapter.addFooterView(view);
    }

    /**
     * 移除FooterView
     *
     * @param recyclerView
     */
    public static void removeFooterView(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter != null && outerAdapter instanceof LuRecyclerViewAdapter) {

            int footerViewCounter = ((LuRecyclerViewAdapter) outerAdapter).getFooterViewsCount();
            if (footerViewCounter > 0) {
                ((LuRecyclerViewAdapter) outerAdapter).removeFooterView();
            }
        }
    }

    /**
     * 移除HeaderView
     *
     * @param recyclerView
     */
    public static void removeHeaderView(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter != null && outerAdapter instanceof LuRecyclerViewAdapter) {

            int headerViewCounter = ((LuRecyclerViewAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                View headerView = ((LuRecyclerViewAdapter) outerAdapter).getHeaderView();
                ((LuRecyclerViewAdapter) outerAdapter).removeHeaderView(headerView);
            }
        }
    }

    /**
     * 请使用本方法替代RecyclerView.ViewHolder的getLayoutPosition()方法
     *
     * @param recyclerView
     * @param holder
     * @return
     */
    public static int getLayoutPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LuRecyclerViewAdapter) {

            int headerViewCounter = ((LuRecyclerViewAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                return holder.getLayoutPosition() - headerViewCounter;
            }
        }

        return holder.getLayoutPosition();
    }

    /**
     * 请使用本方法替代RecyclerView.ViewHolder的getAdapterPosition()方法
     *
     * @param recyclerView
     * @param holder
     * @return
     */
    public static int getAdapterPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LuRecyclerViewAdapter) {

            int headerViewCounter = ((LuRecyclerViewAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                return holder.getAdapterPosition() - headerViewCounter;
            }
        }

        return holder.getAdapterPosition();
    }
}