package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by chuningluo on 1/5/17.
 */

public class SyncUtils {
    private static boolean sInitialized;

    public static void initialize(@NonNull final Context context) {
        if (sInitialized) return;
        sInitialized = true;
        startImmediateSync(context);
    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncPopularMovies = new Intent(context, MovieSyncIntentService.class);
        intentToSyncPopularMovies.putExtra(Intent.EXTRA_SUBJECT, NetworkUtils.POPULAR);
        context.startService(intentToSyncPopularMovies);

        Intent intentToSyncTopRatedMovies = new Intent(context, MovieSyncIntentService.class);
        intentToSyncTopRatedMovies.putExtra(Intent.EXTRA_SUBJECT, NetworkUtils.TOP_RATED);
        context.startService(intentToSyncTopRatedMovies);
    }
}
