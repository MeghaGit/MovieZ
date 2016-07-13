package com.example.watchit.adapters;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.watchit.R;
import com.example.watchit.WatchIt;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Megha on 6/24/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mPoster_URLs;

    // Constructor
    public ImageAdapter(Context c, ArrayList<String> poster_urls) {
        mContext = c;
        mPoster_URLs = poster_urls;
    }

    public int getCount() {
        return mPoster_URLs.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        FrameLayout frameLayout = null;
        ImageView imageView = null;
        GridView gridView = (GridView) parent;
        int width = gridView.getColumnWidth();
        Double height = (width * 1.5);

        if (convertView == null) {

            frameLayout = new FrameLayout(mContext);
            frameLayout.setLayoutParams(new GridView.LayoutParams(width, height.intValue()));
            frameLayout.setPadding(0, 0, 0, 0);

            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height.intValue()));
            imageView.setPadding(0, 0, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                frameLayout.setForeground(mContext.getDrawable(R.drawable.ripple));
                imageView.setTransitionName(mContext.getString(R.string.transition_poster));
            }

            frameLayout.addView(imageView);
        } else {
            frameLayout = (FrameLayout) convertView;
            imageView = (ImageView) frameLayout.getChildAt(0);
        }
        String poster_url = WatchIt.TMD_IMAGE_BASE_URL + mPoster_URLs.get(position);
        Picasso.with(mContext).load(poster_url).placeholder(R.drawable.poster_placeholder).error(R.drawable.poster_not_avail).into(imageView);

        return frameLayout;
    }

}