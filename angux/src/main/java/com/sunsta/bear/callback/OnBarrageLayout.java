package com.sunsta.bear.callback;

import android.view.View;

import com.sunsta.bear.entity.Barrage;

/**
 * 遵循：livery 标准OnItemXXXClickListener
 */
public interface OnBarrageLayout {
    View barrageLayout(View layoutView, Barrage barrage);
}