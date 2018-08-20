package com.android.example.news.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.example.news.R;

/**
 * Helper class for network connectivity
 */

public class NetworkUtils {

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Check for network connectivity and show error otherwise
     *
     * @param context
     * @return <code>true</code> if network connectivity is available, <code>false</code> otherwise and shows an error
     */
    public static boolean checkNetworkAndShowError(Context context) {
        if (isNetworkAvailable(context)) {
            return true;
        } else {
            Toast.makeText(context, context.getString(R.string.internet_connection_error), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
