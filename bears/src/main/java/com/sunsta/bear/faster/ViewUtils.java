package com.sunsta.bear.faster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.tabs.TabLayout;
import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.faster.callback.OnGifListener;
import com.sunsta.bear.layout.tablayout.ValueAnimatorCompat;
import com.sunsta.bear.layout.tablayout.ValueAnimatorCompatImpl;
import com.sunsta.bear.model.entity.Icon;
import com.sunsta.bear.model.entity.IconType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 请关注个人知乎bgwan， 在【an情景】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：UI操作类
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 *
 * @author sunst // sunst0069
 * @version 2.0 |  2021/01/18             |   UI操作类，整合了原livery1.1.x AnimUtils，ViewUtils,LaUi
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class ViewUtils {
    private final static int DURATION = 450;


    public static final ViewUtilsImpl IMPL;

    public static final ValueAnimatorCompat.Creator DEFAULT_ANIMATOR_CREATOR = () -> new ValueAnimatorCompat(new ValueAnimatorCompatImpl());

    private interface ViewUtilsImpl {
        void setBoundsViewOutlineProvider(View view);
    }

    public static class ViewUtilsImplBase implements ViewUtilsImpl {
        @Override
        public void setBoundsViewOutlineProvider(View view) {
            // no-op
        }
    }

    public static class ViewUtilsImplLollipop implements ViewUtilsImpl {
        @Override
        public void setBoundsViewOutlineProvider(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
            }
        }
    }


    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            IMPL = new ViewUtilsImplLollipop();
        } else {
            IMPL = new ViewUtilsImplBase();
        }
    }

    public static ViewUtils getInstance() {
        return LaEnumUi.INSTANCE.getInstance();
    }

    private enum LaEnumUi {
        INSTANCE;
        private ViewUtils laInstance;

        LaEnumUi() {
            laInstance = new ViewUtils();
        }

        private ViewUtils getInstance() {
            return laInstance;
        }
    }


    /*(1):原AnimUtils，包含的动画类别*/
    public void zoom(View view, boolean isZoomAnim) {
        if (isZoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.12f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.12f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    public void disZoom(View view, boolean isZoomAnim) {
        if (isZoomAnim) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(view, "scaleX", 1.12f, 1f),
                    ObjectAnimator.ofFloat(view, "scaleY", 1.12f, 1f)
            );
            set.setDuration(DURATION);
            set.start();
        }
    }

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param flag
     */
    public void rotateArrow(ImageView arrow, boolean flag) {
        float pivotX = arrow.getWidth() / 2f;
        float pivotY = arrow.getHeight() / 2f;
        // flag为true则向上
        float fromDegrees = flag ? 180f : 180f;
        float toDegrees = flag ? 360f : 360f;
        //旋转动画效果   参数值 旋转的开始角度  旋转的结束角度  pivotX x轴伸缩值
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees,
                pivotX, pivotY);
        //该方法用于设置动画的持续时间，以毫秒为单位
        animation.setDuration(350);
        //启动动画
        arrow.startAnimation(animation);
    }


    /**
     * (2)原LaUi
     */
    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public List<Icon> getIcons() {
        List<Icon> icons = new ArrayList();
        icons.add(new Icon(R.mipmap.base_image_arrowback, R.mipmap.base_image_arrowback, IconType.Heart));
        icons.add(new Icon(R.mipmap.base_image_arrowback, R.mipmap.base_image_arrowback, IconType.Star));
        icons.add(new Icon(R.mipmap.base_image_clear_click, R.mipmap.base_image_clear, IconType.Thumb));
        return icons;
    }

    public void playHeartAnimation(@NonNull View animationView) {
        ObjectAnimator animater1 = ObjectAnimator.ofFloat(animationView, "scaleX", 1f, 0.8f, 1f);
        ObjectAnimator animater2 = ObjectAnimator.ofFloat(animationView, "scaleY", 1f, 0.8f, 1f);

        animater1.setRepeatMode(ObjectAnimator.REVERSE);
        animater1.setRepeatCount(Animation.INFINITE);
        animater2.setRepeatMode(ObjectAnimator.REVERSE);
        animater2.setRepeatCount(Animation.INFINITE);

        AnimatorSet animaterSet = new AnimatorSet();
        animaterSet.play(animater1).with(animater2);
        animaterSet.setDuration(1000);
        animaterSet.start();
    }

    public void playExistAnimationListAnimation(@NonNull ImageView imageView) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (animationDrawable != null) {
            animationDrawable.stop();
            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }
            animationDrawable = null;
        } else {
            LaLog.e("aliTips --> not set animationDrawable background or imageView drawable resouce is null");
        }
    }

    public void playAnimationListAnimation(@NonNull Context context, @NonNull ImageView imageView, int[] resouceId) {
        //创建一个AnimationDrawable
        AnimationDrawable animationDrawable = new AnimationDrawable();
        //准备好资源图片
//        int[] ids = {R.drawable.anim_1,R.drawable.anim_2,R.drawable.anim_3,R.drawable.anim_4};
        //通过for循环添加每一帧动画
        for (int i = 0; i < 4; i++) {
            Drawable frame = context.getResources().getDrawable(resouceId[i]);
            //设定时长
            animationDrawable.addFrame(frame, 200);
        }
        animationDrawable.setOneShot(false);
        //将动画设置到背景上
        imageView.setBackground(animationDrawable);
        //开启帧动画
        animationDrawable.start();
    }


    public void stopAnimation(@NonNull ImageView imageView) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (imageView.getAnimation() != null) {
            imageView.clearAnimation();
        }
        if (animationDrawable != null) {
//            animationDrawable.setVisible(true,true);//设置当前动画的第一帧，然后停止，可能联想之类的手机不支持 
            animationDrawable.selectDrawable(0);      //选择当前动画的第一帧，然后停止
            animationDrawable.stop();
        }
    }

    public void stopAnimation(@NonNull ImageView imageView, int selectFrame) {
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        if (imageView.getAnimation() != null) {
            imageView.clearAnimation();
        }
        if (animationDrawable != null) {
//            animationDrawable.setVisible(true,true);//设置当前动画的第一帧，然后停止，可能联想之类的手机不支持 
            animationDrawable.selectDrawable(selectFrame);      //选择当前selectDrawable动画的第一帧，然后停止
            animationDrawable.stop();
        }
    }

    /**
     * 刷新闪动动画
     */
    public void flashOnceUpDown(View view) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -10, 10, 0);
        translationY.setDuration(400);
        translationY.start();
    }

    /**
     * 抖动动画2, 闪动2次
     */
    //
    public void flashTwiceUpDown(View view) {
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -20, 20, 0);
        translationY.setDuration(1000);
        translationY.setInterpolator(new AnticipateOvershootInterpolator());
        translationY.start();
    }

    /**
     * 左右摇晃的动画
     */
    public void playShakeAnimation(View view) {
        TranslateAnimation animation = new TranslateAnimation(0, -5, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);//设置动画持续时间
        animation.setRepeatCount(-1);//设置重复次数
        animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
        view.startAnimation(animation);
    }

    /**
     * ImageView针动画，旋转，注意判空
     */
    public AnimationDrawable rotateIvFrame(@NonNull ImageView imageView) {
        Drawable loadingDrawable = imageView.getDrawable();
        if (null != loadingDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) loadingDrawable;
            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }
            return animationDrawable;
        }
        return null;
    }

    /**
     * 图片跳动 按钮模拟心脏跳动
     *
     * @param view 传递一个viwe布局
     */
    public void playHeartbeatAnimation(final View view) {
//        ScaleAnimation scaleAnimation = new ScaleAnimation();
//        scaleAnimation.setDuration();
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f));
        animationSet.setDuration(300);
        animationSet.setRepeatMode(AnimationSet.REVERSE);
        animationSet.setRepeatCount(Animation.INFINITE);
        animationSet.setInterpolator(new AccelerateInterpolator());
