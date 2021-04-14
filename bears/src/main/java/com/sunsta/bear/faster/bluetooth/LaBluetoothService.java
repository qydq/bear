package com.sunsta.bear.faster.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.sunsta.bear.faster.LaLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 晴雨qy on 2018/3/20.
 * 说明1：ble-v4.0以上为ble蓝牙，v3.0之前都是传统蓝牙。
 * 说明2：Android 版本与 Bluetooth 版本之间是否存在必然关系？答案是否定的。
 * 不同版本的 Android 系统是否有针对不同版本的 Bluetooth 模块提供官方的 API 供开发者调用，这个才是关键。
 * <p>
 * tips: 搜索设备时，蓝牙未打开，则应该先打开蓝牙在搜索
 * 蓝牙配对连接过程中，若蓝牙在搜索，则应该主动关闭蓝牙搜索。
 * version:2.0
 */

public class LaBluetoothService {

    private static String TAG = "LaBluetoothService";

    /*一些常量值，暂时放在LaBluetooth中*/
    public static final int NOTAVAILABLE = 10001;


    private BluetoothLeScanner mBluetoothLeScanner;//蓝牙搜索

    private BluetoothAdapter mBluetoothAdapter = null;//蓝牙适配器

    private BluetoothManager mBluetoothManager;//蓝牙管理4.3类

    private BluetoothGatt mGatt;//蓝牙profile

    private List<BluetoothDevice> scanResult;//扫描到设备的集合

    private BluetoothDevice scanBluetoothDevice;//扫描过程中发现的蓝牙设备

    private BluetoothDevice filterBluetoothDevice;//设备过滤，应该操作的设备

    private String mFilterCondition;//蓝牙设备过滤的AddressfilterCondition

    private static Handler mHandler;

    private int SCAN_PERIOD = 6000;

    private static Context mContext;

    private boolean mAutoConnect = false;//当AddressFilter时，扫描到设备是否主动连接设备

    private static LaBluetoothService itakenstance;

    private BluetoothGattServer mGattServer;

    public static int REQUEST_CODE_BLUETOOTH_ENABLE = 1;

//  开始监听FFE1特征值得通知以下为默认特征的对应uuid
//  const UUID_IBT_SERVICES = "0000FFE0-0000-1000-8000-00805F9B34FB"
//  const UUID_IBT_READ = "0000FFE1-0000-1000-8000-00805F9B34FB"
//  const UUID_IBT_WRITE = "0000FFE3-0000-1000-8000-00805F9B34FB"

    private UUID uuid_ibt_services = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
    private UUID uuid_ibt_read = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
    private UUID uuid_ibt_write = UUID.fromString("0000FFE3-0000-1000-8000-00805F9B34FB");

    private UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private StringBuilder builder = new StringBuilder();//扫描蓝牙设备结果封装
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private static class SingletonHolder {
        private static LaBluetoothService INSTANCE = new LaBluetoothService(mContext.getApplicationContext());
    }

    public synchronized static LaBluetoothService getInstance(@NonNull Context context) {
        mContext = context;
        return SingletonHolder.INSTANCE;
    }

    public synchronized static LaBluetoothService getInstance(@NonNull Context context, Handler handler) {
        mContext = context;
        itakenstance = new LaBluetoothService(context.getApplicationContext(), handler);
        return itakenstance;
    }

    private LaBluetoothService(Context context) {
        this(context, null);
    }

    private LaBluetoothService(Context context, Handler handler) {
        mHandler = handler;
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }


//    ------------------分割线qy--------------

    /**
     * 判断蓝牙是否可用，
     * 返回true设备支持蓝牙，false不支持蓝牙或设备不可用。
     */

    public interface BluetoothAdapterCallBack {
        void returnBluetoothAdapter(BluetoothAdapter bluetoothAdapter);
    }

    private BluetoothAdapterCallBack bluetoothAdapterCallBack;

    public void onBluetoothAdapter(BluetoothAdapterCallBack bluetoothAdapterCallBack) {
        this.bluetoothAdapterCallBack = bluetoothAdapterCallBack;
    }

