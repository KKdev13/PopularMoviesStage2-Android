package com.example.khale.popularmovies_stage2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import android.widget.GridView;
import android.widget.AdapterView;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.khale.popularmovies_stage2.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    private final String popular = "popular";
    private final String top_rated = "top_rated";
    private final String favorite = "favorite";
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieList;
    private TextView tvErrorMessage;
    private ProgressBar pbLoadingIndicator;
    private Context context;
    private String sort = popular;

    //public int selected;
    String TAG = "MainActivity";


    public static final String baseURL = "https://api.themoviedb" + "" + "" + ".org/3/movie/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        tvErrorMessage = (TextView) findViewById(R.id.tv_error_message);

        if (savedInstanceState != null){
            if(savedInstanceState.containsKey("sort")){
                sort = savedInstanceState.getString("sort");
            }
            if(savedInstanceState.containsKey("movies")){
                movieList = savedInstanceState.getParcelableArrayList("movies");
            }
        }else {
            movieList = new ArrayList<>();
        }

        movieAdapter = new MovieAdapter(this, movieList);
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(movieAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        if(savedInstanceState != null){
            if(!savedInstanceState.containsKey("movies")){
                getMovies(sort);
            }
        }else {
            getMovies(sort);
        }
        context = this;

    }

    public void getMovies (String filter) {

        tvErrorMessage.setVisibility(View.INVISIBLE);

        if(filter.equals(favorite)){
            FetchFavoriteMovies fetchFavoriteMovies = new FetchFavoriteMovies(new Callback(){
                @Override
                public void updateAdapter(List<Movie> movies) {
                    if(movies != null){
                        movieAdapter.clear();
                        movieList.addAll(movies);
                        movieAdapter.notifyDataSetChanged();
                    }
                }
            });
            fetchFavoriteMovies.execute(filter);
        } else {
            FetchMovies fetchMovies = new FetchMovies(new Callback(){
                @Override
                public void updateAdapter(List<Movie> movies) {
                    if(movies != null){
                        movieAdapter.clear();
                        movieList.addAll(movies);
                        movieAdapter.notifyDataSetChanged();
                    }
                }
            });
            fetchMovies.execute(filter);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        getMovies(popular);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_top_rated){
            getMovies(top_rated);
            sort = top_rated;
            return true;
        }
        if(id == R.id.action_most_popular){
            getMovies(popular);
            sort = popular;
            return true;
        }
        if(id == R.id.action_favorites){
            getMovies(favorite);
            sort = favorite;
            return true;
        }
        if(id == R.id.action_refresh){
            getMovies(sort);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class FetchMovies extends AsyncTask<String, Void, List<Movie>> {

        private final Callback callback;

        FetchMovies(Callback movieCallback){
            this.callback = movieCallback;
        }

        private List<Movie> fetchMoviesFromJson (String jsonMovieStr) throws JSONException {
            if (jsonMovieStr == null || "".equals(jsonMovieStr)){
                return null;
            }

            JSONObject jsonMovieObj = new JSONObject(jsonMovieStr);
            JSONArray jsonMoviesArray = jsonMovieObj.getJSONArray("results");

            //Movie[] movies = new Movie[jsonMoviesArray.length()];
            List<Movie> movies = new ArrayList<>();

            for (int i = 0; i < jsonMoviesArray.length(); i++){
                JSONObject jsonMovie = jsonMoviesArray.getJSONObject(i);
                Movie movie = new Movie(jsonMovie);
                movies.add(movie);
            }
            return movies;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {

            if(params.length == 0){
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJSON = null;

            Uri uri = Uri.parse(baseURL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter("api_key", context.getString(R.string.API_Key))
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


            }catch(IOException e){
                e.printStackTrace();
                Log.e(TAG, "Error", e);
            } finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
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
        protected void onPostExecute(List<Movie> movies) {

            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null){
                callback.updateAdapter(movies);
            } else {
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }

    }

    public class FetchFavoriteMovies extends AsyncTask<String, Void, List<Movie>>{

        private Callback callback;
        private Context context;

        FetchFavoriteMovies (Callback fmovieCallback){
            this.callback = fmovieCallback;
            context = getApplicationContext();
        }

        private List<Movie> fetchFavoriteMoviesFromCursor(Cursor cursor){
            List<Movie> movies = new ArrayList<>();
            if(cursor != null && cursor.moveToFirst()){
                do {
                    Movie movie = new Movie(cursor);
                    movies.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return movies;
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }
            Cursor cursor = context.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                    MovieContract.MOVIE_COLUMNS, null, null, null);
            return fetchFavoriteMoviesFromCursor(cursor);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            pbLoadingIndicator.setVisibility(View.INVISIBLE);
            if(movies != null){
                callback.updateAdapter(movies);
            }else {
                tvErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("sort", sort);
        if(movieList != null){
            outState.putParcelableArrayList("movies", movieList);
        }
        super.onSaveInstanceState(outState);
    }
}
