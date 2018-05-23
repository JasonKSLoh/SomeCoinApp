package com.jason.experiment.somecoinapp;

import android.app.Application;

import com.jason.experiment.somecoinapp.logging.Logg;

import io.reactivex.plugins.RxJavaPlugins;

/**
 * MainApp
 * Created by jason.
 */
public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(e -> {
            Logg.e(e.getMessage(), e);
        });
    }
}
