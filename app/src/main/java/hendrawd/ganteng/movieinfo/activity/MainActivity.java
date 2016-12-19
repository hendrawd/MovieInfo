package hendrawd.ganteng.movieinfo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hendrawd.ganteng.movieinfo.BuildConfig;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.fragment.DetailFragment;
import hendrawd.ganteng.movieinfo.fragment.MovieListFragment;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.view.CustomAlertDialog;
import hendrawd.ganteng.movieinfo.view.CustomToast;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private final String BUNDLE_SEARCH_STRING = "bundle_search_string";

    private MovieListFragment section1Fragment;
    private MovieListFragment section2Fragment;
    private MovieListFragment section3Fragment;

    private DetailFragment detailFragment;

    private String searchString;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save the fragments's instance
        getSupportFragmentManager().putFragment(outState, "section1Fragment", section1Fragment);
        getSupportFragmentManager().putFragment(outState, "section2Fragment", section2Fragment);
        getSupportFragmentManager().putFragment(outState, "section3Fragment", section3Fragment);
        outState.putString(BUNDLE_SEARCH_STRING, searchString);
        if (getResources().getBoolean(R.bool.isTablet)) {
            getFragmentManager().putFragment(outState, "detailFragment", detailFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            section1Fragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "section1Fragment");
            section2Fragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "section2Fragment");
            section3Fragment = (MovieListFragment) getSupportFragmentManager().getFragment(savedInstanceState, "section3Fragment");
            searchString = savedInstanceState.getString(BUNDLE_SEARCH_STRING);
            if (getResources().getBoolean(R.bool.isTablet)) {
                detailFragment = (DetailFragment) getFragmentManager().getFragment(savedInstanceState, "detailFragment");
            }
        } else {
            section1Fragment = createMovieListFragment(UrlComposer.CATEGORY_POPULAR);
            section2Fragment = createMovieListFragment(UrlComposer.CATEGORY_TOP_RATED);
            section3Fragment = createMovieListFragment(UrlComposer.CATEGORY_UPCOMING);
            if (getResources().getBoolean(R.bool.isTablet)) {
                changeDetailFragment(null);
            }
        }

        setSupportActionBar(toolbar);
        setupViewPager();
        tabs.setupWithViewPager(viewPager);
    }

    public void changeDetailFragment(Movie movieData) {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        detailFragment = new DetailFragment();
        Bundle data = new Bundle();
        data.putParcelable("movie data", movieData);
        detailFragment.setArguments(data);
        fragmentTransaction.replace(R.id.fragment_detail, detailFragment);
        fragmentTransaction.commit();
    }

    public void openDetailActivity(Movie movieData) {
        Intent openDetailIntent = new Intent(this, DetailActivity.class);
        openDetailIntent.putExtra("movie data", movieData);
        this.startActivity(openDetailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem menuItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) menuItem.getActionView();

        //set hint
        searchView.setQueryHint("Search movie");

        //fix x button that floating in the center of action bar in landscape orientation
        searchView.setMaxWidth(Integer.MAX_VALUE);

        if (!TextUtils.isEmpty(searchString)) {
            menuItem.expandActionView();
            searchView.setQuery(searchString, false);
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    MenuItemCompat.collapseActionView(menuItem);
                    Intent openSearchActivityIntent = new Intent(MainActivity.this, SearchActivity.class);
                    openSearchActivityIntent.putExtra(SearchActivity.KEY_SEARCH_QUERY, query);
                    startActivity(openSearchActivityIntent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchString = newText;
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                searchString = "";
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_category_popular:
                viewPager.setCurrentItem(0);
                break;
            case R.id.menu_category_top_rated:
                viewPager.setCurrentItem(1);
                break;
            case R.id.menu_category_upcoming:
                viewPager.setCurrentItem(2);
                break;
            case R.id.menu_show_favorites:
                Intent goToFavoriteActivity = new Intent(this, FavoriteActivity.class);
                startActivity(goToFavoriteActivity);
                break;
            case R.id.menu_about:
                CustomAlertDialog.show(this,
                        "Created with love by hendrawd(hendraz_88@yahoo.co.id) as a final project of Indonesia Android Kejar batch 2, Intermediate class\n\nVersion: " + BuildConfig.VERSION_NAME);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private MovieListFragment createMovieListFragment(String category) {
        MovieListFragment movieListFragment = new MovieListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        movieListFragment.setArguments(bundle);
        return movieListFragment;
    }

    private void setupViewPager() {
        CategoryAdapter categoryAdapter = new CategoryAdapter(getSupportFragmentManager());

        categoryAdapter.addFragment(section1Fragment, getString(R.string.category_popular));
        categoryAdapter.addFragment(section2Fragment, getString(R.string.category_top_rated));
        categoryAdapter.addFragment(section3Fragment, getString(R.string.category_upcoming));

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(categoryAdapter);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        int position = viewPager.getCurrentItem();
        CategoryAdapter categoryAdapter = (CategoryAdapter) viewPager.getAdapter();
        MovieListFragment movieListFragment = (MovieListFragment) categoryAdapter.getItem(position);
        movieListFragment.scrollToTop();
    }

    public void hideFAB() {
        if (fab.isShown()) {
            fab.hide();
        }
    }

    public void showFAB() {
        if (!fab.isShown()) {
            fab.show();
        }
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        confirmQuit();
    }

    private void confirmQuit() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        doubleBackToExitPressedOnce = true;
        CustomToast.show(this, "Press back again to quit");
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public String getCurrentFragmentTitle() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                return getString(R.string.category_popular);
            case 1:
                return getString(R.string.category_top_rated);
            case 2:
                return getString(R.string.category_upcoming);
        }
        return "";
    }

    static class CategoryAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        CategoryAdapter(FragmentManager fm) {
            super(fm);
        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
