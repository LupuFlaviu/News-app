package com.android.example.news.ui;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.repo.NewsRepository;
import com.android.example.news.util.TestUtil;
import com.android.example.news.utils.Constants;
import com.android.example.news.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class NewsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private NewsRepository mRepository;
    private NewsViewModel mViewModel;

    @Before
    public void setup() {
        mRepository = mock(NewsRepository.class);
        mViewModel = new NewsViewModel(mRepository);
    }

    @Test
    public void testNull() {
        assertThat(mViewModel.getArticleList(), notNullValue());
        verify(mRepository, never()).getArticleList(anyInt());
    }

    @Test
    public void dontFetchWithoutObservers() {
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        verify(mRepository, never()).getArticleList(anyInt());
    }

    @Test
    public void fetchWhenObserved() {
        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);

        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        mViewModel.getArticleList().observeForever(mock(Observer.class));
        verify(mRepository, times(1)).getArticleList(period.capture());
        assertThat(period.getValue(), is(Constants.DEFAULT_NEWS_PERIOD));
    }

    @Test
    public void changeWhileObserved() {
        ArgumentCaptor<Integer> period = ArgumentCaptor.forClass(Integer.class);
        mViewModel.getArticleList().observeForever(mock(Observer.class));

        mViewModel.setPeriod(Constants.NEWS_PERIOD_1_DAY);
        mViewModel.setPeriod(Constants.NEWS_PERIOD_30_DAYS);

        verify(mRepository, times(2)).getArticleList(period.capture());
        assertThat(period.getAllValues(), is(Arrays.asList(Constants.NEWS_PERIOD_1_DAY, Constants.NEWS_PERIOD_30_DAYS)));
    }

    @Test
    public void testCallRepo() {
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        mViewModel.getArticleList().observeForever(mock(Observer.class));
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        verify(mRepository).getArticleList(captor.capture());
        assertThat(captor.getValue(), is(Constants.DEFAULT_NEWS_PERIOD));
        reset(mRepository);
        mViewModel.setPeriod(Constants.NEWS_PERIOD_30_DAYS);
        verify(mRepository).getArticleList(captor.capture());
        assertThat(captor.getValue(), is(Constants.NEWS_PERIOD_30_DAYS));
    }

    @Test
    public void sendResultToUI() {
        MutableLiveData<Resource<NewsResponse>> newsResponseDefault = new MutableLiveData<>();
        MutableLiveData<Resource<NewsResponse>> newsResponse1Day = new MutableLiveData<>();
        when(mRepository.getArticleList(Constants.DEFAULT_NEWS_PERIOD)).thenReturn(newsResponseDefault);
        when(mRepository.getArticleList(Constants.NEWS_PERIOD_1_DAY)).thenReturn(newsResponse1Day);
        Observer<Resource<NewsResponse>> observer = mock(Observer.class);
        mViewModel.getArticleList().observeForever(observer);
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        verify(observer, never()).onChanged(any(Resource.class));
        NewsResponse newsResponseFirst = TestUtil.createNewsResponse("a");
        Resource<NewsResponse> newsResponseFirstValue = Resource.success(newsResponseFirst);
        newsResponseDefault.setValue(newsResponseFirstValue);
        verify(observer).onChanged(newsResponseFirstValue);
        reset(observer);
        NewsResponse newsResponseSecond = TestUtil.createNewsResponse("b");
        Resource<NewsResponse> newsResponseSecondValue = Resource.success(newsResponseSecond);
        newsResponse1Day.setValue(newsResponseSecondValue);
        mViewModel.setPeriod(Constants.NEWS_PERIOD_1_DAY);
        verify(observer).onChanged(newsResponseSecondValue);
    }

    @Test
    public void nullNewsResponse() {
        Observer<Resource<NewsResponse>> observer = mock(Observer.class);
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        mViewModel.setPeriod(null);
        mViewModel.getArticleList().observeForever(observer);
        verify(observer).onChanged(null);
    }

    @Test
    public void dontRefreshOnSameData() {
        Observer<Integer> observer = mock(Observer.class);
        mViewModel.mPeriod.observeForever(observer);
        verifyNoMoreInteractions(observer);
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        verify(observer).onChanged(Constants.DEFAULT_NEWS_PERIOD);
        reset(observer);
        mViewModel.setPeriod(Constants.DEFAULT_NEWS_PERIOD);
        verifyNoMoreInteractions(observer);
        mViewModel.setPeriod(Constants.NEWS_PERIOD_30_DAYS);
        verify(observer).onChanged(Constants.NEWS_PERIOD_30_DAYS);
    }
}