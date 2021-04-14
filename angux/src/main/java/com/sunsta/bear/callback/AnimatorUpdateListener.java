package com.sunsta.bear.callback;

import com.sunsta.bear.layout.tablayout.ValueAnimatorCompat;

public interface AnimatorUpdateListener {
    /**
     * <p>Notifies the occurrence of another frame of the animation.</p>
     *
     * @param animator The animation which was repeated.
     */
    void onAnimationUpdate(ValueAnimatorCompat animator);
}