    public boolean bluetoothAvailable() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        } else return getBluetoothAdapter() != null;
    }

    public boolean bluetoothAvailable(@NonNull BluetoothAdapterCallBack bluetoothAdapterCallBack) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        } else if (getBluetoothAdapter() != null) {
            bluetoothAdapterCallBack.returnBluetoothAdapter(getBluetoothAdapter());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否支持BLE设备。
     */
    public boolean bluetoothAvailableBLE() {
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 蓝牙可用的情况下去获取BletoothAdapter,目前demo版本大于18,项目中考虑兼容性保留
     */
    public BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter;
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                if (mBluetoothManager != null) {
                    mBluetoothAdapter = mBluetoothManager.getAdapter();
                }
            }
        }
        return mBluetoothAdapter;
    }


    /**
     * 获取蓝牙管理对象
     */
    public BluetoothManager getBluetoothManager() {
        if (!bluetoothAvailable()) {
            return null;
        }
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }
        return mBluetoothManager;
    }


    /**
     * 检查蓝牙地址是否合法
     * Alphabetic characters must be uppercase to be valid.
     */
    public boolean checkBluetoothAddress(@NonNull String address) {
        return BluetoothAdapter.checkBluetoothAddress(address);
    }

    /**
     * Returns the hardware address of the local Bluetooth adapter
     */
    @SuppressLint("HardwareIds")
    public String getHardwareAddress() {
        return getBluetoothAdapter().getAddress();
    }

    /**
     * 获取已经配对的设备
     */
    public Set<BluetoothDevice> getBundleDevice() {
        return getBluetoothAdapter().getBondedDevices();
    }

    /**
     * 得到设备连接的状态
     * 特别说明，已绑定返回12、绑定中返回11，未匹配10
     */
    public int getBundleState(@NonNull BluetoothDevice device) {
        return device.getBondState();
    }


    /**
     * 看了源码，觉得获取Uuids可以这样处理，反射
     * tip：该方法慎用。
     */

    @SuppressLint("PrivateApi")
    public void getUuids() throws InvocationTargetException, IllegalAccessException {
        Method getUuidsMethod = null;
        ParcelUuid[] uuids = null;
        try {
            getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            uuids = (ParcelUuid[]) getUuidsMethod.invoke(getBluetoothAdapter(), null);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        for (ParcelUuid uuid : uuids) {
            Log.d(TAG, "反射，UUID: " + uuid.getUuid().toString());
        }
    }


    /**
     * 获取本机蓝牙适配器状态，同IOS
     * (num=5表示蓝牙已开启，其他未开启,或蓝牙不可用)
     * 返回0，蓝牙关闭，-1表示手机不支持蓝牙,或蓝牙不可用
     */
    public int getBluetoothAdapterState() {
        if (!bluetoothAvailable()) {
            return -1;
        }
        if (getBluetoothAdapter().isEnabled()) {
            return 5;
        } else {
            return 0;
        }
    }

    /**
     * 打开蓝牙
     */
    public void openBluetoothAdapter() {
        //第一种方法打开蓝牙, 系统会提示应用要打开蓝牙，是否授权；disable不会有任何的提示。
        //boolean result = bluetoothAdapter.enable();
        //第二种方法发送广播, 会弹出一个对话框, 选择是否打开蓝牙, 选择是蓝牙才打开。
        if (getBluetoothAdapter() != null) {
            if (mBluetoothAdapter.isEnabled()) {
                Toast.makeText(mContext, "no need open bluetooth", Toast.LENGTH_SHORT).show();
            } else {
                if (mBluetoothAdapter.enable()) {
                    Toast.makeText(mContext, "bluetooth is opened", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void openBluetoothAdapter(Activity activity) {
        if (getBluetoothAdapter() != null && !getBluetoothAdapter().isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(intent, REQUEST_CODE_BLUETOOTH_ENABLE);
        }
    }

    /**
     * 关闭蓝牙，JS交互尽量少传参数
     */
    public void closeBluetoothAdapter() {
        if (getBluetoothAdapter() != null) {
            if (mBluetoothAdapter.isEnabled() && mBluetoothAdapter.disable()) {
                Toast.makeText(mContext, "bluetooth is closed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "no need close bluetooth", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 开始蓝牙扫描
     */

    /*扫面结果监听回调*/
    private BLEScanCallback BLEScanCallback;

    public void setBLEScanCallback(BLEScanCallback BLEScanCallback) {
        this.BLEScanCallback = BLEScanCallback;
    }

    public void startstartLeScan() {
        //        bluetoothAdapter.startLeScan(uuid, new BluetoothAdapter.LeScanCallback() {
//            @Override
//            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
//
//            }
//        });
    }

    public void startBluetoothDevicesDiscovery() {
        excuteDevicesDiscovery();
    }

    public void startBluetoothDevicesDiscovery(@NonNull String filterCondition) {
        if (!TextUtils.isEmpty(filterCondition)) {
            this.mFilterCondition = filterCondition;
        } else {
            this.mFilterCondition = "";
        }

        excuteDevicesDiscovery();
    }

    /*有过滤条件时是否主动连接设备，默认不主动连接*/
    public void startBluetoothDevicesDiscovery(@NonNull String filterCondition, boolean autoConnect) {
        if (!TextUtils.isEmpty(filterCondition)) {
            this.mFilterCondition = filterCondition;
            this.mAutoConnect = autoConnect;
        } else {
            this.mFilterCondition = "";
            this.mAutoConnect = false;
        }
        excuteDevicesDiscovery();
    }

    public void startBluetoothDevicesDiscovery(BLEScanCallback BLEScanCallback) {
        this.BLEScanCallback = BLEScanCallback;
        excuteDevicesDiscovery();
    }


    private void excuteDevicesDiscovery() {
        Log.d("___", "准备搜索周围的蓝牙设备");
        if (getBluetoothAdapter() != null) {
            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "正在搜索周围蓝牙设备....");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    scanResult = new ArrayList<>();
                    if (scanResult != null) {
                        scanResult.clear();
                    }
                    if (mBluetoothLeScanner != null) {
                        Log.d(TAG, "mBluetoothLeScanner!=null");
                        // Stops scanning after a pre-defined scan period.
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    stopBluetoothDevicesDiscovery();
                                }
                            }
                        }, SCAN_PERIOD);
                        mBluetoothLeScanner.startScan(mScanCallback);//新
//                        bluetoothAdapter.startDiscovery();
//                        bluetoothAdapter.startLeScan("");//或者，mScanCallback
//                        mBluetoothLeScanner.startScan(null, new ScanSettings.Builder().build(), new ScanCallback() {
//                            @Override
//                            public void onScanResult(int callbackType, ScanResult result) {
//                                super.onScanResult(callbackType, result);
//                            }
//                        });
                    } else {
                        Log.d(TAG, "mBluetoothLeScanner==null");
                    }
                }
            } else {
                Log.d("___", "蓝牙没有打开，请打开后再扫描周围蓝牙设备");
                openBluetoothAdapter();
            }
        }
    }

    /**
     * 尝试配对和连接
     *
     * @param bluetoothDevice
     */
    public int createBond(BluetoothDevice bluetoothDevice, Boolean autoConnect) {
        int bondState = scanBluetoothDevice.getBondState();
        Log.d(TAG, "搜索到设备&&绑定状态--" + bondState);

        if (bondState == BluetoothDevice.BOND_NONE) {
            //如果这个设备取消了配对，则尝试配对
            Log.d(TAG, "搜索到设备&&没有绑定--" + "去绑定");
            bluetoothDevice.createBond();
        } else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
            //如果这个设备已经配对完成，则尝试连接
            if (mAutoConnect) {
                /*利用bluetoochDevice去连接设备*/
                createBLEConnection(scanBluetoothDevice, autoConnect);
            }
        }
        return bondState;
    }


    // Device scan callback.
    private ScanCallback mScanCallback = new ScanCallback() {
        /*蓝牙的数据读取和状态改变都会回调这个函数。*/
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && result != null) {
                LaLog.d(TAG, "--mScanCallback--intoMethod:onScanResult:");
                scanBluetoothDevice = result.getDevice();
                if (scanBluetoothDevice == null) {
                    LaLog.d(TAG, "--mScanCallback--intoMethod:onScanResult:" + "没有搜索到蓝牙设备");
                    Log.d(TAG, "搜索周围设备结果：没有搜索到蓝牙设备");
//                    return;
                }


                //如果是外设则可能存在没有ServiceUuids
                ScanRecord scanRecord = result.getScanRecord();
                if (scanRecord != null) {
                    List<ParcelUuid> parcelUuidList = scanRecord.getServiceUuids();
                    if (parcelUuidList != null) {
                        byte[] bytes = scanRecord.getServiceData(parcelUuidList.get(0));
                        if (bytes != null) {
                            builder.append("/n").append(new String(bytes, Charset.forName("UTF-8")));
                        }
                    }
                }
                LaLog.d(TAG, "--mScanCallback--intoMethod:onScanResult:" + "builder=" + builder.toString());


                if (scanBluetoothDevice != null) {
                    String deviceName = scanBluetoothDevice.getName();

                    String devieAddress = scanBluetoothDevice.toString();
                    Log.d(TAG, "搜索到设备mac--" + devieAddress);
                    Log.d(TAG, "搜索到设备name--" + deviceName);

                    if (!TextUtils.isEmpty(mFilterCondition) && devieAddress.contains(mFilterCondition)) {
                        filterBluetoothDevice = scanBluetoothDevice;
                        Log.d(TAG, "搜索到设备Address-filterCondition--" + devieAddress);
//                        stopBluetoothDevicesDiscovery();
                        createBond(filterBluetoothDevice, true);
                    }

                    if (!TextUtils.isEmpty(deviceName) && deviceName.contains(mFilterCondition)) {
                        filterBluetoothDevice = scanBluetoothDevice;
                        Log.d(TAG, "搜索到设备Name-filterCondition--" + deviceName);
                        createBond(filterBluetoothDevice, false);

//                        filterBluetoothDevice.createInsecureRfcommSocketToServiceRecord()
                    }
                }

//                Log.d(TAG, "Device address: " + device.getAddress());
//                Log.d(TAG, "Device net UUIDs: " + device.getUuids());
//
//                ScanRecord record = result.getScanRecord();
//                Log.d(TAG, "Record advertise flags: 0x" + Integer.toHexString(record.getAdvertiseFlags()));
//                Log.d(TAG, "Record Tx power level: " + record.getTxPowerLevel());
//                Log.d(TAG, "Record device name: " + record.getDeviceName());
//                Log.d(TAG, "Record net UUIDs: " + record.getServiceUuids());
//                Log.d(TAG, "Record net data: " + record.getServiceData());

/*过滤条件*/
                for (BluetoothDevice bluetoothDevice : scanResult) {
                    if (bluetoothDevice.getAddress() != null) {
                        scanResult.add(scanBluetoothDevice);
                    }
                }
//                scanResult.add(scanBluetoothDevice);

                //            20180330新加代码,添加打印信息
                BluetoothDevice device = result.getDevice();
                System.out.println("--qydq--Device name: " + device.getName());
                //Returns the hardware address of this BluetoothDevice. For example, "00:11:22:AA:BB:CC".
                System.out.println("--qydq--Device address: " + device.getAddress());
                System.out.println("--qydq--Device net UUIDs: " + device.getUuids());
                //Get the Bluetooth device type of the remote device.
                System.out.println("--qydq--Device type: " + device.getType());
                //Possible values for the bond state are: BOND_NONE, BOND_BONDING, BOND_BONDED.
                System.out.println("--qydq--Device bondState: " + device.getBondState());

                ScanRecord record = result.getScanRecord();
                //Returns the advertising flags indicating the discoverable mode and capability of the device. Returns -1 if the flag field is not set.
                System.out.println("--qydq--Record advertise flags: 0x" + Integer.toHexString(record.getAdvertiseFlags()));
            /*
             txPowerLevel 发射功率等级
	         Returns the transmission power level of the packet in dBm. Returns Integer.MIN_VALUE if the field is not set. This value can be used to calculate the path loss of a received packet using the following equation:
	         pathloss = txPowerLevel - rssi
	         */
                System.out.println("--qydq--Record Tx power level: " + record.getTxPowerLevel());
                System.out.println("--qydq--Record device name: " + record.getDeviceName());
                System.out.println("--qydq--Record net UUIDs: " + record.getServiceUuids());
                //Returns a map of net UUID and its corresponding net data.
                System.out.println("--qydq--Record net data: " + record.getServiceData());
                //Returns a sparse array of manufacturer identifier and its corresponding manufacturer specific
                System.out.println("--qydq--Record manufacturer specific data: " + record.getManufacturerSpecificData());


                //RSSI 信号强度,可以用来测算距离  Returns the received signal strength in dBm. The valid range is [-127, 127].
                System.out.println("--qydq--result rssi: " + result.getRssi());
                //Returns timestamp since boot when the scan record was observed.
                System.out.println("--qydq--result timestampNanos: " + result.getTimestampNanos());

                switch (callbackType) {
                    case ScanSettings.CALLBACK_TYPE_ALL_MATCHES:
                        System.out.println("--qydq--result CALLBACK_TYPE_ALL_MATCHES ");
                        break;
                    case ScanSettings.CALLBACK_TYPE_FIRST_MATCH:
                        System.out.println("--qydq--result CALLBACK_TYPE_FIRST_MATCH ");
                        break;
                    case ScanSettings.CALLBACK_TYPE_MATCH_LOST:
                        System.out.println("--qydq--result CALLBACK_TYPE_MATCH_LOST ");
                        break;
                }


            }
            if (BLEScanCallback != null)
                BLEScanCallback.onScanResult(callbackType, result);
            super.onScanResult(callbackType, result);
        }


        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d(TAG, "onBatchScanResults");
            for (ScanResult result : results) {
//                Toast.makeText(getApplicationContext(), "result:" + result.getDevice().getAddress(), Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (scanResult != null) {
                        scanResult.add(result.getDevice());
                    } else {
                        scanResult = new ArrayList<>();
                        scanResult.add(result.getDevice());
                    }
                }
            }
            if (BLEScanCallback != null)
                BLEScanCallback.onBatchScanResults(results);
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed");
            if (scanResult != null)
                scanResult.clear();
            if (BLEScanCallback != null)
                BLEScanCallback.onScanFailed(errorCode);
        }
    };


    /**
     * 停止蓝牙扫描
     */
    public void stopBluetoothDevicesDiscovery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mBluetoothLeScanner != null) {
                if (mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null && mBluetoothAdapter.isDiscovering()) {
                    mFilterCondition = "";
                    mBluetoothLeScanner.stopScan(mScanCallback);
//                    mBluetoothAdapter.cancelDiscovery();
                }
            } else {
                if (getBluetoothAdapter() != null) {
                    mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                    if (mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null && mBluetoothAdapter.isDiscovering()) {
                        mFilterCondition = "";
                        mBluetoothLeScanner.stopScan(mScanCallback);
//                        mBluetoothAdapter.cancelDiscovery();
                    }
                }
            }
        }
    }

    /**
     * 得到当前应该操作的蓝牙设备。
     * 如果没有扫描，和过滤条件则返回空。
     */
    public BluetoothDevice getfilterBluetoothDevice() {
        return filterBluetoothDevice;
    }

    /**
     * 获取蓝牙模块生效期间所有已发现的蓝牙设备。
     */
    public List<BluetoothDevice> getScanResult() {
        return scanResult;
    }


    /**
     * 监听寻找到新设备的事件。
     */
    public void onBluetoothDeviceFound(List<Object> devices) {
    }

    /**
     * 根据address得到远程蓝牙设备
     */
    public BluetoothDevice getDeviceFromAddress(String address) {
        return getBluetoothAdapter().getRemoteDevice(address);
    }


    /*连接GATT*/
    private BluetoothGattCallback mBluetoothGattCallBack = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            Log.d(TAG, "onPhyUpdate--txPhy==" + txPhy + "rxPhy" + rxPhy + "--status" + status);
            mGatt = gatt;
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            Log.d(TAG, "onPhyRead--txPhy==" + txPhy + "rxPhy" + rxPhy + "--status" + status);
            mGatt = gatt;
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "onConnectionStateChange--status==" + status + ";newState=" + newState);
            mGatt = gatt;
            // CQ :status 表示相应的连接或断开操作是否完成，而不是指连接状态
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onConnectionStateChange--" + "连接已完成");
            } else {
                Log.d(TAG, "onConnectionStateChange--" + "已断开连接");
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                Log.d(TAG, "onConnectionStateChange--" + "蓝牙连接失败");
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "onConnectionStateChange--Connected to GATT server--");
                Log.d(TAG, "onConnectionStateChange--Attempting to start net discoverService" + gatt.discoverServices());
            }
            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mGatt = gatt;
                Log.d(TAG, "onServicesDiscovered--" + "ACTION_GATT_SERVICES_DISCOVERED");
                if (setCharacteristicNotification(uuid_ibt_services, uuid_ibt_read, true)) {
                    Log.d(TAG, "onServicesDiscovered--" + "open notification");
                } else {
                    Log.d(TAG, "onServicesDiscovered--" + "fail notification");
                }



                /*开始监听FFE1特征值得通知*/
