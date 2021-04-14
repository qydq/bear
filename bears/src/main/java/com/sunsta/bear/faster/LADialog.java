package com.sunsta.bear.faster;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton.OnCheckedChangeListener;

import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.listener.OnItemClickListener;
import com.sunsta.bear.immersion.DoubleUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：自定义的an框架dialog createdialog hui返回一个Dialog对象,单例枚举参考
 * version3.0增加一个带ListView的Dialog，ListView后期考虑维护为RecyclerView。
 * 创建Dialog必须手动结束掉。
 * version4.0增加Notifation
 * <p>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0 | 2020/03/27/11:44 | 已完成4.0，把软件更新的对话框放到这里面。还有需要增加处理Handler工具类，防止内存溢出。
 * Dialog属于极度敏感控件，需要doublecheck，并且一般配备500毫秒延迟执行，在顺序弹出对话框的时候需要给用户反应时间，
 * 最重要的是修复java.util.ConcurrentModificationException，参考：https://www.cnblogs.com/loong-hon/p/10256686.html
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public enum LADialog {
    INSTANCE;
    private String TAG = getClass().getName();
    /**
     * 监听带ListView列表，确定，取消，以及checkbox的回调。
     */
    private OnCheckedChangeListener onCheckedChangeListener;//复选框
    private OnItemClickListener onItemClickListener;//列表点击
    private OnCancelListener onCancelListener;//取消监听
    private OnConfirmListener onConfirmListener;//确定监听
    private View.OnClickListener onClickListener;//其它点击事件

    // private ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
    private TreeMap<Integer, MyDialog> mTreeMap = new TreeMap();
    private List<Integer> sequenceList = new ArrayList();
    private MyDialog myDialog;//myDialog
    private Dialog dialog;//内部的dialog
    private boolean controlAll = true;//总控
    private boolean controlCurrent = true;//当前控

    private List<String> listData = new ArrayList<>();
    private String txtDeep;
    private String txtLight;
    private String txtPrimary;//once more use
    private String imageUrl;
    private Activity activity;
    private int layoutResID;

    // style
    public enum STYLE {
        default_style,
        fullscreen_dowoload_bottom,
        fullscreen_owner,
        middle_owner,
        middle_pure_image,
        middle_list,
        middle_download_center,
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public void setOnConfirmListener(OnConfirmListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 设置数据
     */
    public void setListData(List<String> listData) {
        this.listData = listData;
        myDialog.setListData(listData);
    }

    public void setImageUrl(String imageUrl) {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- imageUrl=" + imageUrl);
        this.imageUrl = imageUrl;
        myDialog.setImageUrl(imageUrl);
    }

    public void setTxtDeep(String txtDeep) {
        this.txtDeep = txtDeep;
        myDialog.setTxtDeep(txtDeep);
    }

    public void setTxtLight(String txtLight) {
        this.txtLight = txtLight;
        myDialog.setTxtLight(txtLight);
    }

    public void setTxtPrimary(String txtPrimary) {
        this.txtPrimary = txtPrimary;
        myDialog.setTxtPrimary(txtPrimary);
    }

    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutResID = layoutResID;
    }

    /*--------------------分割线--------------------*/

    public Dialog createLoddingDialog() {
        return null;
    }

    public MyDialog newDialog(Activity activity) {
        this.activity = activity;
        myDialog = new MyDialog(activity);
        return myDialog;
    }

    /**
     * u should get parentActivity avoid "
     * Unable to add window --token null is not valid; is your activity running "
     * when screen window changed or restore activity call onCreate
     */
// public Dialog newDialog(int delayMillis, STYLE style, @NonNull Activity activity) {
// while (activity.getParent() != null) {
// activity = activity.getParent();
// }
// return new MyDialog(activity);
// }
// public void showTimePicker(int data,callBack);
    public void attach(STYLE style, @NonNull Activity activity) {
        attach(1, style, activity);
    }

    public void attach(int level, STYLE style, @NonNull Activity activity) {
        /*level Integer default value 0 , so level sugesstion must 1-100,default is 1*/
        if (level < 1) {
            LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- the [param :level]，Less than 1 is not allowed");
            mTreeMap.clear();
            return;
        }

//        for (int sequenceLevel : sequenceList) {
//            if (sequenceLevel == level) {
//                LaLog.e(AnConstants.VALUE.LOG_FASTER + TAG + "- The level = " + levels + ";value already exists. Please ensure that the level is unique");
//                mTreeMap.clear();
//                return;
//            }
//        }
        Set<Integer> keyset = mTreeMap.keySet();
//        List<Integer> list = new ArrayList(keyset);
        for (int levels : keyset) {
            if (levels == level) {
                LaLog.e(AnConstants.VALUE.LOG_FASTER + TAG + "- The level = " + levels + ";value already exists. Please ensure that the level is unique");
                mTreeMap.clear();
                return;
//                throw new IllegalStateException("The level = " + levels + ";value already exists. Please ensure that the level is unique");
            }
        }
        this.activity = activity;
        if (style == STYLE.middle_list) {
            myDialog = new MyDialog(activity, R.style.an_dialog_middle_list, style);
        } else if (style == STYLE.fullscreen_dowoload_bottom) {
            myDialog = new MyDialog(activity, R.style.an_dialog_loadding_animation, style);
        } else if (style == STYLE.middle_download_center) {
            myDialog = new MyDialog(activity, R.style.an_dialog_middle_download_center, style);
        } else if (style == STYLE.middle_owner) {
            myDialog = new MyDialog(activity, R.style.an_dialog_middle_pure_image, style);
            myDialog.setContentView(layoutResID);
        } else if (style == STYLE.middle_pure_image) {
            myDialog = new MyDialog(activity, R.style.an_dialog_middle_pure_image, STYLE.middle_pure_image);
        } else {
//default will contain confirm ,cancel frame
        }
        globalAllEvent();//发动数据的时候先去开启监听事件

        initGlobalData();//发动数据时再次初始化数据


        mTreeMap.put(level, myDialog);
    }

    public void launch() {
        if (!DoubleUtils.isFastDoubleClick()) {
            new Thread(this::performLaunch).start();
        }
    }

    /**
     * 5.0版本考虑调用自己，并且统一protocolbuff
     */
    private void performLaunch() {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- performLaunch");
        if (null != mTreeMap && mTreeMap.size() > 0) {
            LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- start launch dialog");
            while (controlAll) {
                if (null != mTreeMap && mTreeMap.size() > 0) {
                    Set<Integer> treeSet = mTreeMap.keySet();
                    int firstLevel = treeSet.iterator().next();
                    if (firstLevel > 0) {
                        if (mTreeMap.size() == 0) {
                            controlAll = false;
                            controlCurrent = false;
                            break;
                        }
                        while (controlCurrent) {
                            MyDialog treeDialog = mTreeMap.get(firstLevel);
                            if (null != treeDialog) {
                                Message msg = sHandler.obtainMessage();
                                msg.what = firstLevel;
                                msg.arg1 = firstLevel;
                                msg.obj = treeDialog;
                                sHandler.sendMessage(msg);
                                break;
                            } else {
                                LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- treeDialog == null");
                            }
                        }
                    }
                } else {
                    LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "所有的dialog已经全部执行完毕,这里再次清空数据");
                    controlAll = false;
                    controlCurrent = false;
                    mTreeMap.clear();
                }
            }
        } else {
            LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- finish showAttach || not call Dialog in mTreeMap");
            controlAll = false;
            controlCurrent = false;
            destory();
        }
    }

    private void controlTimer() {
        if (null == mTreeMap || mTreeMap.size() == 0) {
            destory();
            return;
        }
        sHandler.sendEmptyMessageDelayed(0, 400);
    }

    private void reductionControl() {
        controlCurrent = true;
        controlAll = true;
    }

    // private Handler sHandler = new TestHandler(activity);
    private Handler sHandler = new TestHandler(this);

    static class TestHandler extends Handler {
        private WeakReference<Activity> mActivity;
        private WeakReference<LADialog> mLaDialog;

        TestHandler(LADialog laDialog) {
            mLaDialog = new WeakReference<>(laDialog);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LADialog laDialog = mLaDialog.get();
            if (msg.what == 0) {
                laDialog.reductionControl();
            } else {
                if (laDialog != null && msg.obj != null) {
                    laDialog.next(msg.arg1, (MyDialog) msg.obj);
                }
            }
        }
    }

    private void next(int levelKey, @NonNull MyDialog treeDialog) {
        treeDialog.show();
        treeDialog.setGlobalClick(false);
        mTreeMap.remove(levelKey);
        controlCurrent = false;
    }

    private void globalAllEvent() {
        if (null == myDialog) return;
        myDialog.setOnConfirmListener(dialog -> {
            LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- Click ivPrimery:setOnConfirmListener");
            if (onConfirmListener != null) {
                onConfirmListener.onItemClick(myDialog);
            }
            controlTimer();
        });

        myDialog.setOnCancelListener(dialog -> {
            LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- Click ivPrimery:setOnCancelListener");
            if (onCancelListener != null) {
                onCancelListener.onCancel(myDialog);
            }
            controlTimer();
        });

        myDialog.setOnCheckedChangeListener(onCheckedChangeListener);

        myDialog.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- Click ivPrimery:onClickListener");
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(view, position);
                }
            }
        });

        myDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- Click ivPrimery:onClickListener");
                if (null != onClickListener) {
                    onClickListener.onClick(v);
                }
                controlTimer();
            }
        });

