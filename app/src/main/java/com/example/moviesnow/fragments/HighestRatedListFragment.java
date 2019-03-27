package com.example.moviesnow.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.moviesnow.adapters.MovieTitleListAdapter;
import com.example.moviesnow.R;
import com.example.moviesnow.roomdb.MovieInfo;
import com.example.moviesnow.utils.AppController;
import com.example.moviesnow.utils.TmdbUrls;

public class HighestRatedListFragment extends Fragment {

    private ArrayList<MovieInfo> mMovieList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private String url;

    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 4;
    private int pageCount = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_popular_list, container, false);
        mAdapter = new MovieTitleListAdapter(mMovieList, getActivity());
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);

        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ((position + 1) % 4 == 0)
                    return 3;
                else
                    return 1;
            }
        });

        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pageCount = savedInstanceState.getInt("pageCount");
            previousTotal = savedInstanceState.getInt("previousTotal");
            firstVisibleItem = savedInstanceState.getInt("firstVisibleItem");
            visibleItemCount = savedInstanceState.getInt("visibleItemCount");
            totalItemCount = savedInstanceState.getInt("totalItemCount");
            loading = savedInstanceState.getBoolean("loading");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageCount", pageCount);
        outState.putInt("previousTotal", previousTotal);
        outState.putInt("firstVisibleItem", firstVisibleItem);
        outState.putInt("visibleItemCount", visibleItemCount);
        outState.putInt("totalItemCount", totalItemCount);
        outState.putBoolean("loading", loading);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            url = TmdbUrls.BASE_URL + TmdbUrls.API_KEY + TmdbUrls.SORT_R_RATED;
            getMovieList(url);
            return null;
        }
    }

    private void getMovieList(String url) {
        JsonObjectRequest getListData = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray mResultArray = response.getJSONArray("results");
                    for (int i = 0; i < mResultArray.length(); i++) {
                        JSONObject mResultObject = mResultArray.getJSONObject(i);
                        MovieInfo movie = new MovieInfo();
                        movie.setName(mResultObject.getString("title"));
                        movie.setImageUrl("http://image.tmdb.org/t/p/w342/" + mResultObject.getString("poster_path"));
                        movie.setReleaseDate(getResources().getString(R.string.release_date) + mResultObject.getString("release_date"));
                        movie.setOverview(mResultObject.getString("overview"));
                        movie.setId(String.valueOf(mResultObject.getInt("id")));

                        mMovieList.add(movie);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showSnackBar(getString(R.string.error_msg));
            }
        });

        AppController.getInstance().addToRequestQueue(getListData);
    }


    void showSnackBar(String msg) {
        Snackbar.make(mRecyclerView, msg, Snackbar.LENGTH_LONG)
                .show();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mGridLayoutManager.getItemCount();
                firstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        pageCount++;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    String url = TmdbUrls.BASE_URL + TmdbUrls.API_KEY + TmdbUrls.SORT_R_RATED + "&page=" + String.valueOf(pageCount);
                    showSnackBar(getString(R.string.loading_page) + " " + String.valueOf(pageCount));
                    getMovieList(url);

                    loading = true;
                }
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    public  void searchMovieList(String searchString) {
        ArrayList<MovieInfo> searchMovieList = new ArrayList<>();
        for (MovieInfo movie : mMovieList) {
            if((movie.getName().toLowerCase()).contains(searchString.toLowerCase())) {
                searchMovieList.add(movie);

            }
        }
        mAdapter = new MovieTitleListAdapter(searchMovieList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void clearSearchList() {
        mAdapter = new MovieTitleListAdapter(mMovieList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}
