package com.sunsta.bear.faster.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sunsta.bear.faster.LaLog;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LaBluetoothJs {
    public static final String bleobject = "hilink";//与js端交互的统一对象


    /*
     * Created by qy on 2018/3/22.
     * js方法统一
     * <p>
     * <button class='btnclass' type='submit' value='显示一个土司' onclick='bleobject.bluetoothAvailable()'>检查蓝牙是否可用</button>
     * <button class='btnclass' type='submit' value='显示一个土司' onclick='bleobject.openBluetoothAdapter()'>打开蓝牙</button>
     * <button class='btnclass' type='submit' value='显示一个土司' onclick='bleobject.closeBluetoothAdapter()'>关闭蓝牙</button>
     * <button class='btnclass' type='submit' value='显示一个土司' onclick='bleobject.sendData('android收到html中数据，交给你处理')'>传递数据</button>
     * version:2.0
     */

    private static LaBluetoothService laBluetooth;

    private static Context mContext;
    private static WebView mWebView;

    private static boolean addedJavascriptInterface = false;

    /*tips:防止内存泄露。
    初始化当前需求所有需要注入的js*/
    public synchronized static void initLaJsBlluetooth(@NonNull Context context, @NonNull WebView webView) {
        LaLog.d("--qydq--页面加载完成--初始化");
        mContext = context;
        mWebView = webView;
        laBluetooth = LaBluetoothService.getInstance(mContext.getApplicationContext());
        addJavascriptInterface(webView);
    }

    private static void addJavascriptInterface(@NonNull WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        if (!addedJavascriptInterface) {
            // Add javascript interface to be called when the video ends (must be done before page load)
            //noinspection all
            webView.addJavascriptInterface(new JavascriptInterface(), bleobject);// Must match Javascript interface name of VideoEnabledWebChromeClient

            addedJavascriptInterface = true;
        }
    }


    public static class JavascriptInterface {
        @android.webkit.JavascriptInterface
        public void bluetoothAvailable() {
            Log.d("___", "GOT IT-bluetoothAvailable");
            // This code is not executed in the UI thread, so we must force that to happen
            if (laBluetooth.bluetoothAvailable()) {
                Toast.makeText(mContext, "蓝牙可用", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "蓝牙不可用", Toast.LENGTH_SHORT).show();
            }
        }

        @android.webkit.JavascriptInterface
        public void openBluetoothAdapter() {
            Log.d("___", "GOT IT-openBluetoothAdapter");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.openBluetoothAdapter();
        }

        @android.webkit.JavascriptInterface
        public void closeBluetoothAdapter() {
            Log.d("___", "GOT IT-closeBluetoothAdapter");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.closeBluetoothAdapter();
        }

        @android.webkit.JavascriptInterface
        public int getBluetoothAdapterState() {
            Log.d("___", "GOT IT-getBluetoothAdapterState");
            return laBluetooth.getBluetoothAdapterState();
        }

        @android.webkit.JavascriptInterface
        public void startBluetoothDevicesDiscovery() {
            Log.d("___", "GOT IT-startBluetoothDevicesDiscovery");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.startBluetoothDevicesDiscovery();
        }

        @android.webkit.JavascriptInterface
        public void stopBluetoothDevicesDiscovery() {
            Log.d("___", "GOT IT-stopBluetoothDevicesDiscovery");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.stopBluetoothDevicesDiscovery();
        }

        @android.webkit.JavascriptInterface
        public String getBundleDevice() {
            Log.d("___", "GOT IT--getBundleDevice");
            // This code is not executed in the UI thread, so we must force that to happen

            Set<BluetoothDevice> sets = laBluetooth.getBundleDevice();
            StringBuilder deviceBuilder = new StringBuilder();


            if (sets == null || sets.size() == 0) {
                Toast.makeText(mContext, "没有绑定的蓝牙99", Toast.LENGTH_SHORT).show();
                deviceBuilder.append("没有绑定的设备");
//                showInfoFromeAndroid("xx");
            } else {
                Toast.makeText(mContext, "存在绑定的蓝牙888" + sets.toString(), Toast.LENGTH_SHORT).show();

                for (BluetoothDevice bluetoothDevice : sets) {
                    if (sets.size() == 1) {
                        deviceBuilder.append(bluetoothDevice.getAddress());
                    } else {
                        deviceBuilder.append(bluetoothDevice.getAddress() + "#");
                    }
                }

//                showInfoFromeAndroid("xx");
            }

            return deviceBuilder.toString();
//            ActivityTaskManager.getInstance().getActivityInstance("com.aili.view.activity.test.fa.TestActivity");

//            mWebView.loadUrl("javascript:getBundleDevice('" + device + "')");
//
//
//            mWebView.evaluateJavascript("javascript:getBundleDevice('" + device + "')", new ValueCallback<String>() {
//                @Override
//                public void onReceiveValue(String value) {
//                    Toast.makeText(mContext, "evaluateJavascript-" + value, Toast.LENGTH_SHORT).show();
//                }
//            });
        }

        @android.webkit.JavascriptInterface
        public String getScanResult() {
            Log.d("___", "GOT IT--getScanResult");
            // This code is not executed in the UI thread, so we must force that to happen

            List<BluetoothDevice> sets = laBluetooth.getScanResult();
            StringBuilder deviceBuilder = new StringBuilder();
            if (sets == null || sets.size() == 0) {
                Toast.makeText(mContext, "result=null", Toast.LENGTH_SHORT).show();
                deviceBuilder.append("result=null");
            } else {

                for (BluetoothDevice bluetoothDevice : sets) {
                    Toast.makeText(mContext, "扫描结果为：" + bluetoothDevice.getAddress() + "", Toast.LENGTH_SHORT).show();
                    if (sets.size() == 1) {
                        deviceBuilder.append(bluetoothDevice.getAddress());
                    } else {
                        deviceBuilder.append(bluetoothDevice.getAddress() + "#");
                    }
                }
            }
            return deviceBuilder.toString();
        }

        @android.webkit.JavascriptInterface
        public int createBond(String address, boolean autoConnect) {
            Log.d("___", "GOT IT-createBond");
            // This code is not executed in the UI thread, so we must force that to happen
            return laBluetooth.createBond(laBluetooth.getDeviceFromAddress(address), autoConnect);
        }

        @android.webkit.JavascriptInterface
        public void createBLEConnection(String address, boolean autoConnect) {
            Log.d("___", "GOT IT-createBLEConnection");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.createBLEConnection(laBluetooth.getDeviceFromAddress(address), autoConnect);
        }

        @android.webkit.JavascriptInterface
        public void closeBLEConnection() {
            Log.d("___", "GOT IT-createBLEConnection");
            // This code is not executed in the UI thread, so we must force that to happen
            laBluetooth.closeBLEConnection();
        }

        @android.webkit.JavascriptInterface
        public void sendH5DataToAndroid(String data) {
            Log.d("___", "GOT IT");
            // This code is not executed in the UI thread, so we must force that to happen
            Toast.makeText(mContext, "收到传来的数据Sring，data=" + data, Toast.LENGTH_SHORT).show();
        }

        @android.webkit.JavascriptInterface
        public void sendData(int data) {
            Log.d("___", "GOT IT");
            // This code is not executed in the UI thread, so we must force that to happen
            Toast.makeText(mContext, "收到传来的数据int，data=" + data, Toast.LENGTH_SHORT).show();
        }

        @android.webkit.JavascriptInterface
        public void checkstate() {
            if (laBluetooth.getBluetoothAdapterState() == -1) {
                Toast.makeText(mContext, "蓝牙不可用或不支持蓝牙", Toast.LENGTH_SHORT).show();
            } else if (laBluetooth.getBluetoothAdapterState() == 1) {
                Toast.makeText(mContext, "蓝牙已经打开", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
            }
        }


        /*获取edit中的数据*/
        @android.webkit.JavascriptInterface
        public void sendDataFromEdit(String data) {
            Log.d("___", "GOT IT");
            // This code is not executed in the UI thread, so we must force that to happen
            Toast.makeText(mContext, "收到传sendDataFromEditSring，data=" + data, Toast.LENGTH_SHORT).show();
        }


        //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        @android.webkit.JavascriptInterface
        public void showInfoFromJs(String name) {
            Toast.makeText(mContext, name, Toast.LENGTH_SHORT).show();
        }

        @android.webkit.JavascriptInterface
        public void createBLEConnection(String address) {
            Toast.makeText(mContext, "开始连接=" + address, Toast.LENGTH_SHORT).show();
            laBluetooth.createBLEConnection(laBluetooth.getDeviceFromAddress(address + ""), true, new BluetoothGattCallback() {
                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);

//                    gatt.getDevice();
                    if (gatt.getDevice().getType() == BluetoothDevice.DEVICE_TYPE_LE) {

                    }
//                    这个服务中其他特性
                    gatt.connect();
                    gatt.disconnect();
//                    gatt.close();
                    gatt.readCharacteristic(characteristic);//读，反之写
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    BluetoothGattCharacteristic characteristic = gatt.getService(UUID.randomUUID()).getCharacteristic(UUID.randomUUID());
                    //发现服务是可以在这里查找支持的所有服务
                    BluetoothGattService service = gatt.getService(UUID.randomUUID());
                    /*du数据*/
                    gatt.readCharacteristic(characteristic);
                    laBluetooth.setCharacteristicNotification(characteristic, true);
                    /*Toast.makeText(this，“读成功”，Toast.LENGTH_SHORT）.show（）;*/
                    if (gatt.setCharacteristicNotification(characteristic, true)) {
//                        打开/关闭character的notify，如下
//                        BluetoothGattDescriptor descriptor = characteristic
//                                .getDescriptor(UUID.fromString(BluetoothConstants.CLIENT_CHARACTERISTIC_CONFIG));
//
//                        if (descriptor != null) {
//                            descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
//                                    : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
//                        }
//                        result = gatt.writeDescriptor(descriptor);
                    }

                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                }
            });
            Toast.makeText(mContext, "连接失败=" + address, Toast.LENGTH_SHORT).show();
        }

        //在js中调用window.AndroidWebView.showInfoFromJs(name)，便会触发此方法。
        @android.webkit.JavascriptInterface
        public boolean setCharacteristicNotification(String uuidString, String charateristic, boolean opened) {
            return laBluetooth.setCharacteristicNotification(uuidString, charateristic, opened);
        }

        @android.webkit.JavascriptInterface
        public byte[] readBLECharacteristicValues(String uuidString, String charateristic, boolean opened) {
            return laBluetooth.getBLECharacteristicValues(laBluetooth.getSupportedGattCharacteristic(uuidString, charateristic));
        }

        @android.webkit.JavascriptInterface
        public boolean writeBLECharacteristic(String uuidString, String charateristic, boolean opened) {
            return laBluetooth.writeBLECharacteristic(laBluetooth.getSupportedGattCharacteristic(uuidString, charateristic));
        }
    }

    //在java中调用js代码
    public static void sendInfoToJs(String msg) {
        //调用js中的函数：showInfoFromJava(msg)
        mWebView.loadUrl("javascript:showInfoFromJava('" + msg + "')");
    }

    //在java中调用js代码
    public static void showInfoFromeAndroid(String msg) {
        //调用js中的函数：showInfoFromJava(msg)
        Set<BluetoothDevice> sets = laBluetooth.getBundleDevice();
        String device = "";


        if (sets == null || sets.size() == 0) {
            Toast.makeText(mContext, "没有绑定的蓝牙99", Toast.LENGTH_SHORT).show();
            device = "没有绑定的设备";
        } else {
            Toast.makeText(mContext, "存在绑定的蓝牙888" + sets.toString(), Toast.LENGTH_SHORT).show();
            device = sets.toString();

        }
        mWebView.loadUrl("javascript:showInfoFromeAndroid('" + device + "')");
    }

}