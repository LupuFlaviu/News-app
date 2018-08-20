package com.android.example.news.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Model class representing a Media object which is a member of an {@link Article} object
 */
public class Media implements Serializable {

    @SerializedName(value = "media-metadata")
    private List<MediaMetaData> mediaMetaData;

    public Media(List<MediaMetaData> mediaMetaData) {
        this.mediaMetaData = mediaMetaData;
    }

    public List<MediaMetaData> getMediaMetaData() {
        return mediaMetaData;
    }
}
