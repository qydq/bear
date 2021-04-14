package com.sunsta.bear.faster;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * 请关注个人知乎bgwan， 在【an系列】专栏会有【ali框架】的详细使用案例（20190922-正在持续更新中...）
 * <p>
 * 中文描述：蓝牙操作工具类&&后期考虑和LaBluetoothService合并
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2019/06/09
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |   2019/7/9 19:06             |   蓝牙操作工具类&&后期考虑和LaBluetoothService合并
 * @link 知乎主页： https://zhihu.com/people/qydq
 */
public class LABluethUtils {
    private static boolean isPhoneMac = true;
    private static final String TAG = "LABluethUtils";

    private static String macfORWear = "22:22:7C:AF:B4:F6";//模拟手表
    private static String macfORPhone = "24:79:F3:58:E6:58";//手机
    private static String trueWearMac = "22:22:BB:73:B8:CC";//真实手表地址

    /**
     * ps: permission only system apps
     * need add android.permission.
     * <uses-permission android:name="android.permission.BLUETOOTH" />
     * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
     * <uses-permission android:name="android.permission.LOCAL_MAC_ADDRESS" />
     */
    public static String getAddressInReflect() {
        String mac = "00:55";
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Field field = null;
        try {
            field = BluetoothAdapter.class.getDeclaredField("mService");
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                mac = null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            if (method != null) {
                Object obj = method.invoke(bluetoothManagerService);
                if (obj != null) {
                    mac = obj.toString();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mac;
    }

    /**
     * 用inResolver得到蓝牙地址
     */
    public static String getAddresssInResolver(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String macAddress = android.provider.Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
            return macAddress;
        } else {
            return BluetoothAdapter.getDefaultAdapter().getAddress();
        }
    }

    public static String getWifiMacAddress() {
        try {
            String interfaceName = "wlan0";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) {
                    continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    return "";
                }
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) {
                    buf.append(String.format("%02X:", aMac));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return "";
    }
}