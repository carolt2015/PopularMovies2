package com.example.caroline.popularmovies2.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.caroline.popularmovies2.app.R;
import com.example.caroline.popularmovies2.app.data.MovieContract;

// Adapter for movie reviews

public class ReviewsAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView authorTextView;
        public final TextView contentTextView;

        public ViewHolder(View view) {
            authorTextView = (TextView) view.findViewById(R.id.review_author);
            contentTextView = (TextView) view.findViewById(R.id.review_content);
        }
    }

    public ReviewsAdapter(Context context, Cursor c, int flags) {

        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int authorColumnIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int contentColumnIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);

        String author = cursor.getString(authorColumnIndex);
        String content = cursor.getString(contentColumnIndex);

        viewHolder.authorTextView.setText(author);
        viewHolder.contentTextView.setText(content);

    }
}
