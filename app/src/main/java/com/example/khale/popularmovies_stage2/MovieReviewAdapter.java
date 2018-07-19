package com.example.khale.popularmovies_stage2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MovieReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<MovieReview> reviews = new ArrayList<>();
    //Constructor
    public MovieReviewAdapter(Context context, ArrayList<MovieReview> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    public void add(MovieReview review){
        reviews.add(review);
        notifyDataSetChanged();
    }

    public void clear(){
        reviews.clear();
        notifyDataSetChanged();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        public TextView tvReview;
        public TextView tvAuthor;

        public MovieViewHolder (View itemView){
            super(itemView);
            tvReview = (TextView) itemView.findViewById(R.id.tv_review_content);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review, parent, false);

        viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ((MovieViewHolder) holder).tvReview.setText(reviews.get(position).getContent());
        ((MovieViewHolder) holder).tvAuthor.setText(reviews.get(position).getAuthor());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}