//                        const UUID_IBT_SERVICES = "0000FFE0-0000-1000-8000-00805F9B34FB"
//                        const UUID_IBT_READ = "0000FFE1-0000-1000-8000-00805F9B34FB"
//                        const UUID_IBT_WRITE = "0000FFE3-0000-1000-8000-00805F9B34FB"

                UUID uuid_ibt_services = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB");
                UUID uuid_ibt_read = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB");
                UUID uuid_ibt_write = UUID.fromString("0000FFE3-0000-1000-8000-00805F9B34FB");

                UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");


                BluetoothGattService bluetoothGattService1 = getSupportedGattServices(uuid_ibt_services);

                if (bluetoothGattService1 != null) {
                    Log.d(TAG, "onServicesDiscovered--" + "bluetoothGattService1!=null");
                } else {
                    Log.d(TAG, "onServicesDiscovered--" + "bluetoothGattService!=null");
                }
                BluetoothGattCharacteristic readBgc = bluetoothGattService1.getCharacteristic(uuid_ibt_read);
                if (readBgc != null) {
                    Log.d(TAG, "onServicesDiscovered--" + "readBgc!=null");
                } else {
                    Log.d(TAG, "onServicesDiscovered--" + "readBgc!=null");
                }
                setCharacteristicNotification(readBgc, true);

                BluetoothGattDescriptor readBgcDes = readBgc.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
                readBgcDes.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(readBgcDes);


                        /* 1.通过onServicesDiscovered()成功回调获取的BluetoothGatt 我们可以调用gatt的getServices()方法,来获取List<BluetoothGattService>集合。*/
                List<BluetoothGattService> bluetoothGattServices = gatt.getServices();

                //发现服务是可以在这里查找支持的所有服务
