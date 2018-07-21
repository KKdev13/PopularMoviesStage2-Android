package com.example.khale.popularmovies_stage2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.example.khale.popularmovies_stage2.data.MovieContract;

public class DetailActivity extends AppCompatActivity implements MovieTrailerAdapter.OnItemClicked {

    private TextView title, userRating, release, plotSynopsis;
    private ImageView movieImage;
    private Movie movie;
    private MovieReviewAdapter reviewAdapter;
    private MovieTrailerAdapter trailerAdapter;
    private RecyclerView recyclerViewReview;
    private RecyclerView recyclerViewTrailer;
    private FloatingActionButton fbFavorite;
    private Context context;


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

        movie = getIntent().getParcelableExtra("movie");

        if (movie != null){
            title.setText(movie.getTitle());
            userRating.setText("User Rating: " + movie.getVoteAverage());
            release.setText("Release Date: " + movie.getReleaseDate());
            plotSynopsis.setText("Description: " + movie.getOverview());
            loadImage(movie.getImagePath());
        }

        fbFavorite = (FloatingActionButton) findViewById(R.id.fab);

        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);

        recyclerViewReview = (RecyclerView) findViewById(R.id.reviewsRecyclerView);
        recyclerViewTrailer = (RecyclerView)findViewById(R.id.trailersRecyclerView);

        recyclerViewReview.setLayoutManager(reviewLayoutManager);
        recyclerViewTrailer.setLayoutManager(trailerLayoutManager);

        reviewAdapter = new MovieReviewAdapter(this,new ArrayList<MovieReview>());
        recyclerViewReview.setAdapter(reviewAdapter);
        trailerAdapter = new MovieTrailerAdapter(this, new ArrayList<MovieTrailer>());
        recyclerViewTrailer.setAdapter(trailerAdapter);

        trailerAdapter.setOnClick(this);

        context = this;

    }



    private void loadImage(String path){
        String urlBuilder = new StringBuilder()
                .append(MovieAdapter.base_url)
                .append(path).toString();

        Picasso.with(getApplicationContext())
                .load(urlBuilder).into(movieImage);
    }

    @Override
    public void onItemClick(int position) {
        MovieTrailer trailer = trailerAdapter.getItem(position);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer .getKey()));
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(movie != null){
            new FetchReviews().execute(movie.getId());
            new FetchTrailer().execute(movie.getId());

            if(isFavorite(movie.getId()) == 1){
                fbFavorite.setImageResource(R.drawable.ic_star_black_24dp);
            } else {
                fbFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
            }
        }
    }


    public class FetchReviews extends AsyncTask<String, Void, List<MovieReview>>{

        private List<MovieReview> getReviewFromJson(String jsonString) throws JSONException{
            JSONObject reviewJson = new JSONObject(jsonString);
            JSONArray reviewJsonArray = reviewJson.getJSONArray("results");

            List<MovieReview> reviews = new ArrayList<>();

            for(int i = 0; i < reviewJsonArray.length(); i++){
                JSONObject review = reviewJsonArray.getJSONObject(i);
                reviews.add(new MovieReview(review));
            }

            return reviews;
        }


        @Override
        protected List<MovieReview> doInBackground(String... params) {
            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                final String base_url = "http://api.themoviedb.org/3/movie/" +
                        params[0] + "/reviews";

                Uri builtUri = Uri.parse(base_url)
                        .buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.myApi)
                        .build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                return getReviewFromJson(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(List<MovieReview> reviews) {
            if(reviews != null){
                if(reviews.size() > 0){
                    recyclerViewReview.setVisibility(View.VISIBLE);
                    if(reviewAdapter != null){
                        reviewAdapter.clear();
                        for (MovieReview review : reviews){
                            reviewAdapter.add(review);
                        }
                    }
                }
            }
        }
    }

    public class FetchTrailer extends AsyncTask<String, Void, List<MovieTrailer>>{

        private List<MovieTrailer> getTrailerFromJson(String jsonString) throws JSONException{

            JSONObject trailerJson = new JSONObject(jsonString);
            JSONArray trailerJsonArray = trailerJson.getJSONArray("results");

            List<MovieTrailer> trailers = new ArrayList<>();

            for(int i = 0; i < trailerJsonArray.length(); i++){
                JSONObject trailer = trailerJsonArray.getJSONObject(i);
                if(trailer.getString("site").contentEquals("YouTube")){
                    MovieTrailer movieTrailer = new MovieTrailer(trailer);
                    trailers.add(movieTrailer);
                }
            }
            return trailers;
        }

        @Override
        protected List<MovieTrailer> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonString = null;

            try {
                final String base_url = "http://api.themoviedb.org/3/movie/" +
                        params[0] + "/videos";

                Uri builtUri = Uri.parse(base_url).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.myApi)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                jsonString = buffer.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }

            try {
                return getTrailerFromJson(jsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieTrailer> trailers) {
            if(trailers != null){
                if(trailers.size() > 0){
                    recyclerViewTrailer.setVisibility(View.VISIBLE);
                    if(trailerAdapter != null){
                        trailerAdapter.clear();
                        for(MovieTrailer trailer : trailers){
                            trailerAdapter.add(trailer);
                        }

                    }

                }

            }

        }

    }

    @SuppressLint("StaticFieldLeak")
    public void addToFavorite(View view) {
        if (movie != null) {
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    return isFavorite(movie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorite) {
                    if (isFavorite == 1) {
                        new  AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected Integer doInBackground(Void... params) {
                                return getContentResolver().delete
                                        (MovieContract.MovieEntry.CONTENT_URI,
                                                MovieContract.MovieEntry.COLUMN_ID + " = ?",
                                                new String[]{movie.getId()});
                            }

                            @Override
                            protected void onPostExecute(Integer rowsDeleted) {
                                fbFavorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                                Toast toast = Toast.makeText(context, "Favorite removed!", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }.execute();
                    } else {
                        // adding to favorites
                        new AsyncTask<Void, Void, Uri>() {
                            @Override
                            protected Uri doInBackground(Void... params) {
                                ContentValues values = new ContentValues();

                                values.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
                                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                                values.put(MovieContract.MovieEntry.COLUMN_IMAGE_PATH, movie.getImagePath());
                                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                                values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
                                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());

                                return getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
                            }

                            @Override
                            protected void onPostExecute(Uri returnUri) {
                                fbFavorite.setImageResource(R.drawable.ic_star_black_24dp);
                                Toast toast = Toast.makeText(context, "Favorite added!", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }.execute();
                    }
                }
            }.execute();
        }

    }


    public int isFavorite(String id) {
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null, MovieContract.MovieEntry.COLUMN_ID + " = ?", new String[]{id},
                null);
        cursor.close();
        return cursor.getCount();
    }

}
