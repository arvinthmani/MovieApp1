package com.example.moviesnow.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MovieListService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MoviesListDataProvider(this, intent);

    }

}
