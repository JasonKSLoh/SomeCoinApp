package com.jason.experiment.somecoinapp.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * NetworkUtil
 * Created by jason.
 */
public class NetworkUtil {

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
