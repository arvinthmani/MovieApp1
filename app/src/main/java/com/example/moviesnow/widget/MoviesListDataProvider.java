package com.example.moviesnow.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.moviesnow.R;
import com.example.moviesnow.fragments.FavouriteListFragment;
import com.example.moviesnow.models.Movie;
import com.example.moviesnow.utils.ContentProviderHelperMethods;

import java.util.ArrayList;
import java.util.List;

public class MoviesListDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> movieList = new ArrayList<>();
    Context context;
    Intent intent;

    private void initData() {
//
//        FavouriteListFragment listFragment = new FavouriteListFragment();
//        listFragment.getActivity();
//        movieList = ContentProviderHelperMethods.getMovieListFromDatabase(listFragment.getActivity());


        movieList.clear();

        for (int i = 1 ; i<=10 ;i++) {
            movieList.add("Movie No" + i);
        }

    }

    public MoviesListDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widgetItem, "Ho");
        return remoteViews;
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
