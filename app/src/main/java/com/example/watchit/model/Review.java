package com.example.watchit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Megha on 7/1/2016.
 */
public class Review implements Parcelable {


    private String author;
    private String content;
    private String url;
    private String id;

    public Review(String author, String content, String url, String id) {
        this.author = author;
        this.content = content;
        this.url = url;
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Review(Parcel in) {
        String[] data = new String[4];

        in.readStringArray(data);
        this.author = data[0];
        this.content = data[1];
        this.url = data[2];
        this.id = data[3];

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.author,
                this.content,
                this.url, this.id});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
