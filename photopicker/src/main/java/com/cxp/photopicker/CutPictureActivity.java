package com.cxp.photopicker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


import com.cxp.photopicker.utils.PictureUtils;
import com.cxp.photopicker.widgets.cutpictureview.ClipImageLayout;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CutPictureActivity extends AppCompatActivity {

    public static final int RESULT_CUT = 2;
    RelativeLayout cutpickBackButton;
    ClipImageLayout idClipImageLayout;
    Button cutpicSureButton;
    private Uri cropUri;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.cxp.photopicker.R.layout.activity_cut_picture);
        cutpickBackButton= (RelativeLayout) findViewById(com.cxp.photopicker.R.id.cutpick_back_button);
        idClipImageLayout= (ClipImageLayout) findViewById(com.cxp.photopicker.R.id.id_clipImageLayout);
        cutpicSureButton= (Button) findViewById(com.cxp.photopicker.R.id.cutpic_sure_button);
        Intent intent = getIntent();
        String iamgePath = intent.getStringExtra("bitmap");
        cropUri =  intent.getParcelableExtra("cropUri");
        idClipImageLayout.setBitmapPath(iamgePath);
        initEvent();
    }

    private void initEvent() {
        cutpickBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cutpicSureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cropUri == null) {
                     cropUri = PictureUtils.generateTempCroppedImageUri(CutPictureActivity.this);
                }
                mProgressDialog  =new ProgressDialog(CutPictureActivity.this);
                mProgressDialog.setMessage(getString(R.string.progressing));
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                Observable.create(new ObservableOnSubscribe<Uri>() {
                    @Override
                    public void subscribe(ObservableEmitter<Uri> oe) throws Exception {
                        OutputStream os = null;
                        try {
                            Bitmap bmp = idClipImageLayout.clip();
                            os = getContentResolver().openOutputStream(cropUri);
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.flush();
                            bmp.recycle();
                            oe.onNext(cropUri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            oe.onError(e);
                        } finally {
                            if (os != null) {
                                try {
                                    os.close();
                                } catch (IOException ee) {
                                    ee.printStackTrace();
                                }

                            }
                            oe.onComplete();

                        }
                    }
                }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Uri>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Uri uri) {
                        //返回图片uri
                        Intent data=new Intent();
                        data.putExtra("result",uri);
                        setResult(Activity.RESULT_OK,data);
                    }

                    @Override
                    public void onError(Throwable e) {


                    }

                    @Override
                    public void onComplete() {
                        if(mProgressDialog != null && mProgressDialog.isShowing()){
                            mProgressDialog.dismiss();
                        }
                        finish();
                    }
                });

            }
        });


    }

    @Override
    protected void onDestroy() {
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }
}
