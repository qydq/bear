package com.sunsta.bear.engine.picker.widget.wheelview;

final class OnItemSelectedRunnable implements Runnable {
    final INAWheelView loopView;

    OnItemSelectedRunnable(INAWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
    }
}
