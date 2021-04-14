package com.sunsta.bear.model.entity;

/**
 * Created by sunsta 2016.09.19
 * 夜间模式枚举定义参考
 * https://zhihu.com/people/qydq
 */
public enum ResponseDayNightMode {

    DAY("DAY", 1),
    NIGHT("NIGHT", 2),
    SYSTEM("SYSTEM", 3);

    private String name;
    private int code;

    ResponseDayNightMode(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
