package com.example.android.popularmovies;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.data.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chuningluo on 1/1/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private Context context;
    private final static int ANIMATION_DURATION = 200;
    private final static int MAX_LINE = 10;

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public ReviewsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewsAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_cell, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (reviews == null) {
            return 0;
        }
        return reviews.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_author)
        TextView authorTextView;

        @BindView(R.id.review_content)
        TextView contentTextView;

        @BindView(R.id.show_more_or_less)
        TextView showMoreOrLess;

        boolean isExpanded = false;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contentTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (contentTextView.getLineCount() >= MAX_LINE) {
                        showMoreOrLess.setVisibility(View.VISIBLE);
                        showMoreOrLess.setOnClickListener(new View.OnClickListener() {
                            @Override

                            public void onClick(View view) {
                                if (isExpanded) {
                                    showLess();
                                } else {
                                    showMore();
                                }
                                isExpanded = !isExpanded;
                            }
                        });
                    }
                }
            }, 100L);
        }

        private void showLess() {
            contentTextView.setMaxLines(MAX_LINE);
            contentTextView.setEllipsize(TextUtils.TruncateAt.END);
            showMoreOrLess.setText(context.getString(R.string.show_more));
            ObjectAnimator animation = ObjectAnimator.ofInt(contentTextView, "maxLines", MAX_LINE);
            animation.setDuration(ANIMATION_DURATION).start();
        }

        private void showMore() {
            contentTextView.setMaxLines(Integer.MAX_VALUE);
            contentTextView.setEllipsize(null);
            showMoreOrLess.setText(context.getString(R.string.show_less));
            ObjectAnimator animation = ObjectAnimator.ofInt(contentTextView, "maxLines", ANIMATION_DURATION);
            animation.setDuration(ANIMATION_DURATION).start();
        }

        public void bind(int ind) {
            Review review = reviews.get(ind);
            authorTextView.setText(review.getAuthor());
            contentTextView.setText(review.getContent());
        }
    }
}
