package hendrawd.ganteng.movieinfo.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.util.Util;

/**
 * @author hendrawd on 11/17/16
 */

public class YoutubePlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final String KEY_VIDEO_ID = "videoId";
    private static final int RECOVERY_REQUEST = 1;

    @BindView(R.id.youtube_player)
    YouTubePlayerView youtubePlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_player);
        ButterKnife.bind(this);

        youtubePlayer.initialize(getString(R.string.youtube_api_key), this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            //Plays https://www.youtube.com/watch?v=<<VIDEO ID>>
            //example: https://www.youtube.com/watch?v=fhWaJi1Hs
            youTubePlayer.cueVideo(getIntent().getStringExtra(KEY_VIDEO_ID));
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String youtubeUrl = "https://www.youtube.com/watch?v=" + getIntent().getStringExtra(KEY_VIDEO_ID);
            Util.openUrl(youtubeUrl, this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            youtubePlayer.initialize(getString(R.string.youtube_api_key), this);
        }
    }
}
