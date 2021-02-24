package com.hms.example.dummyapplication;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.huawei.hms.ads.HwAds;
import com.huawei.hms.feature.dynamicinstall.FeatureCompat;
import com.huawei.hms.jos.AppUpdateClient;
import com.huawei.hms.jos.JosApps;
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack;
import com.huawei.updatesdk.service.otaupdate.UpdateKey;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);
        HwAds.init(this);
        AppUpdateClient client = JosApps.getAppUpdateClient(this);
        client.checkAppUpdate(this, new CheckUpdateCallBack() {
            @Override
            public void onUpdateInfo(Intent intent) {
                int status = intent.getIntExtra(UpdateKey.STATUS, -1);
                //Log.e("Update info","Code: "+status);
                Log.e("Tag","Hello Punith");
            }

            @Override
            public void onMarketInstallInfo(Intent intent) {

            }

            @Override
            public void onMarketStoreError(int i) {

            }

            @Override
            public void onUpdateStoreError(int i) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        FeatureCompat.install(base);

    }
}
