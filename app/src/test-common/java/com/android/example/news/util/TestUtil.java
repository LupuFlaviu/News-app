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

package com.android.example.news.util;

import com.android.example.news.api.model.Article;
import com.android.example.news.api.model.Media;
import com.android.example.news.api.model.MediaMetaData;
import com.android.example.news.api.model.NewsResponse;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static List<Article> createArticleList(String url, String... titles) {
        List<Article> articleList = new ArrayList<>();
        for (String title : titles) {
            articleList.add(new Article(url, title, createMediaList()));
        }
        return articleList;
    }

    public static NewsResponse createNewsResponse(String url, String... titles) {
        return new NewsResponse(createArticleList(url, titles));
    }

    public static List<Media> createMediaList() {
        List<MediaMetaData> mediaMetaDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mediaMetaDataList.add(new MediaMetaData("https://static01.nyt.com/images/2018/08/14/nyregion/14nyu/00nyu-thumbLarge.jpg"));
        }
        List<Media> mediaList = new ArrayList<>();
        mediaList.add(new Media(mediaMetaDataList));
        return mediaList;
    }
}
