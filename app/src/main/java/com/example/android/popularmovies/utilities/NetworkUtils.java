package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by chuningluo on 12/21/16.
 */

public final class NetworkUtils {
    private static final String BASE_URL= "http://api.themoviedb.org/3/movie/";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String API_KEY = "api_key";
    //TODO: insert API key
    private static final String API_KEY_VALUE = "";

    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String VIDEOS = "videos";
    public static final String REVIEWS = "reviews";

    public static URL buildUrl(String endpoint) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(endpoint)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildUrl(String id, String endpoint) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(id)
                .appendPath(endpoint)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                String response = scanner.next();
                Log.v(TAG, "Response " + response);
                return response;
            } else {
                Log.v(TAG, "No Response");
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String buildImageUrl(String url) {
        return IMAGE_BASE_URL + url;
    }

    public static Uri buildVideoPlayUrl(String key) {
        String videoPlayString =  VIDEO_BASE_URL + key;
        return Uri.parse(videoPlayString);
    }
}
