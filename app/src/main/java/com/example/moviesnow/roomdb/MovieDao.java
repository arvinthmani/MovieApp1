package com.example.moviesnow.roomdb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM MovieInfo ")
    public LiveData<List<MovieInfo>> getRecords();

    @Insert(onConflict = REPLACE)
    public long insertRecord(MovieInfo details);

    @Query("delete from MovieInfo where id = :movieId")
    public int deleteRecord(String movieId);

    @Query("SELECT * from MovieInfo where id = :movieId")
    public MovieInfo getRecord(String movieId);

    @Query("SELECT isFavorite from MovieInfo where id = :movieId")
    public boolean checkRecordPresent(String movieId);

    @Query("SELECT * FROM MovieInfo ")
    public List<MovieInfo> getAllRecords();

}
