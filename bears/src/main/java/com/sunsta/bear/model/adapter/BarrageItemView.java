package com.sunsta.bear.model.adapter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunsta.bear.R;
import com.sunsta.bear.engine.gif.GifImageView;
import com.sunsta.bear.layout.INACircleImageView;

public class BarrageItemView {
    private View converView;
    private TextView tvName, tvContent;
    private ImageView ivPrimary, ivLight, ivMark;
    private FrameLayout frameLayout;
    private RelativeLayout action0;
    private LinearLayout llMark;
    private GifImageView ivGif, ivGif2, ivGif3;
    private INACircleImageView ivCircle;

    public BarrageItemView(View converView) {
        this.converView = converView;

        frameLayout = converView.findViewById(R.id.frameLayout);
        action0 = converView.findViewById(R.id.action0);
        llMark = converView.findViewById(R.id.llMark);
        tvName = converView.findViewById(R.id.tvName);
        tvContent = converView.findViewById(R.id.tvContent);
        ivPrimary = converView.findViewById(R.id.ivPrimary);
        ivLight = converView.findViewById(R.id.ivLight);
        ivMark = converView.findViewById(R.id.ivMark);
        ivGif = converView.findViewById(R.id.ivGif);
        ivGif2 = converView.findViewById(R.id.ivGif2);
        ivGif3 = converView.findViewById(R.id.ivGif3);
        ivCircle = converView.findViewById(R.id.ivCircle);
    }

    public void setConverView(View converView) {
        this.converView = converView;
    }

    public View getConverView() {
        return converView;
    }

    public TextView getTvName() {
        return tvName;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public ImageView getIvPrimary() {
        return ivPrimary;
    }

    public ImageView getIvLight() {
        return ivLight;
    }

    public ImageView getIvMark() {
        return ivMark;
    }

    public FrameLayout getFrameLayout() {
        return frameLayout;
    }

    public RelativeLayout getAction0() {
        return action0;
    }

    public LinearLayout getLlMark() {
        return llMark;
    }

    public GifImageView getIvGif() {
        return ivGif;
    }

    public GifImageView getIvGif2() {
        return ivGif2;
    }

    public GifImageView getIvGif3() {
        return ivGif3;
    }

    public INACircleImageView getIvCircle() {
        return ivCircle;
    }
}