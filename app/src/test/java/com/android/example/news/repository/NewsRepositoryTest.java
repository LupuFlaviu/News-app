package com.android.example.news.repository;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.android.example.news.BuildConfig;
import com.android.example.news.api.ApiResponse;
import com.android.example.news.api.RetrofitService;
import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.repo.NewsRepository;
import com.android.example.news.util.InstantAppExecutors;
import com.android.example.news.util.TestUtil;
import com.android.example.news.utils.Constants;
import com.android.example.news.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.android.example.news.util.ApiUtil.successCall;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class NewsRepositoryTest {
    private NewsRepository mRepository;
    private RetrofitService mService;
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        mService = mock(RetrofitService.class);
        mRepository = new NewsRepository(mService, new InstantAppExecutors());
    }

    @Test
    public void loadNewsFromNetwork() {

        NewsResponse newsResponse = TestUtil.createNewsResponse("a", "b", "c");
        LiveData<ApiResponse<NewsResponse>> call = successCall(newsResponse);
        when(mService.getArticleList(Constants.ALL_SECTIONS, Constants.DEFAULT_NEWS_PERIOD, BuildConfig.ApiKey)).thenReturn(call);

        LiveData<Resource<NewsResponse>> data = mRepository.getArticleList(Constants.DEFAULT_NEWS_PERIOD);
        verify(mService, times(1)).getArticleList(Constants.ALL_SECTIONS, Constants.DEFAULT_NEWS_PERIOD, BuildConfig.ApiKey);

        Observer observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(mService);
        verify(observer).onChanged(Resource.loading(null));
        verify(mService).getArticleList(Constants.ALL_SECTIONS, Constants.DEFAULT_NEWS_PERIOD, BuildConfig.ApiKey);
        verify(observer).onChanged(Resource.success(newsResponse));
    }
}