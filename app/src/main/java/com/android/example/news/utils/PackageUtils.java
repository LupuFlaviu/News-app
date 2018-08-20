package com.android.example.news.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * Utility class for manipulating packages
 */
public class PackageUtils {

    /**
     * Check for package availability in order to start an intent
     *
     * @param ctx    context
     * @param intent the intent that you want to start
     * @return <code>true</code> if there is an appropriate app to open the intent, <code>false</code> otherwise
     */
    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        // check if there is at least 1 appropriate app
        return list.size() > 0;
    }
}
