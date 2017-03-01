
package com.example.caroline.popularmovies2.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caroline.popularmovies2.app.adapters.ReviewsAdapter;
import com.example.caroline.popularmovies2.app.adapters.TrailerAdapter;
import com.example.caroline.popularmovies2.app.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//Loads Movie Detail Page
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public MovieDetailFragment() {
        super();
    }

    public static final String TAG = MovieDetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "DETAIL_MOVIE";
    private final int DETAIL_LOADER = 0;
    private Uri mUri;


    TextView movie_title;
    ImageView img;
    TextView movie_overview;
    TextView movie_rating;
    TextView movie_release;
    ImageButton favButton;
    TextView mTrailersNotFound;
    TextView mReviewsNotFound;

    // Projection for Movie Detail
    private final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULAR,
            MovieContract.MovieEntry.COLUMN_FAVORITE

    };
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE = 2;
    static final int COL_POSTER_PATH = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_RATING = 5;
    static final int COL_RELEASE_DATE = 6;
    static final int COL_POPULAR = 7;
    static final int COL_FAVORITE = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        movie_title = (TextView) rootView.findViewById(R.id.movie_title);
        img = (ImageView) rootView.findViewById(R.id.movie_detail_image);
        movie_overview = (TextView) rootView.findViewById(R.id.movie_overview);
        movie_rating = (TextView) rootView.findViewById(R.id.movie_rating);
        movie_release = (TextView) rootView.findViewById(R.id.movie_release);
        favButton = (ImageButton) rootView.findViewById(R.id.movie_favorites_btn);
        //For trailers layout
        mTrailersNotFound = (TextView) rootView.findViewById(R.id.movie_no_trailers);
        mTrailersNotFound.setVisibility(View.INVISIBLE);
        //For reviews layout
        mReviewsNotFound = (TextView) rootView.findViewById(R.id.movie_no_reviews);
        mReviewsNotFound.setVisibility(View.INVISIBLE);

        int movieId = Utility.fetchMovieIdFromUri(getActivity(), mUri);

        // Cursor for Trailers
        Cursor trailersCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        //Cursor for Reviews
        Cursor reviewsCursor = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null, // all columns
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        //http://stackoverflow.com/questions/12405575/using-a-listadapter-to-fill-a-linearlayout-inside-a-scrollview-layout
        TrailerAdapter trailersAdapter = new TrailerAdapter(getActivity(), trailersCursor, 0);
        final int trailerListCount = trailersAdapter.getCount();

        if (trailerListCount == 0) {
            mTrailersNotFound = (TextView) rootView.findViewById(R.id.movie_no_trailers);
            mTrailersNotFound.setVisibility(View.VISIBLE);
        } else {
            LinearLayout linearLayoutTrailers = (LinearLayout) rootView.findViewById(R.id.linear2ALayoutTrailers);
            for (int i = 0; i < trailerListCount; i++) {
                View item = trailersAdapter.getView(i, null, null);
                linearLayoutTrailers.addView(item);
            }
        }


        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(getActivity(), reviewsCursor, 0);
        final int reviewListCount = reviewsAdapter.getCount();

        if (reviewListCount == 0) {
            mReviewsNotFound = (TextView) rootView.findViewById(R.id.movie_no_reviews);
            mReviewsNotFound.setVisibility(View.VISIBLE);
        } else {
            LinearLayout linearLayoutReviews = (LinearLayout) rootView.findViewById(R.id.linear2BLayoutReviews);
            for (int i = 0; i < reviewListCount; i++) {
                View item = reviewsAdapter.getView(i, null, null);
                linearLayoutReviews.addView(item);
            }
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_movie_detail, menu);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // Cursor for Detail View
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,// Projection
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        if (data != null && data.moveToFirst()) {

            //Get movie title
            String mTitle = data.getString(COL_TITLE);
            movie_title.setText(mTitle);
            // Get the current movieId
            final String movieId = data.getString((COL_MOVIE_ID));

            // Load movie posters with picasso
            final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
            String movie_path = data.getString(COL_POSTER_PATH);
            Uri posterUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                    .appendPath(getActivity().getString(R.string.api_image_size_default))
                    .appendPath(movie_path.substring(1)) //remove the slash
                    .build();
            Picasso.with(getActivity())
                    .load(posterUri)
                    .into(img);

            //Get movie overview
            String overview = data.getString(COL_OVERVIEW);
            movie_overview.setText(overview);

            //Get movie rating
            String rating = data.getString(COL_RATING);
            movie_rating.setText(rating + " /10");

            // Extract and display the year from movie release date
            String release = data.getString(COL_RELEASE_DATE);
            String[] releaseYear = release.split("-");
            movie_release.setText(releaseYear[0]);

            final int _id = data.getInt(COL_ID);

            //Checks whether favorited or not
            final int IS_FAVORITE = data.getInt(COL_FAVORITE);
            //if favorited
            if (IS_FAVORITE == 1) {
                favButton.setImageResource(android.R.drawable.btn_star_big_on);

            } else {// not favorite
                favButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Title collected to display toast when selected/unselected favorites.
                    String movieTitle = data.getString(COL_TITLE);
                    if (IS_FAVORITE == 1) {
                        favButton.setImageResource(android.R.drawable.btn_star_big_off);
                        ContentValues removeFavorite = new ContentValues();
                        //delete from favorite table
                        Toast.makeText(getContext(), movieTitle + " removed from favorites", Toast.LENGTH_SHORT).show();
                        getActivity().getContentResolver().delete(MovieContract.FavoriteEntry.CONTENT_URI,
                                MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID + "= ?",
                                new String[]{String.valueOf(movieId)});

                        //update the movie table
                        ContentValues removeFavoriteFromMovieTable = new ContentValues();
                        removeFavoriteFromMovieTable.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
                        getActivity().getContentResolver().update(
                                MovieContract.MovieEntry.CONTENT_URI,
                                removeFavoriteFromMovieTable,
                                MovieContract.MovieEntry._ID + "= ?",
                                new String[]{String.valueOf(_id)}

                        );


                    } else {
                        favButton.setImageResource(android.R.drawable.btn_star_big_on);

                        //Update movie table
                        ContentValues addFavoriteIntoMovieTable = new ContentValues();
                        addFavoriteIntoMovieTable.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
                        getActivity().getContentResolver().update(
                                MovieContract.MovieEntry.CONTENT_URI,
                                addFavoriteIntoMovieTable,
                                MovieContract.MovieEntry._ID + " = ?",
                                new String[]{String.valueOf(_id)}

                        );
                        ContentValues addFavorite = new ContentValues();
                        ArrayList<ContentValues> contentList = new ArrayList<>();
                        //store  favorites in favorite table
                        addFavorite.put(MovieContract.FavoriteEntry.COLUMN_FAVORITE, 1);
                        addFavorite.put(MovieContract.FavoriteEntry._ID, _id);
                        addFavorite.put(MovieContract.FavoriteEntry.COLUMN_FAVORITE_MOVIE_ID, movieId);

                        contentList.add(addFavorite);
                        ContentValues[] values = new ContentValues[contentList.size()];
                        contentList.toArray(values);
                        Toast.makeText(getContext(), movieTitle + " added to favorites", Toast.LENGTH_SHORT).show();
                        getActivity().getContentResolver().bulkInsert(MovieContract.FavoriteEntry.CONTENT_URI,
                                values);
                    }
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}








