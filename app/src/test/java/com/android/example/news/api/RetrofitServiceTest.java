/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.news.api;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import com.android.example.news.BuildConfig;
import com.android.example.news.api.model.NewsResponse;
import com.android.example.news.utils.Constants;
import com.android.example.news.utils.LiveDataCallAdapterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.android.example.news.util.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(JUnit4.class)
public class RetrofitServiceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private RetrofitService mService;

    private MockWebServer mockWebServer;

    @Before
    public void createService() {
        mockWebServer = new MockWebServer();
        mService = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(RetrofitService.class);
    }

    @After
    public void stopService() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getNews() throws IOException, InterruptedException {
        enqueueResponse("news.json");
        NewsResponse newsResponse = getValue(mService.getArticleList(Constants.ALL_SECTIONS, Constants.DEFAULT_NEWS_PERIOD, BuildConfig.ApiKey)).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/svc/mostpopular/v2/mostviewed/" + Constants.ALL_SECTIONS + "/" + Constants.DEFAULT_NEWS_PERIOD + ".json?api-key=" + BuildConfig.ApiKey));

        assertThat(newsResponse, notNullValue());
        assertThat(newsResponse.getArticleList().get(0).getTitle(), is("Elon Musk Details ‘Excruciating’ Personal Toll of Tesla Turmoil"));
        assertThat(newsResponse.getArticleList().get(1).getMedia().get(0).getMediaMetaData().get(7).getUrl(), is("https://static01.nyt.com/images/2018/08/14/nyregion/14nyu/00nyu-thumbLarge.jpg"));
        assertThat(newsResponse.getArticleList().get(2).getUrl(), is("https://www.nytimes.com/2018/08/14/us/catholic-church-sex-abuse-pennsylvania.html"));
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
