package com.android.example.news.ui.common;

import android.support.v4.app.FragmentManager;

import com.android.example.news.MainActivity;
import com.android.example.news.R;
import com.android.example.news.ui.NewsListFragment;
import com.android.example.news.utils.Constants;

import javax.inject.Inject;

/**
 * A utility class that handles navigation in {@link MainActivity}.
 */
public class NavigationController {
    private final int containerId;
    private final FragmentManager fragmentManager;

    @Inject
    public NavigationController(MainActivity mainActivity) {
        // id of the view holding the fragment content
        this.containerId = R.id.content_frame;
        this.fragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void navigateToNewsList() {
        NewsListFragment newsListFragment = new NewsListFragment();
        fragmentManager.beginTransaction()
                .replace(containerId, newsListFragment, Constants.TAG_FRAGMENT_NEWS_LIST)
                .commitAllowingStateLoss();
    }
}
