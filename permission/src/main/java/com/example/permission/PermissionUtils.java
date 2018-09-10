package com.example.permission;

import android.content.Context;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.checker.StrictChecker;

import java.util.List;

public class PermissionUtils {
    private final Context mContext;
    private static PermissionUtils instance;
    private final StrictChecker checker;
    private final PermissionSetting setting;

    public static PermissionUtils newInstance(Context context) {
        if (instance == null) {
            instance = new PermissionUtils(context);
        }
        return instance;
    }

    private PermissionUtils(Context context) {
        this.mContext = context;
        checker = new StrictChecker();
        setting = new PermissionSetting(context);
    }

    public void checkPermissons(final OnPermissionGrantedListener listener, String... permissions) {
        if (checker.hasPermission(mContext, permissions)) {
            if (listener != null) {
                listener.onPermissionGranted();
            }
        } else {
            AndPermission.with(mContext)
                    .permission(permissions)
                    .rationale(new DefaultRationale())
                    .onDenied(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            if (AndPermission.hasAlwaysDeniedPermission(mContext, permissions)) {
                                setting.showSetting(permissions);
                            }
                        }
                    })
                    .onGranted(new Action() {
                        @Override
                        public void onAction(List<String> permissions) {
                            if (listener != null) {
                                listener.onPermissionGranted();
                            }
                        }
                    })
                    .start();
        }
    }

    public interface OnPermissionGrantedListener {
        void onPermissionGranted();
    }
}
