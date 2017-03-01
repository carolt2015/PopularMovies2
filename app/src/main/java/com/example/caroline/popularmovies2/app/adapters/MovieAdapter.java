package com.example.caroline.popularmovies2.app.adapters;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.caroline.popularmovies2.app.R;
import com.example.caroline.popularmovies2.app.data.MovieContract;
import com.squareup.picasso.Picasso;

// Custom Movie adapter; loads movies

public class MovieAdapter extends CursorAdapter {

    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final ImageView posterImageView;

        public ViewHolder(View view) {
            posterImageView = (ImageView) view.findViewById(R.id.movie_list_item_imageview);
        }
    }

    public MovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_image_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "In bind View");

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int movie_poster_column = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String moviePoster = cursor.getString(movie_poster_column);

        Uri imageUri = Uri.parse("http://image.tmdb.org/t/p/").buildUpon()
                .appendPath(context.getString(R.string.api_image_size_default))
                .appendPath(moviePoster.substring(1))
                .build();

        //Load urls for images using Picasso
        Picasso.with(context)
                .load(imageUri)
                .into(viewHolder.posterImageView);

    }
}
