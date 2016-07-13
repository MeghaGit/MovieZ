package com.example.watchit;

import android.app.Application;

import com.crittercism.app.Crittercism;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Megha on 6/23/2016.
 */
public class WatchIt extends Application {

    public static final String TMD_API_KEY = "2936ba38eb50ad43a9116f484454fef0";
    public static final String TMD_BASE_URL = "http://api.themoviedb.org/3/";
    public static final String TMD_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private static final String CRITTERCISM_APPID = "pluuDPUxIMVm6ekHrxfeV3YDVyQAHqPo";

    public class YouTubeConfig {
        // Google Console APIs developer key
        public static final String DEVELOPER_KEY = "AIzaSyBQrxZTF4ZHCM9n3uoxP3TQWekXBT7rhbs";
    }

    public enum SortingOrder {

        MOST_POPULAR(1),
        HIGHEST_RATED(2),
        UPCOMING(3),
        FAVOURITES(4);
        private int value;

        private SortingOrder(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        initializeCrittercism();
    }

    private void initializeCrittercism() {
        Crittercism.initialize(getApplicationContext(), CRITTERCISM_APPID);
    }
}
