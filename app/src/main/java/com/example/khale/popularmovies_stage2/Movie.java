package com.example.khale.popularmovies_stage2;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.khale.popularmovies_stage2.data.MovieContract;

public class Movie implements Parcelable{
    private String id;
    private String title;
    private String imagePath;
    private String voteAverage;
    private String overview;
    private String releaseDate;
    //private boolean isFavorite = false;

    public Movie(String id, String title, String imagePath, String voteAverage, String overview, String releaseDate){
        this.id = id;
        this.title = title;
        this.imagePath = imagePath;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel parcel){
        id = parcel.readString();
        title = parcel.readString();
        imagePath = parcel.readString();
        voteAverage = parcel.readString();
        overview = parcel.readString();
        releaseDate = parcel.readString();
        //isFavorite = parcel.readInt() == 1;
    }

    public Movie(JSONObject movie)throws JSONException {
        this.id = movie.getString("id");
        this.title = movie.getString("original_title");
        this.imagePath = movie.getString("poster_path");
        this.overview = movie.getString("overview");
        this.voteAverage = movie.getString("vote_average");
        this.releaseDate = movie.getString("release_date");
    }

    public Movie(Cursor cursor){
        this.id = cursor.getString(MovieContract.COL_MOVIE_ID);
        this.title = cursor.getString(MovieContract.COL_TITLE);
        this.imagePath = cursor.getString(MovieContract.COL_IMAGE);
        this.overview = cursor.getString(MovieContract.COL_OVERVIEW);
        this.voteAverage = cursor.getString(MovieContract.COL_RATING);
        this.releaseDate = cursor.getString(MovieContract.COL_DATE);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(imagePath);
        parcel.writeString(voteAverage);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    //Getters

    public String getId() { return id; }
    public String getTitle(){
        return title;
    }
    public String getImagePath(){
        return imagePath;
    }
    public String getVoteAverage(){
        return voteAverage;
    }
    public String getOverview(){
        return overview;
    }
    public String getReleaseDate(){
        return releaseDate;
    }

}
