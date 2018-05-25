package com.cxp.photopicker.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016/11/25.
 */
public class PictureUtils {


    private static final String PHOTO_DATE_FORMAT = "'IMG'_yyyyMMdd_HHmmss";
    public static final String FILE_PROVIDER_AUTHORITY = "com.cxp.photopicker";
    public static String authority = "com.s47gw.babbitt_photoPicker";

    public static Uri generateTempCroppedImageUri(Context context) {
        return FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY,
                new File(pathForTempImage(context, generateTempCroppedPhotoFileName())));
    }

    private static String generateTempCroppedPhotoFileName() {
        final Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(PHOTO_DATE_FORMAT, Locale.US);
        return "ContactPhoto-" + dateFormat.format(date) + "-cropped.png";
    }

    private static String pathForTempImage(Context context, String fileName) {
        final File dir = new File(context.getCacheDir() + "/image_cache");
        dir.mkdirs();
        final File f = new File(dir, fileName);
        return f.getAbsolutePath();
    }

    /**
     * Try to return the absolute file path from the given Uri
     * 根据Uri返回图片完整路径
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getFileProvideRealPath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                Cursor cursor = context.getContentResolver().query(uri, new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (index > -1) {
                            data = cursor.getString(index);
                        }
                    }
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data != null) {
            return pathForTempImage(context, data);
        }
        return null;
    }


    /* *按质量压缩
          * @param bitmap       需要压缩的图片
  * @param compressPath 生成文件路径(例如: /storage/imageCache/1.jpg)
  * @param quality      图片质量，0~100
          * @return if true,保存成功
  */
    public static boolean compressBiamp(Bitmap bitmap, Object compressPath, int quality) {
        FileOutputStream stream = null;
        try {
            if (compressPath instanceof String) {
                stream = new FileOutputStream(new File((String) compressPath));
            }else if(compressPath instanceof File){
                stream = new FileOutputStream((File) compressPath);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);// (0-100)压缩文件
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }


    //按大小压缩
    public static boolean compressBitmap(String filePath, String compressPath, int quality) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        int reqHeight = 800;
        int reqWidth = 480;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        //在内存中创建bitmap对 象，这个对象按照缩放大小创建的
        // options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(compressPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);// (0-100)压缩文件
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return false;
    }

    public static Bitmap compressBitmap(String filePath, int quality) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int reqHeight = 800;
        int reqWidth = 480;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        //在内存中创建bitmap对 象，这个对象按照缩放大小创建的
        // options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    public static Bitmap compressBitmap(String filePath, Context context) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        int reqHeight = defaultDisplay.getHeight();
        int reqWidth = defaultDisplay.getWidth();
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            options.inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        //在内存中创建bitmap对 象，这个对象按照缩放大小创建的
        // options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static List<String> getImagePathFromSD() {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        // String filePath = Environment.getExternalStorageDirectory().toString();
        String filePath = Environment.getExternalStorageDirectory() + "/qqskPic/";
        File imgFile = new File(filePath);
        if (!imgFile.exists()) {
            imgFile.mkdir();
        }
        String filePath1 = Environment.getExternalStorageDirectory() + "/qqskPic";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath1);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    /**
     * @param outUri
     * @param inputPath //要剪切图片的地址
     * @param scaleX    //剪切X方向大小
     * @param scaleY    //剪切Y方向大小
     * @return
     */
    public static HashMap<Intent,File> getCropIntent(Activity context, Uri outUri, String inputPath, int scaleX, int scaleY) {
        HashMap<Intent,File> map=new HashMap<>();
        // 如果使用com.android.camera.action.CROP 则直接打开裁剪照片的activity 那么可以用自己的图片浏览器选择图片 传入参数并使用之
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 如果不设置type，则 ACTION_GET_CONTENT 会弹出异常FATAL EXCEPTION:main android.content.ActivityNotFoundException
        // 而 ACTION_PICK 会弹出可用程序列表 但没有打开图片相关的程序（在我的两个设备上是这样）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  大于等于24即为7.0及以上执行内容
            Uri inputUri = FileProvider.getUriForFile(context, authority, new File(inputPath));
            intent.setDataAndType(inputUri, "image/*");
        }else {
            intent.setDataAndType(Uri.fromFile(new File(inputPath)), "image/*");
        }
        //        intent.setDataAndType(Uri.fromFile(new File(inputPath)), "image/*");
        // 设置在开启的Intent中设置显示的view可裁剪
        // 这段代码里设置成false也能裁剪啊。。。这是为什么？懂的给我讲讲了
        // 这段注释掉就不会跳转到裁剪的activity
        intent.putExtra("crop", "true");
        // 设置x,y的比例，截图方框就按照这个比例来截 若设置为0,0，或者不设置 则自由比例截图(x,y不能一样长,适配华为部分机型)
        intent.putExtra("aspectX", scaleX-1);
        intent.putExtra("aspectY", scaleY);
        // 裁剪区的宽和高 其实就是裁剪后的显示区域 若裁剪的比例不是显示的比例，则自动压缩图片填满显示区域。若设置为0,0 就不显示。若不设置，则按原始大小显示
       /* intent.putExtra("outputX", scaleX);
        intent.putExtra("outputY",scaleY);*/
        // 不知道有啥用。。可能会保存一个比例值 需要相关文档啊
        intent.putExtra("scale", false);
        // true的话直接返回bitmap，可能会很占内存 不建议
        intent.putExtra("return-data", false);
        File file = new File(Environment.getExternalStorageDirectory(), "/babbitt/" + System.currentTimeMillis() + ".png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //  大于等于24即为7.0及以上执行内容
            Uri uri = FileProvider.getUriForFile(context, authority, file);
            intent.putExtra("output", uri);
            //将存储图片的uri读写权限授权给剪裁工具应用
            List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        } else {
            //  低于24即为7.0以下执行内容
            // 上面设为false的时候将MediaStore.EXTRA_OUTPUT即"output"关联一个Uri
            intent.putExtra("output", outUri);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 看参数即可知道是输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        // 面部识别 这里用不上
        intent.putExtra("noFaceDetection", false);
        map.put(intent,file);
        return map;
    }

    /*
 * 强制压缩图片到固定大小(不超过30kb)
 * */
    public static Bitmap ImageCompressL(Bitmap bitmap) {
        double targetwidth = Math.sqrt(30.00 * 1000);
        if (bitmap.getWidth() > targetwidth || bitmap.getHeight() > targetwidth) {
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 计算宽高缩放率
            double x = Math.max(targetwidth / bitmap.getWidth(), targetwidth
                    / bitmap.getHeight());
            // 缩放图片动作
            matrix.postScale((float) x, (float) x);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }


    //通过uri获取bitmap
    public static Bitmap decodeUriAsBitmap(Uri uri, Context ctx) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
