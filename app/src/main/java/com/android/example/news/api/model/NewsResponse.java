package com.android.example.news.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Model class representing a news response
 */
public class NewsResponse implements Serializable {

    @SerializedName(value = "results")
    private List<Article> articleList;

    public NewsResponse(List<Article> articleList) {
        this.articleList = articleList;
    }

    public List<Article> getArticleList() {
        return articleList;
    }
}
