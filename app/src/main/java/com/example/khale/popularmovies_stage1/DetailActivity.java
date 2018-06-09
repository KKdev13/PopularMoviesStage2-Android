package com.example.khale.popularmovies_stage1;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import static com.example.khale.popularmovies_stage1.ImageAdapter.base_url;



public class DetailActivity extends AppCompatActivity {

    private TextView title, userRating, release, plotSynopsis;
    private ImageView movieImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        title = (TextView) findViewById(R.id.title);
        userRating = (TextView) findViewById(R.id.user_rating);
        release = (TextView) findViewById(R.id.release_date);
        movieImage = (ImageView) findViewById(R.id.movie_image);
        plotSynopsis = (TextView) findViewById(R.id.plot);

        Movie movies = getIntent().getParcelableExtra("movie");

        if (movies != null){
            title.setText(movies.getTitle());
            userRating.setText("User Rating: " + movies.getVoteAverage());
            release.setText("Release Date: " + movies.getReleaseDate());
            plotSynopsis.setText("Description: " + movies.getOverview());
            loadImage(movies.getImagePath());
        }

    }



    private void loadImage(String path){
        String urlBuilder = new StringBuilder()
                .append(base_url)
                .append(path).toString();

        Picasso.with(getApplicationContext())
                .load(urlBuilder).into(movieImage);
    }

}
