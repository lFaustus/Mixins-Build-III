package com.fluxinated.mixins.database;

import android.app.Application;
import android.content.Context;

/**
 * Created by User on 08/10/2015.
 */
public class MyApplication extends Application
{
    private static MyApplication mInstance;
    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
    }

    public static MyApplication getInstance()
    {
        return mInstance;
    }

    public static Context getAppContext()
    {
        return mInstance.getApplicationContext();
    }
}
