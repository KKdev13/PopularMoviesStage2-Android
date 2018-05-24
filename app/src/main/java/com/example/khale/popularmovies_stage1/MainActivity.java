package com.example.khale.popularmovies_stage1;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import java.util.ArrayList;

import android.view.Window;
import android.widget.GridView;
import android.widget.AdapterView;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final String popular = "popular";
    private final String top_rated = "top_rated";
    private ImageAdapter imageAdapter;
    private ArrayList<Movie> movieList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;

    String TAG = "MainActivity";

    //Add your api key here
    public static final String api = "Add API key in here!";
    public static final String baseURL = "https://api.themoviedb.org/3/movie/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        movieList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, movieList);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(imageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = imageAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });
        getMovies(popular);

    }

    private void getMovies (String filter) {

        tvErrorMessage.setVisibility(View.INVISIBLE);

        FetchMovies fetchMovies = new FetchMovies(new Callback() {
            @Override
            void updateAdapter(Movie[] movies) {
                if (movies != null) {
                    imageAdapter.clear();
                    Collections.addAll(movieList, movies);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });
        fetchMovies.execute(filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMovies(popular);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_top_rated){
            getMovies(top_rated);
            return true;
        } else if(id == R.id.action_most_popular || id == R.id.action_refresh){
            getMovies(popular);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

     public class FetchMovies extends AsyncTask<String, Void, Movie[]> {

        private final Callback callback;

        FetchMovies(Callback movieCallback){
            this.callback = movieCallback;
        }

        private Movie[] fetchMoviesFromJson (String jsonMovieStr) throws JSONException {
            if (jsonMovieStr == null || "".equals(jsonMovieStr)){
                return null;
            }

            JSONObject jsonMovieObj = new JSONObject(jsonMovieStr);
            JSONArray jsonMoviesArray = jsonMovieObj.getJSONArray("results");

            Movie[] movies = new Movie[jsonMoviesArray.length()];

            for (int i = 0; i < jsonMoviesArray.length(); i++){
                JSONObject jsonObject = jsonMoviesArray.getJSONObject(i);
                movies[i] = new Movie(jsonObject.getString("original_title"),
                        jsonObject.getString("poster_path"),
                        jsonObject.getString("vote_average"),
                        jsonObject.getString("overview"),
                        jsonObject.getString("release_date"));
            }
            return movies;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJSON = null;

            Uri uri = Uri.parse(baseURL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter("api_key", api)
                    .build();

            try {
                /*String base_url = "https://api.themoviedb.org/3/movie/";
                URL url = new URL(base_url + params[0] + "?api_key=" + api);
                Log.d(TAG,"URL: " + url.toString());*/

                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if(inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }
                if(builder.length() == 0) {
                    return null;
                }

                moviesJSON = builder.toString();
                Log.d(TAG, "JSON Parsed: " + moviesJSON);

                /*JSONObject moviesObj = new JSONObject(moviesJSON);
                JSONArray items = moviesObj.getJSONArray("results");
                JSONObject movie, id;
                for(int i =0; i < items.length(); i++) {
                    movie = items.getJSONObject(i);
                    ids.add(movie.getInt("id"));
                    movies.add(movie.getString("poster_path"));
                }*/


            }catch(Exception e){
                e.printStackTrace();
                Log.e(TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return fetchMoviesFromJson(moviesJSON);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {

            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null){
                callback.updateAdapter(movies);
            } else {
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }
}
