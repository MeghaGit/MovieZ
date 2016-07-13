package com.example.watchit.model;

import android.net.Uri;

import com.example.watchit.data.WatchItDB;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Megha on 7/5/2016.
 */

@TableEndpoint(contentProviderName = "WatchItDB", name = Favourites.ENDPOINT)
@Table(database = WatchItDB.class)
public class Favourites extends BaseModel {


    public static final String ENDPOINT = "Favourites";

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = Uri.parse(WatchItDB.BASE_CONTENT_URI + WatchItDB.AUTHORITY).buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @ContentUri(path = Favourites.ENDPOINT, type = ContentUri.ContentType.VND_MULTIPLE + ENDPOINT)
    public static Uri CONTENT_URI = buildUri(ENDPOINT);

    @Column
    @PrimaryKey
    int id;

    @Column
    String title;

    @Column
    String poster_path;

    @Column
    String backdrop_path;

    @Column
    String plot;

    @Column
    String user_rating;

    @Column
    String release_date;

    public int getId() {
        return id;
    }

    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public String getUser_rating() {
        return user_rating;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public void setUser_rating(String user_rating) {
        this.user_rating = user_rating;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}
