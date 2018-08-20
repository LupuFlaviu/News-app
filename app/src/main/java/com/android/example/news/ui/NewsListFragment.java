package com.android.example.news.ui;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.example.news.R;
import com.android.example.news.api.model.Article;
import com.android.example.news.databinding.FragmentNewsListBinding;
import com.android.example.news.di.Injectable;
import com.android.example.news.utils.NetworkUtils;
import com.android.example.news.utils.PackageUtils;

import javax.inject.Inject;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Fragment holding the news list
 */
public class NewsListFragment extends Fragment implements NewsListAdapter.OnSelectedNewsListener, Injectable {

    private RecyclerView.Adapter mAdapter;
    private NewsViewModel mViewModel;
    @Inject
    public ViewModelProvider.Factory mViewModelFactory;
    FragmentNewsListBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // enable overflow menu when this fragment is shown
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(NewsViewModel.class);
        Activity activity = getActivity();
        if (activity != null) {
            if (NetworkUtils.checkNetworkAndShowError(activity)) {
                mViewModel.init();
                mViewModel.getArticleList().observe(this, articles -> {
                    if (articles != null) {
                        if (articles.data != null) {
                            mAdapter = new NewsListAdapter(articles.data.getArticleList(), this);
                            mBinding.recyclerView.setAdapter(mAdapter);
                            mBinding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
                mViewModel.getErrorMessage().observe(this, errorMessage ->
                        Toast.makeText(getActivity(), errorMessage, LENGTH_SHORT).show()
                );
            } else {
                // close app after toast disappears
                new CountDownTimer(2000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        getActivity().finish();
                    }
                }.start();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_list, container, false);
        // use a linear layout manager
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // initialize SearchView
        Activity activity = getActivity();
        if (activity != null) {
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView =
                    (SearchView) searchItem.getActionView();
            searchView.setActivated(true);
            searchView.setQueryHint(getString(R.string.search_hint));
            searchView.onActionViewExpanded();
            searchView.setIconified(false);
            searchView.clearFocus();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // filter list after each character typed
                    ((NewsListAdapter) mAdapter).getFilter().filter(newText);
                    return false;
                }
            });
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // hide keyboard when closing SearchView
                    searchView.clearFocus();
                    return true;
                }
            });
        }
    }

    @Override
    public void onNewsSelected(Article article) {
        Activity activity = getActivity();
        if (activity != null) {
            if (NetworkUtils.checkNetworkAndShowError(activity)) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                // check if there is a browser application, otherwise our application will crash
                if (PackageUtils.isAvailable(activity, browserIntent)) {
                    startActivity(browserIntent);
                } else {
                    Toast.makeText(activity, getString(R.string.package_not_available), LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Refresh the list of news
     *
     * @param period period for retrieving the article list
     */
    public void refresh(int period) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        Activity activity = getActivity();
        if (activity != null) {
            if (NetworkUtils.checkNetworkAndShowError(activity)) {
                mViewModel.refreshArticleList(period);
            } else {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public static NewsListFragment create() {
        return new NewsListFragment();
    }
}
