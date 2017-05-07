package com.filiereticsa.arc.augmentepf;

import android.app.Application;
import android.content.Context;

/**
 * Created by anthony on 07/05/2017.
 */

public class AugmentEPFApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        AugmentEPFApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return AugmentEPFApplication.context;
    }
}
