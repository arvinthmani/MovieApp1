package com.example.moviesnow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import  com.quinny898.library.persistentsearch.SearchBox;

import java.util.ArrayList;
import java.util.List;

import com.example.moviesnow.R;
import com.example.moviesnow.adapters.MovieTitleListAdapter;
import com.example.moviesnow.fragments.FavouriteListFragment;
import com.example.moviesnow.fragments.HighestRatedListFragment;
import com.example.moviesnow.fragments.MovieDetailActivityFragment;
import com.example.moviesnow.fragments.PopularListFragment;

import com.google.firebase.analytics.FirebaseAnalytics;


public class MainActivity extends AppCompatActivity implements MovieTitleListAdapter.OnAdapterItemSelectedListener {

    private FrameLayout mFrameLayout;
    private SearchBox search;
    private Toolbar toolbar;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Fragment currentFragment;
    private PopularListFragment popularListFragment;
    private HighestRatedListFragment highestRatedListFragment;
    private FavouriteListFragment favouriteListFragment;

    private boolean isMovieSearched;
    private int tabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.logEvent("MainActivity",savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (SearchBox) findViewById(R.id.search_box);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AdView mAdView = (AdView) mFrameLayout.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        popularListFragment = new PopularListFragment();
        highestRatedListFragment = new HighestRatedListFragment();
        favouriteListFragment = new FavouriteListFragment();

        adapter.addFragment(popularListFragment, getString(R.string.most_popular));
        adapter.addFragment(highestRatedListFragment, getString(R.string.highest_rated));
        adapter.addFragment(favouriteListFragment, getString(R.string.favourites));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                openSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void closeSearch() {
        search.hideCircularly(this);
        if (search.getSearchText().isEmpty()) toolbar.setTitle("");
    }

    public void openSearch() {
        search.setLogoText("");
        search.revealFromMenuItem(R.id.action_search, this);

        search.setMenuListener(new SearchBox.MenuListener() {

            @Override
            public void onMenuClick() {
            }

        });
        search.setSearchListener(new SearchBox.SearchListener() {

            @Override
            public void onSearchOpened() {
                // Use this to tint the screen
            }

            @Override
            public void onSearchClosed() {
                // Use this to un-tint the screen
                closeSearch();
                toolbar.setTitle(getResources().getString(R.string.app_name));
            }

            @Override
            public void onSearchTermChanged() {
                // React to the search term changing
                // Called after it has updated results
            }

            @Override
            public void onSearch(String searchTerm) {

                switch (tabPosition) {
                    case 0:
                        popularListFragment.searchMovieList(searchTerm);
                        break;
                    case 1:
                        highestRatedListFragment.searchMovieList(searchTerm);
                        break;
                    case 2:
                        favouriteListFragment.searchMovieList(searchTerm);
                        break;
                }

                isMovieSearched = true;
            }

            @Override
            public void onSearchCleared() {
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.setSearchString(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(String id) {
        MovieDetailActivityFragment displayFrag = (MovieDetailActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        if (displayFrag == null) {
            Intent mMovieDetailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
            mMovieDetailIntent.putExtra("id", id);
            startActivity(mMovieDetailIntent);
        } else {
            displayFrag.updateContent(id);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        if(isMovieSearched) {
            switch (tabPosition) {
                case 0:
                    popularListFragment.clearSearchList();
                    break;
                case 1:
                    highestRatedListFragment.clearSearchList();
                    break;
                case 2:
                    favouriteListFragment.clearSearchList();
                    break;
            }
            isMovieSearched = false;
        } else {
            finish();
        }
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
