package com.example.android.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by chuningluo on 1/1/17.
 */

public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {
        public static final String POPULAR_MOVIE_TABLE = "popular_movie";
        public static final String TOP_RATED_MOVIE_TABLE = "top_rated_movie";
        public static final String MY_FAVORITE_MOVIE_TABLE = "my_favorite_movie";

        public static final Uri POPULAR_MOVIE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(POPULAR_MOVIE_TABLE)
                .build();

        public static final Uri TOP_RATED_MOVIE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(TOP_RATED_MOVIE_TABLE)
                .build();

        public static final Uri MY_FAVORITE_MOVIE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(MY_FAVORITE_MOVIE_TABLE)
                .build();

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_ORIGIN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_avg";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildUriWithMovieId(String id, String table) {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(table)
                    .appendPath(id)
                    .build();
        }
    }
}
