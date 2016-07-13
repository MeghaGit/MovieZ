package com.example.watchit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Megha on 6/27/2016.
 */
public class Utilities {


    public static String getPreferredSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int default_sorting_order = WatchIt.SortingOrder.MOST_POPULAR.getValue();
        return prefs.getString(context.getString(R.string.preferred_sorting),
                Integer.toString(default_sorting_order));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getYearFromDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Calendar myCal = null;
        try {
            myCal = new GregorianCalendar();
            myCal.setTime(format.parse(date));
        } catch (ParseException e) {
        }
        return String.valueOf(myCal.get(Calendar.YEAR));
    }

    public static String formatDate(String oldDateString) {
        final String OLD_FORMAT = "yyyy-mm-dd";
        final String NEW_FORMAT = "mm.dd/yy";

// August 12, 2010
        String newDateString = "";

        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);

        try {
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (ParseException e) {

        }


        return newDateString;
    }

    public static String getMovieWebPage(int movieID, String movieName) {

        String BASE_URL = "https://www.themoviedb.org/movie";
        return BASE_URL + "/" + movieID + "-" + movieName.toLowerCase().replace(" ", "-") + "?language=en";
    }
}
