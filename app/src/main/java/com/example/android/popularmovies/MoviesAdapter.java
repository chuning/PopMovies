package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by chuningluo on 12/21/16.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private MovieOnClickHandler movieOnClickHandler;
    private Cursor mCursor;
    private int loaderId;

    public interface MovieOnClickHandler {
        void onClick(String movieId);
    }

    public MoviesAdapter(MovieOnClickHandler handler, int loaderId) {
        movieOnClickHandler = handler;
        this.loaderId = loaderId;
    }

    @Override
    public MoviesAdapter.MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_cell, parent, false);
        MoviesViewHolder viewHolder = new MoviesViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView moviePoster;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.poster);
            itemView.setOnClickListener(this);
        }

        public void bind(int listIdx) {
            mCursor.moveToPosition(listIdx);
            String url = mCursor.getString(MainActivity.IDX_URL);
            String imageUrl = NetworkUtils.buildImageUrl(url);
            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .into(moviePoster);
        }

        @Override
        public void onClick(View view) {
            int idx = getAdapterPosition();
            mCursor.moveToPosition(idx);
            String movieId = mCursor.getString(MainActivity.IDX_MOVIE_ID);
            movieOnClickHandler.onClick(movieId);
        }
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public int getLoaderId() {
        return loaderId;
    }
}
