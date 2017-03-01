package com.example.caroline.popularmovies2.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.caroline.popularmovies2.app.BuildConfig;
import com.example.caroline.popularmovies2.app.MainActivity;
import com.example.caroline.popularmovies2.app.R;
import com.example.caroline.popularmovies2.app.Utility;
import com.example.caroline.popularmovies2.app.models.Movie;
import com.example.caroline.popularmovies2.app.models.Review;
import com.example.caroline.popularmovies2.app.models.Trailer;
import com.example.caroline.popularmovies2.app.rest.MovieClient;
import com.example.caroline.popularmovies2.app.rest.MovieServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 60 * 10; // 10 hours
    private static final int MOVIE_NOTIFICATION_ID = 3001;
    private static long lastSyncTime = 0L;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    private void notifyMovie() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        boolean displayNotifications = prefs.getBoolean(getContext().getString(R.string.pref_enable_notifications_key), true);
        if (!displayNotifications) {
            return;
        }

           //checking the last update and notify if it' the first of the day
            String lastNotificationKey = getContext().getString(R.string.pref_last_notification);
            lastSyncTime = prefs.getLong(lastNotificationKey, 0);

            if (Utility.isOneDayLater(lastSyncTime)) {
                //send notification
                int smallIcon = R.mipmap.ic_launcher;
                Bitmap largeIcon = BitmapFactory.decodeResource(
                        getContext().getResources(),
                        R.mipmap.ic_launcher);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                        .setSmallIcon(smallIcon)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(getContext().getString(R.string.app_name))
                        .setContentText(getContext().getString(R.string.format_notification));

                //Opens the app when user user clicks on the notification.
                Intent notificationIntent = new Intent(getContext(), MainActivity.class);
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                stackBuilder.addNextIntent(notificationIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                // MOVIE_NOTIFICATION_ID allows you to update the notification later on.
                notificationManager.notify(MOVIE_NOTIFICATION_ID, builder.build());

                //refreshing last sync
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.apply();
            }
    }


    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            // schedule the sync adapter
            ContentResolver.addPeriodicSync(newAccount,
                    context.getString(R.string.content_authority),
                    Bundle.EMPTY,
                    SYNC_INTERVAL);

          //Without calling setSyncAutomatically, periodic sync will not be enabled.

            ContentResolver.setSyncAutomatically(newAccount,
                    context.getString(R.string.content_authority),
                    true);

             syncImmediately(context);
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {

        getSyncAccount(context);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync  ");
        String sortby = Utility.getPreferredSortOrder(getContext());
        //retrieves the movieList with selected sort preference
        getMoviesWithRetrofit(sortby);
    }


    public void getMoviesWithRetrofit(final String sortby) {

        final MovieClient client = MovieServiceGenerator.createService(MovieClient.class);
        Call<Movie> topCall = client.getMoviesList(sortby, BuildConfig.API_KEY);

        topCall.enqueue(new Callback<Movie>() {
            @Override

            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful()) {
                    List<Movie.MovieModel> movies = response.body().getMovieList();
                    // Stores movies in database
                    Utility.storeMovieList(getContext(), movies);
                    //loops through each movie id for reviews & trailers
                    for (final Movie.MovieModel movie : movies) {
                        //Retrofit call for trailers
                        Call<Trailer> trailerCall = client.getMovieTrailer(movie.getMovieId(), BuildConfig.API_KEY);
                        trailerCall.enqueue(new Callback<Trailer>() {
                            @Override
                            public void onResponse(Call<Trailer> call, Response<Trailer> response) {
                                if (response.isSuccessful()) {
                                    List<Trailer.MovieTrailer> trailers = response.body().getTrailerList();
                                    // Stores trailers in database
                                    Utility.storeTrailerList(getContext(), movie.getMovieId(), trailers);
                                }
                            }

                            @Override
                            public void onFailure(Call<Trailer> call, Throwable t) {
                                if (call.isCanceled()) {
                                    Log.e(LOG_TAG, "request was cancelled");
                                } else {
                                    Log.e(LOG_TAG, "No network connection?");
                                }
                            }
                        });
                        //Retrofit call for reviews
                        Call<Review> reviewCall = client.getMovieReview(movie.getMovieId(), BuildConfig.API_KEY);
                        reviewCall.enqueue(new Callback<Review>() {
                            @Override
                            public void onResponse(Call<Review> call, Response<Review> response) {
                                if (response.isSuccessful()) {
                                    List<Review.MovieReview> reviews = response.body().getReviewList();
                                    // Stores reviews in database
                                    Utility.storeReviewList(getContext(), movie.getMovieId(), reviews);

                                }
                            }

                            @Override
                            public void onFailure(Call<Review> call, Throwable t) {
                                if (call.isCanceled()) {
                                    Log.e(LOG_TAG, "request was cancelled");
                                } else {
                                    Log.e(LOG_TAG, "No network connection?");
                                }
                            }
                        });
                    }
                }


                notifyMovie();
                // A LocalBroadcast receiver to know when sync has finished
                Intent intent =  new Intent("ACTION_SYNC_FINISHED");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });

    }

}

