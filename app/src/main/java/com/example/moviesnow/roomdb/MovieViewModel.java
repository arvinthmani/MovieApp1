package com.example.moviesnow.roomdb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private LiveData<List<MovieInfo>> movieEntityLiveData;
    private MovieDatabase movieDatabase;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        movieDatabase = MovieDatabase.getInstance(application);
        movieEntityLiveData = movieDatabase.getMovieDao().getRecords();
    }

    public LiveData<List<MovieInfo>> getMovieEntityLiveData(){
        return movieEntityLiveData;
    }

    public void insertRecord(MovieInfo movies) {
        movieDatabase.getMovieDao().insertRecord(movies);
    }

    public void deleteRecord(String id) {
        movieDatabase.getMovieDao().deleteRecord(id);
    }

    public boolean checkRecordPresent(String id) { return movieDatabase.getMovieDao().checkRecordPresent(id); }

    public MovieInfo getRecord(String id) { return movieDatabase.getMovieDao().getRecord(id); }


}
