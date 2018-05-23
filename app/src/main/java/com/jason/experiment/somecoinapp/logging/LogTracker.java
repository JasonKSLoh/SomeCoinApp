package com.jason.experiment.somecoinapp.logging;

/**
 * LogTracker
 * Created by jason.
 */
public interface LogTracker {
    void trackLog(int priority, String tag, String data, Throwable throwable);
}
