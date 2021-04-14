package com.sunsta.bear.listener;

/**
 * Interface definition for a callback to be invoked when the photo is experiencing a drag event
 */
public interface OnDragLayoutListener {

    /**
     * Callback for when the photo is experiencing a drag event. This cannot be invoked when the
     * user is scaling.
     *
     * @param drag The change when drag view ,defalut drag should be set true
     * @param dx   The change of the coordinates in the x-direction
     * @param dy   The change of the coordinates in the y-direction
     */
    void onDrag(boolean drag, float dx, float dy);
}