//                        BluetoothGattService bluetoothGattService = gatt.getService(UUID.randomUUID());
                for (BluetoothGattService bluetoothGattService : bluetoothGattServices) {
                    UUID uuid = bluetoothGattService.getUuid();
                    Log.d(TAG, "onServicesDiscovered--uuid=" + uuid);
                            /*2.从集合中找到我们需要的service后，可以调用该service中的getCharacteristics()方法，来获取List<Characteristic> 集合。*/
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = bluetoothGattService.getCharacteristics();
                    Log.d(TAG, "onServicesDiscovered--遍历特征值=");
                            /*获取指定服务uuid的特征值*/
                    BluetoothGattCharacteristic mBluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(uuid);
//                            gatt.readCharacteristic(mBluetoothGattCharacteristic);
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristics) {
                        if (mBluetoothGattCharacteristic != null) {


                            UUID uuidx = mBluetoothGattCharacteristic.getUuid();
                            if (uuidx != null)
                                Log.d(TAG, "onServicesDiscovered--特征值 uuidx=" + uuidx);
//                                gatt.readCharacteristic(bluetoothGattCharacteristic);
//                                bluetoothGattCharacteristic.getValue();

                            Log.d(TAG, "onServicesDiscovered--指定服务uuid的特征值不为空=--");
                            final int charaProp = mBluetoothGattCharacteristic.getProperties();

//                                bluetoothGattCharacteristic.getWriteType()==BluetoothGattCharacteristic.PROPERTY_READ
                                /*如果该字符串可读*/
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                Log.d(TAG, "onServicesDiscovered--字符串可读--");
                                byte[] value = new byte[20];
                                bluetoothGattCharacteristic.setValue(value[0], BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                String writeBytes = "HYL";
                                bluetoothGattCharacteristic.setValue(writeBytes.getBytes());
                            }
                            if (gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true)) {
                                Log.d(TAG, "onServicesDiscovered--设置通知成功=--" + uuid);
                            }
                                /*3.再从指定的Characteristic中，我们可以通过getDescriptor()方法来获取该特征所包含的descriptor
                                    以上的BluetoothGattService、BluetoothGattCharacteristic、BluetoothGattDescriptor。
                                    我们都可以通过其getUuid()方法，来获取其对应的Uuid，从而判断是否是自己需要的。*/
                            List<BluetoothGattDescriptor> bluetoothGattDescriptors = bluetoothGattCharacteristic.getDescriptors();
                            Log.d(TAG, "onServicesDiscovered--遍历Descriptor=");
                            for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattDescriptors) {
                                Log.d(TAG, "onServicesDiscovered--Descriptor uuid=" + bluetoothGattDescriptor.getUuid());
//                                    bluetoothGattDescriptor.getValue();
                            }


                        }

                    }
                }

                        /*以下为监听打印的结果*/
