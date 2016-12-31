package com.example.android.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.Trailer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chuningluo on 12/31/16.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailerViewHolder> {

    List<Trailer> trailerList;

    final private TrailersAdapterOnclickHandler mClickHandler;

    public TrailersAdapter(TrailersAdapterOnclickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    public interface TrailersAdapterOnclickHandler {
        void onClick(String key);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View trailerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_cell, parent, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(trailerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (trailerList == null) return 0;
        return trailerList.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.trailer_title)
        TextView title;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bind(int idx) {
            title.setText(trailerList.get(idx).getName());
        }

        @Override
        public void onClick(View view) {
            int adapterPos = getAdapterPosition();
            String movieKey = trailerList.get(adapterPos).getKey();
            mClickHandler.onClick(movieKey);
        }
    }

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
        notifyDataSetChanged();
    }

}
