package com.otio.client;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OtioApplication extends Application {
    ExecutorService srv = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
