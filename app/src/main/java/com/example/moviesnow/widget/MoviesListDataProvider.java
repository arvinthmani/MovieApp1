package com.example.moviesnow.widget;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.moviesnow.R;
import com.example.moviesnow.activity.MainActivity;
import com.example.moviesnow.fragments.FavouriteMovieDetailActivityFragment;
import com.example.moviesnow.roomdb.MovieDatabase;
import com.example.moviesnow.roomdb.MovieInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MoviesListDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<MovieInfo> mMovieList = new ArrayList<>();
    Context context;
    Intent intent;


    public MoviesListDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

    }

    @Override
    public void onCreate() {
        new AsyncTaskRunner().execute(context);
    }

    @Override
    public void onDataSetChanged() {
        mMovieList = MainActivity.getMovies();
    }


    private class AsyncTaskRunner extends AsyncTask<Context, Void, List<MovieInfo>> {

        @Override
        protected List<MovieInfo> doInBackground(Context... context) {

            List<MovieInfo> movieList;
            MovieDatabase db = MovieDatabase.getInstance(context[0]);
            movieList = db.getMovieDao().getRecords();

            return movieList;
        }

        protected void onPostExecute(List<MovieInfo> movieList) {
            super.onPostExecute(movieList);
            if (movieList != null && mMovieList != null) {
                if (mMovieList.size() != movieList.size())
                {
                    mMovieList.clear();
                    mMovieList = movieList;
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                            new ComponentName(context, MoviesNowWidget.class));
                    if (appWidgetIds.length > 0) {
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.widget_list);
                    }
                }
            }

        }

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mMovieList != null) {
            return mMovieList.size();
        }
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_title, mMovieList.get(i).getName());
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
