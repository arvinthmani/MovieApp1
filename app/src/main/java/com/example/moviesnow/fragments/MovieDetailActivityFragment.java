package com.example.moviesnow.fragments;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.Observer;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.example.moviesnow.activity.MainActivity;
import com.example.moviesnow.adapters.MovieDetailsAdapter;
import com.example.moviesnow.R;
import com.example.moviesnow.roomdb.MovieInfo;
import com.example.moviesnow.utils.AppController;
import com.example.moviesnow.utils.PaletteNetworkImageView;
import com.example.moviesnow.utils.TmdbUrls;
import com.example.moviesnow.widget.MoviesNowWidget;

public class MovieDetailActivityFragment extends Fragment {

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

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        id = getActivity().getIntent().getStringExtra("id");
        movie = new MovieInfo();

        getMovieDataFromID(id);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_movie_details);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsing_toolbar_layout_movie_details);
        mToolbar = (Toolbar) v.findViewById(R.id.toolbar_movie_details);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBackdrop = (PaletteNetworkImageView) v.findViewById(R.id.backdrop);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);


        mCollapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));

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

        mToolbar.inflateMenu(R.menu.menu_movie_detail);


        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.action_share) {
                    String[] data = trailerInfo.get(0).split(",,");
                    startActivity(Intent.createChooser(shareIntent(TmdbUrls.YOUTUBE_URL + data[0]), "Share Via"));
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

    private class AsyncTaskRunner extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... response) {
            try {
                movie.setId(id);
                movie.setName(response[0].getString("title"));
                movie.setRating(String.valueOf(response[0].getDouble("vote_average")));
                String genres = "";
                JSONArray genreArray = response[0].getJSONArray("genres");
                for (int i = 0; i < genreArray.length(); i++) {
                    String genre = genreArray.getJSONObject(i).getString("name");
                    if (i != genreArray.length() - 1)
                        genres += genre + ", ";
                    else
                        genres += genre + ".";
                }
                movie.setGenere(genres);
                movie.setReleaseDate(response[0].getString("release_date"));
                movie.setStatus(response[0].getString("status"));
                movie.setOverview(response[0].getString("overview"));
                movie.setBackDrop("http://image.tmdb.org/t/p/w780/" + response[0].getString("backdrop_path"));
                movie.setVoteCount(String.valueOf(response[0].getInt("vote_count")));
                movie.setTagLine(response[0].getString("tagline"));
                movie.setRuntime(String.valueOf(response[0].getInt("runtime")));
                movie.setLanguage(response[0].getString("original_language"));
                movie.setPopularity(String.valueOf(response[0].getDouble("popularity")));
                movie.setPoster("http://image.tmdb.org/t/p/w342/" + response[0].getString("poster_path"));

                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                mBackdrop.setImageUrl(movie.getBackDrop(), AppController.getInstance().getImageLoader());
                                mCollapsingToolbarLayout.setTitle(movie.getName());

                                mAdapter = new MovieDetailsAdapter(movie, trailerInfo, reviewInfo, getActivity());
                                mRecyclerView.setAdapter(mAdapter);                            }
                        }
                );


                if (MainActivity.movieViewModel.checkRecordPresent(movie.id)) {
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
                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                fab.show();
                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                if (MainActivity.movieViewModel.checkRecordPresent(movie.id)) {
                                                    MainActivity.movieViewModel.deleteRecord(movie.id);
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
                                                    MainActivity.movieViewModel.insertRecord(movie);
                                                    getActivity().runOnUiThread(
                                                            new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Snackbar.make(view, getResources().getString(R.string.added_to_favourites), Snackbar.LENGTH_LONG)
                                                                            .setAction("Action", null).show();

                                                                    fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_like));
                                                                }
                                                            }
                                                    );

                                                }

                                            }
                                        }).start();

                                    }
                                });
                            }
                        }
                );


                getTrailerInfo(id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public void updateContent(String id) {
        getMovieDataFromID(id);
    }

    private void getMovieDataFromID(final String id) {

        MainActivity.movieViewModel.getMovieEntityLiveData().observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(@Nullable List<MovieInfo> movieEntities) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());

                int appWidgetIds[] = appWidgetManager.getAppWidgetIds(
                        new ComponentName(getActivity(), MoviesNowWidget.class));
                if(appWidgetIds.length > 0) {
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[0], R.id.widget_list);
                }
            }
        });

        String url = TmdbUrls.MOVIE_BASE_URL + id + "?" + TmdbUrls.API_KEY;
        JsonObjectRequest getDetails = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                new AsyncTaskRunner().execute(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackBar(getString(R.string.error_msg));
            }
        });
        AppController.getInstance().addToRequestQueue(getDetails);
    }


    private void getTrailerInfo(final String id) {
        trailerInfo.clear();
        String requestUrl = TmdbUrls.MOVIE_BASE_URL + id + "/videos?" + TmdbUrls.API_KEY;

        JsonObjectRequest mTrailerRequest = new JsonObjectRequest(requestUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mTrailerArray = response.getJSONArray("results");
                    for (int i = 0; i < mTrailerArray.length(); i++) {
                        JSONObject mTrailerObject = mTrailerArray.getJSONObject(i);
                        trailerInfo.add(mTrailerObject.getString("key") + ",," + mTrailerObject.getString("name")
                                + ",," + mTrailerObject.getString("site") + ",," + mTrailerObject.getString("size"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    // Specify Adapter
                    mAdapter = new MovieDetailsAdapter(movie, trailerInfo, reviewInfo, getActivity());
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                    getMovieReviews(id);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getMovieReviews(id);
            }
        });


        AppController.getInstance().addToRequestQueue(mTrailerRequest);
    }

    void getMovieReviews(String id) {
        reviewInfo.clear();
        String reviewUrl = TmdbUrls.MOVIE_BASE_URL + id + "/reviews?" + TmdbUrls.API_KEY;
        JsonObjectRequest mReviewRequest = new JsonObjectRequest(reviewUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    int size = response.getInt("total_results");
                    if (size != 0) {
                        JSONArray mReviewArray = response.getJSONArray("results");
                        for (int i = 0; i < mReviewArray.length(); i++) {
                            JSONObject mReview = mReviewArray.getJSONObject(i);
                            reviewInfo.add(mReview.getString("author") + "-" + mReview.getString("content"));
                        }
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(mReviewRequest);
    }

    void showSnackBar(String msg) {
        Snackbar.make(mCollapsingToolbarLayout, msg, Snackbar.LENGTH_LONG)
                .show();
    }


    public Intent shareIntent(String data) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.movie_extra_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, data);
        return sharingIntent;
    }


}
