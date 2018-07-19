package com.example.khale.popularmovies_stage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> movies;
    public final static String IMAGE_SIZE = "w500";

   // public ImageAdapter(Context context){
    //    mContext = context;
   // }
   public final static String base_url = "http://image.tmdb.org/t/p/w185";
   String LOG_TAG = "ImageAdapter";

   public MovieAdapter(Context context, List<Movie> movies) {
       mContext = context;
       this.movies = movies;
   }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setData(List<Movie> data){
       clear();
       for(Movie movie : data){
           movies.add(movie);
       }
    }

    public View getView(int position, View view, ViewGroup parent){
        Movie movie = getItem(position);
        ImageView imageView;

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageView = (ImageView) inflater.inflate(R.layout.movie, parent,false);
            imageView.setPadding(0, 0, 0, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) view;
        }

        String url = new StringBuilder().append(base_url)
                .append(movie.getImagePath().trim()).toString();

        //Log.v("ImageAdapter", "Image added to view from adapter" + images.size() + " " + pos);
        Picasso.with(mContext).load(url).into(imageView);
        return imageView;
    }

    void clear(){
       if (movies.size() > 0 ){
           movies.clear();
       }
    }

}
