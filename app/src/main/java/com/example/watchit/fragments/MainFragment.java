package com.example.watchit.fragments;


import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.watchit.Logger;
import com.example.watchit.R;
import com.example.watchit.Utilities;
import com.example.watchit.WatchIt;
import com.example.watchit.adapters.ImageAdapter;
import com.example.watchit.model.Favourites;
import com.example.watchit.model.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizlabs.android.dbflow.runtime.FlowContentObserver;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import info.movito.themoviedbapi.model.Video;
import info.movito.themoviedbapi.model.config.TmdbConfiguration;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by Megha on 6/23/2016.
 */
public class MainFragment extends Fragment implements AdapterView.OnItemClickListener, FlowContentObserver.OnModelStateChangedListener {

    public static final String TAG = "main_fragment";

    private Toolbar mToolbar;
    private GridView mGridView;
    private ProgressBar mProgressBar;

    private TmdbApi mTMDBapi;
    private List<MovieDb> movies = null;
    private FlowContentObserver observer = new FlowContentObserver();

    public MainFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView_movies);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        mGridView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setupActionBar();

        if (null == movies)
            fetchMovies();
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    private void fetchMovies() {

        movies = null;
        mProgressBar.setVisibility(View.VISIBLE);

        int sorting_order = Integer.parseInt(Utilities.getPreferredSorting(getActivity()));

        switch (sorting_order) {
            case 1://MOST_POPULAR
            case 2://HIGHEST_RATED
            case 3://UPCOMING
                observer.unregisterForContentChanges(getActivity());
                if (Utilities.isNetworkAvailable(getActivity())) {
                    mGridView.invalidate();
                    new FetchMovies().execute();
                } else {
                    mGridView.invalidate();
                    mGridView.setAdapter(null);
                    mProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), getString(R.string.err_no_internet_connection), Toast.LENGTH_LONG).show();
                }
                break;
            case 4: {//FAVOURITE_MOVIES

                initFetchingFavMovies();
                observer.registerForContentChanges(getActivity(), Favourites.class);
                observer.addModelChangeListener(MainFragment.this);
                observer.setNotifyAllUris(true);
                break;
            }


        }


    }

    private void initFetchingFavMovies() {
        mToolbar.setTitle(R.string.title_favourite_movies);
        ArrayList<String> poster_urls = new ArrayList<>();
        try {
            movies = new FetchFavsFromLocal().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.e(TAG, e.getMessage());
        }
        if (null == movies) {
            Toast.makeText(getActivity(), getString(R.string.err_no_fav_movies), Toast.LENGTH_LONG).show();
            mGridView.setAdapter(null);
            return;
        }

        for (int i = 0; i < movies.size(); i++) {
            MovieDb movie = movies.get(i);
            poster_urls.add(movie.getPosterPath());
        }

        mGridView.setAdapter(new ImageAdapter(getActivity(), poster_urls));

        //show first movies detail for twoPane screens
        ((Callback) getActivity()).onShowDetails(movies.get(0), null, null, null, false);
    }

    private class FetchMovies extends AsyncTask<Void, String, ArrayList<String>> {

        String title;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            int sorting_order = Integer.parseInt(Utilities.getPreferredSorting(getActivity()));
            ArrayList<Video> tmdb_videos = null;
            ArrayList<Reviews> tmdb_reviews = null;

            ArrayList<String> poster_urls = new ArrayList<>();
            TmdbConfiguration config = new TmdbConfiguration();
            config.setBaseUrl(WatchIt.TMD_BASE_URL);
            mTMDBapi = new TmdbApi(WatchIt.TMD_API_KEY);

            switch (sorting_order) {

                case 1://MOST_POPULAR
                    title = getString(R.string.title_popular_movies);
                    movies = mTMDBapi.getMovies().getPopularMovies("en", 0).getResults();
                    break;

                case 2://HIGHEST_RATED
                    title = getString(R.string.title_top_rated);
                    movies = mTMDBapi.getMovies().getTopRatedMovies("en", 0).getResults();
                    break;

                case 3://UPCOMING
                    title = getString(R.string.title_upcoming);
                    movies = mTMDBapi.getMovies().getUpcoming("en", 0).getResults();
                    break;
            }

            for (int i = 0; i < movies.size(); i++) {
                MovieDb movie = movies.get(i);
                poster_urls.add(movie.getPosterPath());
            }


            //show first movies detail for twoPane screens
            tmdb_videos = (ArrayList<Video>) mTMDBapi.getMovies().getVideos(movies.get(0).getId(), "en");
            tmdb_reviews = (ArrayList<Reviews>) mTMDBapi.getReviews().getReviews(movies.get(0).getId(), "en", 0).getResults();
            ((Callback) getActivity()).onShowDetails(movies.get(0), fetchVideos(tmdb_videos), fetchReviews(tmdb_reviews), null, false);

            return poster_urls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> urls) {
            super.onPostExecute(urls);

            mToolbar.setTitle(title);
            mProgressBar.setVisibility(View.GONE);
            mGridView.setAdapter(new ImageAdapter(getActivity(), urls));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieDb movie = movies.get(position);
        new FetchVideos(movie,view).execute();
    }

    private class FetchVideos extends AsyncTask<Void, Void, ArrayList<Video>> {


        MovieDb movie;
        View view;

        FetchVideos(MovieDb movie, View view) {
            this.movie = movie;
            this.view = view;
        }

        @Override
        protected ArrayList<Video> doInBackground(Void... param) {
            ArrayList<Video> tmdb_videos = null;

            if (Utilities.isNetworkAvailable(getActivity())) {

                tmdb_videos = (ArrayList<Video>) mTMDBapi.getMovies().getVideos(this.movie.getId(), "en");
            }

            return tmdb_videos;
        }

        @Override
        protected void onPostExecute(ArrayList<Video> tmdb_videos) {
            new FetchReviews(movie, tmdb_videos, view).execute();
        }
    }

    private class FetchReviews extends AsyncTask<Void, Void, ArrayList<Reviews>> {

        MovieDb movie;
        ArrayList<Video> tmdb_videos;
        View view;

        FetchReviews(MovieDb movie, ArrayList<Video> tmdb_videos, View view) {
            this.movie = movie;
            this.tmdb_videos = tmdb_videos;
            this.view = view;
        }

        @Override
        protected ArrayList<Reviews> doInBackground(Void... param) {
            ArrayList<Reviews> tmdb_reviews = null;
            if (Utilities.isNetworkAvailable(getActivity())) {
                tmdb_reviews = (ArrayList<Reviews>) mTMDBapi.getReviews().getReviews(this.movie.getId(), "en", 0).getResults();
            }
            return tmdb_reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<Reviews> tmdb_reviews) {
            ((Callback) getActivity()).onShowDetails(this.movie, fetchVideos(tmdb_videos), fetchReviews(tmdb_reviews),view, true);
        }
    }

    /**
     * creates parcelable Video objects' list
     *
     * @param tmdb_videos
     * @return
     */
    private ArrayList<com.example.watchit.model.Video> fetchVideos(ArrayList<Video> tmdb_videos) {

        if (null == tmdb_videos)
            return null;

        ArrayList<com.example.watchit.model.Video> videos = new ArrayList<>();
        for (Video tmdb_video : tmdb_videos) {
            String key = tmdb_video.getKey();
            String site = tmdb_video.getSite();
            String name = tmdb_video.getName();
            videos.add(new com.example.watchit.model.Video(key, site, name));
        }
        return videos;
    }

    private ArrayList<Review> fetchReviews(ArrayList<Reviews> tmdb_reviews) {

        if (null == tmdb_reviews)
            return null;

        ArrayList<Review> reviews = new ArrayList<>();
        for (Reviews tmdb_review : tmdb_reviews) {
            String author = tmdb_review.getAuthor();
            String content = tmdb_review.getContent();
            String url = tmdb_review.getUrl();
            String id = tmdb_review.getId();
            reviews.add(new Review(author, content, url, id));
        }
        return reviews;
    }


    @Override
    public void onModelStateChanged(@Nullable Class<? extends Model> table, BaseModel.Action action, @NonNull SQLCondition[] primaryKeyValues) {
        try {
            movies = new FetchFavsFromLocal().execute().get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.e(TAG, e.getMessage());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> poster_urls = new ArrayList<>();

                if (null == movies) {
                    Toast.makeText(getActivity(), getString(R.string.err_no_fav_movies), Toast.LENGTH_LONG).show();
                    mGridView.setAdapter(null);
                    return;
                }

                for (int i = 0; i < movies.size(); i++) {
                    MovieDb movie = movies.get(i);
                    poster_urls.add(movie.getPosterPath());
                }

                mProgressBar.setVisibility(View.GONE);
                mGridView.setAdapter(new ImageAdapter(getActivity(), poster_urls));

            }
        });

    }

    private class FetchFavsFromLocal extends AsyncTask<Void, Void, List<MovieDb>> {
        @Override
        protected List<MovieDb> doInBackground(Void... voids) {


            List<Favourites> favs = new Select().from(Favourites.class).queryList();
            if (null != favs && 0 < favs.size()) {

                StringBuffer json = new StringBuffer();
                String delim = "";
                json.append("{\n" +
                        "  \"page\": 1,\n" +
                        "  \"results\": [\n");
                for (Favourites fav : favs) {

                    json.append(delim +
                            "    {\n" +
                            "      \"poster_path\": \"\\" + fav.getPoster_path() + "\",\n" +
                            "      \"overview\": \"" + fav.getPlot() + "\",\n" +
                            "      \"release_date\": \"" + fav.getRelease_date() + "\",\n" +
                            "      \"id\": " + fav.getId() + ",\n" +
                            "      \"title\": \"" + fav.getTitle() + "\",\n" +
                            "      \"backdrop_path\": \"\\" + fav.getBackdrop_path() + "\",\n" +
                            "      \"vote_average\": " + fav.getUser_rating() + "\n" +
                            "    }");
                    delim = ",";


                }
                json.append("]}");
                try {
                    ObjectMapper jsonMapper = new ObjectMapper();
                    MovieResultsPage page = jsonMapper.readValue(json.toString(), MovieResultsPage.class);
                    movies = page.getResults();
                } catch (IOException e) {
                    Logger.e(TAG, e.getMessage());
                }
            }
            return movies;
        }

        @Override
        protected void onPostExecute(List<MovieDb> movieDbs) {
            super.onPostExecute(movieDbs);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void onSortingOrderChanged() {
        fetchMovies();
    }

    public interface Callback {
        void onShowDetails(MovieDb movie, ArrayList<com.example.watchit.model.Video> videos, ArrayList<Review> reviews, View view, boolean isItemClicked);
    }
}
