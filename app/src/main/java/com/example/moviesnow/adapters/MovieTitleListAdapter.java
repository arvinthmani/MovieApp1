package com.example.moviesnow.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import com.example.moviesnow.R;
import com.example.moviesnow.roomdb.MovieInfo;
import com.example.moviesnow.utils.AppController;

public class MovieTitleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MovieInfo> mMovieList = new ArrayList<MovieInfo>();
    private Activity mAct;
    private LayoutInflater mInflater;
    private OnAdapterItemSelectedListener mAdapterCallback;

    public MovieTitleListAdapter(ArrayList<MovieInfo> mMovieList, Activity activity) {
        this.mMovieList = mMovieList;
        this.mAct = activity;

        mAdapterCallback = (OnAdapterItemSelectedListener) mAct;
        mInflater = (LayoutInflater) this.mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == 2) {
            View v = mInflater.inflate(R.layout.layout_holder_movie_small, parent, false);
            vh = new ViewHolderSmall(v);
        } else {
            View v = mInflater.inflate(R.layout.layout_holder_movie_large, parent, false);
            vh = new ViewHolderLarge(v);
        }

        return vh;
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) % 4 == 0)
            return 1;
        else
            return 2;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case 2:
                ((ViewHolderSmall) holder).getImageView().setImageUrl(mMovieList.get(position).getImageUrl(), AppController.getInstance().getImageLoader());
                ((ViewHolderSmall) holder).getTitleView().setText(mMovieList.get(position).getName());

                ((ViewHolderSmall) holder).getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAdapterCallback != null) {
                            mAdapterCallback.onItemSelected(mMovieList.get(position).getId());
                        }
                    }
                });

                break;
            case 1:
                ((ViewHolderLarge) holder).getImageView().setImageUrl(mMovieList.get(position).getImageUrl(), AppController.getInstance().getImageLoader());
                ((ViewHolderLarge) holder).getTitleView().setText(mMovieList.get(position).getName());
                ((ViewHolderLarge) holder).getOverviewView().setText(mMovieList.get(position).getOverview());
                ((ViewHolderLarge) holder).getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAdapterCallback != null) {
                            mAdapterCallback.onItemSelected(mMovieList.get(position).getId());
                        }
                    }
                });
                ((ViewHolderLarge) holder).getReadMoreView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAdapterCallback != null) {
                            mAdapterCallback.onItemSelected(mMovieList.get(position).getId());
                        }
                    }
                });
                break;
        }
    }


    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public interface OnAdapterItemSelectedListener {
        void onItemSelected(String id);
    }

    public static class ViewHolderSmall extends RecyclerView.ViewHolder {

        private NetworkImageView imageView;
        private TextView titleView;

        public ViewHolderSmall(View v) {
            super(v);
            imageView = (NetworkImageView) v.findViewById(R.id.image);
            titleView = (TextView) v.findViewById(R.id.title);
        }

        public NetworkImageView getImageView() {
            return imageView;
        }

        public TextView getTitleView() {
            return titleView;
        }

    }

    public static class ViewHolderLarge extends RecyclerView.ViewHolder {

        private NetworkImageView imageView;
        private TextView titleView, overviewView, readMoreView;

        public ViewHolderLarge(View v) {
            super(v);
            imageView = (NetworkImageView) v.findViewById(R.id.image);
            titleView = (TextView) v.findViewById(R.id.title);
            overviewView = (TextView) v.findViewById(R.id.overview);
            readMoreView = (TextView) v.findViewById(R.id.read_more);
        }

        public NetworkImageView getImageView() {
            return imageView;
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getOverviewView() {
            return overviewView;
        }

        public TextView getReadMoreView() {
            return readMoreView;
        }
    }
}
