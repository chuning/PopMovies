package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chuningluo on 12/21/16.
 */

public class Movie implements Parcelable{
    private String id;
    private String url;
    private String originTitle;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public Movie() {

    }

    protected Movie(Parcel in) {
        id = in.readString();
        url = in.readString();
        originTitle = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginTitle() {
        return originTitle;
    }

    public void setOriginTitle(String originTitle) {
        this.originTitle = originTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(url);
        parcel.writeString(originTitle);
        parcel.writeString(overview);
        parcel.writeString(voteAverage);
        parcel.writeString(releaseDate);
    }

    public static ContentValues getMovieContentValues(String id, String url, String voteAverage, String overview, String title, String releaseDate) {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        cv.put(MovieContract.MovieEntry.COLUMN_URL, url);
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGIN_TITLE, title);
        cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);
        return cv;
    }
}
