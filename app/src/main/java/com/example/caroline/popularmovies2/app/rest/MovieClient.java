package com.example.caroline.popularmovies2.app.rest;


import com.example.caroline.popularmovies2.app.models.Movie;
import com.example.caroline.popularmovies2.app.models.Review;
import com.example.caroline.popularmovies2.app.models.Trailer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieClient {
     //using this route the it will generate the following URL:
    // http://api.themoviedb.org/3/movie/popular?api_key=API_KEY

    //https://futurestud.io/tutorials/retrofit-getting-started-and-android-client

    @GET("movie/{sort_by}")
    Call<Movie> getMoviesList(@Path("sort_by") String sortOrder,
                              @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<Review> getMovieReview(@Path("id") int id,
                                @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<Trailer> getMovieTrailer(@Path("id") int id,
                                  @Query("api_key") String apiKey);


}




