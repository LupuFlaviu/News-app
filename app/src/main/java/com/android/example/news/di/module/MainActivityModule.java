package com.android.example.news.di.module;

import com.android.example.news.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Helper class for providing MainActivity
 */
@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}
