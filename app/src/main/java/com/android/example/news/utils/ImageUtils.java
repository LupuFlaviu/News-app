package com.android.example.news.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Helper class for working with images (download, load, edit, etc.)
 */

public class ImageUtils {

    public void loadImage(String url, ImageView imageView) {
        Picasso.get().load(url).into(imageView);
    }
}
