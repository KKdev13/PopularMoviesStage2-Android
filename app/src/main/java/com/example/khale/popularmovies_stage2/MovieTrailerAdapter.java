package com.example.khale.popularmovies_stage2;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieTrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<MovieTrailer> trailers = new ArrayList<>();

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    private OnItemClicked onClick;

    public MovieTrailerAdapter(Context context, ArrayList<MovieTrailer> trailers){
        this.context = context;
        this.trailers = trailers;
    }

    public void add (MovieTrailer trailer){
        trailers.add(trailer);
        notifyDataSetChanged();
    }

    public void clear(){
        trailers.clear();
        notifyDataSetChanged();
    }

    public MovieTrailer getItem(int position){
        return trailers.get(position);
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        public MovieViewHolder(View itemView){
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_trailer_thumbnail);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailer, parent, false);
        viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        String id = trailers.get(position).getKey();
        String trailerURL = "http://img.youtube.com/vi/".concat(id).concat("/hqdefault" + ".jpg");

        Picasso.with(context)
                .load(trailerURL)
                .placeholder(R.drawable.thumbnail)
                .into(((MovieViewHolder)holder).imageView);

        ((MovieViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public void setOnClick(OnItemClicked onClick){
        this.onClick = onClick;
    }
}
