package com.sunsta.bear.faster;


import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>Detects Keyboard Status changes and fires events only once for each change.</p>
 */
public class KeyboardStatusDetector {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 100;
    private OnKeyBordStateListener onKeyBordStateListener;
    private boolean keyboardVisible = false;

    public KeyboardStatusDetector register(@NonNull Activity activity) {
        register(ViewUtils.getInstance().getRootView(activity));
        return this;
    }

    //记录原始窗口高度
    private int mWindowHeight = 0;

    public KeyboardStatusDetector register(@Nullable final View detectorView) {
        if (detectorView == null) {
            return this;
        }
        ViewTreeObserver viewTreeObserver = detectorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                detectorView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = detectorView.getRootView().getHeight();
                int patchBottomTop = (rect.bottom + rect.top);
                heightDiff = heightDiff - patchBottomTop;
                if (mWindowHeight == 0) {
                    //一般情况下，这是原始的窗口高度
                    mWindowHeight = rect.height();
                }
                //两次窗口高度相减，就是软键盘高度
                int softKeyboardHeight = mWindowHeight - rect.height();
                if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                    if (!keyboardVisible) {
                        keyboardVisible = true;
                        if (onKeyBordStateListener != null) {
                            onKeyBordStateListener.keyboardVisible(true, softKeyboardHeight);
                            return;
                        }
                    }
                } else {
                    if (keyboardVisible) {
                        keyboardVisible = false;
                        if (onKeyBordStateListener != null) {
                            onKeyBordStateListener.keyboardVisible(false, softKeyboardHeight);
                        }
                    }
                }
//                ViewTreeObserver viewTreeObserver = detectorView.getViewTreeObserver();
//                viewTreeObserver.removeOnGlobalLayoutListener(this);
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });

        return this;
    }

    public KeyboardStatusDetector setKeyBordStateListener(OnKeyBordStateListener listener) {
        onKeyBordStateListener = listener;
        return this;
    }

    public interface OnKeyBordStateListener {
        void keyboardVisible(boolean visible, int keybordHeight);
    }
}