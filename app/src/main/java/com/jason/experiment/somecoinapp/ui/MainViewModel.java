package com.jason.experiment.somecoinapp.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jason.experiment.somecoinapp.logging.LogTracker;
import com.jason.experiment.somecoinapp.logging.Logg;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * MainViewModel
 * Created by jason.
 */
public class MainViewModel extends ViewModel implements LogTracker{

    public static final int NAV_ITEM_LISTING  = 0;
    public static final int NAV_ITEM_FOLLOWED = 1;
    public static final int NAV_ITEM_SEARCH   = 2;

    private static final int MAX_LOG_LENGTH = 20000;


    private MutableLiveData<Integer>     currentNavItem = new MutableLiveData<>();
    private NoNullEventLiveData<Boolean> backPressed    = new NoNullEventLiveData<>();
    private MutableLiveData<String>      logData        = new MutableLiveData<>();


    public MainViewModel() {
        currentNavItem.setValue(0);
        backPressed.setValue(null);
        logData.setValue("");
    }

    public void backPressed() {
        backPressed.postValue(Boolean.TRUE);
    }

    public LiveData<Boolean> getBackPressed() {
        return backPressed;
    }

    public LiveData<String> getLogData(){
        return logData;
    }

    public void clearLogData(){
        logData.postValue("");
    }

    public void setBackPressed(Boolean pressed) {
        backPressed.postValue(pressed);
    }

    public LiveData<Integer> getCurrentNavItem() {
        return currentNavItem;
    }

    public void setCurrentNavItem(Integer navItem) {
        currentNavItem.postValue(navItem);
    }

    @Override
    public void trackLog(int priority, String tag, String data, Throwable throwable) {
        try{
            String stackTraceString = "";
            if(throwable != null){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                stackTraceString = sw.toString();
            }
            String currentLog = logData.getValue();
            String logEntry = Logg.translatePriority(priority) + '[' + tag + "]\n" +
                    data + '\n' +
                    stackTraceString + "\n\n";
//                Log.getStackTraceString(throwable) + "\n\n";
            String finalEntry = logEntry + currentLog;
            if(finalEntry.length() > MAX_LOG_LENGTH){
                finalEntry = finalEntry.substring(0, MAX_LOG_LENGTH);
            }
            logData.postValue(finalEntry);
        } catch (Exception ignored){
        }
    }
}
