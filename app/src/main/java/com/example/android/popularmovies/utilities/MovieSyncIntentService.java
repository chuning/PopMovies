package com.example.android.popularmovies.utilities;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by chuningluo on 1/5/17.
 */

public class MovieSyncIntentService extends IntentService {
    public MovieSyncIntentService() {
        super("Movie Intent Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra(Intent.EXTRA_SUBJECT)) {
            String endpoint = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            FetchMovieTask.fetch(this, endpoint);
        }
    }
}
