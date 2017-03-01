package com.example.caroline.popularmovies2.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.caroline.popularmovies2.app.sync.MovieSyncAdapter;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements MovieFragment.CallbackMovie {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

        } else {
            mTwoPane = false;
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }

        //http://facebook.github.io/stetho/
        Stetho.initializeWithDefaults(this);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        //Initialize syncAdapter
        MovieSyncAdapter.initializeSyncAdapter(getApplicationContext());

        //variable to store our current preferred sortOrder
        mSortOrder = Utility.getPreferredSortOrder(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check whether the preference has changed by comparing whatever is stored in the settings
        String sortOrder = Utility.getPreferredSortOrder(this);
        if (sortOrder != null && !sortOrder.equals(mSortOrder)) {
            //If changed get the MovieFragment
            MovieFragment mf = (MovieFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment);
            if (null != mf) {
                //Call settings changed by restarting loader
                mf.onSettingsChanged();
            }

            //Update sortOrder
            mSortOrder = sortOrder;
        }

    }

    //( FOR TWO PANE MODE)
    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI, contentUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MovieDetailFragment.TAG)
                    .commit();

        } else {

            Intent intent = new Intent(this, MovieDetail.class)
                    .setData(contentUri);
            startActivity(intent);
        }

    }

}
