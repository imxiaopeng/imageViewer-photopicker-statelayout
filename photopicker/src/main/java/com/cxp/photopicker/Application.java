package com.cxp.photopicker;

import android.content.Context;

/**
 */
public class Application {

    private static Context context;

    public static Context getCtx() {
        return context;
    }
    public static void setContext(Context ctx){
        context = ctx;
    }



}
