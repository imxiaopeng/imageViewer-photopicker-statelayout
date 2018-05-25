package com.cxp.photopicker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/3/21.
 */

public class BitmapUtils {
    public static Bitmap getbitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            bitmap = decodeBitmap(bitmap, 80, 80);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static interface OnSaveSuccessListener {
        void onSuccess(File file);
    }

    /**
     * 从本地path中获取bitmap，压缩后保存小图片到本地
     *
     * @param path 图片存放的路径
     * @return 返回压缩后图片的存放路径
     */
    public static void compressPictureFile(File path, OnSaveSuccessListener onSaveSuccessListener) {
        try {
            if (new FileInputStream(path).available() / 1024 <= 200) {
                onSaveSuccessListener.onSuccess(path);
                return;
            }
            String compressdPicPath = "";
            String fileName = path.getName();
//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
      /*  //★如果不压缩直接从path获取bitmap，这个bitmap会很大，下面在压缩文件到100kb时，会循环很多次，
        // ★而且会因为迟迟达不到100k，options一直在递减为负数，直接报错
        //★ 即使原图不是太大，options不会递减为负数，也会循环多次，UI会卡顿，所以不推荐不经过压缩，直接获取到bitmap
        Bitmap bitmap=BitmapFactory.decodeFile(path);*/
//      ★★★★★★★★★★★★★★重点★★★★★★★★★★★★★
            Bitmap bitmap = decodeSampledBitmapFromPath(path.getAbsolutePath(), 106, 106);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

/* options表示 如果不压缩是100，表示压缩率为0。如果是70，就表示压缩率是70，表示压缩30%; */
            int options = 100;
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            while (baos.toByteArray().length / 1024 > 200) {
// 循环判断如果压缩后图片是否大于200kb继续压缩

                baos.reset();
                options -= 10;
                if (options < 11) {//为了防止图片大小一直达不到200kb，options一直在递减，当options<0时，下面的方法会报错
                    // 也就是说即使达不到200kb，也就压缩到10了
                    bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
                    break;
                }
// 这里压缩options%，把压缩后的数据存放到baos中
                bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
            }

            String mDir = Environment.getExternalStorageDirectory() + "/yile";
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();//文件不存在，则创建文件
            }
            File file = new File(mDir, fileName);
            FileOutputStream fOut = null;

            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(baos.toByteArray());
                out.flush();
                out.close();
                onSaveSuccessListener.onSuccess(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据图片要显示的宽和高，对图片进行压缩，避免OOM
     *
     * @param path
     * @param width  要显示的imageview的宽度
     * @param height 要显示的imageview的高度
     * @return
     */
    private static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {

//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = caculateInSampleSize(options, width, height);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;

    }

    private static Bitmap decodeBitmap(Bitmap bmp, int width, int height) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//      获取图片的宽和高，并不把他加载到内存当中
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
//      使用获取到的inSampleSize再次解析图片(此时options里已经含有压缩比 options.inSampleSize，再次解析会得到压缩后的图片，不会oom了 )
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size(), options);
        return bitmap;

    }

    /**
     * 根据需求的宽和高以及图片实际的宽和高计算SampleSize
     *
     * @param options
     * @param reqWidth  要显示的imageview的宽度
     * @param reqHeight 要显示的imageview的高度
     * @return
     * @compressExpand 这个值是为了像预览图片这样的需求，他要比所要显示的imageview高宽要大一点，放大才能清晰
     */
    private static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;

        int inSampleSize = 1;

        if (width >= reqWidth || height >= reqHeight) {

            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(width * 1.0f / reqHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);

        }

        return inSampleSize;
    }

}
