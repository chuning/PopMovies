package com.example.android.popularmovies.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;

import java.net.URL;

/**
 * Created by chuningluo on 1/5/17.
 */

public class FetchMovieTask {
    synchronized public static void fetch(Context context, String endpoint) {
        URL movieRequestUrl = NetworkUtils.buildUrl(endpoint);
        ContentResolver resolver = context.getContentResolver();
        Uri uri;
        if (endpoint.equals(NetworkUtils.POPULAR)) {
            uri = MovieContract.MovieEntry.POPULAR_MOVIE_URI;
        } else {
            uri = MovieContract.MovieEntry.TOP_RATED_MOVIE_URI;
        }
        try {
            String movieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            ContentValues[] movies = ParserUtils.getMoviesContentValuesFromJson(movieResponse);
            if (movies != null && movies.length != 0) {
                resolver.delete(uri, null, null);
                resolver.bulkInsert(uri, movies);
                Log.d("network", "fetch from online");
            }
        } catch (Exception e) {
            Log.d("network", uri.toString());
            context.getContentResolver().notifyChange(uri, null);
            e.printStackTrace();
        }
    }
}

