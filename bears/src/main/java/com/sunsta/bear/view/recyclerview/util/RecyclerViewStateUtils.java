package com.sunsta.bear.view.recyclerview.util;

import android.app.Activity;
import android.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.sunsta.bear.view.recyclerview.recyclerview.LRecyclerView;
import com.sunsta.bear.view.recyclerview.recyclerview.LRecyclerViewAdapter;
import com.sunsta.bear.view.recyclerview.view.LoadingFooter;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：分页展示数据时，RecyclerView的FooterView State 操作工具类
 * RecyclerView一共有几种State：Normal/Loading/Error/TheEnd
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
@Deprecated
public class RecyclerViewStateUtils {

    /**
     * 设置LRecyclerViewAdapter的FooterView State
     *
     * @param instance      context
     * @param recyclerView  recyclerView
     * @param pageSize      分页展示时，recyclerView每一页的数量
     * @param state         FooterView State
     * @param errorListener FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, int pageSize, LoadingFooter.State state, View.OnClickListener errorListener) {
        if (instance == null || instance.isFinishing()) {
            return;
        }

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof LRecyclerViewAdapter)) {
            return;
        }

        LRecyclerViewAdapter lRecyclerViewAdapter = (LRecyclerViewAdapter) outerAdapter;

        //只有一页，不显示FooterView
        if (lRecyclerViewAdapter.getInnerAdapter().getItemCount() < pageSize) {
            return;
        }

        LoadingFooter footerView;
        //已经有footerView了
        if (lRecyclerViewAdapter.getFooterViewsCount() > 0) {
            footerView = (LoadingFooter)lRecyclerViewAdapter.getFooterView();
            footerView.setState(state);
            footerView.setVisibility(View.VISIBLE);

            if (state == LoadingFooter.State.NetWorkError) {
                footerView.setOnClickListener(errorListener);
            } else if (state == LoadingFooter.State.NoMore){
                ((LRecyclerView)recyclerView).setNoMore(true);
            }

        }
        recyclerView.scrollToPosition(lRecyclerViewAdapter.getItemCount() - 1);

    }

    /**
     * 设置LRecyclerViewAdapter的FooterView State
     *
     * @param instance      context
     * @param recyclerView  recyclerView
     * @param pageSize      分页展示时，recyclerView每一页的数量
     * @param state         FooterView State
     * @param errorListener FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Fragment instance, RecyclerView recyclerView, int pageSize, LoadingFooter.State state, View.OnClickListener errorListener) {
        if (instance == null || instance.isDetached()) {
            return;
        }

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof LRecyclerViewAdapter)) {
            return;
        }

        LRecyclerViewAdapter lRecyclerViewAdapter = (LRecyclerViewAdapter) outerAdapter;

        //只有一页，不显示FooterView
        if (lRecyclerViewAdapter.getInnerAdapter().getItemCount() < pageSize) {
            return;
        }

        LoadingFooter footerView;
        //已经有footerView了
        if (lRecyclerViewAdapter.getFooterViewsCount() > 0) {
            footerView = (LoadingFooter)lRecyclerViewAdapter.getFooterView();
            footerView.setState(state);
            footerView.setVisibility(View.VISIBLE);

            if (state == LoadingFooter.State.NetWorkError) {
                footerView.setOnClickListener(errorListener);
            } else if (state == LoadingFooter.State.NoMore){
                ((LRecyclerView)recyclerView).setNoMore(true);
            }

        }
        recyclerView.scrollToPosition(lRecyclerViewAdapter.getItemCount() - 1);

    }

    /**
     * 获取当前RecyclerView.FooterView的状态
     *
     * @param recyclerView
     */
    public static LoadingFooter.State getFooterViewState(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LRecyclerViewAdapter) {
            if (((LRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                LoadingFooter footerView = (LoadingFooter) ((LRecyclerViewAdapter) outerAdapter).getFooterView();
                return footerView.getState();
            }
        }

        return LoadingFooter.State.Normal;
    }

    /**
     * 设置当前RecyclerView.FooterView的状态
     *
     * @param recyclerView
     * @param state
     */
    public static void setFooterViewState(RecyclerView recyclerView, LoadingFooter.State state) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof LRecyclerViewAdapter) {
            if (((LRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                LoadingFooter footerView = (LoadingFooter) ((LRecyclerViewAdapter) outerAdapter).getFooterView();
                footerView.setState(state);
            }
        }
    }
}
