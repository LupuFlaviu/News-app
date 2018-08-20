package com.android.example.news.ui;

import android.app.Activity;
import android.app.Instrumentation;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.android.example.news.R;
import com.android.example.news.api.model.Article;
import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.testing.SingleFragmentActivity;
import com.android.example.news.util.EspressoTestUtil;
import com.android.example.news.util.RecyclerViewMatcher;
import com.android.example.news.util.TaskExecutorWithIdlingResourceRule;
import com.android.example.news.util.TestUtil;
import com.android.example.news.util.ViewModelUtil;
import com.android.example.news.vo.Resource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewsFragmentTest {
    @Rule
    public IntentsTestRule<SingleFragmentActivity> mActivityRule =
            new IntentsTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule mExecutorRule =
            new TaskExecutorWithIdlingResourceRule();
    private MutableLiveData<Resource<NewsResponse>> mArticleList = new MutableLiveData<>();
    private MutableLiveData<String> mErrorMessage = new MutableLiveData<>();
    private NewsListFragment mNewsListFragment;
    private NewsViewModel mViewModel;
    private Article mArticle;
    private final String ARTICLE_TITLE = "Elon Musk Details ‘Excruciating’ Personal Toll of Tesla Turmoil";
    private final String ARTICLE_URL = "https://www.nytimes.com/2018/08/16/business/elon-musk-interview-tesla.html";


    @Before
    public void init() {
        EspressoTestUtil.disableProgressBarAnimations(mActivityRule);
        mNewsListFragment = NewsListFragment.create();
        mViewModel = mock(NewsViewModel.class);
        mArticle = mock(Article.class);
        when(mViewModel.getArticleList()).thenReturn(mArticleList);
        when(mViewModel.getErrorMessage()).thenReturn(mErrorMessage);
        when(mArticle.getUrl()).thenReturn("http://www.nytimes.com");

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
        NewsResponse newsResponse = TestUtil.createNewsResponse(ARTICLE_URL, ARTICLE_TITLE);
        this.mArticleList.postValue(Resource.success(newsResponse));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        this.mArticleList.postValue(Resource.loading(newsResponse));
        onView(withId(R.id.text_title)).check(matches(withText(ARTICLE_TITLE)));
    }

    @Test
    public void testLoaded() {
        NewsResponse newsResponse = TestUtil.createNewsResponse(ARTICLE_URL, ARTICLE_TITLE);
        this.mArticleList.postValue(Resource.success(newsResponse));
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())));
        onView(withId(R.id.text_title)).check(matches(withText(ARTICLE_TITLE)));
    }

    @Test
    @Ignore
    public void testError() {
        mArticleList.postValue(Resource.error("Error", null));
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()));
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
        setArticles(ARTICLE_URL, "a", "b", "c");
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
        setArticles(ARTICLE_URL, "a", "b", "c");
        ViewInteraction textView = onView(
                allOf(withId(R.id.text_title), withText("a"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recycler_view),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText("a")));
        Matcher<Intent> expectedIntent = allOf(hasAction(Intent.ACTION_VIEW));
        intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        onView(withId(R.id.recycler_view)).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(expectedIntent);
    }

    @Test
    public void testEmptyList() {
        this.mArticleList.postValue(null);
        onView(withId(R.id.recycler_view)).check(matches(not(hasDescendant(withText(anyString())))));
    }

    @Test
    @Ignore
    public void testNullArticles() {
        setArticles(ARTICLE_URL, "a", "b", "c");
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("a"))));
        mArticleList.postValue(null);
        onView(withId(R.id.recycler_view)).check(matches(not(hasDescendant(withText(anyString())))));
    }

    private void setArticles(String url, String... titles) {
        this.mArticleList.postValue(Resource.success(TestUtil.createNewsResponse(url, titles)));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}