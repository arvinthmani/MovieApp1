package com.example.moviesnow.widget;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.volley.toolbox.NetworkImageView;
import com.example.moviesnow.R;
import com.example.moviesnow.cinephile.MainActivity;
import com.example.moviesnow.fragments.FavouriteListFragment;
import com.example.moviesnow.models.Movie;
import com.example.moviesnow.utils.AppController;
import com.example.moviesnow.utils.ContentProviderHelperMethods;
import com.example.moviesnow.utils.DatabaseHelper;
import com.example.moviesnow.utils.MoviesContentProvider;

import java.util.ArrayList;
import java.util.List;

public class MoviesListDataProvider implements RemoteViewsService.RemoteViewsFactory {

    ArrayList<Movie> mMovieList = new ArrayList<>();
    Context context;
    Intent intent;


    public MoviesListDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        mMovieList.clear();
        Uri contentUri = MoviesContentProvider.CONTENT_URI;
        Cursor c = this.context.getContentResolver().query(contentUri, null, null, null, null);
        if (c.moveToFirst()) {
            do {

                Movie movie = new Movie(c.getString(c.getColumnIndex(DatabaseHelper.KEY_TITLE)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_POSTER)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_DATE)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_OVERVIEW)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_ID)));

                mMovieList.add(movie);
            } while (c.moveToNext());
        }
        c.close();
        Log.d("movie","constructor movie list=============" + mMovieList.size());
    }

    @Override
    public void onCreate() {


    }

    @Override
    public void onDataSetChanged() {

        mMovieList.clear();
        Uri contentUri = MoviesContentProvider.CONTENT_URI;
        Cursor c = this.context.getContentResolver().query(contentUri, null, null, null, null);
        if (c.moveToFirst()) {
            do {

                Movie movie = new Movie(c.getString(c.getColumnIndex(DatabaseHelper.KEY_TITLE)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_POSTER)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_DATE)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_OVERVIEW)),
                        c.getString(c.getColumnIndex(DatabaseHelper.KEY_ID)));

                mMovieList.add(movie);
            } while (c.moveToNext());
        }
        c.close();

        Log.d("movie","movie list=============" + mMovieList.size());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        Log.d("movie","getCount movie list=============" + mMovieList.size());

        return mMovieList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        Log.d("movie","getViewAt movie list=============" + mMovieList.size());
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_title, mMovieList.get(i).getTitle());

        return  remoteViews;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
