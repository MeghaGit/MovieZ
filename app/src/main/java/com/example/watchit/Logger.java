package com.example.watchit;

import android.util.Log;

/**
 * Created by Megha on 5/20/2016.
 */
public class Logger {
    private static final boolean DEBUG = true;


    public static void d(String TAG, String log_msg) {
        if (DEBUG)
            Log.d(TAG, log_msg);
    }
    public static void v(String TAG, String log_msg) {
        if (DEBUG)
            Log.v(TAG, log_msg);
    }
    public static void i(String TAG, String log_msg) {
        if (DEBUG)
            Log.i(TAG, log_msg);
    }
    public static void e(String TAG, String log_msg) {
        if (DEBUG)
            Log.e(TAG, log_msg);
    }
    public static void w(String TAG, String log_msg) {
        if (DEBUG)
            Log.w(TAG, log_msg);
    }
}
