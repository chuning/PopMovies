package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by chuningluo on 1/1/17.
 */

public class MovieProvider extends ContentProvider {
    public static final int CODE_POPULAR_MOVIE = 100;
    public static final int CODE_POPULAR_MOVIE_WITH_ID = 101;
    public static final int CODE_TOP_RATED_MOVIE = 200;
    public static final int CODE_TOP_RATED_MOVIE_WITH_ID = 201;
    public static final int CODE_MY_FAVORITE_MOVIE = 300;
    public static final int CODE_MY_FAVORITE_MOVIE_WITH_ID = 301;

    private MovieDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.MovieEntry.POPULAR_MOVIE_TABLE, CODE_POPULAR_MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.POPULAR_MOVIE_TABLE + "/#", CODE_POPULAR_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE, CODE_TOP_RATED_MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE + "/#", CODE_TOP_RATED_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE, CODE_MY_FAVORITE_MOVIE);
        matcher.addURI(authority, MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE + "/#", CODE_MY_FAVORITE_MOVIE_WITH_ID);
        return matcher;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor;
        String table;
        boolean queryWithId = false;
        int matchCode = sUriMatcher.match(uri);

        switch (matchCode) {
            case CODE_POPULAR_MOVIE:
                table = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
                break;
            case CODE_POPULAR_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
                queryWithId = true;
                break;
            case CODE_TOP_RATED_MOVIE:
                table = MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
                break;
            case CODE_TOP_RATED_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
                queryWithId = true;
                break;
            case CODE_MY_FAVORITE_MOVIE:
                table = MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE;
                break;
            case CODE_MY_FAVORITE_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE;
                queryWithId = true;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (queryWithId) {
            String movieId = uri.getLastPathSegment();
            String[] selectionArguments = new String[]{movieId};
            cursor = db.query(
                    table,
                    projection,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " =? ",
                    selectionArguments,
                    null,
                    null,
                    sortOrder
            );
        } else {
            cursor = db.query(
                    table,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_MY_FAVORITE_MOVIE:
                db.insert(MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE, null, contentValues);
                break;
            default:
                throw new UnsupportedOperationException("insert fail");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String table;
        switch (sUriMatcher.match(uri)) {
            case CODE_POPULAR_MOVIE:
                table = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
                break;
            case CODE_TOP_RATED_MOVIE:
                table = MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        db.beginTransaction();
        int rowsInserted = 0;
        try {
            for (ContentValues cv : values) {
                long _id = db.insert(table, null, cv);
                if (_id != -1) {
                    rowsInserted++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numRowsDeleted;
        if (selection == null) selection = "1";

        String table;
        int matchCode = sUriMatcher.match(uri);

        switch (matchCode) {
            case CODE_POPULAR_MOVIE:
                table = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
                break;
            case CODE_POPULAR_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
                break;
            case CODE_TOP_RATED_MOVIE:
                table = MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
                break;
            case CODE_TOP_RATED_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
                break;
            case CODE_MY_FAVORITE_MOVIE:
                table = MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE;
                break;
            case CODE_MY_FAVORITE_MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                table, selection, selectionArgs);
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
