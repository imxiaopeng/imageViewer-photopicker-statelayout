图片选择器：兼容了7.0以上使用fileprovider的权限问题；扩展是否开启裁剪、图片多选、拍照的功能。
photopicker使用：
1.AndroidManifest.xml中修改
    <provider
        android:authorities="xx_photoPicker"//修改此处名称保证唯一性
        ..
    >
    </provider>
2.调起PhotoPickerActivity
    注意判断6.0以上SD卡读取权限、打开摄像头权限。
    private void startActivityForPicture() {
    File dir = new File(Environment.getExternalStorageDirectory(), "babbitt");//启用裁剪模式图片输出目录
            File realPath = new File(dir, System.currentTimeMillis() + ".png");//启用裁剪模式图片输出路径
            String externalStorageState = Environment.getExternalStorageState();
            if (!dir.exists() && (externalStorageState.equals(Environment.MEDIA_MOUNTED))) {
                dir.mkdirs();
            }
            Intent intent = new Intent(mContext, PhotoPickerActivity.class);
            intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, true);//是否启用摄像头
            intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 0);//单选或者多选(0单选1多选)
            intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 1);//最多选几张
            intent.putExtra(PhotoPickerActivity.NEED_CUT, true);//是否裁剪
            intent.putExtra(PhotoPickerActivity.DIR, dir);//
            intent.putExtra(PhotoPickerActivity.REALPATH, realPath);//
            startActivityForResult(intent, PICK_PHOTO);
        }

     @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data == null) {
                return;
            }
            if (requestCode == PICK_PHOTO && resultCode == RESULT_OK) {//相册选择图片
                String imgPath = data.getStringExtra(KEY_RESULT);//一个图片文件的完整路径可构造(new File(imgPath))
                Glide.with(mContext).load(imgPath).into(ivHead);
                //TODO 上传服务器
                }
        }