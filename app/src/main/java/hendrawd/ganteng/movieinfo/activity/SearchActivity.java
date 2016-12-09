package hendrawd.ganteng.movieinfo.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.adapter.MovieAdapter;
import hendrawd.ganteng.movieinfo.network.GsonRequest;
import hendrawd.ganteng.movieinfo.network.NetworkChecker;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.VolleySingleton;
import hendrawd.ganteng.movieinfo.network.response.GetMovieList;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.util.EncodingUtil;
import hendrawd.ganteng.movieinfo.util.Logger;
import hendrawd.ganteng.movieinfo.view.CustomToast;
import hendrawd.ganteng.movieinfo.view.GridSpacingItemDecoration;

/**
 * @author hendrawd on 11/18/16
 */

public class SearchActivity extends AppCompatActivity {

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler.layout";
    private static final String BUNDLE_MOVIE_DATA = "movie.data";
    private static final String BUNDLE_LAST_RESPONSE = "last.response";
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;
    public static final String KEY_SEARCH_QUERY = "SEARCH_QUERY";
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;
    private GetMovieList getMovieListResponse;
    private boolean requestingData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        setupRecyclerView();

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Search results");
        }

        if (savedInstanceState == null) {
            requestSearch(1);
        } else {
            getMovieListResponse = savedInstanceState.getParcelable(BUNDLE_LAST_RESPONSE);

            List<Movie> lastMovieList = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIE_DATA);
            if (lastMovieList != null && !lastMovieList.isEmpty()) {
                recyclerView.setAdapter(new MovieAdapter(lastMovieList));
                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }

            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    private String getSearchUrl(int page) {
        String query = getIntent().getStringExtra(KEY_SEARCH_QUERY);
        String url = null;
        if (!TextUtils.isEmpty(query)) {
            String encodedQuery = EncodingUtil.encodeURIComponent(query);
            url = UrlComposer.getSearchUrl(encodedQuery, page);

        }
        return url;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void requestSearch(final int page) {
        if (NetworkChecker.isNetworkAvailable(this)) {
            if (!requestingData) {
                String searchUrl = getSearchUrl(page);
                if (!TextUtils.isEmpty(searchUrl)) {
                    requestingData = true;
                    @SuppressWarnings("unchecked") final GsonRequest gsonRequest = new GsonRequest(
                            searchUrl,
                            GetMovieList.class,
                            null,
                            new Response.Listener<GetMovieList>() {
                                @Override
                                public void onResponse(GetMovieList movieResponse) {
                                    List<Movie> movieList = movieResponse.getResults();
                                    if (movieList == null || movieList.isEmpty()) {
                                        tvEmpty.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                        fab.setVisibility(View.GONE);
                                    } else {
                                        tvEmpty.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                        fab.setVisibility(View.VISIBLE);
                                        if (page == 1) {
                                            recyclerView.setAdapter(new MovieAdapter(movieResponse.getResults()));
                                        } else {
                                            ((MovieAdapter) recyclerView.getAdapter()).append(movieResponse.getResults());
                                        }
                                    }
                                    requestingData = false;
                                    getMovieListResponse = movieResponse;
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    if (volleyError != null) {
                                        CustomToast.show(SearchActivity.this, volleyError.getMessage());
                                        Logger.d("SearchMovieList", volleyError.getMessage());
                                    }
                                    requestingData = false;
                                }
                            });
                    VolleySingleton.getInstance(SearchActivity.this).addToRequestQueue(gsonRequest);
                }
            }
        } else {
            CustomToast.show(SearchActivity.this, getString(R.string.no_internet_connection));
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        int spanCount = SPAN_COUNT_PORTRAIT;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = SPAN_COUNT_LANDSCAPE;
        }

        //make the spacing consistent from an item to another item and to parent
        int spacingPixels = (int) getResources().getDimension(R.dimen.card_margin);
        final boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacingPixels, includeEdge));

        final GridLayoutManager mLayoutManager = new GridLayoutManager(recyclerView.getContext(), spanCount);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                visibleItemCount = gridLayoutManager.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                //end of scroll
                if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                    if (getMovieListResponse.getPage() < getMovieListResponse.getTotal_pages()) {
                        requestSearch(getMovieListResponse.getPage() + 1);
                    }
                }
            }
        });
    }

    @OnClick(R.id.fab)
    public void onClick() {
        //recyclerView.smoothScrollToPosition(0);
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());

        if (getMovieListResponse != null)
            outState.putParcelable(BUNDLE_LAST_RESPONSE, getMovieListResponse);

        MovieAdapter movieAdapter = (MovieAdapter) recyclerView.getAdapter();
        if (movieAdapter != null)
            outState.putParcelableArrayList(BUNDLE_MOVIE_DATA, new ArrayList<>(movieAdapter.getData()));
    }
}
