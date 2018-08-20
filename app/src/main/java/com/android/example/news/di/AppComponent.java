package com.android.example.news.di;

import android.app.Application;

import com.android.example.news.NewsApp;
import com.android.example.news.di.module.AppModule;
import com.android.example.news.di.module.MainActivityModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

/**
 * Interface for creating the application component and providing the {@link NewsApp} object
 */

// should have only one instance
@Singleton
// modules used by this component
@Component(modules = {
        AndroidInjectionModule.class,
        AppModule.class,
        MainActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

    void inject(NewsApp newsApp);
}
