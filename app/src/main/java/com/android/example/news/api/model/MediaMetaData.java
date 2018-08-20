package com.android.example.news.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model class representing an MediaMetaData object which is a member of an {@link Media} object
 */
public class MediaMetaData implements Serializable {

    @SerializedName(value = "url")
    private String url;

    public String getUrl() {
        return url;
    }
}
