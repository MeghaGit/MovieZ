package com.example.watchit.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.watchit.R;
import com.example.watchit.fragments.DetailFragment;
import com.example.watchit.model.Review;
import com.example.watchit.model.Video;

import java.util.ArrayList;

import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Megha on 6/27/2016.
 */
public class DetailActivity extends AppCompatActivity {


    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieDb movie = (MovieDb) getIntent().getExtras().get(DetailFragment.KEY_MOVIE);
        ArrayList<Video> videos = (ArrayList<Video>) getIntent().getExtras().get(DetailFragment.KEY_VIDEOS);
        ArrayList<Review> reviews = (ArrayList<Review>) getIntent().getExtras().get(DetailFragment.KEY_REVIEWS);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DetailFragment.KEY_MOVIE, movie);
        bundle.putParcelableArrayList(DetailFragment.KEY_VIDEOS, videos);
        bundle.putParcelableArrayList(DetailFragment.KEY_REVIEWS, reviews);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(bundle);


// Defines enter transition only for shared element
//        ChangeBounds changeBoundsTransition = TransitionInflater.from(this).inflateTransition(R.transition.change_bounds);
//        setSharedElementEnterTransition(changeBoundsTransition);
//ft.addSharedElement()
        ft.add(R.id.fragment_detail, fragment, TAG).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
