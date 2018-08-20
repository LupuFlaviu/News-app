package com.android.example.news.api;

import android.arch.lifecycle.LiveData;

import com.android.example.news.api.model.NewsResponse;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitService {

    /**
     * Method for retrieving the news list from the API
     *
     * @param section used for specifying which news sections should be included in the response (e.g. "all-sections")
     * @param period  used for specifying how old should the articles included in the response be (e.g. Possible values are {@value com.android.example.news.utils.Constants#DEFAULT_NEWS_PERIOD}
     *                {@value com.android.example.news.utils.Constants#NEWS_PERIOD_1_DAY}
     *                {@value com.android.example.news.utils.Constants#NEWS_PERIOD_30_DAYS}
     * @param key     used for specifying an API KEY
     * @return the list of articles
     */

    @Headers({"Accept: application/json",
            "Content-Type: application/json",
    })
    @GET("/svc/mostpopular/v2/mostviewed/{section}/{period}.json")
    LiveData<ApiResponse<NewsResponse>> getArticleList(@Path("section") String section, @Path("period") int period, @Query("api-key") String key);
}
