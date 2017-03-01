package com.example.caroline.popularmovies2.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.caroline.popularmovies2.app.data.MovieContract;
import com.example.caroline.popularmovies2.app.models.Movie;
import com.example.caroline.popularmovies2.app.models.Review;
import com.example.caroline.popularmovies2.app.models.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of helper methods used through the application
 */

public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                context.getString(R.string.sort_movies_key),
                context.getString(R.string.sort_movie_popular));
    }

    public static int fetchMovieIdFromUri(Context context, Uri movieUri) {
        long _id = MovieContract.MovieEntry.getIdFromUri(movieUri);

        Cursor c = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null);

        if (c.moveToFirst()) {
            int movieIdIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            return c.getInt(movieIdIndex);
        } else {
            return -1;
        }
    }

    //Checks if one day has passed since the last timestamp
    public static boolean isOneDayLater(long lastTimestamp) {
        // 1000 milliseconds/second *
        // 60 seconds/minute *
        // 60 minutes/hour *
        // 24 hours/day
        final long ONE_DAY = 1000 * 60 * 60 * 24;

        long now = System.currentTimeMillis();

        long timePassed = now - lastTimestamp;
        return (timePassed >= ONE_DAY);
    }

    //  Store the list of movies  into the database
    public static void storeMovieList(Context context, List<Movie.MovieModel> movieList) {
        ArrayList<ContentValues> cvList = new ArrayList<>();
        int movieListLength = movieList.size();
        Log.d(LOG_TAG, movieListLength + " items fetched");

        for (int i = 0; i < movieListLength; i++) {
            Movie.MovieModel movie = movieList.get(i);
            ContentValues contentValues = new ContentValues();


            int movieId = movie.getMovieId();
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);

            //get the movie data from the JSON response
            //get the title
            String title = movie.getTitle();
            contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);

            //get the poster url
            String posterPath = movie.getPosterPath();
            contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);

            //get the description of the movie
            String overview = movie.getOverview();
            contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);

            //get the rating
            double rating = movie.getRating();
            contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

            //get the movie release date
            String releaseDate = movie.getReleaseDate();
            contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

            //get the movie popularity date
            double popularity = movie.getPopularity();
            contentValues.put(MovieContract.MovieEntry.COLUMN_POPULAR, popularity);

            cvList.add(contentValues);
        }

        //insert into the DB
        ContentValues[] values = new ContentValues[cvList.size()];
        cvList.toArray(values);
        context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);

    }

    //  Store the list of trailer info into the database
    public static void storeTrailerList(Context context, int movieId, List<Trailer.MovieTrailer> trailerList) {

        ArrayList<ContentValues> contentList = new ArrayList<>();
        int trailerListLength = trailerList.size();
        int trailerCount = 1; // for trailer title display

        if (trailerListLength == 0) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, "");
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TITLE, "");
            contentValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "");
            contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, "");
            contentList.add(contentValues);
        } else {
            for (int i = 0; i < trailerListLength; i++) {
                Trailer.MovieTrailer trailer = trailerList.get(i);
                ContentValues contentValues = new ContentValues();
                contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
                contentValues.put(MovieContract.TrailerEntry.COLUMN_TITLE, "Trailer " + (trailerCount));
                contentValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, trailer.getKey());
                contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                contentList.add(contentValues);
                trailerCount++;
            }
        }
        ContentValues[] values = new ContentValues[contentList.size()];
        contentList.toArray(values);
        context.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, values);
    }

    //  Store the list of reviews into the database
    public static void storeReviewList(Context context, int movieId, List<Review.MovieReview> reviewList) {

        ArrayList<ContentValues> cvList = new ArrayList<>();
        int reviewListLength = reviewList.size();
        int reviewCount = 1;

        if (reviewListLength == 0) {
            ContentValues cv = new ContentValues();

            cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "");
            cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "");
            cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "");
            cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, "");

            cvList.add(cv);
        } else {
            for (int i = 0; i < reviewListLength; i++) {
                Review.MovieReview movieReview = reviewList.get(i);
                ContentValues cv = new ContentValues();

                cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, movieReview.getAuthor() + "    ****  Review " + reviewCount + "   ****");
                cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT, movieReview.getContent());
                cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, movieReview.getId());
                cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                cvList.add(cv);
                reviewCount++;

            }
        }
        ContentValues[] values = new ContentValues[cvList.size()];
        cvList.toArray(values);

        context.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, values);

    }

}
