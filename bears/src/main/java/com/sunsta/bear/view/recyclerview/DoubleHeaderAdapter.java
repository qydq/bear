package com.sunsta.bear.view.recyclerview;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：
 * The adapter to assist the {@link DoubleHeaderAdapter} in creating and binding the headers and
 * sub-header views.
 * @param <H> the header view holder
 * @param <S> the sub-header view holder
 *            <p>
 *            （可选）英文描述：
 *            The adapter to assist the {@link DoubleHeaderAdapter} in creating and binding the headers and
 *            sub-header views.
 * @param <H> the header view holder
 * @param <S> the sub-header view holder
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

public interface DoubleHeaderAdapter<H extends RecyclerView.ViewHolder, S extends RecyclerView.ViewHolder> {

    /**
     * Returns the header id for the item at the given position.
     * @param position the item position
     * @return the header id
     */
    long getHeaderId(int position);

    /**
     * Returns the sub-header id for the item at the given position.
     * @param position the item position
     * @return the sub-header id
     */
    long getSubHeaderId(int position);

    /**
     * Creates a new header ViewHolder.
     * @param parent the header's view parent
     * @return a view holder for the created header view
     */
    H onCreateHeaderHolder(ViewGroup parent);

    /**
     * Creates a new sub-header ViewHolder.
     * @param parent the sub-header's view parent
     * @return a view holder for the created sub-header view
     */
    S onCreateSubHeaderHolder(ViewGroup parent);

    /**
     * Updates the header view to reflect the header data for the given position
     * @param viewholder the header view holder
     * @param position   the header's item position
     */
    void onBindHeaderHolder(H viewholder, int position);

    /**
     * Updates the sub-header view to reflect the header data for the given position
     * @param viewholder the sub-header view holder
     * @param position   the sub-header's item position
     */
    void onBindSubHeaderHolder(S viewholder, int position);
}
