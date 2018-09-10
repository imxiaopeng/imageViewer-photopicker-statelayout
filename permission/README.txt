//6.0以上权限校验工具类，使用方法如下：
注意PermissionSetting导包为当前包下
        import com.example.permission.DefaultRationale;
        import com.example.permission.PermissionSetting;
        private void checkPermissions(Context mContext,String... permissions) {
        PermissionSetting setting = new PermissionSetting(mContext);
                StrictChecker checker = new StrictChecker();
                if (checker.hasPermission(mContext, permissions)) {
                   //TODO 已经获取权限
                } else {
                    //申请权限
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
                                    //TODO 获得权限

                                }
                            })
                            .start();
                }
            }
