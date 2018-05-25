package com.cxp.imageviewer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;


public class ImagePreviewFragment extends Fragment {

    private String path;
    private ScaleImageView iv;

    public ImagePreviewFragment() {
    }
    public static ImagePreviewFragment newInstance(String path) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString("path", path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString("path");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        iv = (ScaleImageView) view.findViewById(R.id.iv);
        iv.setOnSingleTapListener(new ScaleImageView.OnSingleTapListener() {
            @Override
            public void onSingleTap() {
                getActivity().finish();
            }
        });
        Glide.with(getActivity()).load(path).asBitmap().placeholder(R.drawable.loading).into(new SimpleTarget<Bitmap>(width,height) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                iv.setImageBitmap(resource);
            }
        });
    }
    public void reset(){
        if(iv!=null){
            iv.reset();
        }
    }
}
