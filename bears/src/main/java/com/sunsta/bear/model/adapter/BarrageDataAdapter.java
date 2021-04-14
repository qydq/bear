package com.sunsta.bear.model.adapter;

import android.os.SystemClock;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.sunsta.bear.R;
import com.sunsta.bear.callback.OnBarrageClickListener;
import com.sunsta.bear.callback.OnBarrageLayout;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.engine.gif.GifImageView;
import com.sunsta.bear.entity.Barrage;
import com.sunsta.bear.faster.LaLog;
import com.sunsta.bear.faster.ScreenUtils;
import com.sunsta.bear.faster.ValueOf;
import com.sunsta.bear.immersion.RichTextView;
import com.sunsta.bear.layout.INABarrageView;
import com.sunsta.bear.layout.INACircleImageView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：视频播放弹幕控制的Adapter适配器
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2017/8/18
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 4.0 |   2020/08/29          |   最新版本移除不再提供出支持的AbsBarrageDataAdapter作为继承关系，替代为自己OnBarrageLayout，自定义布局内容
 * 提供了Barrage实体，并且如果要扩展，可以重写an_item_barrage
 * @link 知乎主页：href="https://zhihu.com/people/qydq
 * <p> ---Revision History:  ->  : |version|date|updateinfo| ---------
 */
public final class BarrageDataAdapter<T> {
    private static final String TAG = "BarrageDataAdapter";

    private WeakReference<INABarrageView> mBarrageView = new WeakReference<INABarrageView>(null);

    public void setBarrageView(INABarrageView view) {
        mBarrageView = new WeakReference<>(view);
    }

    public INABarrageView getBarrageView() {
        return mBarrageView.get();
    }

    private float barrageHeight;

    private OnBarrageLayout onBarrageLayout;
    private OnBarrageClickListener barrageClickListener;

    @LayoutRes
    private int mBarrageLayoutId;


    public BarrageDataAdapter(@LayoutRes int barrageLayoutId) {
        this.mBarrageLayoutId = barrageLayoutId;
    }

    public BarrageDataAdapter() {
        this.mBarrageLayoutId = 0;
    }

    public void addBarrage(Barrage obj) {
        addBarrage(obj, null);
    }

    public void addBarrage(Barrage obj, OnBarrageLayout barrageDataAdapter) {
        if (mBarrageView.get() == null) {
            return;
        }
        mBarrageView.get().addBarrage(obj);
        this.onBarrageLayout = barrageDataAdapter;
    }

    public void addRowBarrage(Barrage obj) {
        addRowBarrage(obj, null);
    }

    public void addRowBarrage(Barrage obj, OnBarrageLayout barrageDataAdapter) {
        if (mBarrageView.get() == null) {
            return;
        }
        mBarrageView.get().addRowBarrage(obj);
        this.onBarrageLayout = barrageDataAdapter;
    }

    public void addBarrageToRow(int rowIndex, Barrage obj) {
        addBarrageToRow(rowIndex, obj, null);
    }

    public void addBarrageToRow(int rowIndex, Barrage obj, OnBarrageLayout barrageDataAdapter) {
        if (mBarrageView.get() == null) {
            return;
        }
        mBarrageView.get().addBarrageToRow(rowIndex, obj);
        this.onBarrageLayout = barrageDataAdapter;
    }

    /**
     * Barrage Templates
     **/
    public static class BarrageType {
        public static final String TEXT = "text";
        public static final String IMAGE_TEXT = "image_text";
    }

    public void destroyView(ViewGroup root, Barrage obj, View view) {
        LaLog.d(TAG, "destroyView " + view);
    }

    public boolean isViewFromObject(View view, Barrage obj) {
        return view.getTag().equals(obj.getType());
    }

    public void addBarrageList(List<Barrage> obj) {
        addBarrageList(obj, null);
    }

    public void addBarrageList(List<Barrage> obj, OnBarrageLayout barrageDataAdapter) {
        if (mBarrageView.get() == null) {
            return;
        }
        for (Barrage barrage : obj) {
            mBarrageView.get().addBarrage(barrage);
            SystemClock.sleep(69);
        }
        this.onBarrageLayout = barrageDataAdapter;
    }

