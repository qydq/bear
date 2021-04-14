package com.sunsta.bear.engine.picker.widget.wheelview;

import android.view.MotionEvent;

final class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    final INAWheelView loopView;

    LoopViewGestureListener(INAWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
