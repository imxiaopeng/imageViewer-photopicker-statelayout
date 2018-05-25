package com.cxp.imageviewer;

import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/26.
 */

public class ImageViewer {
    private static int width;
    private static int height;
    private static ImageViewer m;
    private Context mContext;
    private WindowManager manager;
    private ArrayList<String> mList;
    private int mPosition;

    public static ImageViewer getInstance(Context context) {
        if (m == null) {
            m=new ImageViewer(context);
        }
        return m;
    }

    /**
     * 设置数据
     * @param list
     * @return
     */
    public ImageViewer setData(ArrayList<String> list){
        mList = list;
        if(mList==null){
            mList=new ArrayList<>();
        }
        return this;
    }

    /**
     * 设置索引
     * @param i
     * @return
     */
    public ImageViewer setPosition(int i){
        i=i<0?0:i;
        i=i>mList.size()-1?mList.size()-1:i;
        mPosition=i;
        return this;
    }

    /**
     * 打开activity
     */
    public void show(){
        Intent intent=new Intent(mContext,ImagePreviewActivity.class);
        intent.putExtra("list",mList);
        intent.putExtra("position",mPosition);
        mContext.startActivity(intent);
    }
    private ImageViewer(Context context){
        mContext=context;
        manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    /*public int getWidth() {
        width=manager.getDefaultDisplay().getWidth();
        return width;
    }

    public int getHeight() {
        height=manager.getDefaultDisplay().getHeight();
        return height;
    }*/
}