    public View createView(ViewGroup root, View converView, Barrage barrage) {
//        布局内容优先级最高，牺牲部分性能的同时保证扩展性，点击事件保证action0[RelativeLayout] 和frameLayout[FrameLayout]
        if (barrage.getBarrageLayout() != 0) {
            mBarrageLayoutId = barrage.getBarrageLayout();
        }
        RelativeLayout action0;
        if (converView != null) {
            LaLog.d(ValueOf.logLivery(String.format("x %f y %f l %d t %d r %d", converView.getX(), converView.getY(), converView.getLeft(), converView.getTop(), converView.getRight())));
            if (mBarrageLayoutId == 0) {
                return loadBarrageData(converView, barrage);
            } else {
                if (onBarrageLayout != null) {
                    action0 = converView.findViewById(R.id.action0);
                    if (action0 != null) {
                        action0.setOnClickListener(view -> {
                            if (barrageClickListener != null) {
                                barrageClickListener.onClick(barrage);
                            }
                        });
                    }
                    converView.setTag(BarrageDataAdapter.BarrageType.IMAGE_TEXT);//complex default is image_text _type
                    return onBarrageLayout.barrageLayout(converView, barrage);
                } else {
                    return loadBarrageData(converView, barrage);
                }
            }
        }
        if (mBarrageLayoutId == 0) {
            converView = LayoutInflater.from(root.getContext()).inflate(R.layout.an_item_barrage, root, false);
            loadBarrageData(converView, barrage);
        } else {
            converView = LayoutInflater.from(root.getContext()).inflate(mBarrageLayoutId, root, false);
            if (onBarrageLayout != null) {
                converView = onBarrageLayout.barrageLayout(converView, barrage);
                action0 = converView.findViewById(R.id.action0);
                if (action0 != null) {
                    action0.setOnClickListener(view -> {
                        if (barrageClickListener != null) {
                            barrageClickListener.onClick(barrage);
                        }
                    });
                }
                converView.setTag(BarrageDataAdapter.BarrageType.IMAGE_TEXT);//complex default is image_text _type
            } else {
                loadBarrageData(converView, barrage);
            }
        }
        return converView;
    }

