package com.example.watchit.activities;

import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.watchit.R;
import com.example.watchit.Utilities;
import com.example.watchit.fragments.DetailFragment;
import com.example.watchit.fragments.MainFragment;
import com.example.watchit.fragments.MainFragment.Callback;
import com.example.watchit.model.Review;
import com.example.watchit.model.Video;

import java.util.ArrayList;

import info.movito.themoviedbapi.model.MovieDb;

public class MainActivity extends AppCompatActivity implements Callback {

    private boolean mTwoPane;
    int last_preferred_sorting_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_detail) != null) {
            mTwoPane = true;

            if (savedInstanceState == null)
                getFragmentManager().beginTransaction().add(R.id.fragment_detail, new DetailFragment(), DetailFragment.DETAIL_FRAGMENT_TAG).commit();
        } else {
            mTwoPane = false;
        }
        last_preferred_sorting_order = Integer.parseInt(Utilities.getPreferredSorting(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (last_preferred_sorting_order != Integer.parseInt(Utilities.getPreferredSorting(this))) {
            MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_main);
            fragment.onSortingOrderChanged();
            last_preferred_sorting_order = Integer.parseInt(Utilities.getPreferredSorting(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_setting: {

                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void onShowDetails(MovieDb movie, ArrayList<Video> videos, ArrayList<Review> reviews, View view, boolean isItemClicked) {
        if (mTwoPane) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putSerializable(DetailFragment.KEY_MOVIE, movie);
            bundle.putParcelableArrayList(DetailFragment.KEY_VIDEOS, videos);
            bundle.putParcelableArrayList(DetailFragment.KEY_REVIEWS, reviews);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != view) {
                    ViewCompat.setTransitionName(view, getString(R.string.transition_poster));
                    Transition changeBoundsTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_bounds);
//                    ft.setSharedElementEnterTransition(changeBoundsTransition);
                    ft.addSharedElement(view, getString(R.string.transition_poster));
                }
            }

            ft.replace(R.id.fragment_detail, fragment, DetailFragment.DETAIL_FRAGMENT_TAG).commit();
        } else if (isItemClicked) {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.KEY_MOVIE, movie);
            intent.putExtra(DetailFragment.KEY_VIDEOS, videos);
            intent.putExtra(DetailFragment.KEY_REVIEWS, reviews);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.transition_poster));
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }

        }
    }
}
