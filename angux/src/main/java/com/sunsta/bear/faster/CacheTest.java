package com.sunsta.bear.faster;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by sun on 2019/7/7.
 */

public class CacheTest implements Serializable {
    private static final long serialVersionUID = 1L;

    //{1,2,3,4}
    public LinkedHashSet<Integer> serviceIdSet = new LinkedHashSet<>();//传递一个serviceId则保存一个，下次判断是否支持服务就用这个持久化
    //{1=1,2=0,3=1}
    public LinkedHashMap<Integer, Integer> mapCommandResultMap = new LinkedHashMap<>();//包含command和result的对应关系//持久化这个map
    //{1={1=1,2=0,3=1},2={1=1,2=0,3=1}}
    public LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> structMap = new LinkedHashMap<>();//struct//持久化这个structMap


    public LinkedHashSet<Integer> getServiceIdSet() {
        return serviceIdSet;
    }

    public void setServiceIdSet(LinkedHashSet<Integer> serviceIdSet) {
        this.serviceIdSet = serviceIdSet;
    }

    public LinkedHashMap<Integer, Integer> getMapCommandResultMap() {
        return mapCommandResultMap;
    }

    public void setMapCommandResultMap(LinkedHashMap<Integer, Integer> mapCommandResultMap) {
        this.mapCommandResultMap = mapCommandResultMap;
    }

    @Override
    public String toString() {
        return "CacheTest{" +
                "serviceIdSet=" + serviceIdSet +
                ", mapCommandResultMap=" + mapCommandResultMap +
                ", structMap=" + structMap +
                '}';
    }

    public LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> getStructMap() {
        return structMap;
    }

    public void setStructMap(LinkedHashMap<Integer, LinkedHashMap<Integer, Integer>> structMap) {
        this.structMap = structMap;
    }

}
