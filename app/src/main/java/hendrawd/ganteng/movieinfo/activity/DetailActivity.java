package hendrawd.ganteng.movieinfo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.db.DbHelper;
import hendrawd.ganteng.movieinfo.network.GsonRequest;
import hendrawd.ganteng.movieinfo.network.NetworkChecker;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.VolleySingleton;
import hendrawd.ganteng.movieinfo.network.response.GetReviewList;
import hendrawd.ganteng.movieinfo.network.response.GetVideoList;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.network.response.Review;
import hendrawd.ganteng.movieinfo.network.response.Video;
import hendrawd.ganteng.movieinfo.util.GenreMapper;
import hendrawd.ganteng.movieinfo.util.Logger;
import hendrawd.ganteng.movieinfo.util.TextToSpeechHelper;
import hendrawd.ganteng.movieinfo.util.Util;
import hendrawd.ganteng.movieinfo.view.AutoFitImageView;
import hendrawd.ganteng.movieinfo.view.ContinuedLineView;
import hendrawd.ganteng.movieinfo.view.CustomToast;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static hendrawd.ganteng.movieinfo.activity.YoutubePlayerActivity.KEY_VIDEO_ID;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.iv_backdrop_image)
    AutoFitImageView ivBackdropImage;
    @BindView(R.id.tv_overview)
    TextView tvOverview;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.rating_bar)
    MaterialRatingBar ratingBar;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.ll_video_container)
    LinearLayout llVideoContainer;
    @BindView(R.id.tv_related_videos)
    TextView tvRelatedVideos;
    @BindView(R.id.tv_genre)
    TextView tvGenre;
    @BindView(R.id.iv_speaker)
    ImageView ivSpeaker;
    @BindView(R.id.pb_text_to_speech)
    ProgressBar pbTextToSpeech;
    @BindView(R.id.tv_original_title)
    TextView tvOriginalTitle;
    @BindView(R.id.tv_latest_reviews)
    TextView tvLatestReviews;
    @BindView(R.id.ll_review_container)
    LinearLayout llReviewContainer;

    private TextToSpeechHelper textToSpeechHelper;
    private String firstVideoKey;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //change the speaker image color to white
        ivSpeaker.setColorFilter(Color.WHITE);

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
            actionbar.setDisplayHomeAsUpEnabled(true);

        showData();
    }

    private Movie getMovieData() {
        return getIntent().getParcelableExtra("movie data");
    }

    private void showData() {
        Movie movieData = getMovieData();

        collapsingToolbar.setTitle(movieData.getTitle());
        collapsingToolbar.setExpandedTitleColor(Util.getColor(this, R.color.colorAccent));
        collapsingToolbar.setContentScrimColor(Color.parseColor("#aa000000"));

        //set backdrop image glide
        String backdropPath = movieData.getBackdrop_path();
        if (TextUtils.isEmpty(backdropPath)) {
            ivBackdropImage.setImageResource(R.drawable.error_landscape);
        } else {
            Glide.with(ivBackdropImage.getContext())
                    .load(UrlComposer.getBackdropUrl(backdropPath))
                    .placeholder(R.drawable.placeholder_landscape)
                    .error(R.drawable.error_landscape)
                    .into(ivBackdropImage);
        }

        tvOriginalTitle.setText(movieData.getOriginal_title());
        tvOverview.setText(movieData.getOverview());
        tvReleaseDate.setText(movieData.getRelease_date());
        float rating = movieData.getVote_average() / 2;
        String sRating = String.format(Locale.US, "%.2f", rating);
        tvRating.setText(getString(R.string.rating_number, sRating));
        ratingBar.setRating(rating);

        //set genres text
        if (GenreMapper.isInitialized()) {
            int[] genreIds = movieData.getGenre_ids();
            if (genreIds != null && genreIds.length > 0) {
                int genreIdsLength = genreIds.length;
                String sGenres = "";
                for (int i = 0; i < genreIdsLength; i++) {
                    sGenres += GenreMapper.get(genreIds[i]);
                    if (i != genreIdsLength - 1) {
                        sGenres += ", ";
                    }
                }
                tvGenre.setText(sGenres);
            } else {
                tvGenre.setText(R.string.no_information);
            }
        } else {
            tvGenre.setText(R.string.no_information);
        }

        if (NetworkChecker.isNetworkAvailable(this)) {
            String movieId = movieData.getId();
            getVideoData(movieId);
            getReviewData(movieId);
        } else {
            CustomToast.show(DetailActivity.this, getString(R.string.no_internet_connection));
        }
    }

    private void getVideoData(String movieId) {
        if (!TextUtils.isEmpty(movieId)) {
            final GsonRequest<GetVideoList> gsonRequest = new GsonRequest<>(
                    UrlComposer.getVideoUrl(this, movieId),
                    GetVideoList.class,
                    null,
                    new Response.Listener<GetVideoList>() {
                        @Override
                        public void onResponse(GetVideoList videoResponse) {
                            List<Video> videos = videoResponse.getResults();
                            if (videos != null && !videos.isEmpty()) {
                                tvRelatedVideos.setVisibility(View.VISIBLE);
                                llVideoContainer.removeAllViews();
                                int videosLength = videos.size();
                                for (int i = 0; i < videosLength; i++) {
                                    Video video = videos.get(i);
                                    if (i == 0) {
                                        firstVideoKey = video.getKey();
                                    }
                                    generateVideoView(video, i == (videosLength - 1));
                                }
                            } else {
                                tvRelatedVideos.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (volleyError != null) {
                                CustomToast.show(DetailActivity.this, volleyError.getMessage());
                                Logger.e("GetVideoList", volleyError.getMessage());
                            }
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(gsonRequest);
        }
    }

    private void getReviewData(String movieId) {
        if (!TextUtils.isEmpty(movieId)) {
            final GsonRequest<GetReviewList> gsonRequest = new GsonRequest<>(
                    UrlComposer.getReviewUrl(this, movieId, 1),
                    GetReviewList.class,
                    null,
                    new Response.Listener<GetReviewList>() {
                        @Override
                        public void onResponse(GetReviewList reviewResponse) {
                            List<Review> reviews = reviewResponse.getResults();
                            if (reviews != null && !reviews.isEmpty()) {
                                tvLatestReviews.setVisibility(View.VISIBLE);
                                llReviewContainer.removeAllViews();
                                int reviewsLength = reviews.size();
                                for (int i = 0; i < reviewsLength; i++) {
                                    Review review = reviews.get(i);
                                    generateReviewView(review, i == (reviewsLength - 1));
                                }
                            } else {
                                tvLatestReviews.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            if (volleyError != null) {
                                CustomToast.show(DetailActivity.this, volleyError.getMessage());
                                Logger.e("GetReviewList", volleyError.getMessage());
                            }
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(gsonRequest);
        }
    }

    private void generateVideoView(final Video video, boolean lastItem) {
        final ViewGroup nullParent = null;
        LayoutInflater inflater = LayoutInflater.from(this);
        View row = inflater.inflate(R.layout.row_video, nullParent);
        AutoFitImageView ivThumbnail = (AutoFitImageView) row.findViewById(R.id.iv_thumbnail);
        TextView tvName = (TextView) row.findViewById(R.id.tv_name);
        TextView tvType = (TextView) row.findViewById(R.id.tv_type);

        //load image glide
        String videoKey = video.getKey();
        if (TextUtils.isEmpty(videoKey)) {
            ivThumbnail.setImageResource(R.drawable.error_landscape);
        } else {
            Glide.with(ivThumbnail.getContext())
                    .load(UrlComposer.getYoutubeThumbnail(video.getKey()))
                    .error(R.drawable.error_landscape)
                    //.placeholder(R.drawable.placeholder_landscape)
                    .into(ivThumbnail);
        }

        //set info texts
        tvName.setText(video.getName());
        tvType.setText(getString(R.string.video_type, video.getType()));

        //set click action
        row.findViewById(R.id.main_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playYoutube(video.getKey());
            }
        });

        llVideoContainer.addView(row);

        if (!lastItem) {
            llVideoContainer.addView(getLineView());
        }
    }

    private void generateReviewView(final Review review, boolean lastItem) {
        final ViewGroup nullParent = null;
        LayoutInflater inflater = LayoutInflater.from(this);
        View row = inflater.inflate(R.layout.row_review, nullParent);
        TextView tvAuthor = (TextView) row.findViewById(R.id.tv_author);
        TextView tvContent = (TextView) row.findViewById(R.id.tv_content);

        //set info texts
        tvAuthor.setText(getString(R.string.review_author, review.getAuthor()));
        tvContent.setText(review.getContent());

        //set click action
        row.findViewById(R.id.main_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.openUrl(review.getUrl(), DetailActivity.this);
            }
        });

        llReviewContainer.addView(row);

        if (!lastItem) {
            llReviewContainer.addView(getLineView());
        }
    }

    private View getLineView() {
        ContinuedLineView continuedLineView = new ContinuedLineView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Util.dp2px(1, this));
        continuedLineView.setLayoutParams(params);
        continuedLineView.setLineColor(Util.getColor(this, R.color.colorAccent));
        return continuedLineView;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_detail:
                String shareBody = "[" + getString(R.string.app_name) + "] \nMovie name : " + collapsingToolbar.getTitle() + "\n\n" +
                        tvOverview.getText().toString() + "\n\n" +
                        "\u2605 " + tvRating.getText().toString() + "\n" +
                        "Release date : " + tvReleaseDate.getText().toString() + "\n";
                Util.shareTextUrl(shareBody, getString(R.string.app_name), this);
                return true;
            case R.id.menu_first_trailer:
                if (firstVideoKey != null) {
                    Util.shareTextUrl("[" + getString(R.string.app_name) + "] \nhttps://www.youtube.com/watch?v=" + firstVideoKey, getString(R.string.app_name), this);
                } else {
                    CustomToast.show(this, getString(R.string.no_trailers));
                }
                return true;
            case R.id.menu_favorite:
                final Movie movieData = getMovieData();
                if (DbHelper.movieInDb(movieData)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmation")
                            .setMessage("Do you want to remove this movie from your favorite list?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DbHelper.deleteFavoriteMovie(movieData.getId());
                                    CustomToast.show(DetailActivity.this, movieData.getTitle() + " has been removed from your favorite movie list");
                                    item.setTitle(getString(R.string.favorite));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle("Confirmation")
                            .setMessage("Do you want to add this movie to your favorite list?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int responseInsert = DbHelper.insertFavoriteMovie(movieData);
                                    switch (responseInsert) {
                                        case DbHelper.ERROR_DUPLICATE_DATA:
                                            CustomToast.show(DetailActivity.this,
                                                    "Movie already favorited");
                                            break;
                                        case DbHelper.ERROR_TOO_MANY_DATA:
                                            CustomToast.show(DetailActivity.this,
                                                    "Favorite movies can't be more than 20, please unfavorite other movie first");
                                            break;
                                        case DbHelper.SUCCESS:
                                            CustomToast.show(DetailActivity.this,
                                                    movieData.getTitle() + " has been added to your favorite movie list");
                                            item.setTitle(getString(R.string.unfavorite));
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        if (DbHelper.movieInDb(getMovieData()))
            menu.findItem(R.id.menu_favorite).setTitle(getString(R.string.unfavorite));
        else
            menu.findItem(R.id.menu_favorite).setTitle(getString(R.string.favorite));

        return true;
    }

    private void playYoutube(String videoId) {
        if (Util.isPackageInstalled("com.google.android.youtube", getPackageManager())) {
            //kalau youtube terinstall
            Intent openYoutubePlayerActivity = new Intent(this, YoutubePlayerActivity.class);
            openYoutubePlayerActivity.putExtra(KEY_VIDEO_ID, videoId);
            startActivity(openYoutubePlayerActivity);
        } else {
            //kalau youtube tidak terinstall
            String youtubeUrl = "https://www.youtube.com/watch?v=" + videoId;
            Util.openUrl(youtubeUrl, this);
        }
    }

    @OnClick(R.id.iv_speaker)
    public void onClick() {
        pbTextToSpeech.setVisibility(View.VISIBLE);
        String text = tvOverview.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            if (textToSpeechHelper == null) {
                textToSpeechHelper = new TextToSpeechHelper();
                textToSpeechHelper.setCallback(new TextToSpeechHelper.SpeakCallback() {
                    @Override
                    public void onSpeak() {
                        pbTextToSpeech.setVisibility(View.GONE);
                    }
                });
            }
            textToSpeechHelper.speak(this, text);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (textToSpeechHelper != null) {
            textToSpeechHelper.stop();
        }
    }
}
