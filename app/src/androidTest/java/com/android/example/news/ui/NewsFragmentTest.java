package com.android.example.news.ui;

import android.app.Instrumentation;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.android.example.news.R;
import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.testing.SingleFragmentActivity;
import com.android.example.news.util.EspressoTestUtil;
import com.android.example.news.util.RecyclerViewMatcher;
import com.android.example.news.util.TaskExecutorWithIdlingResourceRule;
import com.android.example.news.util.TestUtil;
import com.android.example.news.util.ViewModelUtil;
import com.android.example.news.vo.Resource;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewsFragmentTest {
    @Rule
    public ActivityTestRule<SingleFragmentActivity> mActivityRule =
            new ActivityTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule mExecutorRule =
            new TaskExecutorWithIdlingResourceRule();
    private MutableLiveData<Resource<NewsResponse>> mArticleList = new MutableLiveData<>();
    private MutableLiveData<String> mErrorMessage = new MutableLiveData<>();
    private NewsListFragment mNewsListFragment;
    private NewsViewModel mViewModel;
    private final String ARTICLE_TITLE = "Elon Musk Details ‘Excruciating’ Personal Toll of Tesla Turmoil";


    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(mActivityRule);
        mNewsListFragment = NewsListFragment.create();
        mViewModel = mock(NewsViewModel.class);
        when(mViewModel.getArticleList()).thenReturn(mArticleList);
        when(mViewModel.getErrorMessage()).thenReturn(mErrorMessage);

        mNewsListFragment.mViewModelFactory = ViewModelUtil.createFor(mViewModel);
        mActivityRule.getActivity().setFragment(mNewsListFragment);
    }

    @Test
    public void testLoading() {
        mArticleList.postValue(Resource.loading(null));
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
    }

    @Test
    public void testValueWhileLoading() {
        NewsResponse newsResponse = TestUtil.createNewsResponse(ARTICLE_TITLE);
        this.mArticleList.postValue(Resource.loading(newsResponse));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_title)).check(matches(withText(ARTICLE_TITLE)));
    }

    @Test
    public void testLoaded() {
        NewsResponse newsResponse = TestUtil.createNewsResponse(ARTICLE_TITLE);
        this.mArticleList.postValue(Resource.success(newsResponse));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_title)).check(matches(withText(ARTICLE_TITLE)));
    }

    @Test
    public void testError() {
        mArticleList.postValue(Resource.error("Error", null));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withText(R.string.internet_connection_error)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        mArticleList.postValue(Resource.loading(null));

        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
        onView(withText(R.string.internet_connection_error)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(not(isDisplayed())));
        NewsResponse newsResponse = TestUtil.createNewsResponse(ARTICLE_TITLE);
        this.mArticleList.postValue(Resource.success(newsResponse));

        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withText(R.string.internet_connection_error)).inRoot(withDecorView(not(mActivityRule.getActivity().getWindow().getDecorView()))).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_title)).check(matches(withText(ARTICLE_TITLE)));
    }

    @Test
    public void testArticles() {
        setArticles("a", "b", "c");
        onView(listMatcher().atPosition(0))
                .check(matches(hasDescendant(withText("a"))));
        onView(listMatcher().atPosition(1))
                .check(matches(hasDescendant(withText("b"))));
        onView(listMatcher().atPosition(2))
                .check(matches(hasDescendant(withText("c"))));
    }

    @NonNull
    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view);
    }

    @Test
    public void testArticleClick() {
        setArticles("a", "b", "c");
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW), hasData(hasHost("www.nytimes.com")));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(0, null));
        onView(withText("c")).perform(click());
        intended(expectedIntent);
    }

    @Test
    public void testEmptyList() {
        this.mArticleList.postValue(null);
        onView(withId(R.id.text_title)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testNullArticles() {
        setArticles("a", "b", "c");
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("a"))));
        mArticleList.postValue(null);
        onView(listMatcher().atPosition(0)).check(doesNotExist());
    }

    private void setArticles(String... titles) {
        this.mArticleList.postValue(Resource.success(TestUtil.createNewsResponse(titles)));
    }
}