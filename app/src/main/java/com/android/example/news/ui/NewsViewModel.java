package com.android.example.news.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.VisibleForTesting;

import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.repo.NewsRepository;
import com.android.example.news.utils.AbsentLiveData;
import com.android.example.news.utils.Constants;
import com.android.example.news.utils.Objects;
import com.android.example.news.vo.Resource;

import javax.inject.Inject;

public class NewsViewModel extends ViewModel {

    private final NewsRepository mRepo;
    private LiveData<Resource<NewsResponse>> mArticleList;
    @VisibleForTesting
    MutableLiveData<Integer> mPeriod = new MutableLiveData<>();

    @Inject
    public NewsViewModel(NewsRepository newsRepository) {
        this.mRepo = newsRepository;
    }

    @VisibleForTesting
    public void init() {
        mPeriod.setValue(Constants.DEFAULT_NEWS_PERIOD);
        mArticleList = Transformations.switchMap(mPeriod, period -> {
            if (period == null) {
                return AbsentLiveData.create();
            } else {
                return mRepo.getArticleList(period);
            }
        });
    }

    @VisibleForTesting
    public void setPeriod(Integer period) {
        if (Objects.equals(this.mPeriod.getValue(), period)) {
            return;
        }
        this.mPeriod.setValue(period);
    }

    @VisibleForTesting
    public LiveData<Resource<NewsResponse>> getArticleList() {
        return mArticleList;
    }

    @VisibleForTesting
    public void refreshArticleList(int period) {
        mPeriod.setValue(period);
    }

    @VisibleForTesting
    public LiveData<String> getErrorMessage() {
        return mRepo.getErrorMessage();
    }
}
