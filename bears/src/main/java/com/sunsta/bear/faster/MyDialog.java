package com.sunsta.bear.faster;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.listener.OnItemClickListener;
import com.sunsta.bear.model.adapter.SListAdapter;
import com.sunsta.bear.immersion.DoubleUtils;

import java.util.ArrayList;
import java.util.List;

import static com.sunsta.bear.faster.LADialog.STYLE.default_style;

public class MyDialog extends Dialog {
    private String TAG = getClass().getName();
    private Activity activity;
    private TextView tvDeep, tvLight, tvText, tvConfirm;//should tvConfirm
    private ImageView ivPrimary, ivTopImage, ivCancle;
    private Button btnConfirm, btnCancel;
    private RelativeLayout frameLayout;
    private ProgressBar progressBar;
    private ListView listView;

    private boolean cancelable = true;//默认都能够取消
    private LADialog.STYLE style = default_style;
    private List<String> listData = new ArrayList<>();
    private int layoutResID;

    private String txtDeep;
    private String txtLight;
    private String txtPrimary;//once more use
    private String imageUrl;

    public void setGlobalClick(boolean globalClick) {
        isGlobalClick = globalClick;
    }

    private boolean isGlobalClick = false;

    /**
     * 带进度条的ProgressDialog
     */
    private ProgressDialog progressDialog;
    private AnimationDrawable animationDrawable;

    /**
     * 监听带ListView列表，确定，取消，以及checkbox的回调。
     */
    private OnCheckedChangeListener onCheckedChangeListener;//复选框
    private OnItemClickListener onItemClickListener;//列表点击
    private OnCancelListener onCancelListener;//取消监听
    private LADialog.OnConfirmListener onConfirmListener;//确定监听
    private View.OnClickListener onClickListener;//其它点击事件

    private SListAdapter adapter;

    public MyDialog(@NonNull Context context) {
        super(context, R.style.an_dialog);
        this.activity = (Activity) context;
    }

