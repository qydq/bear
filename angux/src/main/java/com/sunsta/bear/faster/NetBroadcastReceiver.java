package com.sunsta.bear.faster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.sunsta.bear.view.BaseActivity;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：自定义检查手机网络状态是否切换的广播接受器。
 * /*记得在manifest中注册
 * <receiver android:name="cn.broadcastreceiver.NetBroadcastReceiver">
 * <intent-filter>
 * <action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
 * </intent-filter>
 * </receiver>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2017/12/26           |   自定义检查手机网络状态是否切换的广播接受器
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class NetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetEvevt evevt = BaseActivity.evevt;
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            int netWrokState = NetBroadcastReceiverUtils.getNetworkState(context);
            if (evevt != null) {
                if (!BaseActivity.firstNoInspectNet) {
                    evevt.onNetChange(netWrokState);
                } else {
                    BaseActivity.firstNoInspectNet = false;
                }
            }
        }
    }

    public interface NetEvevt {
        void onNetChange(int netModile);
    }
}