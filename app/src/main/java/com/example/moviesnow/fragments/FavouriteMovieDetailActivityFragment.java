package com.example.moviesnow.fragments;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

import com.example.moviesnow.activity.MainActivity;
import com.example.moviesnow.adapters.MovieDetailsAdapter;
import com.example.moviesnow.R;
import com.example.moviesnow.roomdb.MovieInfo;
import com.example.moviesnow.utils.AppController;
import com.example.moviesnow.utils.PaletteNetworkImageView;
import com.example.moviesnow.widget.MoviesNowWidget;


public class FavouriteMovieDetailActivityFragment extends Fragment {

    private String id;
    private MovieInfo movie;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private PaletteNetworkImageView mBackdrop;

    private ArrayList<String> trailerInfo = new ArrayList<>();
    private ArrayList<String> reviewInfo = new ArrayList<>();

    private FloatingActionButton fab;



    public FavouriteMovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite_movie_detail, container, false);


        id = getActivity().getIntent().getStringExtra("id");
        movie = new MovieInfo();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_movie_details);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar_layout_movie_details);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar_movie_details);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBackdrop = (PaletteNetworkImageView) v.findViewById(R.id.backdrop);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);


        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

        getMovieDataFromID(id);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mBackdrop.setResponseObserver(new PaletteNetworkImageView.ResponseObserver() {
            @Override
            public void onSuccess() {
                int colorDark = mBackdrop.getDarkVibrantColor();
                int colorLight = mBackdrop.getVibrantColor();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    getActivity().getWindow().setStatusBarColor(colorDark);
                    fab.setBackgroundTintList(ColorStateList.valueOf(colorDark));
                }
                mCollapsingToolbarLayout.setContentScrimColor(colorLight);
            }

            @Override
            public void onError() {

            }
        });

        mToolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_back));
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_share) {
                    startActivity(Intent.createChooser(shareIntent(movie.getName()), getResources().getString(R.string.share_via)));
                    return true;
                }
                return true;
            }
        });

        return v;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            id = savedInstanceState.getString("id");
            trailerInfo = savedInstanceState.getStringArrayList("trailerInfo");
            reviewInfo = savedInstanceState.getStringArrayList("reviewInfo");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("id", id);
        outState.putStringArrayList("trailerInfo", trailerInfo);
        outState.putStringArrayList("reviewInfo", reviewInfo);
    }

    public void getMovieDataFromID(final String id) {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        movie = MainActivity.movieDatabase.getMovieDao().getRecord(id);
                        getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        mBackdrop.setImageUrl(movie.getBackDrop(), AppController.getInstance().getImageLoader());
                                        mCollapsingToolbarLayout.setTitle(movie.getName());

                                        mAdapter = new MovieDetailsAdapter(movie, trailerInfo, reviewInfo, getActivity());
                                        mRecyclerView.setAdapter(mAdapter);

                                    }
                                }
                        );

                        if (MainActivity.movieDatabase.getMovieDao().checkRecordPresent(movie.id)) {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_like));
                                        }
                                    }
                            );
                        } else {
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_like_outline));
                                        }
                                    }
                            );
                        }
                    }
                }
        ).start();

        fab.show();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (MainActivity.movieDatabase.getMovieDao().checkRecordPresent(movie.id)) {
                            MainActivity.movieDatabase.getMovieDao().deleteRecord(id);
                            MainActivity.updateMoviesList();
                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(view, getResources().getString(R.string.removed_from_favourites), Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_like_outline));
                                        }
                                    }
                            );

                        } else {
                            movie.isFavorite = true;
                            MainActivity.movieDatabase.getMovieDao().insertRecord(movie);
                            MainActivity.updateMoviesList();

                            getActivity().runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            Snackbar.make(view, getResources().getString(R.string.added_to_favourites), Snackbar.LENGTH_LONG)
                                                    .show();

                                            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_like));
                                        }
                                    }
                            );


                        }
                        getActivity().runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());

                                        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                                                new ComponentName(getActivity(), MoviesNowWidget.class));
                                        if(appWidgetIds.length > 0) {
                                            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.widget_list);
                                        }

                                    }
                                }
                        );
                    }

                }).start();

            }
        });

    }


    public Intent shareIntent(String data) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.movie_extra_subject));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, data);
        return sharingIntent;
    }


}
