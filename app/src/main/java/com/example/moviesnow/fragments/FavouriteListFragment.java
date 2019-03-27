package com.example.moviesnow.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.example.moviesnow.activity.MainActivity;
import com.example.moviesnow.adapters.FavouriteListAdapter;
import com.example.moviesnow.R;
import com.example.moviesnow.roomdb.MovieInfo;


public class FavouriteListFragment extends Fragment {

    private ArrayList<MovieInfo> mMovieList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_popular_list, container, false);
        mAdapter = new FavouriteListAdapter(mMovieList, getActivity());
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

        getMovieList();
        setupRecyclerView(mRecyclerView);
        return mRecyclerView;
    }

    public void getMovieList() {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        List<MovieInfo> list = MainActivity.movieDatabase.getMovieDao().getRecords();
                        mMovieList.clear();
                        for (MovieInfo movie : list) {
                            mMovieList.add(movie);
                        }
                    }
                }
        ).start();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList("mMovieList");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mMovieList", mMovieList);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMovieList();
        mAdapter.notifyDataSetChanged();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
    }

    public  void searchMovieList(String searchString) {
        ArrayList<MovieInfo> searchMovieList = new ArrayList<>();
        for (MovieInfo movie : mMovieList) {
            if((movie.getName().toLowerCase()).contains(searchString.toLowerCase())) {
                searchMovieList.add(movie);

            }
        }
        mAdapter = new FavouriteListAdapter(searchMovieList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void clearSearchList() {
        mAdapter = new FavouriteListAdapter(mMovieList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

}
