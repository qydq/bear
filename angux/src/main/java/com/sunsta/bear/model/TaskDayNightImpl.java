package com.sunsta.bear.model;

import android.content.SharedPreferences;

import com.sunsta.bear.model.entity.ResponseDayNightMode;
import com.sunsta.bear.faster.DayNightHelper;

/**
 * Created by qydda on 2016/12/7.
 */

public class TaskDayNightImpl implements TaskDayNight {
    private DayNightHelper helper = null;

    public TaskDayNightImpl(SharedPreferences sp) {
        helper = new DayNightHelper(sp);
    }

    @Override
    public boolean setDayModel() {
        return helper.setMode(ResponseDayNightMode.DAY);
    }

    @Override
    public boolean setNightMode() {
        return helper.setMode(ResponseDayNightMode.NIGHT);
    }

    @Override
    public boolean isDay() {
        return helper.isDay();
    }

    @Override
    public boolean isNight() {
        return helper.isNight();
    }
}
