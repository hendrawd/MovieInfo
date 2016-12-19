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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.adapter.MovieAdapter;
import hendrawd.ganteng.movieinfo.db.DbHelper;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.view.GridSpacingItemDecoration;

/**
 * @author hendrawd on 11/18/16
 */

public class FavoriteActivity extends AppCompatActivity {

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler.layout";
    private static final String BUNDLE_MOVIE_DATA = "movie.data";
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        ButterKnife.bind(this);

        setupRecyclerView();

        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle("Favorite movies");
        }

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    protected void onResume() {
        loadFromDb();
        super.onResume();
    }

    private void showRecyclerViewData(List<Movie> movieList) {
        if (movieList != null && !movieList.isEmpty()) {
            recyclerView.setAdapter(new MovieAdapter(movieList));
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
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

    private void loadFromDb() {
        List<Movie> movieList = DbHelper.getFavoriteMovies();
        showRecyclerViewData(movieList);
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

        MovieAdapter movieAdapter = (MovieAdapter) recyclerView.getAdapter();
        if (movieAdapter != null)
            outState.putParcelableArrayList(BUNDLE_MOVIE_DATA, new ArrayList<>(movieAdapter.getData()));
    }
}
