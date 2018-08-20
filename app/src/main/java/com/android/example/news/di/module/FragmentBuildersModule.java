package com.android.example.news.di.module;

import com.android.example.news.ui.NewsListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Helper class for providing different fragments
 */
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract NewsListFragment contributeNewsListFragment();
}

