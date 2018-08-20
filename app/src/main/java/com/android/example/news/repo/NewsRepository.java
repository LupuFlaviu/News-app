package com.android.example.news.repo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.example.news.AppExecutors;
import com.android.example.news.BuildConfig;
import com.android.example.news.api.ApiResponse;
import com.android.example.news.api.RetrofitService;
import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.vo.Resource;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.android.example.news.utils.Constants.ALL_SECTIONS;

/**
 * Helper class for getting news data
 */
// should be created only once
@Singleton
public class NewsRepository {

    private MutableLiveData<NewsResponse> mArticleList = new MutableLiveData<>();
    private final RetrofitService mRetrofitService;
    private MutableLiveData<String> mErrorMessage = new MutableLiveData<>();
    private final AppExecutors mAppExecutors;

   /* private Callback<NewsResponse> mArticleListCallback = new Callback<NewsResponse>() {
        @Override
        public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
            // if there is some kind of error, show it
            ResponseBody responseBody = response.errorBody();
            if (responseBody != null) {
                try {
                    mErrorMessage.setValue(responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mArticleList.setValue(Resource.success(response.body()));
            }
        }

        @Override
        public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
            mErrorMessage.setValue(t.getMessage());
        }
    };*/

    @Inject
    public NewsRepository(RetrofitService retrofitService, AppExecutors appExecutors) {
        mRetrofitService = retrofitService;
        mAppExecutors = appExecutors;
    }

    /**
     * Get article list on the specified period
     *
     * @param period period for retrieving the article list
     * @return {@link LiveData<Resource<NewsResponse>>} object
     */
    public LiveData<Resource<NewsResponse>> getArticleList(int period) {
        return new NetworkBoundResource<NewsResponse, NewsResponse>(mAppExecutors) {

            @Override
            protected void saveCallResult(@NonNull NewsResponse item) {
                mArticleList.setValue(item);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<NewsResponse>> createCall() {
                return mRetrofitService.getArticleList(ALL_SECTIONS, period, BuildConfig.ApiKey);
            }

            @NonNull
            @Override
            protected LiveData<NewsResponse> loadFromNetwork() {
                mArticleList.postValue(null);
                return mArticleList;
            }
        }.asLiveData();
    }

    public MutableLiveData<String> getErrorMessage() {
        return mErrorMessage;
    }
}
