package com.sunsta.bear.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.faster.ThreadPool;
import com.sunsta.bear.faster.ViewUtils;
import com.sunsta.bear.immersion.RichTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有本【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：提供一个INABar兼容之前an-aw-base框架的布局。
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2018/02/12           |   提供一个INABar兼容之前an-aw-base框架的布局。
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class INABarLayout extends RelativeLayout {
    protected LinearLayout anBackLl;
    protected LinearLayout anRightLl;//standard
    protected RelativeLayout anRightRl;//multi,a,b
    protected LinearLayout anRightLlA;
    protected LinearLayout anRightLlB;
    protected LinearLayout anLlCenter;

    protected LinearLayout llCenterPb2;
    protected LinearLayout llCenterPb;

    protected RelativeLayout frameLayout;

    protected ImageView anBackIv;
    protected ImageView anRightIv;
    protected ImageView anRightIvA;//右b
    protected ImageView anRightIvB;//左a
    protected ImageView anCenterPb2;

    protected TextView anBackTx;
    protected TextView anTitleTx;

    protected TextView anRightTx;
    protected TextView anRightTxA;//左a
    protected TextView anRightTxB;//右b
    protected TextView anTxPb2;


    protected View fitBackView;
    protected View fitCenterView;
    protected View fitRightView;
    protected View fitRightViewA;
    protected View fitRightViewB;

    protected ProgressBar anCenterPb;
    protected ProgressBar anTopPb;
    protected ProgressBar anBottomPb;

    protected boolean openComplex = false;
    protected boolean open_backOpp = false;
    protected boolean openMaterial = false;
    protected boolean open_progress = false;
    protected boolean reLayout = false;
    protected boolean materialLayout = true;


    protected boolean showRRight = false;
    protected boolean showRight = false;
    protected boolean blodTxTitle = false;
    protected boolean showBack = true;
    protected boolean showTopPb = true;
    protected boolean showCenterPb = true;
    protected boolean showBottomPb = false;
    protected boolean showPbTx2Anim = true;


    //默认设置complex，都是可见的
    //默认设置complex=false,都是不可见的
    protected int visibleRightIv = View.VISIBLE;
    protected int visibleRRightIv = View.VISIBLE;
    protected int visibleBackIv = View.VISIBLE;
    protected int visibleBackTx = View.VISIBLE;
    protected int visibleRightTx = View.VISIBLE;
    protected int visibleRRightTx = View.VISIBLE;
    private static AnimationDrawable animationDrawable;
    private String title;
    private String tvRight;
    private String txBack;
    private Drawable leftDrawable;
    private Drawable rightDrawable;
    private Drawable rrightDrawable;
    private Drawable background;
    private int anTxColorId;
    private int anTxRightColorId;
    private int anTxRRightColorId;
    private int anTxTitleColorId;
    private int anTxBackColorId;
    private int ananIndeterminateTint;
    private String tvRRight;

    public INABarLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public INABarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public INABarLayout(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.INABarLayout);
            _initAttr(ta);
//            LayoutInflater.from(getContext()).inflate(R.layout.base_headview_standard_complex, this);
            LayoutInflater.from(getContext()).inflate(R.layout.base_headview_standard, this);
            _initView();
            _initStyle();
            parseStyle(ta);
        }
    }


    private void _initAttr(TypedArray ta) {
        openComplex = ta.getBoolean(R.styleable.INABarLayout_open_complex, openComplex);
        open_backOpp = ta.getBoolean(R.styleable.INABarLayout_open_backopp, open_backOpp);
        openMaterial = ta.getBoolean(R.styleable.INABarLayout_open_material, openMaterial);
        open_progress = ta.getBoolean(R.styleable.INABarLayout_open_progress, open_progress);
        reLayout = ta.getBoolean(R.styleable.INABarLayout_reLayout, reLayout);
        blodTxTitle = ta.getBoolean(R.styleable.INABarLayout_blodTxTitle, blodTxTitle);
        materialLayout = ta.getBoolean(R.styleable.INABarLayout_materialLayout, materialLayout);

        /*标准注释2：可见状态控制*/
        showRRight = ta.getBoolean(R.styleable.INABarLayout_showRRight, showRRight);
        showRight = ta.getBoolean(R.styleable.INABarLayout_showRight, showRight);
        showBack = ta.getBoolean(R.styleable.INABarLayout_showBack, showBack);

        //进度条状态可视化
        showTopPb = ta.getBoolean(R.styleable.INABarLayout_showTopPb, showTopPb);
        showCenterPb = ta.getBoolean(R.styleable.INABarLayout_showCenterPb, showCenterPb);
        showBottomPb = ta.getBoolean(R.styleable.INABarLayout_showBottomPb, showBottomPb);
        showPbTx2Anim = ta.getBoolean(R.styleable.INABarLayout_showPbTx2Anim, showPbTx2Anim);


        visibleRightIv = ta.getInt(R.styleable.INABarLayout_visibilityRightIv, visibleRightIv);
        visibleRRightIv = ta.getInt(R.styleable.INABarLayout_visibilityRRightIv, visibleRRightIv);
        visibleBackIv = ta.getInt(R.styleable.INABarLayout_visibilityBackIv, visibleBackIv);

        visibleBackTx = ta.getInt(R.styleable.INABarLayout_visibilityBackTx, visibleBackTx);
        visibleRightTx = ta.getInt(R.styleable.INABarLayout_visibilityRightTx, visibleRightTx);
        visibleRRightTx = ta.getInt(R.styleable.INABarLayout_visibilityRRightTx, visibleRightTx);

        title = ta.getString(R.styleable.INABarLayout_anTxTitle);
        tvRight = ta.getString(R.styleable.INABarLayout_anTxRight);
        txBack = ta.getString(R.styleable.INABarLayout_anTxBack);
        leftDrawable = ta.getDrawable(R.styleable.INABarLayout_anIvBack);
        rightDrawable = ta.getDrawable(R.styleable.INABarLayout_anIvRight);
        background = ta.getDrawable(R.styleable.INABarLayout_anBarbackground);
        anTxColorId = ta.getResourceId(R.styleable.INABarLayout_anTxColor, 0);
        anTxRightColorId = ta.getResourceId(R.styleable.INABarLayout_anTxRightColor, 0);
        anTxRRightColorId = ta.getResourceId(R.styleable.INABarLayout_anTxRRightColor, 0);
        anTxTitleColorId = ta.getResourceId(R.styleable.INABarLayout_anTxTitleColor, 0);
        anTxBackColorId = ta.getResourceId(R.styleable.INABarLayout_anTxBackColor, 0);
        ananIndeterminateTint = ta.getResourceId(R.styleable.INABarLayout_anIndeterminateTint, 0);
        rrightDrawable = ta.getDrawable(R.styleable.INABarLayout_anIvRRight);
        tvRRight = ta.getString(R.styleable.INABarLayout_anTxRRight);
    }

    private void _initView() {
        anBackLl = findViewById(R.id.anLlBack);
        anRightLl = findViewById(R.id.anRightLl);
        llCenterPb2 = findViewById(R.id.llCenterPb2);
        llCenterPb = findViewById(R.id.llCenterPb);

        anBackIv = findViewById(R.id.anBackIv);
        anRightIv = findViewById(R.id.anRightIv);
        anRightIvA = findViewById(R.id.anRightIvA);
        anRightIvB = findViewById(R.id.anRightIvB);

        anCenterPb2 = findViewById(R.id.anCenterPb2);
        anLlCenter = findViewById(R.id.anLlCenter);


        anRightTx = findViewById(R.id.anRightTx);
        anRightTxA = findViewById(R.id.anRightTxA);
        anRightTxB = findViewById(R.id.anRightTxB);

        anTitleTx = findViewById(R.id.anTxTitle);
        anBackTx = findViewById(R.id.anBackTx);
        anTxPb2 = findViewById(R.id.anTxPb2);

        frameLayout = findViewById(R.id.frameLayout);

        fitBackView = findViewById(R.id.fitBackView);
        fitCenterView = findViewById(R.id.fitCenterView);
        fitRightView = findViewById(R.id.fitRightView);
        fitRightViewA = findViewById(R.id.fitRightViewA);
        fitRightViewB = findViewById(R.id.fitRightViewB);

        anRightRl = findViewById(R.id.anRightRl);
        anRightLl = findViewById(R.id.anRightLl);
        anRightLlA = findViewById(R.id.anRightLlA);
        anRightLlB = findViewById(R.id.anRightLlB);

        anCenterPb = findViewById(R.id.anCenterPb);
        anTopPb = findViewById(R.id.anTopPb);
        anBottomPb = findViewById(R.id.anBottomPb);

        anRightTx = findViewById(R.id.anRightTx);
        anRightTxA = findViewById(R.id.anRightTxA);
        anRightTxB = findViewById(R.id.anRightTxB);
    }


    /**
     * 统一Java代码设置，显示右边布局，复合布局，标准布局
     */
    private void rightLayout(boolean show) {
        if (openComplex) {
            anRightRl.setVisibility(show ? VISIBLE : GONE);
            anRightLl.setVisibility(GONE);
        } else {
            anRightLl.setVisibility(show ? VISIBLE : GONE);
            anRightRl.setVisibility(GONE);
        }
    }

    private void showRight() {
        if (showRight) {
            showRightLayout();
        }
        if (openComplex) {
            anRightLlA.setVisibility(showRight ? VISIBLE : GONE);
            anRightLlB.setVisibility(showRRight ? VISIBLE : GONE);
        }
    }

    private void showRRight() {
        if (showRRight) {
            showRightLayout();
        }
        if (openComplex) {
            anRightLlA.setVisibility(showRight ? VISIBLE : GONE);
            anRightLlB.setVisibility(showRRight ? VISIBLE : GONE);
        }
    }

    public void showRightLayout() {
        rightLayout(true);
    }

    public void hideRightLayout() {
        rightLayout(false);
    }

    public void openComplex() {
        openComplex = true;
        showRRight();
    }

    public void closeComplex() {
        openComplex = false;
        showRRight();
    }

    public void openMaterialFit() {
        openMaterial = true;
        materialFit(true);
    }

    public void closeMaterialFit() {
        openMaterial = false;
        materialFit(false);
    }

    private void materialFit(boolean show) {
        fitBackView.setVisibility(show ? VISIBLE : GONE);
        fitRightView.setVisibility(show ? VISIBLE : GONE);
        fitRightViewA.setVisibility(show ? VISIBLE : GONE);
        fitRightViewB.setVisibility(show ? VISIBLE : GONE);
        fitCenterView.setVisibility(show ? VISIBLE : GONE);
    }

    public void materialLayout(boolean materialLayout) {
        this.materialLayout = materialLayout;
        if (materialLayout) {
            anBackLl.setBackground(getResources().getDrawable(R.drawable.in_selector_md));
            anRightLl.setBackground(getResources().getDrawable(R.drawable.in_selector_md));
            anRightLlA.setBackground(getResources().getDrawable(R.drawable.in_selector_md));
            anRightLlB.setBackground(getResources().getDrawable(R.drawable.in_selector_md));
            anLlCenter.setBackground(getResources().getDrawable(R.drawable.in_selector_md));


        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                anBackLl.setForeground(null);
                anRightLl.setForeground(null);
                anRightLlA.setForeground(null);
                anRightLlB.setForeground(null);
            }
            anLlCenterNofouce();
            anTxPb2.setOnClickListener(null);
            anBackLl.setBackground(null);
            anLlCenter.setBackground(null);
            anRightLl.setBackground(null);
            anRightLlA.setBackground(null);
            anRightLlB.setBackground(null);
        }
    }

    private void anLlCenterNofouce() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            anLlCenter.setForeground(null);
            anTitleTx.setForeground(null);
            anTitleTx.setOnClickListener(null);
            anTitleTx.setBackground(null);
        }
    }

    public void showBackLayout(boolean show) {
        anBackLl.setVisibility(show ? VISIBLE : INVISIBLE);
    }

    private void _initStyle() {
        rightLayout(openComplex);
        showRight();
        showRRight();
        showBackLayout(showBack);
        if (openMaterial) {
            anTopPb.setVisibility(GONE);
            openMaterialFit();
        } else {
            closeMaterialFit();
        }
        if (!materialLayout) {
            materialLayout(false);
        }
        if (open_progress) {
            setPbVisibility(VISIBLE);
            if (!showTopPb) {
                setTopPbVisibility(GONE);
            }
            if (!showCenterPb) {
                setCenterPbVisibility(GONE);
            }
            if (showBottomPb) {
                setBottomPbVisibility(VISIBLE);
            } else {
                setBottomPbVisibility(GONE);
            }
        }
    }

    @SuppressLint("ResourceType")
    private void parseStyle(@NonNull TypedArray ta) {
        /*base_simple*/

        if (title != null) {
            setTitle(title);
        }
        if (tvRight != null) {
            setRightTx(tvRight);
        }
        if (txBack != null) {
            setBackTx(txBack);
        }
        //只有配置了reLayout属性才会去重新计算bar控件图标的大小
        if (reLayout) {
            requestInaLayout(ta);
        }
        openbackOpp(open_backOpp);
        if (null != leftDrawable) {
            anBackIv.setImageDrawable(leftDrawable);
        }
        if (null != rightDrawable) {
            anRightIv.setImageDrawable(rightDrawable);
        }
        if (null != background) {
            frameLayout.setBackgroundDrawable(background);
            frameLayout.setBackground(background);
        }
        //判断是否为-1，防止其它无效设置导致应用程序崩溃
        if (anTxColorId != 0 && anTxColorId != -1) {
            setTxColor(anTxColorId);
        }

        if (anTxRightColorId != 0 && anTxRightColorId != -1) {
            setRightTxColor(anTxRightColorId);
        }
        if (anTxRRightColorId != 0 && anTxRRightColorId != -1) {
            setRRightTxColor(anTxRRightColorId);
        }
        if (anTxTitleColorId != 0 && anTxTitleColorId != -1) {
            setTitleTxColor(anTxTitleColorId);
        }
        if (anTxBackColorId != 0 && anTxBackColorId != -1) {
            setBackTxColor(anTxTitleColorId);
        }

        if (ananIndeterminateTint != 0 && ananIndeterminateTint != -1) {
            setIndeterminateTint(ananIndeterminateTint);
        }
        if (tvRRight != null) {
            setRRightTx(tvRRight);
        }
        setVisibleRightTx(visibleRightTx);
        if (openComplex) {
            setVisibleRRightTx(visibleRRightTx);
        }
        if (null != rrightDrawable) {
            anRightIvB.setImageDrawable(rrightDrawable);
        }
        if (null != rightDrawable) {
            anRightIvA.setImageDrawable(rightDrawable);
        }
        setRightIvVisibility(visibleRightIv);
        setRRightIvVisibility(visibleRRightIv);
        setBackIvVisibility(visibleBackIv);
        setBackTxVisibility(visibleBackTx);

        if (blodTxTitle && anTitleTx != null) {
            RichTextView.setTextBold(anTitleTx);
        }
        ta.recycle();
    }

    private void requestInaLayout(@NonNull TypedArray ta) {
        int anBackIvWidth = ta.getDimensionPixelSize(R.styleable.INABarLayout_anIvBackWidth, (int) getResources().getDimension(R.dimen.an_ivbar_width));
        anBackIv.setLayoutParams(new LayoutParams(anBackIvWidth, anBackIvWidth));
        int anRightIvWidth = ta.getDimensionPixelSize(R.styleable.INABarLayout_anIvRightWidth, 10);
        if (openComplex) {
            int anRRightIvWidth = ta.getDimensionPixelSize(R.styleable.INABarLayout_anIvRRightWidth, 10);
            anRightIvB.setLayoutParams(new LayoutParams(anRRightIvWidth, anRRightIvWidth));
            anRightIvA.setLayoutParams(new LayoutParams(anRightIvWidth, anRightIvWidth));
        } else {
            anRightIv.setLayoutParams(new LayoutParams(anRightIvWidth, anRightIvWidth));
        }
    }

    public void openbackOpp(boolean open_backOpp) {
        this.open_backOpp = open_backOpp;
//        invalidate();
        if (open_backOpp) {
            anBackIv.setImageResource(R.drawable.in_selector_leftback_opp);
        } else {
            anBackIv.setImageResource(R.drawable.in_selector_leftback);
        }
    }

    public void openProgress(boolean open_progress) {
        this.open_progress = open_progress;
        invalidate();
    }

    public void setVisibleRightIv(int visibleRightIv) {
        this.visibleRightIv = visibleRightIv;
        setRightIvVisibility(visibleRightIv);
    }

    public void setVisibleRRightIv(int visibleRRightIv) {
        this.visibleRRightIv = visibleRRightIv;
        setRRightIvVisibility(visibleRRightIv);
    }

    public void setVisibleBackIv(int visibleBackIv) {
        this.visibleBackIv = visibleBackIv;
        setBackIvVisibility(visibleBackIv);
    }

    public void setVisibleBackTx(int visibleBackTx) {
        this.visibleBackTx = visibleBackTx;
        setBackTxVisibility(visibleBackTx);
    }

    public void setVisibleRightTx(int visibleRightTx) {
        this.visibleRightTx = visibleRightTx;
        setRightTxVisibility(visibleRightTx);
    }

    public void setVisibleRRightTx(int visibleRRightTx) {
        this.visibleRRightTx = visibleRRightTx;
        setRRightTxVisibility(visibleRRightTx);
    }

    public void setBackIvResource(@DrawableRes int resId) {
        anBackIv.setImageResource(resId);
    }

    public void setOnBackLlClickListener(OnClickListener listener) {
        anBackLl.setOnClickListener(listener);
    }

    public void setRightIvResource(@DrawableRes int resId) {
        if (openComplex) {
            anRightIvA.setImageResource(resId);
        } else {
            anRightIv.setImageResource(resId);
        }
    }

    public void setRRightIvResource(@DrawableRes int resId) {
        anRightIvB.setImageResource(resId);
    }

    public void setOnClickListener(OnClickListener listener) {
        setTitleTxColor(R.color.in_font_selector_bar);
        anTitleTx.setOnClickListener(null);
        anTxPb2.setOnClickListener(null);
        frameLayout.setOnClickListener(listener);
    }

    public void setOnTitleClickListener(OnClickListener listener) {
        anLlCenter.setClickable(true);
        if (materialLayout) {
            anLlCenter.setBackground(getResources().getDrawable(R.drawable.in_selector_md));
        }
        if (!(anTxTitleColorId != 0 && anTxTitleColorId != -1)) {
            setTitleTxColor(R.color.in_font_selector_bar);
        }
        anTitleTx.setOnClickListener(listener);
        anTxPb2.setOnClickListener(listener);
        anLlCenter.setOnClickListener(listener);
    }

    public void setOnRightLlClickListener(OnClickListener listener) {
        if (!(anTxRightColorId != 0 && anTxRightColorId != -1)) {
            setRightTxColor(R.color.in_font_selector_bar);
        }
        anRightLl.setOnClickListener(listener);
        anRightLlA.setOnClickListener(listener);
    }

    public void setOnRRightLlClickListener(OnClickListener listener) {
        if (!(anTxRRightColorId != 0 && anTxRRightColorId != -1)) {
            setRRightTxColor(R.color.in_font_selector_bar);
        }
        anRightLlB.setOnClickListener(listener);
    }

    private void setRRightIvVisibility(int visibility) {
        anRightIvB.setVisibility(visibility);
    }

    private void setRightIvVisibility(int visibility) {
        anRightIv.setVisibility(visibility);
        anRightIvA.setVisibility(visibility);
    }

    private void setRightVisibility(int visibility) {
        setRightLlVisibility(visibility);
        setRRightLlVisibility(visibility);
    }

    private void setBackIvVisibility(int visibility) {
        anBackIv.setVisibility(visibility);
    }


    public void setRRightLlVisibility(int visibility) {
        anRightLlB.setVisibility(visibility);
    }

    public void setRightLlVisibility(int visibility) {
        anRightLl.setVisibility(visibility);
    }

    public void setVisibility(int visibility) {
        frameLayout.setVisibility(visibility);
    }

    public void setCenterPbVisibility(int visibility) {
        anCenterPb.setVisibility(visibility);
    }

    public void setTopPbVisibility(int visibility) {
        anTopPb.setVisibility(visibility);
    }

    public void setBottomPbVisibility(int visibility) {
        anBottomPb.setVisibility(visibility);
    }

    public void setPbVisibility(int visibility) {
        setTopPbVisibility(visibility);
        setCenterPbVisibility(visibility);
    }

    public void hideCenterPb() {
        setCenterPb2(false, 0, AnConstants.EMPTY);
    }

    public void setCenterPbTx2(String pbTxt) {
        if (!TextUtils.isEmpty(pbTxt)) {
            anTxPb2.setVisibility(VISIBLE);
            RichTextView.setRichText(anTxPb2, pbTxt);
        }
        anCenterPb2.clearAnimation();
        anCenterPb2.setVisibility(GONE);
        llCenterPb.setVisibility(GONE);
        llCenterPb2.setVisibility(VISIBLE);
    }

    public void setShowPbTx2Anim(boolean showPbTx2Anim) {
        this.showPbTx2Anim = showPbTx2Anim;
    }

    public void showCenterPb2() {
        showCenterPb2(AnConstants.EMPTY);
    }

    public void showCenterPb2(String pbTxt) {
        showCenterPb2(0, pbTxt);
    }

    public void showCenterPb2(int drawableRes, String pbTxt) {
        setCenterPb2(true, drawableRes, pbTxt);
    }

    private void setCenterPb2(boolean show, int drawableRes, String pbTxt) {
        if (show) {
            llCenterPb.setVisibility(GONE);
            llCenterPb2.setVisibility(VISIBLE);
            if (anCenterPb2 != null) {
                anCenterPb2.setVisibility(VISIBLE);
                if (drawableRes != 0) {
//                        anCenterPb2.setBackgroundResource(drawableRes);
                    GlideEngine.getInstance().loadImage(drawableRes, R.drawable.in_selector_loading, anCenterPb2);
                } else {
                    animationDrawable = ViewUtils.getInstance().rotateIvFrame(anCenterPb2);
                }
            }
            if (anTxPb2 != null && !TextUtils.isEmpty(pbTxt)) {
                if (showPbTx2Anim) {
                    String orignalTx2 = anTxPb2.getText().toString().trim();
                    anTxPb2.setVisibility(VISIBLE);
                    if (!pbTxt.equals(orignalTx2)) {
                        anTxPb2.setVisibility(VISIBLE);
                        RichTextView.setRichText(anTxPb2, pbTxt);
                        ThreadPool.FLAG_WORKING = false;
//                            sequencePoint(anTxPb2, pbTxt);
                        RichTextView.repatSequenceText(anTxPb2, pbTxt);
                    }
                } else {
                    RichTextView.setRichText(anTxPb2, pbTxt);
                }
            }
        } else {
            if (showPbTx2Anim) {
                ThreadPool.FLAG_WORKING = true;
            }
            llCenterPb2.setVisibility(GONE);
            llCenterPb.setVisibility(VISIBLE);
            if (null != animationDrawable && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }
            anTxPb2.setVisibility(INVISIBLE);
//            anTxPb2.setText(pbTxt);

            setPbVisibility(View.GONE);
            anCenterPb2.clearAnimation();
        }
    }

    private static void sequencePoint(TextView tvPoint, String valuse) {
        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong % 3 == 0) {
                            tvPoint.setText(valuse + "•");
                        } else if (aLong % 3 == 1) {
                            tvPoint.setText(valuse + "••");
                        } else {
                            tvPoint.setText(valuse + "•••");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void setRRightTxVisibility(int visibility) {
        anRightTxB.setVisibility(visibility);
    }

    private void setRightTxVisibility(int visibility) {
        anRightTxA.setVisibility(visibility);
        anRightTx.setVisibility(visibility);
    }

    private void setBackTxVisibility(int visibility) {
        anBackTx.setVisibility(visibility);
    }

    public void setTxColor(@ColorRes int txColorId) {
        setBackTxColor(txColorId);
        setTitleTxColor(txColorId);
        setRightTxColor(txColorId);
        setRRightTxColor(txColorId);
    }

    public void setIndeterminateTint(@ColorRes int colorId) {
        anCenterPb.setIndeterminateTintList(getResources().getColorStateList(colorId));
//        anCenterPb.setProgressDrawable();
//        anTopPb.setIndeterminateTintList(getResources().getColorStateList(colorId));
//        anBottomPb.setIndeterminateTintList(getResources().getColorStateList(colorId));
    }

    public void setBackTxColor(@ColorRes int colorId) {
        anTxBackColorId = colorId;
        RichTextView.setTextColor(anBackTx, colorId);
    }

    public void setRRightTxColor(@ColorRes int colorId) {
        anTxRRightColorId = colorId;
        RichTextView.setTextColor(anRightTxB, colorId);
    }

    public void setRightTxColor(@ColorRes int colorId) {
        anTxRightColorId = colorId;
        RichTextView.setTextColor(anRightTx, colorId);
        RichTextView.setTextColor(anRightTxA, colorId);
    }

    public void setTitleTxColor(@ColorRes int colorId) {
        anTxTitleColorId = colorId;
        RichTextView.setTextColor(anTitleTx, colorId);
        setPb2TxColor(colorId);
    }

    public void setPb2TxColor(@ColorRes int colorId) {
        RichTextView.setTextColor(anTxPb2, colorId);
    }

    public void setBoldTitle() {
        RichTextView.setTextBold(anTitleTx);
    }

    public void setBoldBackTx() {
        RichTextView.setTextBold(anBackTx);
    }

    public void setTitle(String title) {
        RichTextView.setRichText(anTitleTx, title);
    }

    public void setTitle(@StringRes int resId) {
        setTitle(getResources().getString(resId));
    }

    public void setBackTx(String backTx) {
        RichTextView.setRichText(anBackTx, backTx);
    }

    public void setBackTx(@StringRes int resId) {
        setBackTx(getResources().getString(resId));
    }

    public void setRightTx(String rightTx) {
        RichTextView.setRichText(anRightTx, rightTx);
        RichTextView.setRichText(anRightTxA, rightTx);
    }

    public void setRightTx(@StringRes int resId) {
        setRightTx(getResources().getString(resId));
    }

    public void setRRightTx(String rrightTx) {
        RichTextView.setRichText(anRightTxB, rrightTx);
    }

    public void setRRightTx(@StringRes int resId) {
        setRRightTx(getResources().getString(resId));
    }

    public void setBackgroundColor(@ColorInt int color) {
        frameLayout.setBackgroundColor(color);
    }

    public void setBackgroundResource(@DrawableRes int drawableRes) {
        frameLayout.setBackgroundResource(drawableRes);
    }

    public void setBackgroundResource(Drawable drawableRes) {
        frameLayout.setBackground(drawableRes);
    }

    public void setBackgroundAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
        frameLayout.setAlpha(alpha);
    }

    public LinearLayout getLeftLayout() {
        return anBackLl;
    }

    public LinearLayout getRightLayout() {
        if (openComplex) {
            return anRightLlA;
        } else {
            return anRightLl;
        }
    }

    public LinearLayout getRRightLayout() {
        return anRightLlB;
    }

    public ProgressBar getCenterPb() {
        return anCenterPb;
    }

    public ProgressBar getTopPb() {
        return anTopPb;
    }

    public ProgressBar getBottomPb() {
        return anBottomPb;
    }

    public TextView getRightTx() {
        return anRightTx;
    }

    public TextView getRRightTx() {
        return anRightTxB;
    }

    public TextView getTitleTx() {
        return anTitleTx;
    }
}