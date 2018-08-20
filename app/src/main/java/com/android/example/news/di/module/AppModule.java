package com.android.example.news.di.module;

import android.app.Application;

import com.android.example.news.api.RetrofitService;
import com.android.example.news.utils.LiveDataCallAdapterFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.example.news.utils.Constants.API_ENDPOINT;
import static com.android.example.news.utils.Constants.SSL_PROTOCOL;

/**
 * Helper class for providing the Application module
 */

// module used by this module
@Module(includes = ViewModelModule.class)
public class AppModule {

    /**
     * Provides HTTP Cache
     *
     * @param application Application context
     * @return a {@link Cache} object
     */
    @Provides
    // should be created only once
    @Singleton
    public Cache provideHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(application.getCacheDir(), cacheSize);
    }

    /**
     * Provide Gson
     *
     * @return {@link Gson} object
     */
    @Provides
    // should be created only once
    @Singleton
    public Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }

    /**
     * Provides the HTTP client
     *
     * @param cache The cache that should be used
     * @return {@link OkHttpClient} object
     */
    @Provides
    // should be created only once
    @Singleton
    public OkHttpClient provideOkhttpClient(Cache cache) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        // interceptor for logging
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(logInterceptor);
        // bypass servers without trusted certificates, most probably those which are not secured (http)
        SSLSocketFactory factory = getSSLSocketFactory();
        if (factory != null) {
            client.sslSocketFactory(factory, getTrustManager());
        }
        client.hostnameVerifier((hostname, session) ->
                true
        );
        return client.build();
    }

    /**
     * Provide the {@link Retrofit} object
     *
     * @param gson         used by {@link GsonConverterFactory}
     * @param okHttpClient need for {@link Retrofit}
     * @return {@link Retrofit} object
     */

    @Provides
    @Singleton
    public RetrofitService provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .baseUrl(API_ENDPOINT)
                .client(okHttpClient)
                .build();
        return retrofit.create(RetrofitService.class);
    }

    /**
     * Helper method for bypassing servers without trusted certificates
     *
     * @return {@link SSLSocketFactory}
     */
    private SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            return sslContext.getSocketFactory();

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Helper method for providing {@link X509TrustManager}
     *
     * @return {@link X509TrustManager}
     */
    private X509TrustManager getTrustManager() {
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (trustManagerFactory != null) {
            try {
                trustManagerFactory.init((KeyStore) null);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }

            return (X509TrustManager) trustManagers[0];
        }
        return null;
    }
}
