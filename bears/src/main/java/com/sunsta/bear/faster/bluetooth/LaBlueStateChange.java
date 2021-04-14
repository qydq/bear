package com.sunsta.bear.faster.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by 晴雨 on 2018/3/20.
 * 蓝牙设备连接断开连接回调ok
 */

public interface LaBlueStateChange {
    void onStateConnected(BluetoothGatt gatt, int status, int newState);

    void onStateDisConnected(BluetoothGatt gatt, int status, int newState);

    void onServicesDiscovered(BluetoothGatt gatt, int status);

    void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
}
