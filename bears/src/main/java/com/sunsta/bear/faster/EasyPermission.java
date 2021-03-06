/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sunsta.bear.faster;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h2>请关注个人知乎Bgwan， 在【an系列】专栏会有本【livery框架】的使用案例（20190922-正在持续更新中...</h2>
 * 中文描述：当然申请权限别忘记在Mainfest.xml中配置 Requests permission.
 * Utility to request and check System permissions for apps targeting Android M (API >= 23).
 * <br/><a href="https://zhihu.com/people/qydq">
 * --------温馨提示：知识是应该分享的，an系列框架可以点击这里关注我获取更详细的信息</a><br/>
 * <h3><a href="https://zhuanlan.zhihu.com/p/80668416">版权声明：(C) 2016 The Android Developer Sunst</a></h3>
 * <br>创建日期（可选）：2016/7/07
 * <br>邮件Email：qyddai@gmail.com
 * <br>Github：<a href ="https://qydq.github.io">qydq</a>
 * <br>知乎主页：<a href="https://zhihu.com/people/qydq">Bgwan</a>
 * @author sunst // sunst0069
 * @version 3.0 | 2019/09/10 | rx方式申请权限
 */
public class EasyPermission {
    public static final int SETTINGS_REQ_CODE = 16061;

    public interface PermissionCallback extends ActivityCompat.OnRequestPermissionsResultCallback {
        void onPermissionGranted(int requestCode, List<String> perms);

        void onPermissionDenied(int requestCode, List<String> perms);
    }

    private Object object;
    private String[] mPermissions;
    private String mRationale = "shouldShowRationale should open those permission:";
    private int mRequestCode;
    /**
     * group权限只需要申请一个，则会对应申请一组的权限
     */
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_CALENDAR = Manifest.permission.READ_CALENDAR;//2
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;//2
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;//2
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;//3
    public static final String PERMISSION_READ_SMS = Manifest.permission.READ_SMS;//6
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;//7
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_BODY_SENSORS = Manifest.permission.BODY_SENSORS;

    public static final String[] requestPermissions = {
            PERMISSION_RECORD_AUDIO,
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_READ_CALENDAR,
            PERMISSION_CAMERA,
            PERMISSION_BODY_SENSORS,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_READ_SMS
    };