//        myDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                return false;
//            }
//        });
    }

    private void initGlobalData() {
        setListData(listData);
        setTxtDeep(txtDeep);
        setTxtLight(txtLight);
        setTxtPrimary(txtPrimary);
        setImageUrl(imageUrl);
    }

    /*
    * -------------弹出的对话框是一个列表（创建含有ListView的Dialog）--------------------
    <string-array name="sunst_dialog">
    <item>小团子</item>
    <item>李芳芳</item>
    </string-array>
    */
    public Dialog createListDialog(@NonNull Context activity, @NonNull List<String> listData, String listTitle, boolean shouldShow, boolean cancelable, OnItemClickListener onItemClickListener) {
        return new MyDialog(activity, R.style.an_dialog_loadding_standard, STYLE.middle_list);
    }

    public Dialog createListDialog(@NonNull Context activity, @NonNull List<String> listData, String listTitle, boolean cancelable, OnItemClickListener onItemClickListener) {
        return new MyDialog(activity, R.style.an_dialog_loadding_standard, STYLE.middle_list);
    }

    public Dialog createListDialog(@NonNull Context activity, String[] items, String listTitle, boolean cancelable, OnItemClickListener onItemClickListener) {
        return new MyDialog(activity, R.style.an_dialog_loadding_standard, STYLE.middle_list);
    }

    public Dialog createListDialog(@NonNull Context activity, String[] items, boolean cancelable, OnItemClickListener onItemClickListener) {
        return createListDialog(activity, Arrays.asList(items), AnConstants.EMPTY, cancelable, onItemClickListener);
    }

    public Dialog createListDialog(@NonNull Context activity, @ArrayRes int resArrayId, String listTitle, boolean cancelable, OnItemClickListener onItemClickListener) {
        return createListDialog(activity, Arrays.asList(activity.getResources().getStringArray(resArrayId)), listTitle, cancelable, onItemClickListener);
    }

    public Dialog createListDialog(@NonNull Context activity, @ArrayRes int resArrayId, boolean cancelable, OnItemClickListener onItemClickListener) {
        return createListDialog(activity, Arrays.asList(activity.getResources().getStringArray(resArrayId)), AnConstants.EMPTY, cancelable, onItemClickListener);
    }

    public Dialog createListDialog(@NonNull Context activity, @ArrayRes int resArrayId, OnItemClickListener onItemClickListener) {
        return createListDialog(activity, Arrays.asList(activity.getResources().getStringArray(resArrayId)), AnConstants.EMPTY, false, onItemClickListener);
    }

    public Dialog createSuperDialog(@NonNull Context activity, String txt, boolean cancelable, OnItemClickListener onItemClickListener) {
        return new MyDialog(activity, R.style.an_dialog_loadding_standard, STYLE.middle_list);
    }

    public void cancelDialog() {
        if (null != myDialog) {
            myDialog.cancelDialog();
        }
    }

    public void destory() {
        cancelDialog();
        if (null != mTreeMap) {
            mTreeMap.clear();
        }
        if (null != sHandler) {
            sHandler.removeCallbacksAndMessages(null);
        }
        reductionControl();
    }

    public interface OnConfirmListener {
        void onItemClick(DialogInterface dialog);
    }

    /*伪代码*/
    private void preDownload() {
        /*（1）下载文件首先检查权限*/
// RxPermissions.ob

        /*（2）有权限去下载，是否显示进度对话框*/
// InternetClient.getInstance().download()

        /*（3）如果显示进度对话框，则再弹窗，是否后台下载（取消对话框）*/
// LADialog.INSTANCE.showProgressDownloadDialog();
    }
}