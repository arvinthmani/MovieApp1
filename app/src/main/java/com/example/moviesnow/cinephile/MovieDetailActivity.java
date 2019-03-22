package com.example.moviesnow.cinephile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.example.moviesnow.R;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MovieDetailActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("MovieDetailActivity",savedInstanceState);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
    }

}
