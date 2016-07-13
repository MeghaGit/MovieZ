package com.example.watchit.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.watchit.R;
import com.example.watchit.WatchIt;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

/**
 * Created by Megha on 6/30/2016.
 */
public class VideoFragment extends YouTubePlayerFragment implements YouTubePlayer.OnInitializedListener {

    public static String TAG = VideoFragment.class.getSimpleName();
    public static final String KEY_VIDEO_KEY = "video_key";

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    private String video_key = "";

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        video_key = getArguments().getString(KEY_VIDEO_KEY);

        // Initializing video player with developer key
        if (null != video_key) {
            initialize(WatchIt.YouTubeConfig.DEVELOPER_KEY, this);
        }


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.err_player), errorReason.toString());
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            player.cueVideo(video_key);

            // Hiding player controls
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            initialize(WatchIt.YouTubeConfig.DEVELOPER_KEY, this);
        }
    }

}