    /**
     * 当提供对外布局时候，可以直接调用本方法，或者参考本方法实现
     */
    public View loadBarrageData(View converView, Barrage barrage) {
        TextView tvName, tvContent;
        ImageView ivPrimary, ivLight, ivMark;
        FrameLayout frameLayout;
        RelativeLayout action0;
        LinearLayout llMark;
        GifImageView ivGif;
        INACircleImageView ivCircle;
        frameLayout = converView.findViewById(R.id.frameLayout);
        action0 = converView.findViewById(R.id.action0);
        llMark = converView.findViewById(R.id.llMark);
        tvName = converView.findViewById(R.id.tvName);
        tvContent = converView.findViewById(R.id.tvContent);
        ivPrimary = converView.findViewById(R.id.ivPrimary);
        ivLight = converView.findViewById(R.id.ivLight);
        ivMark = converView.findViewById(R.id.ivMark);
        ivGif = converView.findViewById(R.id.ivGif);
        ivCircle = converView.findViewById(R.id.ivCircle);

        if (tvName != null) {
            String name = barrage.getUserName();
            if (barrage.getTextLightColor() != 0) {
                RichTextView.setTextColor(tvName, barrage.getTextLightColor());
            }
            if (!TextUtils.isEmpty(name)) {
                tvName.setVisibility(View.VISIBLE);
                tvName.setText(name);
            } else {
                tvName.setVisibility(View.GONE);
            }
        }

        if (tvContent != null) {
            String content = barrage.getContent();
            if (barrage.getTextPrimaryColor() != 0) {
                RichTextView.setTextColor(tvContent, barrage.getTextPrimaryColor());
            }
            if (!TextUtils.isEmpty(content)) {
                tvContent.setVisibility(View.VISIBLE);
                RichTextView.setRichText(tvContent, content);
            } else {
                tvContent.setVisibility(View.GONE);
            }
//            tvContent.setOnClickListener(view -> {
//                if (barrageClickListener != null) {
//                    barrageClickListener.onItemClick(barrage);
//                }
//            });
            if (barrage.getTextPrimarySize() != 0) {
                RichTextView.setTextSize(tvContent, barrage.getTextPrimarySize());
            }
        }

        if (ivGif != null) {
            if (barrage.getGifIvId() != 0) {
                ivGif.setVisibility(View.VISIBLE);
                ivGif.setImageResource(barrage.getGifIvId());
            } else {
                ivGif.setVisibility(View.GONE);
            }
        }
        if (action0 != null) {
            action0.setOnClickListener(view -> {
                if (barrageClickListener != null) {
                    barrageClickListener.onClick(barrage);
                }
            });
        }

        if (BarrageType.TEXT.equals(barrage.getType())) {
            //仅仅是一个文字sbarrageView
            converView.setX(0);
            if (ivLight != null) {
                ivLight.setVisibility(View.GONE);
            }
            if (ivPrimary != null) {
                ivPrimary.setVisibility(View.GONE);
            }

            if (ivMark != null) {
                ivMark.setVisibility(View.GONE);
            }
            converView.setTag(BarrageType.TEXT);
        } else if (BarrageType.IMAGE_TEXT.equals(barrage.getType())) {
            //图文的barrageView
            String primaryLink = barrage.getPrimaryLink();
            int primaryIvId = barrage.getPrimaryIvId();
            if (TextUtils.isEmpty(primaryLink) && primaryIvId == 0) {
                if (ivPrimary != null) {
                    ivPrimary.setVisibility(View.GONE);
                }
            } else {
                if (ivPrimary != null) {
                    ivPrimary.setVisibility(View.VISIBLE);
                    if (primaryIvId != 0) {
                        GlideEngine.getInstance().loadCornerImage(primaryIvId, barrage.getIvPrimaryRadius(), ivPrimary);
                    }
                    if (!TextUtils.isEmpty(primaryLink)) {
                        GlideEngine.getInstance().loadCornerImage(primaryLink, barrage.getIvPrimaryRadius(), ivPrimary);
                    }
                }
            }

            if (ivLight != null) {
                String lightLink = barrage.getLightLink();
                int lightIvId = barrage.getLightIvId();
                if (TextUtils.isEmpty(lightLink) && lightIvId == 0) {
                    ivLight.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(lightLink)) {
                        ivLight.setVisibility(View.VISIBLE);
                        GlideEngine.getInstance().loadImage(lightLink, ivLight);
                    } else {
                        if (lightIvId != 0) {
                            ivLight.setVisibility(View.VISIBLE);
                            GlideEngine.getInstance().loadImage(lightIvId, ivLight);
//                            ivLight.setImageResource(lightIvId);//gif not use
                        } else {
                            ivLight.setVisibility(View.GONE);
                        }
                    }
                }
            }
            if (ivMark != null) {
                String markLink = barrage.getMarkLink();
                int markIvId = barrage.getMarkIvId();
                if (TextUtils.isEmpty(markLink) && markIvId == 0) {
                    ivMark.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(markLink)) {
                        ivMark.setVisibility(View.VISIBLE);
                        GlideEngine.getInstance().loadImage(markLink, ivMark);
                    } else {
                        if (markIvId != 0) {
                            ivMark.setVisibility(View.VISIBLE);
                            GlideEngine.getInstance().loadImage(markIvId, ivMark);
//                            ivMark.setImageResource(markIvId);//gif not use
                        } else {
                            ivMark.setVisibility(View.GONE);
                        }
                    }
                }
            }

