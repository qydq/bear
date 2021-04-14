package com.sunsta.bear.engine.picker.listener;

/**
 * brief:城市选择回调
 * <br> author：晴雨【qy】
 * <br> email：staryumou@163.com
 * <br> website：https://qydq.github.io
 * <br> Copyrigth(c),2017/1/10,孙顺涛,inasst.com
 * <br> version 2.0
 */

public interface OnCitySelectListener {
    void onCitySelect(String str);
    void onCitySelect(String prov, String city, String area);
}
