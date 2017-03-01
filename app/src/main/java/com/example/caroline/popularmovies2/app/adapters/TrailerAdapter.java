package com.example.caroline.popularmovies2.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caroline.popularmovies2.app.R;
import com.example.caroline.popularmovies2.app.data.MovieContract;

//Adapter for movie trailers

public class TrailerAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView arrowPlayImage;
        public final TextView trailerTitleTextView;

        public ViewHolder(View view) {
            arrowPlayImage = (ImageView) view.findViewById(R.id.image_arrow_play);
            trailerTitleTextView = (TextView) view.findViewById(R.id.trailer_name);
        }
    }

    public TrailerAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }


    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.arrowPlayImage.setImageResource(android.R.drawable.ic_media_play);

        int trailerTitleColumn = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TITLE);
        viewHolder.trailerTitleTextView.setText(cursor.getString(trailerTitleColumn));

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int youtubeKeyColumn = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                String youtubeKey = cursor.getString(youtubeKeyColumn);
                Uri videoUri = Uri.parse("http://www.youtube.com/watch?v=" + youtubeKey);

                Intent playTrailer = new Intent(Intent.ACTION_VIEW, videoUri);
                context.startActivity(playTrailer);
            }
        });
    }
}