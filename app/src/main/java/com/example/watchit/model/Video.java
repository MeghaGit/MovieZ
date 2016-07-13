package com.example.watchit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Megha on 6/30/2016.
 */
public class Video implements Parcelable {

    private String key;
    private String site;
    private String name;

    public Video(String key, String site, String name) {
        this.key = key;
        this.site = site;
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Video(Parcel in) {
        String[] data = new String[3];

        in.readStringArray(data);
        this.key = data[0];
        this.site = data[1];
        this.name = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.key,
                this.site,
                this.name});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}

