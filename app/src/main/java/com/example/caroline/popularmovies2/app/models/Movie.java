package com.example.caroline.popularmovies2.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The response from the server, a JSON object with an array ("results")
 */

public class Movie {

    @SerializedName("results")
    List<MovieModel> movieList;

    public List<MovieModel> getMovieList() {
        return movieList;
    }

    public static class MovieModel {
        @SerializedName("id")
        private int mMovieId;

        @SerializedName("original_title")
        private String mTitle;

        @SerializedName("poster_path")
        private String mPosterPath;

        @SerializedName("overview")
        private String mOverview;

        @SerializedName("vote_average")
        private double mRating;

        @SerializedName("release_date")
        private String mReleaseDate;

        @SerializedName("popularity")
        private double mPopularity;

        public int getMovieId() {return mMovieId;}

        public String getTitle() {return mTitle;}

        public String getPosterPath() {return mPosterPath;}

        public String getOverview() {return mOverview;}

        public double getRating() {return mRating;}

        public String getReleaseDate() {return mReleaseDate;}

        public double getPopularity() {return mPopularity;}

    }

}