            if (ivCircle != null) {
                String circleLink = barrage.getCircleLink();
                int circleIvId = barrage.getCircleIvId();
                if (TextUtils.isEmpty(circleLink) && circleIvId == 0) {
                    ivCircle.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(circleLink)) {
                        ivCircle.setVisibility(View.VISIBLE);
                        GlideEngine.getInstance().loadImage(circleLink, ivCircle);
                    } else {
                        if (circleIvId != 0) {
                            ivCircle.setVisibility(View.VISIBLE);
                            GlideEngine.getInstance().loadImage(circleIvId, ivCircle);
//                            ivCircle.setImageResource(circleIvId);//gif not use
                        } else {
                            ivCircle.setVisibility(View.GONE);
                        }
                    }
                }
            }
            converView.setTag(BarrageType.IMAGE_TEXT);
        }


        //同时：规则为：弹幕的高度单位dp，设置默认0，如果小于30dp,则用39dp(默认an_heingt);大于30则使用，设置的动态值
        if (tvContent != null) {
            barrageHeight = tvContent.getResources().getDimensionPixelOffset(R.dimen.an_dimen_barrage_height);
            if (barrage.getBarrageHeight() != 0) {
                float barrageIdHeight = tvContent.getResources().getDimensionPixelOffset(barrage.getBarrageHeight());
                if (barrageIdHeight > barrageHeight) {
                    barrageHeight = barrageIdHeight;
                }
            }
            if (barrage.isFillBarrageWidth()) {
                float resultWidth = Layout.getDesiredWidth(tvContent.getText().toString(), 0, tvContent.getText().length(), tvContent.getPaint()) + ScreenUtils.dp2px(barrage.getPatchBarrageWidth() + 30);
                if ((!TextUtils.isEmpty(barrage.getPrimaryLink()) || barrage.getPrimaryIvId() != 0) && ivPrimary != null) {
                    ivPrimary.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            float resultWidth2 = resultWidth + ivPrimary.getWidth();
                            ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                            params.width = (int) resultWidth2; //这里需要加上一个offset
                            params.height = (int) barrageHeight;
                            frameLayout.setLayoutParams(params);

                            ivPrimary.getViewTreeObserver().removeOnPreDrawListener(this);
                            return false;
                        }
                    });
                } else {
//                    int dipContentWidth = ScreenUtils.px2dip(this, pxContentWidth);
                    /*
                     *livery注意点1，必须使用px长度，宽度的单位
                     * */
                    ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                    params.width = (int) resultWidth; //这里需要加上一个offset
                    params.height = (int) barrageHeight;
                    frameLayout.setLayoutParams(params);
                }
            }

            if (barrage.isFillGifWidth() && ivGif != null) {
                float resultWidth = Layout.getDesiredWidth(tvContent.getText().toString(), 0, tvContent.getText().length(), tvContent.getPaint()) + ScreenUtils.dp2px(barrage.getPatchBarrageWidth() + 30);
                if ((!TextUtils.isEmpty(barrage.getPrimaryLink()) || barrage.getPrimaryIvId() != 0) && ivPrimary != null) {
                    ivPrimary.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            float resultWidth2 = resultWidth + ivPrimary.getWidth();
                            ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                            params.width = (int) resultWidth2; //这里需要加上一个offset
                            params.height = (int) barrageHeight;
                            ScreenUtils.setLayoutParams(ivGif, params.width, params.height);
//                            ivGif.setLayoutParams(params);
                            ivPrimary.getViewTreeObserver().removeOnPreDrawListener(this);
                            return false;
                        }
                    });
                } else {
//                    int dipContentWidth = ScreenUtils.px2dip(this, pxContentWidth);
                    /*
                     *livery注意点2，必须使用px长度，宽度的单位
                     * */
                    ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                    params.width = (int) resultWidth; //这里需要加上一个offset
                    params.height = (int) barrageHeight;
                    ScreenUtils.setLayoutParams(ivGif, params.width, params.height);
//                    ivGif.setLayoutParams(params);
                }
            }

            if (barrage.getBackground() != 0 && action0 != null) {
                action0.setBackgroundResource(barrage.getBackground());
            }

            if (llMark != null) {
                if (barrage.getMarkBackground() != 0) {
                    llMark.setVisibility(View.VISIBLE);
                    llMark.setBackgroundResource(barrage.getMarkBackground());
                } else {
                    llMark.setVisibility(View.GONE);
                }
            }

            /*
             * livery注意点2： 在本布局中，tvContent拿到的是它父布局的RelativeLayout.LayoutParams,即包裹着tvContent的action0
             * 并且：[tvContent.getWidth始终为0的解决方案](https://blog.csdn.net/lj402159806/article/details/53380089)
             * 需要注意的是：在不适用的使用需要调用：vto.removeOnPreDrawListener(onPreDrawListener);
             */
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvContent.getLayoutParams();
//        layoutParams.width = tvContent.getWidth();
//        layoutParams.height = (int) (pxFrameLayoutHeight);
//        frameLayout.setLayoutParams(layoutParams);
        }
        return converView;
    }

    public void setBarrageClickListener(OnBarrageClickListener barrageClickListener) {
        this.barrageClickListener = barrageClickListener;
    }
}