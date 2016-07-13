package com.example.watchit.data;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;

/**
 * Created by Megha on 7/5/2016.
 */

@ContentProvider(authority = WatchItDB.AUTHORITY, database = WatchItDB.class, baseContentUri = WatchItDB.BASE_CONTENT_URI)
@Database(name = WatchItDB.NAME, version = WatchItDB.VERSION)
public class WatchItDB {

    public static final String NAME = "watch_it";
    public static final int VERSION = 1;
    public static final String AUTHORITY = "com.example.watchit";
    public static final String BASE_CONTENT_URI = "content://";
}