//                        D/LaBluetooth: onConnectionStateChange--status==0--newState0
//                        D/LaBluetooth: onConnectionStateChange,status=success
//                        D/LaBluetooth: onConnectionStateChange,=STATE_DISCONNECTED
//                        D/LaBluetooth: onConnectionStateChange--status==0--newState2
//                        D/LaBluetooth: onConnectionStateChange,status=success
//                        D/LaBluetooth: onConnectionStateChange,STATE_CONNECTED,蓝牙连接成功，去discoverService=true
//                        D/LaBluetooth: onServicesDiscovered--status==0
//                        D/LaBluetooth: ServiceDiscovered--ACTION_GATT_SERVICES_DISCOVERED
//                        D/LaBluetooth: ServiceDiscovered--uuid=--00001800-0000-1000-8000-00805f9b34fb

                        /*帮助文档中的，服务UUID*/
//                        const UUID_IBT_SERVICES = "0000FFE0-0000-1000-8000-00805F9B34FB"
//                        const UUID_IBT_READ = "0000FFE1-0000-1000-8000-00805F9B34FB"
//                        const UUID_IBT_WRITE = "0000FFE3-0000-1000-8000-00805F9B34FB"
                        /*检查到的UUID*/
//                        6537B4E-E148-1E91-CD7C-F05D4985AB7F


                UUID writeUuid = UUID.fromString("6537B4E-E148-1E91-CD7C-F05D4985AB7F");
                BluetoothGattService writeBluetoothGattService = gatt.getService(writeUuid);
                if (writeBluetoothGattService == null) {
                    Log.d(TAG, "onServicesDiscovered-qy本地检测为空--");
                } else {
                    Log.d(TAG, "onServicesDiscovered-qy本地检测不为空--");
                    BluetoothGattCharacteristic writeCharacteristic = writeBluetoothGattService.getCharacteristic(writeUuid);
                    if (writeCharacteristic != null) {
                                /*写入特征值，首先我们的特征值属性，满足BluetoothGattCharacteristic.PROPERTY_WRITE或BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                                如果其property都不包含这两个，写特征值writeCharacteristic()函数直接返回false，什么都不做处理。*/
                        writeCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        writeCharacteristic.setValue("qy");
                        boolean isSuccess = gatt.writeCharacteristic(writeCharacteristic);
                        Log.d(TAG, "onServicesDiscovered-qy本地检测，写入数据是否成功--" + isSuccess);

                        if (gatt.setCharacteristicNotification(writeCharacteristic, true)) {
                            Log.d(TAG, "onServicesDiscovered-qy本地检测设置特征的通知true--");
                        } else {
                            Log.d(TAG, "onServicesDiscovered-qy本地检测设置特征的通知失败--");

                            BluetoothGattDescriptor descriptor = writeCharacteristic.getDescriptor(writeUuid);

                            if (descriptor != null) {
                                descriptor.setValue(true ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                            }
                            boolean result = gatt.writeDescriptor(descriptor);

                            Log.d(TAG, "onServicesDiscovered-qy本地检测设置特征的通知失败--result--" + result);

                        }
                                /*读数据*/
                        if (gatt.readCharacteristic(writeCharacteristic)) {
                            Log.d(TAG, "onServicesDiscovered-qy本地检测读取数据成功--data=--" + writeCharacteristic.getValue().toString());
                        }

                    } else {
                        Log.d(TAG, "onServicesDiscovered-qy本地检测readCharacteristic等于空--");
                    }
                }
            }
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicRead--status==" + status);
            mGatt = gatt;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "--onCharacteristicRead开启读写的服务--");
            } else {
                Log.d(TAG, "onCharacteristicRead--" + "读取的数据");
            }

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "onCharacteristicWrite--status==" + status);
            Log.d(TAG, "onCharacteristicWrite--values==" + characteristic.getValue().toString());

            mGatt = gatt;
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicChanged--");
            mGatt = gatt;

