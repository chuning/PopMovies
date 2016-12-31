package com.example.android.popularmovies.utilities;

import android.content.ContentValues;

import com.example.android.popularmovies.data.Movie;
import com.example.android.popularmovies.data.Review;
import com.example.android.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chuningluo on 12/21/16.
 */

public class ParserUtils {
    static final String RESULTS = "results";
    static final String POSTER_PATH = "poster_path";
    static final String ORIGINAL_TITLE = "original_title";
    static final String OVERVIEW = "overview";
    static final String VOTE_AVG = "vote_average";
    static final String RELEASE_DATE = "release_date";
    static final String KEY = "key";
    static final String NAME = "name";
    static final String ID = "id";
    static final String AUTHOR = "author";
    static final String CONTENT = "content";
    static final String URL = "url";

    public static ContentValues[] getMoviesContentValuesFromJson(String moviesJsonStr)
            throws JSONException {
        JSONObject moviesJson = new JSONObject(moviesJsonStr);

        JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);

        ContentValues[] movieContentValues = new ContentValues[moviesArray.length()];

        for (int i = 0; i < movieContentValues.length; i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            String id = movieObject.getString(ID);
            String posterUrl = movieObject.getString(POSTER_PATH);
            String title = movieObject.getString(ORIGINAL_TITLE);
            String overview = movieObject.getString(OVERVIEW);
            String rating = movieObject.getString(VOTE_AVG);
            String releaseDate = movieObject.getString(RELEASE_DATE);
            movieContentValues[i] = Movie.getMovieContentValues(id, posterUrl, rating, overview, title, releaseDate);
        }
        return movieContentValues;
    }

    public static List<Trailer> getTrailerListFromJson(String trailersJsonStr)
            throws JSONException {
        List<Trailer> trailers = new ArrayList<>();

        JSONObject trailersJson = new JSONObject(trailersJsonStr);

        JSONArray trailersArray = trailersJson.getJSONArray(RESULTS);

        for (int i = 0; i < trailersArray.length(); i++) {
            Trailer trailer = new Trailer();
            JSONObject trailerObject = trailersArray.getJSONObject(i);
            String key = trailerObject.getString(KEY);
            String name = trailerObject.getString(NAME);
            trailer.setKey(key);
            trailer.setName(name);
            trailers.add(trailer);
        }
        return trailers;
    }

    public static List<Review> getReviewsListFromJson(String reviewsJsonStr)
            throws JSONException {
        List<Review> reviews = new ArrayList<>();

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);

        JSONArray reviewsArray = reviewsJson.getJSONArray(RESULTS);

        for (int i = 0; i < reviewsArray.length(); i++) {
            Review review = new Review();
            JSONObject trailerObject = reviewsArray.getJSONObject(i);
            String id = trailerObject.getString(ID);
            String author = trailerObject.getString(AUTHOR);
            String content = trailerObject.getString(CONTENT);
            String url = trailerObject.getString(URL);

            review.setId(id);
            review.setAuthor(author);
            review.setContent(content);
            review.setUrl(url);
            reviews.add(review);
        }
        return reviews;
    }

}