    public MyDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.activity = (Activity) context;
    }

    public MyDialog(@NonNull Context context, @StyleRes int themeResId, @NonNull LADialog.STYLE style) {
        super(context, themeResId);
        this.activity = (Activity) context;
        this.style = style;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- setOnItemClickListener=");
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- setOnCheckedChangeListener=");
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    public void setOnCancelListener(OnCancelListener onCancelListener) {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- setOnCancelListener=");
        this.onCancelListener = onCancelListener;
    }

    public void setOnConfirmListener(LADialog.OnConfirmListener onConfirmListener) {
        LaLog.d(AnConstants.VALUE.LOG_FASTER + TAG + "- setOnConfirmListener=");
        this.onConfirmListener = onConfirmListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setContentView(@LayoutRes int layoutResID) {
        this.layoutResID = layoutResID;
    }

    public void setTxtDeep(String txtDeep) {
        this.txtDeep = txtDeep;
    }

    public void setTxtLight(String txtLight) {
        this.txtLight = txtLight;
    }

    public void setTxtPrimary(String txtPrimary) {
        this.txtPrimary = txtPrimary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        while (activity.getParent() != null) {
            activity = activity.getParent();
        }
        View inflateView = null;
        Window window = getWindow();
        assert window != null;
        if (style == LADialog.STYLE.middle_list) {
            setContentView(R.layout.base_dialog_listview);
            initMiddleList();
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
                final float scale = activity.getResources().getDisplayMetrics().density;
                attributes.y = (int) (8 * scale + 0.5f);
                window.setAttributes(attributes);
            }
        } else if (style == LADialog.STYLE.fullscreen_dowoload_bottom) {
            inflateView = fullSceenDownload(inflateView, window);
        } else if (style == LADialog.STYLE.middle_download_center) {
            setContentView(R.layout.base_dialog_download_middle);
            inflateView = View.inflate(activity, R.layout.base_dialog_download_middle, null);//R.style.custom_dialog
            WindowManager.LayoutParams attributes = window.getAttributes();//ok是否
            DisplayMetrics dm = new DisplayMetrics();

            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            attributes.width = (int) (dm.widthPixels * 0.72);
            attributes.height = (int) (dm.heightPixels * 0.55);
            window.setAttributes(attributes);

        } else if (style == LADialog.STYLE.middle_owner) {
            if (layoutResID != 0) {
                setContentView(layoutResID);
            }
        } else if (style == LADialog.STYLE.middle_pure_image) {
            inflateView = View.inflate(activity, R.layout.base_dialog_image, null);
            setContentView(inflateView);
        }
        setCanceledOnTouchOutside(cancelable);
        setCancelable(cancelable);
        window.setGravity(Gravity.TOP);
        assert inflateView != null;
        globalEvent(inflateView);
    }

    private View fullSceenDownload(View inflateView, Window window) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        inflateView = inflater.inflate(activity.getResources().getLayout(R.layout.base_dialog_download_fullscreen), null);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        addContentView(inflateView, layoutParams);
// setContentView(inflateView);

        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        if (attributes != null) {
            attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
            final float scale = activity.getResources().getDisplayMetrics().density;
            attributes.y = (int) (8 * scale + 0.6f);
            window.setAttributes(attributes);
        }
        return inflateView;
    }

    /*
     * 该类数据可以封装成protocobuff数据
     * */
    private void triggerData() {
        if (!TextUtils.isEmpty(imageUrl) && null != ivPrimary) {
            GlideEngine.getInstance().loadCircleImage(imageUrl, 10, ivPrimary);
// GlideEngine.getInstance().loadCornerImage(activity, imageUrl, 12, ivPrimary);
        }

        if (null != tvDeep && !TextUtils.isEmpty(txtDeep)) {
            tvDeep.setText(txtDeep);
// if (divider != null)
// divider.setVisibility(View.GONE);
        }

        if (null != tvLight && !TextUtils.isEmpty(txtLight)) {
// tvLight.setText("新版本信息，当前版本信息更新了一些问题，优化了布局体验");
            tvLight.setText(txtLight);
        }
        if (null != tvText && !TextUtils.isEmpty(txtPrimary)) {
            tvText.setText(txtPrimary);
        }

        if (null != ivCancle) {
            if (cancelable) {
                ivCancle.setVisibility(View.VISIBLE);
            } else {
                ivCancle.setVisibility(View.GONE);
            }
        }
    }

    private void globalEvent(View inflateView) {
        tvDeep = inflateView.findViewById(R.id.tvDeep);
        tvText = inflateView.findViewById(R.id.tvText);
        tvLight = inflateView.findViewById(R.id.tvLight);

        tvConfirm = inflateView.findViewById(R.id.tvConfirm);//1
        ivCancle = inflateView.findViewById(R.id.ivCancle);//1
        btnConfirm = inflateView.findViewById(R.id.btnConfirm);//1
        btnCancel = inflateView.findViewById(R.id.btnCancel);//1

        ivPrimary = inflateView.findViewById(R.id.ivPrimary);

        ivTopImage = inflateView.findViewById(R.id.ivTopImage);
        progressBar = inflateView.findViewById(R.id.innerProgressBar);

        frameLayout = inflateView.findViewById(R.id.frameLayout);

        if (null != tvLight) {
            tvLight.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        if (null != tvText) {
            tvText.setMovementMethod(ScrollingMovementMethod.getInstance());
        }

        triggerData();

        setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (!DoubleUtils.isFastDoubleClick()) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    if (null != onCancelListener) {
                        onCancelListener.onCancel(dialogInterface);
                    }
                    return false;//返回true的情况表示，不能取消这个dialog，返回false往上分发
                }
            }
            return false;
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!isGlobalClick) {
                    if (null != onCancelListener) {
                        onCancelListener.onCancel(dialogInterface);
                    }
                }
            }
        });

        if (null != frameLayout) {
            frameLayout.setOnClickListener(this::onGlobalClick);
        }

        if (null != ivCancle) {
            ivCancle.setOnClickListener(this::onGlobalClick);
        }
        if (null != tvConfirm) {
            tvConfirm.setOnClickListener(this::onGlobalClick);
        }
        if (null != btnConfirm) {
            btnConfirm.setOnClickListener(this::onGlobalClick);
        }
        if (null != btnCancel) {
            btnCancel.setOnClickListener(this::onGlobalClick);
        }

        if (null != ivPrimary) {
            ivPrimary.setOnClickListener(this::onGlobalClick);
        }

        if (null != ivTopImage) {
            ivTopImage.setOnClickListener(this::onGlobalClick);
        }
    }

    private void onGlobalClick(View view) {
        if (!DoubleUtils.isFastDoubleClick()) {
            isGlobalClick = true;
            int id = view.getId();
            if (id == R.id.btnConfirm || id == R.id.tvConfirm) {
                if (null != onConfirmListener) {
                    onConfirmListener.onItemClick(this);
                }
                cancelDialog();
            } else if (id == R.id.ivCancle || id == R.id.btnCancel || id == R.id.frameLayout) {
                if (null != onCancelListener) {
                    onCancelListener.onCancel(this);
                }
                cancelDialog();
            } else if (id == R.id.ivPrimary) {
                if (null != onClickListener) {
                    onClickListener.onClick(view);
                }
                cancelDialog();
            }
        }
    }

    private void initMiddleList() {
        listView = findViewById(R.id.listView);//need check
        CheckBox checkBox = findViewById(R.id.checkbox);
        adapter = new SListAdapter(listData);
        listView.setAdapter(adapter);

        /*
         * List默认选择第一个
         * */
        listView.setItemChecked(0, true);
        adapter.setChoosePosition(0);

        listView.setOnItemClickListener(new ItemClickListener(adapter, listView, onItemClickListener));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
            }
        });
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        private SListAdapter adapter;
        private ListView listView;
        private OnItemClickListener onItemClickListener;

        ItemClickListener(SListAdapter adapter, ListView listView, OnItemClickListener onItemClickListener) {
            this.adapter = adapter;
            this.listView = listView;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            listView.setItemChecked(position, true);
            adapter.setChoosePosition(position);
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(view, position);
        }
    }

    /**
     * 隐藏掉DiaLog
     */
    public void cancelDialog() {
        if (null != animationDrawable && animationDrawable.isRunning())
            animationDrawable.stop();
        if (isShowing())
            cancel();
        if (null != progressDialog && progressDialog.isShowing())
            progressDialog.dismiss();
        if (ivPrimary != null) {
            ivPrimary.clearAnimation();
        }
    }

    /**
     * 显示一个Notification
     * @param context   上下文对象。
     * @param showTitle 显示的标题
     * @param showInfo  显示的信息
     * @param channelId 型的信道通量
     * @param isCancel  是否可以取消
     * @return Notification
     */
    public Notification showNotification(@NonNull Context context,
                                         String showTitle,
                                         String showInfo,
                                         Intent intent,
                                         String channelId,
                                         boolean isCancel) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);//v7就ok
// builder = new Notification.Builder(context);//v4就ok
        int smallIconId = R.mipmap.ic_color_copy_fav;
// Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher)).getBitmap();//过时的解决方法。
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_color_copy_fav);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = bitmapDrawable.getBitmap();
        builder.setLargeIcon(largeIcon)
                .setSmallIcon(smallIconId)
                .setContentTitle(showTitle)
                .setContentText(showInfo)
                .setTicker(showTitle)
                .setAutoCancel(isCancel)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
        int NOTIFICATION_START = 99;
        Notification n = builder.build();
        nm.notify(NOTIFICATION_START, n);
        return n;
    }
}