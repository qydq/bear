package com.sunsta.bear.view.recyclerview.recyclerview;


import androidx.recyclerview.widget.GridLayoutManager;
/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：RecyclerView为GridLayoutManager时，设置了HeaderView，就会用到这个SpanSizeLookup
 * （可选）英文描述：RecyclerView为GridLayoutManager时，设置了HeaderView，就会用到这个SpanSizeLookup
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
public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private LRecyclerViewAdapter adapter;
    private int mSpanSize = 1;

    public HeaderSpanSizeLookup(LRecyclerViewAdapter adapter, int spanSize) {
        this.adapter = adapter;
        this.mSpanSize = spanSize;
    }

    @Override
    public int getSpanSize(int position) {
        boolean isHeaderOrFooter = adapter.isRefreshHeader(position) || adapter.isHeader(position) || adapter.isFooter(position);
        return isHeaderOrFooter ? mSpanSize : 1;
    }
}