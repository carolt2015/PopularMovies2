package com.example.caroline.popularmovies2.app;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.caroline.popularmovies2.app.adapters.MovieAdapter;
import com.example.caroline.popularmovies2.app.data.MovieContract;
import com.example.caroline.popularmovies2.app.sync.MovieSyncAdapter;

import java.util.ArrayList;

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MovieFragment() {
    }

    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    public static final int MOVIE_LOADER = 0;
    String sortOrderSetting;
    MovieAdapter adapter;
    GridView mGridView;
    Boolean mNoFavorites = false;

    //https://developer.android.com/guide/components/broadcasts.html#security_considerations_and_best_practices
    //http://stackoverflow.com/questions/9433674/get-notified-when-requestsync-function-completed
    private BroadcastReceiver syncBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // restore favorite movies after sync
            restoreFavoriteMovies();
        }
    };

    //To register for local broadcasts, call registerReceiver
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("ACTION_SYNC_FINISHED");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(syncBroadcastReceiver, filter);
        restoreFavoriteMovies();
    }

    //To stop receiving broadcasts, call unregisterReceiver
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(syncBroadcastReceiver);
        super.onPause();
    }

    // Interface for movie selection
    public interface CallbackMovie {
        public void onItemSelected(Uri movieUri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkConnection();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    public void checkConnection() {
        if (!isOnline()) {
            Toast.makeText(getContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    // This boolean method checks for connection.(If no connection, then no need to proceed)
    //http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }

    void onSettingsChanged() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    public void restoreFavoriteMovies() {

        ArrayList<String> movieIds = new ArrayList<>();
        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.FavoriteEntry.CONTENT_URI,
                new String[]{MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID},
                MovieContract.FavoriteEntry.COLUMN_FAVORITE + "= ?",
                new String[]{Integer.toString(1)},
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                movieIds.add(cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID)));
                cursor.moveToNext();
            }
            cursor.close();
            //From arrayList to load back to movie table
            int movieListLength = movieIds.size();
            ContentValues addFavoriteIntoMovieTable = new ContentValues();
            for (int i = 0; i < movieListLength; i++) {
                addFavoriteIntoMovieTable.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
                getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        addFavoriteIntoMovieTable,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieIds.get(i))}
                );
            }
            //  we restart the loader again with updated list
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        adapter = new MovieAdapter(getActivity(), null, 0);
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mGridView = (GridView) rootView.findViewById(R.id.movie_grid);
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {

                Cursor currentData = (Cursor) adapterView.getItemAtPosition(position);
                if (currentData != null) {
                    final int MOVIE_ID_COL = currentData.getColumnIndex(MovieContract.MovieEntry._ID);
                    Uri movieUri = MovieContract.MovieEntry.buildMovieWithId(currentData.getInt(MOVIE_ID_COL));
                    ((CallbackMovie) getActivity()).onItemSelected(movieUri);
                }

            }

        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final int NUMBER_OF_MOVIES = 20;
        sortOrderSetting = Utility.getPreferredSortOrder(getActivity());
        String sortOrder;
        //sort by popular
        if (sortOrderSetting.equals(getString(R.string.sort_movie_popular))) {
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULAR + " DESC";

            // returns sort by Popular
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH},
                    null,
                    null,
                    sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
        }
        //sort by rating
        else if (sortOrderSetting.equals(getString(R.string.sort_movie_top_rated))) {
            sortOrder = MovieContract.MovieEntry.COLUMN_RATING + " DESC";

            // returns sort by TopRated
            return new CursorLoader(getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH},
                    null,
                    null,
                    sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
        } else {
            mNoFavorites = true;
            //returns favorite movies and return FavoriteCursor;
            return new CursorLoader(
                    getActivity(),
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH},
                    MovieContract.FavoriteEntry.COLUMN_FAVORITE + "= ?",
                    new String[]{Integer.toString(1)},
                    null);

        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        adapter.swapCursor(data);
        //check for favorites
        if (mNoFavorites == true && data.getCount() == 0) {
            Toast.makeText(getContext(), "You have no favorite movies!! ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}