    public static final String[] GROUP_PERMISSONS_CONTACTS = {
            PERMISSION_GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };
    public static final String[] GROUP_PERMISSONS_PHONE = {
            Manifest.permission.READ_CALL_LOG,
            PERMISSION_READ_PHONE_STATE,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG
    };
    public static final String[] GROUP_PERMISSONS_CALENDAR = {
            PERMISSION_READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };
    public static final String[] GROUP_PERMISSONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION
    };
    public static final String[] GROUP_PERMISSONS_STORAGE = {
            PERMISSION_READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final String[] GROUP_PERMISSONS_SMS = {
            Manifest.permission.RECEIVE_WAP_PUSH,
            PERMISSION_READ_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS
    };

    @StringRes
    private int mPositiveButtonText = android.R.string.ok;
    @StringRes
    private int mNegativeButtonText = android.R.string.cancel;

    private EasyPermission(Object object) {
        this.object = object;
    }

    public static EasyPermission with(Activity activity) {
        return new EasyPermission(activity);
    }

    public static EasyPermission with(Fragment fragment) {
        return new EasyPermission(fragment);
    }

    public EasyPermission permissions(String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    public EasyPermission rationale(String rationale) {
        this.mRationale = rationale;
        return this;
    }

    public EasyPermission addRequestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    public EasyPermission positveButtonText(@StringRes int positiveButtonText) {
        this.mPositiveButtonText = positiveButtonText;
        return this;
    }

    public EasyPermission nagativeButtonText(@StringRes int negativeButtonText) {
        this.mNegativeButtonText = negativeButtonText;
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void request() {
        requestPermissions(object, mRationale, mPositiveButtonText, mNegativeButtonText, mRequestCode, mPermissions);
    }

    /**
     * Check if the calling context has a set of permissions.
     */
    public static boolean hasPermissions(Context context, String... perms) {
//        Always return true for SDK<M, let the system deal with the permissions
        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            return true;
        }
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            /*针对安装应用的权限patch*/
            if (Manifest.permission.REQUEST_INSTALL_PACKAGES.equals(perm)) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        hasPerm = context.getPackageManager().canRequestPackageInstalls();
                    }
                } catch (Exception e) {
                    LaLog.e("com.sunsta.livery.ErrorMark : No android.permission.REQUEST_INSTALL_PACKAGES Permission.");
                    e.printStackTrace();
                    return false;
                }
            }
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    /**
     * 开启设置安装未知来源应用权限界面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void installPermissionSetting(Context context, int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    /**
     * 开启设置安装未知来源应用权限界面
     */
    private static void installPermissionSettingApp(Activity activity, int requestCode) {
        Intent intent = new Intent();
        Uri packageURI = Uri.parse("package:" + activity.getPackageName());
        intent.setData(packageURI);
        if (Build.VERSION.SDK_INT >= 26) {
            intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        } else {
            intent.setAction(Settings.ACTION_SECURITY_SETTINGS);
        }
        activity.startActivityForResult(intent, requestCode);
        ToastUtils.s(activity, "请开启未知应用安装权限");
    }

    /**
     * Request a set of permissions, showing rationale if the system requests it.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(final Object object, String rationale, final int requestCode,
                                          final String... perms) {
        requestPermissions(object, rationale, android.R.string.ok, android.R.string.cancel, requestCode,
                perms);
    }

    /**
     * Request a set of permissions, showing rationale if the system requests it.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestPermissions(final Object object, String rationale, @StringRes int positiveButton,
                                          @StringRes int negativeButton, final int requestCode, final String... permissions) {
        checkCallingObjectSuitability(object);

        PermissionCallback mCallBack = (PermissionCallback) object;

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            mCallBack.onPermissionGranted(requestCode, Arrays.asList(permissions));
            return;
        }

        final List<String> deniedPermissions = findDeniedPermissions(getActivity(object), permissions);

        boolean shouldShowRationale = false;
        for (String perm : deniedPermissions) {
            shouldShowRationale =
                    shouldShowRationale || shouldShowRequestPermissionRationale(object, perm);
        }

        if (isEmpty(deniedPermissions)) {
            mCallBack.onPermissionGranted(requestCode, Arrays.asList(permissions));
        } else {

            final String[] deniedPermissionArray =
                    deniedPermissions.toArray(new String[deniedPermissions.size()]);

            if (shouldShowRationale) {
                Activity activity = getActivity(object);
                if (null == activity) {
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(activity).setMessage(rationale)
                        .setPositiveButton(positiveButton, (dialog1, which) -> executePermissionsRequest(object, deniedPermissionArray, requestCode))
//                        .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // act as if the permissions were denied
//                                ((PermissionCallback) object).onPermissionDenied(requestCode,
//                                        deniedPermissions);
//                            }
//                        })
                        .setCancelable(false)
                        .create();

                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            } else {
                executePermissionsRequest(object, deniedPermissionArray, requestCode);
            }
        }
    }

    @TargetApi(23)
    private static void executePermissionsRequest(Object object, String[] perms, int requestCode) {
        checkCallingObjectSuitability(object);
        for (String value : perms) {
            if (Manifest.permission.REQUEST_INSTALL_PACKAGES.equals(value)) {
                installPermissionSettingApp(getActivity(object), requestCode);
            }
        }
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).requestPermissions(perms, requestCode);
        }
    }

    /**
     * Handle the result of a permission request.
     */
    public static void onRequestPermissionsResult(Object object, int requestCode, String[] permissions, int[] grantResults) {
        checkCallingObjectSuitability(object);

        PermissionCallback mCallBack = (PermissionCallback) object;

        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (Manifest.permission.REQUEST_INSTALL_PACKAGES.equals(permissions[i])) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!getActivity(object).getPackageManager().canRequestPackageInstalls()) {
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permissions[i]);
                }
            }
        }
        if (isEmpty(deniedPermissions)) {
            mCallBack.onPermissionGranted(requestCode, Arrays.asList(permissions));
        } else {
            mCallBack.onPermissionDenied(requestCode, deniedPermissions);
        }
    }

    /**
     * with a {@code null} argument for the negative buttonOnClickListener.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkDeniedPermissionsNeverAskAgain(final Object object, String rationale,
                                                              @StringRes int positiveButton, @StringRes int negativeButton, List<String> deniedPerms) {
        return checkDeniedPermissionsNeverAskAgain(object, rationale, positiveButton, negativeButton, null,
                deniedPerms);
    }

    /**
     * 在OnActivityResult中接收判断是否已经授权
     * 使用{@link EasyPermission#hasPermissions(Context, String...)}进行判断
     * <p>
     * If user denied permissions with the flag NEVER ASK AGAIN, open a dialog explaining the
     * permissions rationale again and directing the user to the app settings. After the user
     * returned to the app, {@link Activity#(int, int, Intent)} or
     * {@link Fragment#onActivityResult(int, int, Intent)} or
     * {@link android.app.Fragment#onActivityResult(int, int, Intent)} will be called with
     * {@value #SETTINGS_REQ_CODE} as requestCode
     * <p>
     * <p>
     * NOTE: use of this method is optional, should be called from
     * {@link PermissionCallback#onPermissionDenied(int, List)}
     * @return {@code true} if user denied at least one permission with the flag NEVER ASK AGAIN.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkDeniedPermissionsNeverAskAgain(final Object object, String rationale,
                                                              @StringRes int positiveButton, @StringRes int negativeButton,
                                                              @Nullable DialogInterface.OnClickListener negativeButtonOnClickListener,
                                                              List<String> deniedPerms) {
        boolean shouldShowRationale;
        for (String perm : deniedPerms) {
            shouldShowRationale = shouldShowRequestPermissionRationale(object, perm);

            if (!shouldShowRationale) {
                final Activity activity = getActivity(object);
                if (null == activity) {
                    return true;
                }

                AlertDialog dialog = new AlertDialog.Builder(activity).setMessage(rationale)
                        .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openAppSettingsScreen(activity, object);
                            }
                        })
                        .setNegativeButton(negativeButton, negativeButtonOnClickListener)
                        .setCancelable(false)
                        .create();

                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                return true;
            }
        }
        return false;
    }

    private static void openAppSettingsScreen(Context context, Object object) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startAppSettingsScreen(object, intent);
    }

    /**
     * 打开应用设置详情
     */
    public static void openAppSettingsScreen(@NonNull Context context) {
        openAppSettingsScreen(context, context);
    }

    @TargetApi(11)
    private static void startAppSettingsScreen(Object object, Intent intent) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }
    }

    private static void checkCallingObjectSuitability(Object object) {
        if (!((object instanceof Fragment)
                || (object instanceof Activity)
                || (object instanceof android.app.Fragment))) {
            throw new IllegalArgumentException("Caller must be an Activity or a Fragment.");
        }

        if (!(object instanceof PermissionCallback)) {
            throw new IllegalArgumentException("Caller must implement PermissionCallback.");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (Manifest.permission.REQUEST_INSTALL_PACKAGES.equals(value)) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (!activity.getPackageManager().canRequestPackageInstalls()) {
                            denyPermissions.add(value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new SecurityException("Need to declare android.permission.REQUEST_INSTALL_PACKAGES to call this api..");
                }
            } else {
                if (activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED) {
                    denyPermissions.add(value);
                }
            }
        }
        return denyPermissions;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean shouldShowRequestPermissionRationale(Object object, String perm) {
        if (object instanceof Activity) {
            return ActivityCompat.shouldShowRequestPermissionRationale((Activity) object, perm);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).shouldShowRequestPermissionRationale(perm);
        } else {
            return false;
        }
    }

    public static Activity getActivity(Object object) {
        if (object instanceof Activity) {
            return ((Activity) object);
        } else if (object instanceof Fragment) {
            return ((Fragment) object).getActivity();
        } else if (object instanceof android.app.Fragment) {
            return ((android.app.Fragment) object).getActivity();
        } else {
            return null;
        }
    }

    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }
}