//6.0以上权限校验工具类，使用方法如下：
PermissionUtils.newInstance(mContext)
                        .checkPermissons(new PermissionUtils.OnPermissionGrantedListener() {
                            @Override
                            public void onPermissionGranted() {
                               //TODO do something
                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
