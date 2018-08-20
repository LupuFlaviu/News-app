package com.android.example.news;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.example.news.databinding.ActivityNewsListBinding;
import com.android.example.news.ui.NewsListFragment;
import com.android.example.news.ui.common.NavigationController;
import com.android.example.news.utils.Constants;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import static com.android.example.news.utils.Constants.TAG_FRAGMENT_NEWS_LIST;

/**
 * Launcher activity
 */
public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;

    @Inject
    NavigationController mNavigationController;

    private ActivityNewsListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_list);
        // set toolbar as the ActionBar
        setSupportActionBar(mBinding.toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            // display left icon on toolbar and set the menu icon for it
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(R.string.toolbar_title);
        }
        mBinding.navView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mBinding.drawerLayout.closeDrawers();
                    switch (menuItem.getItemId()) {
                        case R.id.action_1_day:
                            refreshList(Constants.NEWS_PERIOD_1_DAY);
                            return true;
                        case R.id.action_7_days:
                            refreshList(Constants.DEFAULT_NEWS_PERIOD);
                            return true;
                        case R.id.action_30_days:
                            refreshList(Constants.NEWS_PERIOD_30_DAYS);
                    }
                    return true;
                });
        // if there is no saved instance, show the news fragment
        if (savedInstanceState == null) {
            mNavigationController.navigateToNewsList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_news_list_activtiy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawerLayout.openDrawer(GravityCompat.START);
                return false;
            case R.id.option_1_day:
                refreshList(Constants.NEWS_PERIOD_1_DAY);
                return false;
            case R.id.option_7_days:
                refreshList(Constants.DEFAULT_NEWS_PERIOD);
                return false;
            case R.id.option_30_days:
                refreshList(Constants.NEWS_PERIOD_30_DAYS);
                return false;
        }
        return false;
    }

    /**
     * Get news fragment and trigger a list refresh
     *
     * @param period period for retrieving the article list
     */
    private void refreshList(int period) {
        NewsListFragment fragment = (NewsListFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_NEWS_LIST);
        if (fragment != null) {
            fragment.refresh(period);
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mDispatchingAndroidInjector;
    }
}