/*简单 解析数据*/
// For all other profiles, writes the data formatted in HEX.对于所有的文件，写入十六进制格式的文件
            //这里读取到数据
            final byte[] data = characteristic.getValue();
            for (int i = 0; i < data.length; i++) {
                Log.d(TAG, "onCharacteristicChanged--data--" + data[i]);
                System.out.println("data......" + data[i]);
            }
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    //以十六进制的形式输出
                    stringBuilder.append(String.format("%02X ", byteChar));
                // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
//                intent.putExtra(EXTRA_DATA, new String(data));
                Log.d(TAG, "onCharacteristicChanged--stringBuilder--" + stringBuilder.toString());

            }


                    /*注意：如果该特征的属性没有设置value为：descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);    则收不到订阅信息。*/
//                    characteristic.getDescriptors().get(0).setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorRead--status==" + status);
            mGatt = gatt;
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "onDescriptorWrite--status==" + status);
            mGatt = gatt;
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            mGatt = gatt;
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            mGatt = gatt;
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            mGatt = gatt;
            super.onMtuChanged(gatt, mtu, status);
        }
    };


    BluetoothDevice connectedDevice = null;//uuid已连接的设备。

    /*根据 uuid 获取处于已连接状态的设备。ok*/
    public BluetoothDevice getConnectedBluetoothDevices(UUID uuid) {
        final UUID[] myUUID = {uuid};

        mBluetoothAdapter.startLeScan(myUUID, new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                connectedDevice = device;
            }
        });
        return connectedDevice;
    }

    /*连接低功耗蓝牙设备。
    * 当连接蓝牙设备的时候，如正在搜索则先停止搜索，反正则连接。
    * */
    public void createBLEConnection(@NonNull BluetoothDevice device,
                                    @NonNull boolean autoConnect,
                                    @NonNull BluetoothGattCallback bluetoothGattCallback) {
        stopBluetoothDevicesDiscovery();
        Log.d(TAG, "开始建立蓝牙连接请求....");
        excuteBLEConnect(device, autoConnect, bluetoothGattCallback);
    }

    public void createBLEConnection(@NonNull BluetoothDevice device,
                                    @NonNull boolean autoConnect) {
        stopBluetoothDevicesDiscovery();
        Log.d(TAG, "开始建立蓝牙连接请求....");
        excuteBLEConnect(device, autoConnect, mBluetoothGattCallBack);
    }

    public void createBLEConnection(@NonNull String address,
                                    @NonNull boolean autoConnect) {
        stopBluetoothDevicesDiscovery();
        Log.d(TAG, "开始建立蓝牙连接请求....");
        excuteBLEConnect(getDeviceFromAddress(address), autoConnect, mBluetoothGattCallBack);
    }

    public void createBLEConnection(@NonNull String address,
                                    @NonNull boolean autoConnect,
                                    @NonNull BluetoothGattCallback bluetoothGattCallback) {
        stopBluetoothDevicesDiscovery();
        Log.d(TAG, "开始建立蓝牙连接请求....");
        excuteBLEConnect(getDeviceFromAddress(address), autoConnect, bluetoothGattCallback);
    }

    /**
     * 两个设备通过BLE通信，首先需要建立GATT连接。这里我们讲的是Android设备作为client端，连接GATT Server。数据发送方向总是从server推送到client
     */
    private void excuteBLEConnect(BluetoothDevice device, boolean autoConnect, BluetoothGattCallback bluetoothGattCallback) {
        if (getBluetoothAdapterState() == 5) {
            Log.d(TAG, device.getName() + "设备" + device.getAddress() + "正在连接....");
            //函数成功，返回BluetoothGatt对象，它是GATT profile的封装。通过这个对象，我们就能进行GATT Client端的相关操作。
            // BluetoothGattCallback用于传递一些连接状态及结果。
            mGatt = device.connectGatt(mContext, autoConnect, bluetoothGattCallback);


            /*以下为测试代码，晴雨*/

            //连接远程设备
//            boolean connectResult = mGatt.connect();
//            Log.d(TAG, "--测试代码connectResult=" + connectResult);
            //搜索连接设备所支持的service
//            boolean discoverResult = mGatt.discoverServices();
//            Log.d(TAG, "--测试代码discoverResult=" + discoverResult);
            //断开与远程设备的GATT连接
//            bluetoothGatt.disconnect();
            //关闭GATT Client端
//            bluetoothGatt.close();
            //读取指定的characteristic。
            //boolean readResult = bluetoothGatt.readCharacteristic(characteristic);
            //设置当指定characteristic值变化时，发出通知
            //boolean setResult = bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            //获取远程设备所支持的services
//            List<BluetoothGattService> gattServices = mGatt.getServices();
//            for (BluetoothGattService gattService : gattServices) {
//                Log.d(TAG, "--测试代码gattService=" + gattService.toString());
//            }

        } else if (getBluetoothAdapterState() == 0) {
            Log.d(TAG, "please open bluetooth请先打开蓝牙在进行操作");
        } else {
            Log.d(TAG, "not support bluetooth");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void openGattServer() {
        mGattServer = getBluetoothManager().openGattServer(mContext, mBluetoothGattServerCallBack);

        /*2.初始化特征值*/
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString("0000bbb1-0000-1000-8000-00805f9b34fb"),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY + BluetoothGattCharacteristic.PROPERTY_WRITE + BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE + BluetoothGattCharacteristic.PERMISSION_READ);

        /*3.设置特征属性*/
        BluetoothGattDescriptor descriptor = new BluetoothGattDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"), BluetoothGattDescriptor.PERMISSION_WRITE);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        characteristic.addDescriptor(descriptor);

        /*4.设置服务*/
        final BluetoothGattService service = new BluetoothGattService(UUID.fromString("0000bbb0-0000-1000-8000-00805f9b34fb"),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        /*tip:SERVICE_TYPE_SECONDARY  ;*/
        service.addCharacteristic(characteristic);

        /*5.添加服务*/
        boolean isSuccess = mGattServer.addService(service);
        LaLog.d(TAG, " 添加mGattServer：" + isSuccess);
//        adb logcat -v time > d:\log.txt

        /*6.开启广播*/
        startService();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startService() {
        //判断你的设备到底支持不支持BLE Peripheral,不支持则返回空
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        Log.e(TAG, "mBluetoothLeAdvertiser" + mBluetoothLeAdvertiser);
        if (mBluetoothLeAdvertiser == null) {
            return;
        }
        startAdvertising();  //初始化BLE蓝牙广播
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startAdvertising() {
        byte[] broadcastData = {0x34, 0x56};
//        String bleName = "小郎";
//        byte[] broadcastData = bleName.getBytes();
        //广播设置参数，广播数据，还有一个是Callback
        mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(broadcastData), mAdvertiseCallback);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        return mAdvertiseSettings;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdvertiseData createAdvertiseData(byte[] data) {
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.addManufacturerData(0x01AC, data);


        mDataBuilder.addServiceUuid(ParcelUuid.fromString("00001000-0000-1000-8000-00805f9b34fb"));
        mDataBuilder.setIncludeDeviceName(true);  //设置是否携带设备名称
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        return mAdvertiseData;
    }


    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            LaLog.d(TAG, "开启广播成功");

//            initGattServer();  //初始化GATT服务


            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            LaLog.d(TAG, "开启广播失败 errorCode" + errorCode);
            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
//                Toast.makeText(mContext, "Operation failed due to ali internal error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
//                Toast.makeText(mContext, "Operation failed due to ali internal error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
//                Toast.makeText(mContext, "Operation failed due to ali internal error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Failed to start advertising as the advertising is already started");
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
//                Toast.makeText(mContext, "Operation failed due to ali internal error", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Operation failed due to ali internal error");
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
//                Toast.makeText(mContext, "This feature is not supported on this platform", Toast.LENGTH_LONG).show();
                Log.e(TAG, "This feature is not supported on this platform");
            } else {
//                Toast.makeText(mContext, "onStartFailure errorCode", Toast.LENGTH_LONG).show();
                Log.e(TAG, "onStartFailure errorCode" + errorCode);
            }
        }
    };


    private BluetoothGattServerCallback mBluetoothGattServerCallBack = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
        }

        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }

        @Override
        public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
            super.onExecuteWrite(device, requestId, execute);
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        @Override
        public void onMtuChanged(BluetoothDevice device, int mtu) {
            super.onMtuChanged(device, mtu);
        }

        @Override
        public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(device, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
            super.onPhyRead(device, txPhy, rxPhy, status);
        }
    };

    /*断开与低功耗蓝牙设备的连接*/
    public void closeBLEConnection(@NonNull BluetoothGatt gatt) {
        gatt.disconnect();
        gatt.close();
    }

    public void closeBLEConnection() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    private LaBlueStateChange laBlueStateChange;

    /*监听低功耗蓝牙连接状态的改变事件，包括开发者主动连接或断开连接，设备丢失，连接异常断开等等ok*/
    public void onLaBLEConnectionStateChange(LaBlueStateChange laBlueStateChange) {
        this.laBlueStateChange = laBlueStateChange;
    }

    /**
     * 当连接设备时，设置蓝牙状态改变回调，默认newState应该保持100,在BluetoothGattCallback中回调回来，如果是读的特征则设置status的值为1ok，todo需要再优化
     */
    public void onBLEConnectionStateChange(@NonNull BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, @NonNull int status, int newState) {

        Log.d(TAG, "连接监听回调--qy-重要的一部--");

        if (laBlueStateChange != null) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                laBlueStateChange.onStateConnected(gatt, status, newState);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                laBlueStateChange.onStateDisConnected(gatt, status, newState);
            } else if (status == BluetoothGatt.GATT_SUCCESS) {
                laBlueStateChange.onServicesDiscovered(gatt, status);
            } else if (status == 1) {
                laBlueStateChange.onCharacteristicRead(gatt, characteristic, status);
            }
        }
    }


    /*获获取蓝牙设备所有 net（服务）ok，tip:获取蓝牙服务需要先连接后才能获取*/
    public BluetoothGattService getBLEDeviceServices(@NonNull BluetoothGatt bluetoothGatt, @NonNull UUID uuid) {
        return bluetoothGatt.getService(uuid);
    }

    public BluetoothGattService getBLEDeviceServices(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return bluetoothGattCharacteristic.getService();
    }


    /**
     * 连接低功耗蓝牙设备时，在bluetoothGattcal中设置mGatt参数，该参数在数据交换的时候可能会用到
     */
    public void setBluetoothGatt(@NonNull BluetoothGatt gatt) {
        this.mGatt = gatt;
    }

    /**
     * 读取数据
     * <p>
     * 读取低功耗蓝牙设备的特征值的二进制数据值。注意：必须设备的特征值支持rea
     * d才可以成功调用，具体参照 characteristic 的 properties 属性ok
     */
    public boolean readBLECharacteristic(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.PROPERTY_READ);
        if (bluetoothGattCharacteristic.getPermissions() == BluetoothGattCharacteristic.PERMISSION_READ) {

        }
        return !(mGatt == null || getBluetoothAdapter() == null) && mGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    /**
     * 写数据
     * <p>
     * 向低功耗蓝牙设备特征值中写入二进制数据。注意：必须设备的特征值支持write才可以成功调用，具体参照 characteristic 的 properties 属性
     * tips: 并行调用多次读写接口存在读写失败的可能性ok
     */
    public boolean writeBLECharacteristic(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        bluetoothGattCharacteristic.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        return !(mGatt == null || getBluetoothAdapter() == null) && mGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }


    /**
     * 读取低功耗蓝牙特征值得二进制数据
     */
    public byte[] getBLECharacteristicValues(@NonNull BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        return bluetoothGattCharacteristic.getValue();
    }

    /**
     * BLE设备之前设置是否开启状态改变通知
     * <p> ios中方法名为：notifyBLECharacteristicValueChange
     * 启用低功耗蓝牙设备特征值变化时的 notify 功能，订阅特征值。注意：
     * 必须设备的特征值支持notify或者indicate才可以成功调用，具体参照 characteristic 的 properties 属性
     * 另外，必须先启用notify才能监听到设备 characteristicValueChange 事件
     */
    public boolean setCharacteristicNotification(@NonNull BluetoothGattCharacteristic characteristic, @NonNull boolean notification) {
        return mGatt != null && mGatt.setCharacteristicNotification(characteristic, notification);
    }

    public boolean setCharacteristicNotification(UUID serviceUuid, UUID characteristicUuid, boolean enable) {
        BluetoothGattService bluetoothGattService = getSupportedGattServices(serviceUuid);
        boolean result = false;
        if (bluetoothGattService != null) {
            BluetoothGattCharacteristic bluetoothGattCharacteristic = getSupportedGattCharacteristic(bluetoothGattService, characteristicUuid);
            result = setCharacteristicNotification(bluetoothGattCharacteristic, enable);
            if (result && mGatt != null) {
                BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG);
//        bluetoothGattDescriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});
                bluetoothGattDescriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                result = mGatt.writeDescriptor(bluetoothGattDescriptor);// descriptor write
            }
        }
        return result;
    }

    public boolean setCharacteristicNotification(String serviceUuid, String characteristicUuid, boolean enable) {
        return setCharacteristicNotification(UUID.fromString(serviceUuid), UUID.fromString(characteristicUuid), enable);
    }

    /**
     * 获取蓝牙设备某个服务中的所有 BluetoothGattService（特征值）
     */
    public BluetoothGattService getSupportedGattServices(@NonNull UUID uuid) {
        return mGatt == null ? null : mGatt.getService(uuid);
    }

    /**
     * 获取蓝牙设备某个服务中的所有 characteristic（特征值）
     */
    public BluetoothGattCharacteristic getSupportedGattCharacteristic(@NonNull BluetoothGattService bluetoothGattService, @NonNull UUID supportUuid) {
        return mGatt == null ? null : bluetoothGattService.getCharacteristic(supportUuid);
    }

    public BluetoothGattCharacteristic getSupportedGattCharacteristic(@NonNull UUID serviceUuid, @NonNull UUID supportUuid) {
        return mGatt == null ? null : getSupportedGattCharacteristic(getSupportedGattServices(serviceUuid), supportUuid);
    }

    public BluetoothGattCharacteristic getSupportedGattCharacteristic(@NonNull String serviceUuid, @NonNull String supportUuid) {
        return mGatt == null ? null : getSupportedGattCharacteristic(getSupportedGattServices(UUID.fromString(serviceUuid)), UUID.fromString(supportUuid));
    }

    public interface BLECharacteristicCallBack {
    }

    /*监听低功耗蓝牙设备的特征值变化。必须先启用notify接口才能接收到设备推送的notification。*/
    private void onBLECharacteristicValueChange(BLECharacteristicCallBack bleCharacteristicCallBack) {

    }


    /*以下为补充方法*/

    /**
     * 扫描BLE设备.注意该方法无法扫描标准蓝牙,只能扫描BLE设备
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scanBleDevice(final boolean enabled) {
        if (mBluetoothAdapter == null) {
            return;
        }
        /*qy tips:
        为什么不能再使用单例的BluetoothAdapter? 原因如下:
        bluetoothAdapter.startLeScan() //deprecated
        http://stackoverflow.com/questions/30223071/startlescan-replacement-to-current-api
        Remember that the method: public BluetoothLeScanner getBluetoothLeScanner () isn't static.
        If you do: BluetoothAdapter.getBluetoothLeScanner()
        you will get ali error, since getDefaultAdapter() is a static method, but getBluetoothLeScanner() isn't.
        You need ali instance of a BluetoothAdapter.
         */
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (enabled) {
            //scan分为2类,而在android L之前,搜索条件只有uuid
            //(1)直接搜索全部周围peripheral(外围的)设备,搜索结果将通过这个callback返回
            scanner.startScan(mScanCallback);
            //(2)根据过滤条件搜索设备
            final List<ScanFilter> scanFilters = new ArrayList<>();
            //uuid格式8-4-4-4-12(32位,128bit)
            //address格式(12位,48bit)
            scanFilters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString("00000000-0000-0000-0000-000000000000")).setDeviceAddress("00:00:00:00:00:00").build());
            ScanSettings scanSettings = new ScanSettings.Builder()
                    //require API 23
                    //.setCallbackType(0).setMatchMode(0).setNumOfMatches(0)
                    .setReportDelay(0).setScanMode(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE).build();
            scanner.startScan(scanFilters, scanSettings, mScanCallback);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
        } else {
            scanner.stopScan(mScanCallback);
        }
    }


}
