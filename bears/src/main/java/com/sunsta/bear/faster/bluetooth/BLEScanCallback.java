package com.sunsta.bear.faster.bluetooth;

import android.bluetooth.le.ScanResult;

import java.util.List;

/**
 * Created by qy on 2018/3/21.
 * 扫描后结果处理,ok
 */

public interface BLEScanCallback {
    void onScanResult(int callbackType, ScanResult result);

    void onBatchScanResults(List<ScanResult> results);

    void onScanFailed(int errorCode);
}
