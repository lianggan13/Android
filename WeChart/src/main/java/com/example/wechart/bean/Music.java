package com.example.wechart.bean;

import android.net.Uri;

// @data
public class Music {
    private String name;
    private String singer;
    private Uri uri;

    public Music(String name, String singer, Uri uri) {
        this.name = name;
        this.singer = singer;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
