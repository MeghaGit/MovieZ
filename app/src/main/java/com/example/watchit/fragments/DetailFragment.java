package com.example.watchit.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.watchit.ColorUtils;
import com.example.watchit.Logger;
import com.example.watchit.R;
import com.example.watchit.Utilities;
import com.example.watchit.WatchIt;
import com.example.watchit.activities.DetailActivity;
import com.example.watchit.model.Favourites;
import com.example.watchit.model.Favourites_Table;
import com.example.watchit.model.Review;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by Megha on 6/29/2016.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    public static final String DETAIL_FRAGMENT_TAG = "detail_fragment";
    public static final String KEY_MOVIE = "movie";
    public static final String KEY_VIDEOS = "videos";
    public static final String KEY_REVIEWS = "reviews";
    private static final String TAG = DetailFragment.class.getSimpleName();

    private CollapsingToolbarLayout mCollapsingToolbar = null;
    private ImageView mPoster, mToolbarImage;
    private FloatingActionButton mFavourite;
    private TextView mReleaseYear, mRating, mDuration, mReleaseDate, mGener, mPlot, mReadMore, mReadLess;
    private ViewSwitcher mSwitcherReadLines;
    private View mReviewsSection;
    private Button mShare;
    private LinearLayout mLayoutReviews;

    private MovieDb mMovie;
    private ArrayList<com.example.watchit.model.Video> mVideos;
    private ArrayList<Review> mReviews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            mMovie = (MovieDb) getArguments().get(KEY_MOVIE);
            mVideos = (ArrayList<com.example.watchit.model.Video>) getArguments().get(KEY_VIDEOS);
            mReviews = (ArrayList<Review>) getArguments().get(KEY_REVIEWS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {

        mCollapsingToolbar = (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        mToolbarImage = (ImageView) rootView.findViewById(R.id.imageView_toolbar);
        mFavourite = (FloatingActionButton) rootView.findViewById(R.id.fab_favourite);
        mPoster = (ImageView) rootView.findViewById(R.id.imageView_poster);
        mReleaseYear = (TextView) rootView.findViewById(R.id.textView_release_year);
        mRating = (TextView) rootView.findViewById(R.id.textView_rating);
        mDuration = (TextView) rootView.findViewById(R.id.textView_duration);
        mReleaseDate = (TextView) rootView.findViewById(R.id.textView_release_date);
        mGener = (TextView) rootView.findViewById(R.id.textView_genre);
        mPlot = (TextView) rootView.findViewById(R.id.textView_plot);
        mReadMore = (TextView) rootView.findViewById(R.id.textView_read_more);
        mReadLess = (TextView) rootView.findViewById(R.id.textView_read_less);
        mSwitcherReadLines = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher_read_lines);
        mReviewsSection = rootView.findViewById(R.id.view_section_reviews);
        mLayoutReviews = (LinearLayout) rootView.findViewById(R.id.layout_reviews);
        mShare = (Button) rootView.findViewById(R.id.imageButton_share);

        mReadMore.setOnClickListener(this);
        mReadLess.setOnClickListener(this);
        mShare.setOnClickListener(this);

        if (null != mMovie) {
            setMovieDetail();
        }

        if (getActivity().getClass().equals(DetailActivity.class)) {
            setupActionBar(rootView);
        }
    }

    private void setupActionBar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_detail);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setMovieDetail() {

        //setup collapsing toolbar with a title & an image
        mCollapsingToolbar.setTitle(mMovie.getTitle());
        Picasso.with(getActivity()).load(WatchIt.TMD_IMAGE_BASE_URL + mMovie.getBackdropPath()).into(mToolbarImage);

        try {
            Bitmap backdrop_bitmap = new GetBitmapTask().execute(WatchIt.TMD_IMAGE_BASE_URL + mMovie.getBackdropPath()).get();
            int scripColor =
                    ColorUtils.getAverageColor(backdrop_bitmap);
            mCollapsingToolbar.setContentScrimColor(scripColor);


            //set status bar color accordingly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && 0 != scripColor) {
                getActivity().
                        getWindow().
                        setStatusBarColor(
                                Color.parseColor(
                                        ColorUtils.changeColorHSB(scripColor)));
            }
        } catch (InterruptedException |
                ExecutionException e) {
            Logger.e(TAG, e.getMessage());
        }


        //set poster
        Picasso.with(getActivity()).load(WatchIt.TMD_IMAGE_BASE_URL + mMovie.getPosterPath()).placeholder(R.drawable.poster_placeholder).error(R.drawable.poster_not_avail).into(mPoster);

        //setup favourite icon
        mFavourite.setOnClickListener(null);
        List<Favourites> favs = new Select().from(Favourites.class).where(Favourites_Table.id.is(mMovie.getId())).queryList();
        if (null != favs && 0 < favs.size()) {
            mFavourite.setSelected(true);
        }
        mFavourite.setOnClickListener(this);

        mReleaseYear.setText(Utilities.getYearFromDate(mMovie.getReleaseDate()));
        mRating.setText(getString(R.string.rating, mMovie.getVoteAverage()));

        if (0 < mMovie.getRuntime()) {
            mDuration.setText(Html.fromHtml("<i>" + String.valueOf(mMovie.getRuntime() + "mins</i>")));
        } else {
            mDuration.setText(Html.fromHtml("<i>120mins</i>"));
        }
        mReleaseDate.setText(Utilities.formatDate(mMovie.getReleaseDate()));

        if (null != mMovie.getGenres() && 0 < mMovie.getGenres().size()) {
            StringBuffer genres = new StringBuffer();
            String delim = "";
            for (Genre genre : mMovie.getGenres()) {
                genres.append(delim + genre.getName());
                delim = ", ";
            }
            mGener.setText(genres.toString());
        }

        mPlot.setText(mMovie.getOverview());
        mSwitcherReadLines.setVisibility(View.VISIBLE);
        Logger.d(TAG, "\nvote average: " + mMovie.getVoteAverage());

        //No need of "Read more" if lines are <= 3
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (3 < mPlot.getLineCount()) {
                    mPlot.setMaxLines(3);
                } else {
                    mSwitcherReadLines.setVisibility(View.GONE);
                }
            }
        }, 5);

        VideoFragment fragment = new VideoFragment();

        Bundle bundle = new Bundle();
        if (null != mVideos && 0 < mVideos.size()) {
            bundle.putString(VideoFragment.KEY_VIDEO_KEY, mVideos.get(0).getKey());
        }
        fragment.setArguments(bundle);

        //ToDo add support for API < 17
        getChildFragmentManager().beginTransaction().add(R.id.fragment_youtube_player, fragment, VideoFragment.TAG).commit();

        //set reviews
        if (null != mReviews && mReviews.size() > 0) {

            mReviewsSection.setVisibility(View.VISIBLE);
            for (Review review : mReviews) {
                View vi = getActivity().getLayoutInflater().inflate(R.layout.list_item_review, null);
                TextView author = (TextView) vi.findViewById(R.id.textView_review_author);
                TextView content = (TextView) vi.findViewById(R.id.textView_review_content);
                author.setText(review.getAuthor());
                content.setText(review.getContent());
                mLayoutReviews.addView(vi);
            }
        }
    }

    private class GetBitmapTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return ColorUtils.getBitmapFromURL(params[0]);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.textView_read_more: {
                mPlot.setMaxLines(Integer.MAX_VALUE);
                mSwitcherReadLines.setDisplayedChild(1);
                break;

            }
            case R.id.textView_read_less: {
                mPlot.setMaxLines(3);
                mSwitcherReadLines.setDisplayedChild(0);
                break;
            }
            case R.id.fab_favourite: {

                boolean isFav = !mFavourite.isSelected();
                if (isFav) {
                    addToFavourites();
                    Snackbar.make(mFavourite, getString(R.string.added_to_fav), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    removeFromFavourites();
                                    mFavourite.setSelected(false);
                                }
                            })
                            .setActionTextColor(Color.GREEN)
                            .show();
                } else {
                    removeFromFavourites();
                    Snackbar.make(mFavourite, getString(R.string.removed_from_fav), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addToFavourites();
                                    mFavourite.setSelected(true);
                                }
                            })
                            .setActionTextColor(getResources().getColor(R.color.slackbarActionNegative))
                            .show();
                }
                mFavourite.setSelected(isFav);
                break;
            }
            case R.id.imageButton_share: {
                Intent i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.text_share, Utilities.getMovieWebPage(mMovie.getId(), mMovie.getTitle())));
                startActivity(Intent.createChooser(i, "Share via"));
                break;
            }
        }
    }

    private void addToFavourites() {

        Favourites fav = new Favourites();

        fav.setId(mMovie.getId());
        fav.setTitle(mMovie.getTitle());
        fav.setPoster_path(mMovie.getPosterPath());
        fav.setBackdrop_path(mMovie.getBackdropPath());
        fav.setPlot(mMovie.getOverview());
        fav.setRelease_date(mMovie.getReleaseDate());
        fav.setUser_rating(String.valueOf(mMovie.getVoteAverage()));

        fav.save();

    }

    private void removeFromFavourites() {

        List<Favourites> favs = new Select().from(Favourites.class).where(Favourites_Table.id.is(mMovie.getId())).queryList();
        if (null != favs && 0 < favs.size()) {
            favs.get(0).delete();
        }

    }
}
