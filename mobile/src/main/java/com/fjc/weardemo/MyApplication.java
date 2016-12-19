package com.fjc.weardemo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.antfortune.freeline.FreelineCore;

/**
 * Created by FJC on 2016/12/8.
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        FreelineCore.init(this);
    }

}
