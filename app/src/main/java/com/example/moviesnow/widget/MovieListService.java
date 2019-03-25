package com.example.moviesnow.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.moviesnow.R;
import com.example.moviesnow.models.Movie;
import com.example.moviesnow.utils.DatabaseHelper;
import com.example.moviesnow.utils.MoviesContentProvider;

import java.util.ArrayList;

public class MovieListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Movie service", "call Movie service");
        return new MoviesListDataProvider(this, intent);

    }

}
