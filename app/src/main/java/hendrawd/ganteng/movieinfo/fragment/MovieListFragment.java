package hendrawd.ganteng.movieinfo.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import hendrawd.ganteng.movieinfo.R;
import hendrawd.ganteng.movieinfo.activity.MainActivity;
import hendrawd.ganteng.movieinfo.adapter.EmptyAdapter;
import hendrawd.ganteng.movieinfo.adapter.MovieAdapter;
import hendrawd.ganteng.movieinfo.network.GsonRequest;
import hendrawd.ganteng.movieinfo.network.NetworkChecker;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.VolleySingleton;
import hendrawd.ganteng.movieinfo.network.response.GetMovieList;
import hendrawd.ganteng.movieinfo.network.response.Movie;
import hendrawd.ganteng.movieinfo.util.Logger;
import hendrawd.ganteng.movieinfo.util.Util;
import hendrawd.ganteng.movieinfo.view.CustomToast;
import hendrawd.ganteng.movieinfo.view.GridSpacingItemDecoration;

public class MovieListFragment extends Fragment {

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler.layout";
    private static final String BUNDLE_MOVIE_DATA = "movie.data";
    private static final String BUNDLE_LAST_RESPONSE = "last.response";
    private static final int SPAN_COUNT_PORTRAIT = 2;
    private static final int SPAN_COUNT_LANDSCAPE = 4;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    private GetMovieList getMovieListResponse;
    private boolean requestingData = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainContent = inflater.inflate(R.layout.fragment_movie_list, container, false);

        ButterKnife.bind(this, mainContent);

        setupRecyclerView();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMovieList(1);
            }
        });

        if (savedInstanceState == null) {
            getMovieList(1);
        } else {
            getMovieListResponse = savedInstanceState.getParcelable(BUNDLE_LAST_RESPONSE);

            List<Movie> lastMovieList = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIE_DATA);
            if (lastMovieList != null && !lastMovieList.isEmpty()) {
                recyclerView.setAdapter(new MovieAdapter(lastMovieList));
            }

            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }

        return mainContent;
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

        GridLayoutManager mLayoutManager = new GridLayoutManager(recyclerView.getContext(), spanCount);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private float floatingActionButtonHideScrollTreesHold = Util.dp2px(4, getActivity());
            private float deltaScroll = 0;
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //ignore scroll if it is EmptyAdapter
                if (recyclerView.getAdapter() instanceof MovieAdapter) {
                    GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    //end of scroll
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        if (getMovieListResponse.getPage() < getMovieListResponse.getTotal_pages()) {
                            getMovieList(getMovieListResponse.getPage() + 1);
                        }
                    }

                    deltaScroll = deltaScroll + dy;
                    if (dy > 0) {
                        //ketika scroll ke bawah
                        if (Math.abs(deltaScroll) > floatingActionButtonHideScrollTreesHold) {
                            deltaScroll = 0;
                            ((MainActivity) getActivity()).hideFAB();
                        }
                    } else {
                        //ketika scroll ke atas
                        if (Math.abs(deltaScroll) > floatingActionButtonHideScrollTreesHold) {
                            deltaScroll = 0;
                            ((MainActivity) getActivity()).showFAB();
                        }
                    }
                }
            }
        });
    }

    private void getMovieList(final int page) {
        if (NetworkChecker.isNetworkAvailable(getActivity())) {
            if (!requestingData) {
                String category = getArguments().getString("category");
                if (!TextUtils.isEmpty(category)) {
                    requestingData = true;
                    final GsonRequest<GetMovieList> gsonRequest = new GsonRequest<>(
                            UrlComposer.getMovieUrl(getActivity(), category, page),
                            GetMovieList.class,
                            null,
                            new Response.Listener<GetMovieList>() {
                                @Override
                                public void onResponse(GetMovieList movieResponse) {
                                    if (page == 1) {
                                        if (getMovieListResponse != null &&
                                                movieResponse != null &&
                                                getMovieListResponse.getResults().get(0).getId().equals(
                                                        movieResponse.getResults().get(0).getId())
                                                ) {
                                            CustomToast.show(getActivity(),
                                                    getString(R.string.no_new_movies,
                                                            ((MainActivity) getActivity()).getCurrentFragmentTitle()
                                                    ));
                                        } else {
                                            if (movieResponse != null)
                                                recyclerView.setAdapter(new MovieAdapter(movieResponse.getResults()));
                                        }
                                    } else {
                                        ((MovieAdapter) recyclerView.getAdapter()).append(movieResponse.getResults());
                                    }

                                    swipeRefresh.setRefreshing(false);
                                    requestingData = false;
                                    getMovieListResponse = movieResponse;
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    if (volleyError != null) {
                                        CustomToast.show(getActivity(), volleyError.getMessage());
                                        Logger.e("GetMovieList", volleyError.getMessage());
                                    }

                                    swipeRefresh.setRefreshing(false);
                                    requestingData = false;
                                }
                            });
                    VolleySingleton.getInstance(getActivity()).addToRequestQueue(gsonRequest);
                }
            }
        } else {
            CustomToast.show(getActivity(), getString(R.string.no_internet_connection));
            recyclerView.setAdapter(new EmptyAdapter());
            swipeRefresh.setRefreshing(false);
        }
    }

    public void scrollToTop() {
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());

        if (getMovieListResponse != null)
            outState.putParcelable(BUNDLE_LAST_RESPONSE, getMovieListResponse);

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            if (adapter instanceof MovieAdapter) {
                MovieAdapter movieAdapter = (MovieAdapter) adapter;
                outState.putParcelableArrayList(BUNDLE_MOVIE_DATA, new ArrayList<>(movieAdapter.getData()));
            }
        }
    }
}