//        animationSet.setInterpolator((Interpolator) AnimationUtils.DECELERATE_INTERPOLATOR);
        // 结尾停在最后一针
//        animationSet.setFillAfter(true);
        // 对动画进行监听
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            // 开始时候怎么样
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 结束时候怎么
            @Override
            public void onAnimationEnd(Animation animation) {
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(new ScaleAnimation(1.2f, 1.0f, 1.2f,
                        1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f));
                animationSet.setDuration(300);
                animationSet.setRepeatMode(AnimationSet.REVERSE);
                animationSet.setRepeatCount(Animation.INFINITE);
                animationSet.setInterpolator(new DecelerateInterpolator());
//                animationSet.setFillAfter(false);

                playHeartbeatAnimation(view);
                // 实现心跳的View
//                view.startAnimation(animationSet);
            }
        });
        // 实现心跳的View
        view.startAnimation(animationSet);
    }

    /**
     * 不管软键盘状态如何，都隐藏软键盘
     *
     * @param activity    上下文Activity
     * @param togleMethod 是否使用toggleSoft方法隐藏软键盘
     */
    public void hideSoftKeyboard(@NonNull Activity activity, @Nullable boolean togleMethod) {
        if (keyboardIsActive(activity)) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                if (togleMethod) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    Window window = activity.getWindow();
                    if (window != null && window.getAttributes() != null && window.getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                        if (activity.getCurrentFocus() != null)
                            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        }
    }

    /**
     * 打开软键盘通用
     *
     * @param activity 上下文Activity
     */
    public void openSoftKeyboard(@NonNull Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.findFocus();
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 隐藏软键盘通用
     *
     * @param activity 上下文Activity
     */
    public void hideSoftKeyboard(@NonNull Activity activity) {
        hideSoftKeyboard(activity, false);
    }

    /**
     * 得到打开的软键盘的状态
     *
     * @param activity 上下文Activity
     * @return 返回软键盘打开的状态，默认为false
     */
    public boolean keyboardIsActive(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm != null && imm.isActive();
    }

    /**
     * night and dat set change animation
     *
     * @param activity 上下文Activity
     */
    public void showAnimation(@NonNull Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(activity);
            view.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), cacheBitmap));
            ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorView).addView(view, layoutParam);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorView).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }


    /**
     * 获取一个 View 的缓存视图
     *
     * @param view 一个View试图
     * @return bitmap
     */

    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    /*-keep class com.bumptech.glide.** {*;}

    for glide 4.xx.xx
     */
    public void loadOneTimeGif(@NonNull Object gifResource, final ImageView gifImageView) {
        loadOneTimeGif(gifResource, gifImageView, null);
    }

    public void loadOneTimeGifWithRes(@DrawableRes int gifResource, final ImageView gifImageView) {
        loadOneTimeGif(gifResource, gifImageView, null);
    }

    public void loadOneTimeGifWithRes(@DrawableRes int gifResource, final ImageView gifImageView, final OnGifListener gifListener) {
        Glide.with(gifImageView.getContext()).asGif().load(gifResource).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    Field gifStateField = GifDrawable.class.getDeclaredField("state");
                    gifStateField.setAccessible(true);
                    Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                    Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                    gifFrameLoaderField.setAccessible(true);
                    Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                    Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                    gifDecoderField.setAccessible(true);
                    Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                    Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
                    Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                    getDelayMethod.setAccessible(true);
//设置只播放一次
                    resource.setLoopCount(1);
//获得总帧数
                    int count = resource.getFrameCount();
                    int delay = 0;
                    for (int i = 0; i < count; i++) {
//计算每一帧所需要的时间进行累加
                        delay += (int) getDelayMethod.invoke(gifDecoder, i);
                    }
                    gifImageView.postDelayed(() -> gifCallback(gifListener), delay);
                } catch (NoSuchFieldException e) {
                    LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                } catch (ClassNotFoundException e) {
                    LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                } catch (IllegalAccessException e) {
                    LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                } catch (NoSuchMethodException e) {
                    LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                } catch (InvocationTargetException e) {
                    LaLog.e(ValueOf.logLivery(AnConstants.VALUE.LOG_LIVERY_EXCEPTION, e.getClass().toString(), e.getMessage()));
                }
                return false;
            }
        }).into(gifImageView);
    }


    public void loadOneTimeGif(@NonNull Object resouceGifMode, final ImageView gifImageView, final OnGifListener gifListener) {
        Glide.with(gifImageView.getContext()).asGif().load(resouceGifMode).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                try {
                    Field gifStateField = GifDrawable.class.getDeclaredField("state");
                    gifStateField.setAccessible(true);
                    Class gifStateClass = Class.forName("com.bumptech.glide.load.resource.gif.GifDrawable$GifState");
                    Field gifFrameLoaderField = gifStateClass.getDeclaredField("frameLoader");
                    gifFrameLoaderField.setAccessible(true);
                    Class gifFrameLoaderClass = Class.forName("com.bumptech.glide.load.resource.gif.GifFrameLoader");
                    Field gifDecoderField = gifFrameLoaderClass.getDeclaredField("gifDecoder");
                    gifDecoderField.setAccessible(true);
                    Class gifDecoderClass = Class.forName("com.bumptech.glide.gifdecoder.GifDecoder");
                    Object gifDecoder = gifDecoderField.get(gifFrameLoaderField.get(gifStateField.get(resource)));
                    Method getDelayMethod = gifDecoderClass.getDeclaredMethod("getDelay", int.class);
                    getDelayMethod.setAccessible(true);
//设置只播放一次
                    resource.setLoopCount(1);
//获得总帧数
                    int count = resource.getFrameCount();
                    int delay = 0;
                    for (int i = 0; i < count; i++) {
//计算每一帧所需要的时间进行累加
                        delay += (int) getDelayMethod.invoke(gifDecoder, i);
                    }
                    gifImageView.postDelayed(() -> gifCallback(gifListener), delay);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).into(gifImageView);
    }

    private void gifCallback(OnGifListener gifListener) {
        if (gifListener != null) {
            gifListener.gifPlayComplete();
        }
    }

    /*其中缓存策略可以为:Source及None,None及为不缓存,Source缓存原型.如果为ALL和Result就不行
DiskCacheStrategy.NONE 什么都不缓存
DiskCacheStrategy.SOURCE 仅仅只缓存原来的全分辨率的图像。
DiskCacheStrategy.RESULT 仅仅缓存最终的图像，即，降低分辨率后的（或者是转换后的）
DiskCacheStrategy.ALL 缓存所有版本的图像（默认行为）*/
    public void fastLoadGif(@NonNull Context gifContext, @NonNull Object resouceGifMode, @NonNull ImageView gifImageView) {
        Glide.with(gifContext).asGif().load(resouceGifMode).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(gifImageView);
    }

    public void fastLoadGif(@NonNull Context gifContext, @DrawableRes int resouceId, @NonNull ImageView gifImageView) {
        Glide.with(gifContext).asGif().load(resouceId).diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(gifImageView);
    }

    public void saveFastLoadGif(@DrawableRes int resouceId, @NonNull final ImageView gifImageView) {
        Glide.with(gifImageView.getContext()).load(resouceId).diskCacheStrategy(DiskCacheStrategy.RESOURCE).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadOneTimeGif(resource, gifImageView);
                return false;

            }
        }).into(gifImageView);
    }

    public void stopLoadGif(@NonNull ImageView gifImageView) {
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable().getCurrent();
        if (gifDrawable != null) {
            if (gifDrawable.isRunning()) {
                gifDrawable.stop();
            } else {
                gifDrawable.start();
            }
        }
    }

    /**
     * 防止内存抖动 glide default memory cache values is false, so ...
     */
    public void flushLoadGif(@NonNull Object resouceGifMode, ImageView gifImageView) {
        Transformation<Bitmap> circle = new CircleCrop();
        Glide.with(gifImageView.getContext()).load(resouceGifMode).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                loadOneTimeGif(resource, gifImageView);
                return false;
            }
        }).optionalTransform(circle).skipMemoryCache(true).into(gifImageView);
    }

    /**
     * 设置tablayout的indictor防止失效
     */
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        try {
            Field tabStrip = tabs.getClass().getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout llTab = null;
            llTab = (LinearLayout) tabStrip.get(tabs);
            int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
            int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

            for (int i = 0; i < llTab.getChildCount(); i++) {
                View child = llTab.getChildAt(i);
                child.setPadding(0, 0, 0, 0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                params.leftMargin = left;
                params.rightMargin = right;
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /*播放一个渐变，渐显的动画*/
    public enum AnimationState {
        STATE_SHOW,
        STATE_HIDDEN
    }

    /**
     * 渐隐渐现动画
     *
     * @param view     需要实现动画的对象
     * @param state    需要实现的状态
     * @param duration 动画实现的时长（ms）
     */
    public void showHideAnimation(final View view, AnimationState state, long duration) {
        float start = 0f;
        float end = 0f;
        if (state == AnimationState.STATE_SHOW) {
            end = 1f;
            view.setVisibility(View.VISIBLE);
        } else if (state == AnimationState.STATE_HIDDEN) {
            start = 1f;
            view.setVisibility(View.INVISIBLE);
        }
        AlphaAnimation animation = new AlphaAnimation(start, end);
        animation.setDuration(duration);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
        });
        view.setAnimation(animation);
        animation.start();
    }

    public void toolBarLayoutAnimation(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            ObjectAnimator fingerUpAnim = ObjectAnimator.ofFloat(view, "translationY", 0, view.getHeight());
            fingerUpAnim.setDuration(500);
            fingerUpAnim.start();
            fingerUpAnim.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                if (value == view.getHeight()) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
        } else {//不存在则显示
            view.setVisibility(View.VISIBLE);
            ObjectAnimator fingerUpAnim = ObjectAnimator.ofFloat(view, "translationX", view.getHeight(), 0);
            fingerUpAnim.setDuration(500);
            fingerUpAnim.start();
        }
    }


    /**
     * (3)原ViewUtils
     */

    public View getRootView(Activity activity) {
        View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
//        View rootView = getRootView();
//        rootView.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View inflateView = inflater.inflate(R.layout.layout_error, null);
        ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

//        setContentView(inflateView, layoutParams);
//        constainitLayout.addView(inflateView, layoutParams);
        return rootView;
    }

    private View getRootView2(Activity context) {
        return context.getWindow().getDecorView().findViewById(android.R.id.content);
    }

    public ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    public View getInflaterView(@NonNull Context activity, @LayoutRes int resource, @Nullable ViewGroup parent, boolean attachToRoot) {
        return getLayoutInflater(activity).inflate(resource, parent, attachToRoot);
    }

    public View getInflaterView(@NonNull Context activity, @LayoutRes int resource, @Nullable ViewGroup parent) {
        return getLayoutInflater(activity).inflate(resource, parent, false);
    }

    public View getInflaterView(@NonNull Context activity, @LayoutRes int resource) {
        return getLayoutInflater(activity).inflate(resource, null);
    }

    /**
     * 注意：其它需要自定义view会用到的转换，如果是activity父类有这个方法
     */
    public LayoutInflater getLayoutInflater(@NonNull Context activity) {
        return LayoutInflater.from(activity);
    }
}