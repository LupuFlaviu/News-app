package com.android.example.news.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.android.example.news.di.ViewModelKey;
import com.android.example.news.ui.NewsViewModel;
import com.android.example.news.viewmodel.NewsViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Helper class for binding custom {@link ViewModelProvider.Factory} and ViewModels
 */
@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    // specifies class to bind
    @ViewModelKey(NewsViewModel.class)
    abstract ViewModel bindNewsViewModel(NewsViewModel newsViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(NewsViewModelFactory factory);
}
