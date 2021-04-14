package com.sunsta.bear.model.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.engine.GlideEngine;
import com.sunsta.bear.immersion.RichTextView;

import static android.view.View.GONE;


public class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ErrorViewHolder> {
    private RecyclerView.Adapter mAdapter;
    private String txtLight;
    private String txtPrimary;//once more use
    private String imageUrl;
    private Context context;
    private int imageResouce;
    private int textPrimaryColor;
    private int textLightColor;


    public ErrorAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTxtLight(String txtLight) {
        this.txtLight = txtLight;
    }

    public void setTxtPrimary(String txtPrimary) {
        this.txtPrimary = txtPrimary;
    }

    public void setImageResouce(@DrawableRes int imageResouce) {
        this.imageResouce = imageResouce;
    }

    public void setTextPrimaryColor(int textPrimaryColor) {
        this.textPrimaryColor = textPrimaryColor;
    }

    public void setTextLightColor(int textLightColor) {
        this.textLightColor = textLightColor;
    }

    @NonNull
    @Override
    public ErrorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new ErrorViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_error, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ErrorViewHolder holder, int position) {
        if (!TextUtils.isEmpty(txtPrimary)) {
            RichTextView.setRichText(holder.tvText, txtPrimary);
        } else {
            holder.tvText.setVisibility(GONE);
        }
        if (imageResouce > 0) {
            holder.ivError.setImageResource(imageResouce);
        }
        if (!TextUtils.isEmpty(imageUrl) && null != context) {
            GlideEngine.getInstance().loadImage(imageUrl, holder.ivError);
        }
        if (!TextUtils.isEmpty(txtLight)) {
            holder.tvLight.setText(txtLight);
        }
        if (null != onClickListener) {
            holder.itemView.setOnClickListener(onClickListener);
            holder.ivError.setOnClickListener(onClickListener);
            holder.tvLight.setOnClickListener(onClickListener);
        }
        if (null != mIvListener) {
            holder.ivError.setOnClickListener(view -> mIvListener.onClick());
        }
        if (null != mOnTextClickListener) {
            holder.tvLight.setOnClickListener(view -> mOnTextClickListener.onClick());
        }
        if (textLightColor > 0) {
            RichTextView.setTextColor(holder.tvLight, textLightColor);
        }
        if (textPrimaryColor > 0) {
            RichTextView.setTextColor(holder.tvText, textPrimaryColor);
        }
    }

    private OnIvClickListener mIvListener;

    public interface OnIvClickListener {
        void onClick();
    }

    public void setOnIvClickListener(OnIvClickListener mListener) {
        this.mIvListener = mListener;
    }

    private OnTextClickListener mOnTextClickListener;

    public interface OnTextClickListener {
        void onClick();
    }

    public void setOnTxtClickListener(OnTextClickListener mListener) {
        this.mOnTextClickListener = mListener;
    }

    private View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                return mAdapter.getItemCount();
            }
        }
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mAdapter != null) {
            if (mAdapter.getItemCount() > 0) {
                return 1;
            }
        }
        return AnConstants.ERROR_VIEW_TYPE;
    }

    static class ErrorViewHolder extends RecyclerView.ViewHolder {
        TextView tvLight;
        TextView tvText;
        ImageView ivError;
        View itemView;

        ErrorViewHolder(View view) {
            super(view);
            itemView = view;
            tvText = view.findViewById(R.id.tvPrimary);
            tvLight = view.findViewById(R.id.tvLight);
            ivError = view.findViewById(R.id.ivError);
        }
    }
}