package com.example.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.SyncUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.MovieOnClickHandler {
    @BindView(R.id.movies_grid)
    RecyclerView moviesGrid;

    @BindView(R.id.error_message_display)
    TextView mErrorMessageDisplay;

    @BindView(R.id.loading_indicator)
    ProgressBar mLoadingIndicator;

    private MoviesAdapter moviesAdapter;
    private boolean isInitialLoad = true;

    private static final int numberOfColumns = 2;
    private static final int POPULAR_MOVIE_LOADER = 10;
    private static final int TOP_RATED_MOVIE_LOADER = 11;
    private static final int MY_FAVORITE_LOADER = 12;

    public static final int IDX_MOVIE_ID = 0;
    public static final int IDX_URL = 1;
    private static final String PREVIOUS_LOADER_ID = "loaderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showLoading();

        int loaderId = POPULAR_MOVIE_LOADER;
        if (savedInstanceState != null) {
            loaderId = savedInstanceState.getInt(PREVIOUS_LOADER_ID);
        }
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        moviesGrid.setLayoutManager(layoutManager);
        moviesGrid.setHasFixedSize(true);
        moviesAdapter = new MoviesAdapter(this, loaderId);
        moviesGrid.setAdapter(moviesAdapter);

        SyncUtils.initialize(this);
        getSupportLoaderManager().initLoader(loaderId, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int preLoaderId = moviesAdapter.getLoaderId();
        outState.putInt(PREVIOUS_LOADER_ID, preLoaderId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        int loaderId = 0;
        switch (menuId) {
            case R.id.sortPopular:
                loaderId = POPULAR_MOVIE_LOADER;
                break;
            case R.id.sortHighestRate:
                loaderId = TOP_RATED_MOVIE_LOADER;
                break;
            case R.id.myFavorite:
                loaderId = MY_FAVORITE_LOADER;
                break;
        }
        item.setChecked(true);
        moviesAdapter = new MoviesAdapter(this, loaderId);
        moviesGrid.setAdapter(moviesAdapter);
        getSupportLoaderManager().initLoader(loaderId, null, this);
        return true;
    }

    @Override
    public void onClick(String movieId) {
        Intent intent = new Intent(this, MovieDetailActivity.class);
        Uri uri = MovieContract.MovieEntry.buildUriWithMovieId(movieId, getTableNameFromLoaderId(moviesAdapter.getLoaderId()));
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, movieId);
        startActivity(intent);
    }

    private String getTableNameFromLoaderId(int loaderId) {
        switch (loaderId) {
            case POPULAR_MOVIE_LOADER:
                return MovieContract.MovieEntry.POPULAR_MOVIE_TABLE;
            case TOP_RATED_MOVIE_LOADER:
                return MovieContract.MovieEntry.TOP_RATED_MOVIE_TABLE;
            case MY_FAVORITE_LOADER:
                return MovieContract.MovieEntry.MY_FAVORITE_MOVIE_TABLE;
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        switch (id) {
            case POPULAR_MOVIE_LOADER:
                uri = MovieContract.MovieEntry.POPULAR_MOVIE_URI;
                break;
            case TOP_RATED_MOVIE_LOADER:
                uri = MovieContract.MovieEntry.TOP_RATED_MOVIE_URI;
                break;
            case MY_FAVORITE_LOADER:
                uri = MovieContract.MovieEntry.MY_FAVORITE_MOVIE_URI;
                break;
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID, MovieContract.MovieEntry.COLUMN_URL};
        String sortOrder = MovieContract.MovieEntry._ID + " ASC";
        Log.d("network", " create loader " + id );
        return new CursorLoader(this, uri, projection, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("network", " loader id " + loader.getId());
        if (loader.getId() != moviesAdapter.getLoaderId()) {
            return;
        }
        if (data.getCount() == 0) {
            showErrorMessage(loader.getId());
        } else {
            moviesAdapter.swapCursor(data);
            showMoviesGrid();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }

    private void showLoading() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        moviesGrid.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showMoviesGrid() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesGrid.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage(int id) {
        if (id == MY_FAVORITE_LOADER) {
            mErrorMessageDisplay.setText(getString(R.string.no_favorite_movie));
        } else {
            if (isInitialLoad) {
                isInitialLoad = false;
                return;
            }
            mErrorMessageDisplay.setText(getString(R.string.error_message));
        }
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        moviesGrid.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
