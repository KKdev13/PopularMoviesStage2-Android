package com.example.khale.popularmovies_stage1;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    private String title;
    private String imagePath;
    private String voteAverage;
    private String overview;
    private String releaseDate;

    public Movie(String title, String imagePath, String voteAverage, String overview, String releaseDate){
        this.title = title;
        this.imagePath = imagePath;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel parcel){
        title = parcel.readString();
        imagePath = parcel.readString();
        voteAverage = parcel.readString();
        overview = parcel.readString();
        releaseDate = parcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(imagePath);
        parcel.writeString(voteAverage);
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
    }
}
