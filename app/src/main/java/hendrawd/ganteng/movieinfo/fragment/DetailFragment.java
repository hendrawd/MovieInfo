package hendrawd.ganteng.movieinfo.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.activity.YoutubePlayerActivity;
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

public class DetailFragment extends Fragment {
    @BindView(R.id.iv_backdrop_image)
    AutoFitImageView ivBackdropImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tv_original_title)
    TextView tvOriginalTitle;
    @BindView(R.id.tv_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_genre)
    TextView tvGenre;
    @BindView(R.id.tv_rating)
    TextView tvRating;
    @BindView(R.id.rating_bar)
    MaterialRatingBar ratingBar;
    @BindView(R.id.iv_speaker)
    ImageView ivSpeaker;
    @BindView(R.id.pb_text_to_speech)
    ProgressBar pbTextToSpeech;
    @BindView(R.id.tv_overview)
    TextView tvOverview;
    @BindView(R.id.tv_related_videos)
    TextView tvRelatedVideos;
    @BindView(R.id.ll_video_container)
    LinearLayout llVideoContainer;
    @BindView(R.id.tv_latest_reviews)
    TextView tvLatestReviews;
    @BindView(R.id.ll_review_container)
    LinearLayout llReviewContainer;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    @BindView(R.id.button_menu)
    ImageView buttonMenu;

    private TextToSpeechHelper textToSpeechHelper;
    private String firstVideoKey;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainContent = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, mainContent);

        //change the speaker image color to white
        ivSpeaker.setColorFilter(Color.WHITE);

        showData();

        return mainContent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
        if (DbHelper.movieInDb(getMovieData()))
            menu.findItem(R.id.menu_favorite).setTitle(getString(R.string.unfavorite));
        else
            menu.findItem(R.id.menu_favorite).setTitle(getString(R.string.favorite));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail:
                String shareBody = "[" + getString(R.string.app_name) + "] \nMovie name : " + collapsingToolbar.getTitle() + "\n\n" +
                        tvOverview.getText().toString() + "\n\n" +
                        "\u2605 " + tvRating.getText().toString() + "\n" +
                        "Release date : " + tvReleaseDate.getText().toString() + "\n";
                Util.shareTextUrl(shareBody, getString(R.string.app_name), getActivity());
                return true;
            case R.id.menu_first_trailer:
                if (firstVideoKey != null) {
                    Util.shareTextUrl("[" + getString(R.string.app_name) + "] \nhttps://www.youtube.com/watch?v=" + firstVideoKey, getString(R.string.app_name), getActivity());
                } else {
                    CustomToast.show(getActivity(), getString(R.string.no_trailers));
                }
                return true;
            case R.id.menu_favorite:
                final Movie movieData = getMovieData();
                if (DbHelper.movieInDb(movieData)) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Confirmation")
                            .setMessage("Do you want to remove this movie from your favorite list?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DbHelper.deleteFavoriteMovie(movieData.getId());
                                    CustomToast.show(getActivity(), movieData.getTitle() + " has been removed from your favorite movie list");
                                    item.setTitle(getString(R.string.favorite));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Confirmation")
                            .setMessage("Do you want to add this movie to your favorite list?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    int responseInsert = DbHelper.insertFavoriteMovie(movieData);
                                    switch (responseInsert) {
                                        case DbHelper.ERROR_DUPLICATE_DATA:
                                            CustomToast.show(getActivity(),
                                                    "Movie already favorited");
                                            break;
                                        case DbHelper.ERROR_TOO_MANY_DATA:
                                            CustomToast.show(getActivity(),
                                                    "Favorite movies can't be more than 20, please unfavorite other movie first");
                                            break;
                                        case DbHelper.SUCCESS:
                                            CustomToast.show(getActivity(),
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

    private void showData() {
        Movie movieData = getMovieData();
        if (movieData == null)
            return;
        else {
            tvEmpty.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
        }

        collapsingToolbar.setTitle(movieData.getTitle());
        collapsingToolbar.setExpandedTitleColor(Util.getColor(getActivity(), R.color.colorAccent));
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

        if (NetworkChecker.isNetworkAvailable(getActivity())) {
            String movieId = movieData.getId();
            getVideoData(movieId);
            getReviewData(movieId);
        } else {
            CustomToast.show(getActivity(), getString(R.string.no_internet_connection));
        }
    }

    private void getVideoData(String movieId) {
        if (!TextUtils.isEmpty(movieId)) {
            final GsonRequest<GetVideoList> gsonRequest = new GsonRequest<>(
                    UrlComposer.getVideoUrl(getActivity(), movieId),
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
                                CustomToast.show(getActivity(), volleyError.getMessage());
                                Logger.e("GetVideoList", volleyError.getMessage());
                            }
                        }
                    });
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(gsonRequest);
        }
    }

    private void getReviewData(String movieId) {
        if (!TextUtils.isEmpty(movieId)) {
            final GsonRequest<GetReviewList> gsonRequest = new GsonRequest<>(
                    UrlComposer.getReviewUrl(getActivity(), movieId, 1),
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
                                CustomToast.show(getActivity(), volleyError.getMessage());
                                Logger.e("GetReviewList", volleyError.getMessage());
                            }
                        }
                    });
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(gsonRequest);
        }
    }

    private void generateVideoView(final Video video, boolean lastItem) {
        final ViewGroup nullParent = null;
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
        LayoutInflater inflater = LayoutInflater.from(getActivity());
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
                Util.openUrl(review.getUrl(), getActivity());
            }
        });

        llReviewContainer.addView(row);

        if (!lastItem) {
            llReviewContainer.addView(getLineView());
        }
    }

    private View getLineView() {
        ContinuedLineView continuedLineView = new ContinuedLineView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) Util.dp2px(1, getActivity()));
        continuedLineView.setLayoutParams(params);
        continuedLineView.setLineColor(Util.getColor(getActivity(), R.color.colorAccent));
        return continuedLineView;
    }

    private Movie getMovieData() {
        if (getArguments() != null)
            return getArguments().getParcelable("movie data");
        else
            return null;
    }

    private void playYoutube(String videoId) {
        if (Util.isPackageInstalled("com.google.android.youtube", getActivity().getPackageManager())) {
            //kalau youtube terinstall
            Intent openYoutubePlayerActivity = new Intent(getActivity(), YoutubePlayerActivity.class);
            openYoutubePlayerActivity.putExtra(KEY_VIDEO_ID, videoId);
            startActivity(openYoutubePlayerActivity);
        } else {
            //kalau youtube tidak terinstall
            String youtubeUrl = "https://www.youtube.com/watch?v=" + videoId;
            Util.openUrl(youtubeUrl, getActivity());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (textToSpeechHelper != null) {
            textToSpeechHelper.stop();
        }
    }

    @OnClick({R.id.button_menu, R.id.iv_speaker})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_menu:
                showListMenu(view);
                break;
            case R.id.iv_speaker:
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
                    textToSpeechHelper.speak(getActivity(), text);
                }
                break;
        }
    }

    private void showListMenu(View anchor) {
        final ListPopupWindow popupWindow = new ListPopupWindow(getActivity());

        List<HashMap<String, String>> data = new ArrayList<>();

        HashMap<String, String> map1 = new HashMap<>();
        map1.put("menu", "Share Detail");

        HashMap<String, String> map2 = new HashMap<>();
        map2.put("menu", "Share First Trailer");

        data.add(map1);
        data.add(map2);

        final Movie movieData = getMovieData();
        HashMap<String, String> map3 = new HashMap<>();
        if (DbHelper.movieInDb(movieData)) {
            map3.put("menu", getString(R.string.unfavorite));
        } else {
            map3.put("menu", getString(R.string.favorite));
        }
        data.add(map3);

        ListAdapter adapter = new SimpleAdapter(
                getActivity(),
                data,
                android.R.layout.simple_list_item_1, // You may want to use your own cool layout
                new String[]{"menu"}, // These are just the keys that the data uses
                new int[]{android.R.id.text1}); // The view ids to map the data to

        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(adapter);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        popupWindow.setWidth(displaymetrics.widthPixels / 3); // note: don't use pixels, use a dimen resource

        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        String shareBody = "[" + getString(R.string.app_name) + "] \nMovie name : " + collapsingToolbar.getTitle() + "\n\n" +
                                tvOverview.getText().toString() + "\n\n" +
                                "\u2605 " + tvRating.getText().toString() + "\n" +
                                "Release date : " + tvReleaseDate.getText().toString() + "\n";
                        Util.shareTextUrl(shareBody, getString(R.string.app_name), getActivity());
                        break;
                    case 1:
                        if (firstVideoKey != null) {
                            Util.shareTextUrl("[" + getString(R.string.app_name) + "] \nhttps://www.youtube.com/watch?v=" + firstVideoKey, getString(R.string.app_name), getActivity());
                        } else {
                            CustomToast.show(getActivity(), getString(R.string.no_trailers));
                        }
                        break;
                    case 2:
                        if (DbHelper.movieInDb(movieData)) {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Confirmation")
                                    .setMessage("Do you want to remove this movie from your favorite list?")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            DbHelper.deleteFavoriteMovie(movieData.getId());
                                            CustomToast.show(getActivity(), movieData.getTitle() + " has been removed from your favorite movie list");
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .create()
                                    .show();
                        } else {
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Confirmation")
                                    .setMessage("Do you want to add this movie to your favorite list?")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            int responseInsert = DbHelper.insertFavoriteMovie(movieData);
                                            switch (responseInsert) {
                                                case DbHelper.ERROR_DUPLICATE_DATA:
                                                    CustomToast.show(getActivity(),
                                                            "Movie already favorited");
                                                    break;
                                                case DbHelper.ERROR_TOO_MANY_DATA:
                                                    CustomToast.show(getActivity(),
                                                            "Favorite movies can't be more than 20, please unfavorite other movie first");
                                                    break;
                                                case DbHelper.SUCCESS:
                                                    CustomToast.show(getActivity(),
                                                            movieData.getTitle() + " has been added to your favorite movie list");
                                                    break;
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .create()
                                    .show();
                        }
                        break;
                }
                popupWindow.dismiss();
            }
        }); // the callback for when a list item is selected
        popupWindow.show();
    }
}
