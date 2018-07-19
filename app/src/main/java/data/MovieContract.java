package data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.example.khale.popularmovies_stage2";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //The path for the Movies directory
    public static final String PATH_MOVIES = "movies";

    //An inner class that defines the contents of the Movies table
    public static final class MovieEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        //Define Movies table and Column names
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE_PATH = "image_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMoviesUri (long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE};

    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE    = 2;
    public static final int COL_IMAGE    = 3;
    public static final int COL_OVERVIEW = 4;
    public static final int COL_RATING   = 5;
    public static final int COL_DATE     = 6;




}
