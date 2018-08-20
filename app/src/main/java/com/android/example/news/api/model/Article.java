package com.android.example.news.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


/**
 * Model class representing an article
 */
public class Article implements Serializable {

    @SerializedName(value = "url")
    private String url;
    @SerializedName(value = "title")
    private String title;
    @SerializedName(value = "byline")
    private String byline;
    @SerializedName(value = "published_date")
    private String published_date;
    @SerializedName(value = "media")
    private List<Media> mediaList;

    public Article(String url, String title, List<Media> media) {
        this.url = url;
        this.title = title;
        this.mediaList = media;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return byline;
    }

    public String getDate() {
        return published_date;
    }

    public List<Media> getMedia() {
        return mediaList;
    }
}
