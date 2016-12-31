package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.utilities.ParserUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chuningluo on 12/21/16.
 */

public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,
        TrailersAdapter.TrailersAdapterOnclickHandler {
    @BindView(R.id.movie_title)
    TextView titleTextView;

    @BindView(R.id.overview)
    TextView overviewTextView;

    @BindView(R.id.detail_poster)
    ImageView posterImageView;

    @BindView(R.id.detail_rating)
    TextView ratingTextView;

    @BindView(R.id.detail_release_date)
    TextView releaseDateTextView;

    @BindView(R.id.trailers)
    RecyclerView trailersRecyclerView;

    @BindView(R.id.reviews)
    RecyclerView reviewsRecyclerView;

    @BindView(R.id.favorite_button)
    ImageButton favoriteButton;

    @BindView(R.id.trailers_loading_indicator)
    ProgressBar loadingView;

    @BindView(R.id.no_trailers)
    TextView noTrailersTextView;

    @BindView(R.id.no_reviews)
    TextView noReviewsTextView;

    private TrailersAdapter trailersAdapter;
    private ReviewsAdapter reviewsAdapter;
    private String movieId;
    private Uri mUri;
    private String url, title, overview, releaseDate, rating;
    private boolean isFavorite = false;

    public static final String[] MOVIE_DETAIL_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_URL,
            MovieContract.MovieEntry.COLUMN_ORIGIN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_URL = 1;
    public static final int INDEX_MOVIE_ORIGIN_TITLE = 2;
    public static final int INDEX_MOVIE_OVERVIEW = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_RELEASE_DATE = 5;

    private static final int MOVIE_DETAIL_LOADER = 100;
    private static final int TRAILER_LOADER = 101;
    private static final int REVIEWS_LOADER = 102;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        ButterKnife.bind(this);

        Intent movieIntent = getIntent();
        if (movieIntent != null) {
            if (movieIntent.hasExtra(Intent.EXTRA_SUBJECT)) {
                movieId = movieIntent.getStringExtra(Intent.EXTRA_SUBJECT);
                setUpFavoriteButton(this);

                showLoading();
                trailersAdapter = new TrailersAdapter(this);
                trailersRecyclerView.setAdapter(trailersAdapter);
                trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                trailersRecyclerView.setHasFixedSize(true);
                trailersRecyclerView.setNestedScrollingEnabled(false);
                getSupportLoaderManager().initLoader(TRAILER_LOADER, null, this);

                reviewsAdapter = new ReviewsAdapter(this);
                reviewsRecyclerView.setAdapter(reviewsAdapter);
                reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
                trailersRecyclerView.setNestedScrollingEnabled(false);
                getSupportLoaderManager().initLoader(REVIEWS_LOADER, null, this);
            }

            mUri = movieIntent.getData();
            if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, cursorLoaderCallbacks);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        if (menuId == R.id.share) {
            if (trailersAdapter.trailerList.isEmpty()) {
                return true;
            }
            String key = trailersAdapter.trailerList.get(0).getKey();
            Uri uri = NetworkUtils.buildVideoPlayUrl(key);
            String text = getString(R.string.share_movie_template, title, uri.toString());
            ShareCompat.IntentBuilder
                    .from(this)
                    .setChooserTitle(getString(R.string.share_movie_title))
                    .setText(text)
                    .setType("text/plain")
                    .startChooser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFavoriteButton(@NonNull final Context context) {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Uri queryFavoriteMovie = MovieContract.MovieEntry.buildUriWithMovieId(movieId, MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE);
                Cursor cursor = context.getContentResolver().query(
                        queryFavoriteMovie,
                        null,
                        null,
                        null,
                        null);
                if (cursor != null && cursor.getCount() > 0) {
                    isFavorite = true;
                }
                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isFavorite) {
                    setUpFavoriteButton(true);
                } else {
                    setUpFavoriteButton(false);
                }
            }
        }.execute();
    }

    private LoaderManager.LoaderCallbacks<Cursor> cursorLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case MOVIE_DETAIL_LOADER:
                    return new CursorLoader(MovieDetailActivity.this,
                            mUri,
                            MOVIE_DETAIL_PROJECTION,
                            null,
                            null,
                            null);
                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.moveToFirst()) {
                title = data.getString(INDEX_MOVIE_ORIGIN_TITLE);
                titleTextView.setText(title);

                overview = data.getString(INDEX_MOVIE_OVERVIEW);
                overviewTextView.setText(overview);

                url = data.getString(INDEX_MOVIE_URL);
                String imageUrl = NetworkUtils.buildImageUrl(url);
                Picasso.with(MovieDetailActivity.this)
                        .load(imageUrl)
                        .into(posterImageView);

                releaseDate = data.getString(INDEX_MOVIE_RELEASE_DATE);
                releaseDateTextView.setText(releaseDate);

                rating = data.getString(INDEX_MOVIE_VOTE_AVERAGE);
                ratingTextView.setText(getResources().getString(R.string.rating_template, rating));
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    @Override
    public Loader<String> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            protected void onStartLoading() {
                if (movieId == null) {
                    return;
                }
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String endpoint;
                switch (id) {
                    case TRAILER_LOADER:
                        endpoint = NetworkUtils.VIDEOS;
                        break;
                    case REVIEWS_LOADER:
                        endpoint = NetworkUtils.REVIEWS;
                        break;
                    default:
                        throw new RuntimeException("Loader Not Implemented: " + id);
                }
                try {
                    URL url = NetworkUtils.buildUrl(movieId, endpoint);
                    String results = NetworkUtils.getResponseFromHttpUrl(url);
                    return results;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) {
            showNoConnectionError();
            return;
        }
        switch (loader.getId()) {
            case TRAILER_LOADER:
                try {
                    List<Trailer> trailers = ParserUtils.getTrailerListFromJson(data);
                    showTrailers(trailers);
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case REVIEWS_LOADER:
                try {
                    List<Review> reviewList = ParserUtils.getReviewsListFromJson(data);
                    showReviews(reviewList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }
    }

    private void showNoConnectionError() {
        loadingView.setVisibility(View.GONE);
        noTrailersTextView.setText(getString(R.string.no_internet));
        noTrailersTextView.setVisibility(View.VISIBLE);
        noReviewsTextView.setText(getString(R.string.no_internet));
        noReviewsTextView.setVisibility(View.VISIBLE);
    }

    private void showTrailers(List<Trailer> trailers) {
        loadingView.setVisibility(View.GONE);
        if (trailers == null || trailers.isEmpty()) {
            noTrailersTextView.setVisibility(View.VISIBLE);
        } else {
            trailersAdapter.setTrailerList(trailers);
            trailersRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showReviews(List<Review> reviews) {
        loadingView.setVisibility(View.GONE);
        if (reviews == null || reviews.isEmpty()) {
            noReviewsTextView.setVisibility(View.VISIBLE);
        } else {
            reviewsAdapter.setReviews(reviews);
            reviewsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        reviewsAdapter.setReviews(null);
        trailersAdapter.setTrailerList(null);
    }

    @Override
    public void onClick(String key) {
        Uri uri = NetworkUtils.buildVideoPlayUrl(key);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void addToFavorite(View view) {
        if (!isFavorite) {
            addMovieToDb();
            setUpFavoriteButton(true);
        } else {
            removeMovieFromDb();
            setUpFavoriteButton(false);
        }
        isFavorite = !isFavorite;
    }

    private void setUpFavoriteButton(Boolean isLiked) {
        int colorId = isLiked ? R.color.colorAccent : R.color.background;
        favoriteButton.setColorFilter(getResources().getColor(colorId));
    }

    private void removeMovieFromDb() {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = " + movieId;
        getContentResolver().delete(MovieContract.MovieEntry.MY_FAVORITE_MOVIE_URI, selection, null);
    }

    private void addMovieToDb() {
        ContentValues cv = Movie.getMovieContentValues(movieId, url, rating, overview, title, releaseDate);
        getContentResolver().insert(MovieContract.MovieEntry.MY_FAVORITE_MOVIE_URI, cv);
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        trailersRecyclerView.setVisibility(View.INVISIBLE);
        reviewsRecyclerView.setVisibility(View.INVISIBLE);
    }
}
