package com.sunsta.bear.view;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：mvp的BaseView的接口类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 2.0 |   2019/12/28           |   增加一个通用弹框管理接口回调，主要是配合发布alidd:1.0.15版本
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public interface BaseView<T> {
    void setPresenter(T presenter);
}