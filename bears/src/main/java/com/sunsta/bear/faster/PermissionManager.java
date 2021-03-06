package com.sunsta.bear.faster;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sunsta.bear.AnConstants;
import com.sunsta.bear.R;
import com.sunsta.bear.callback.TakeResultListener;
import com.sunsta.bear.model.entity.InvokeParam;
import com.sunsta.bear.model.entity.TContextWrap;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：权限管理类 * <br/>
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期：2016/9/22
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 1.0 |  2016/9/22             |   权限管理类
 */
public class PermissionManager {
    public enum TPermission {
        STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE),
        CAMERA(Manifest.permission.CAMERA);
        String stringValue;

        TPermission(String stringValue) {
            this.stringValue = stringValue;
        }

        public String stringValue() {
            return stringValue;
        }
    }

    public enum TPermissionType {
        GRANTED("已授权"),
        DENIED("未授权"),
        WAIT("等待授权"),
        NOT_NEED("无需授权"),
        ONLY_CAMERA_DENIED("没有拍照权限"),
        ONLY_STORAGE_DENIED("没有读写SD卡权限");
        String stringValue;

        TPermissionType(String stringValue) {
            this.stringValue = stringValue;
        }

        public String stringValue() {
            return stringValue;
        }
    }

    private final static String[] methodNames = {
            "onPickFromCapture",
            "onPickFromCaptureWithCrop",
            "onPickMultiple",
            "onPickMultipleWithCrop",
            "onPickFromDocuments",
            "onPickFromDocumentsWithCrop",
            "onPickFromGallery",
            "onPickFromGalleryWithCrop",
            "onCrop"
    };

    /**
     * 检查当前应用是否被授予相应权限
     * @param contextWrap
     * @param method
     * @return
     */
    public static TPermissionType checkPermission(@NonNull TContextWrap contextWrap, @NonNull Method method) {
        String methodName = method.getName();
        boolean contain = false;
        for (int i = 0, j = methodNames.length; i < j; i++) {
            if (TextUtils.equals(methodName, methodNames[i])) {
                contain = true;
                break;
            }
        }
        if (!contain) return TPermissionType.NOT_NEED;

        boolean cameraGranted = true, storageGranted = ContextCompat.checkSelfPermission(contextWrap.getActivity(), TPermission.STORAGE.stringValue()) == PackageManager.PERMISSION_GRANTED ? true : false;

        if (TextUtils.equals(methodName, "onPickFromCapture") || TextUtils.equals(methodName, "onPickFromCaptureWithCrop")) {
            cameraGranted = ContextCompat.checkSelfPermission(contextWrap.getActivity(), TPermission.CAMERA.stringValue()) == PackageManager.PERMISSION_GRANTED ? true : false;
        }

        boolean granted = storageGranted && cameraGranted;
        if (!granted) {
            ArrayList<String> permissions = new ArrayList<>();
            if (!storageGranted) permissions.add(TPermission.STORAGE.stringValue());
            if (!cameraGranted) permissions.add(TPermission.CAMERA.stringValue());
            requestPermission(contextWrap, permissions.toArray(new String[permissions.size()]));
        }
        return granted ? TPermissionType.GRANTED : TPermissionType.WAIT;
    }

    public static void requestPermission(@NonNull TContextWrap contextWrap, @NonNull String[] permissions) {
        if (contextWrap.getFragment() != null) {
            contextWrap.getFragment().requestPermissions(permissions, AnConstants.CAPTURE.PERMISSION_REQUEST_TAKE_PHOTO);
        } else {
            ActivityCompat.requestPermissions(contextWrap.getActivity(), permissions, AnConstants.CAPTURE.PERMISSION_REQUEST_TAKE_PHOTO);
        }
    }

    public static TPermissionType onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == AnConstants.CAPTURE.PERMISSION_REQUEST_TAKE_PHOTO) {
            boolean cameraGranted = true, storageGranted = true;
            for (int i = 0, j = permissions.length; i < j; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (TextUtils.equals(TPermission.STORAGE.stringValue(), permissions[i])) {
                        storageGranted = false;
                    } else if (TextUtils.equals(TPermission.CAMERA.stringValue(), permissions[i])) {
                        cameraGranted = false;
                    }
                }
            }
            if (cameraGranted && storageGranted) return TPermissionType.GRANTED;
            if (!cameraGranted && storageGranted) return TPermissionType.ONLY_CAMERA_DENIED;
            if (!storageGranted && cameraGranted) return TPermissionType.ONLY_STORAGE_DENIED;
            if (!storageGranted && !cameraGranted) return TPermissionType.DENIED;
        }
        return TPermissionType.WAIT;
    }

    public static void handlePermissionsResult(Activity activity, TPermissionType type, InvokeParam invokeParam, TakeResultListener listener) {
        String tip = null;
        switch (type) {
            case DENIED:
                listener.takeFail(null, tip = activity.getResources().getString(R.string.permission_camera_storage_hint));
                break;
            case ONLY_CAMERA_DENIED:
                listener.takeFail(null, tip = activity.getResources().getString(R.string.permission_camera_hint));
                break;
            case ONLY_STORAGE_DENIED:
                listener.takeFail(null, tip = activity.getResources().getString(R.string.permission_white_external_hint));
                break;
            case GRANTED:
                try {
                    invokeParam.getMethod().invoke(invokeParam.getProxy(), invokeParam.getArgs());
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.takeFail(null, tip = activity.getResources().getString(R.string.permission_camera_storage_hint));
                }
                break;
            default:
                break;
        }
        if (tip != null) Toast.makeText(activity, tip, Toast.LENGTH_LONG).show();

    }
}
