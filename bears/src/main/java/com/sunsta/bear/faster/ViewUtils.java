/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunsta.bear.faster;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunsta.bear.layout.tablayout.ValueAnimatorCompat;
import com.sunsta.bear.layout.tablayout.ValueAnimatorCompatImpl;

public class ViewUtils {
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

    public static ValueAnimatorCompat createAnimator() {
        return DEFAULT_ANIMATOR_CREATOR.createAnimator();
    }

    public static View getInflaterView(@NonNull Context activity, @LayoutRes int resource, @Nullable ViewGroup parent, boolean attachToRoot) {
        return getLayoutInflater(activity).inflate(resource, parent, attachToRoot);
    }

    public static View getInflaterView(@NonNull Context activity, @LayoutRes int resource, @Nullable ViewGroup parent) {
        return getLayoutInflater(activity).inflate(resource, parent, false);
    }

    public static View getInflaterView(@NonNull Context activity, @LayoutRes int resource) {
        return getLayoutInflater(activity).inflate(resource, null);
    }

    /**
     * 注意：其它需要自定义view会用到的转换，如果是activity父类有这个方法
     */
    public static LayoutInflater getLayoutInflater(@NonNull Context activity) {
        return LayoutInflater.from(activity);
    }
}