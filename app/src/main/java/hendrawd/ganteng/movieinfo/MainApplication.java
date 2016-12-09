package hendrawd.ganteng.movieinfo;

import android.app.Application;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import hendrawd.ganteng.movieinfo.db.DbHelper;
import hendrawd.ganteng.movieinfo.network.GsonRequest;
import hendrawd.ganteng.movieinfo.network.UrlComposer;
import hendrawd.ganteng.movieinfo.network.VolleySingleton;
import hendrawd.ganteng.movieinfo.network.response.Genre;
import hendrawd.ganteng.movieinfo.network.response.GetGenreList;
import hendrawd.ganteng.movieinfo.util.GenreMapper;
import hendrawd.ganteng.movieinfo.util.Logger;

/**
 * @author hendrawd on 11/21/16
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (UrlComposer.isApiKeyEmpty()) {
            throw new IllegalArgumentException("Provide your API KEY in "
                    + UrlComposer.class.getName()
                    + "."
                    + UrlComposer.class.getDeclaredFields()[0].getName());
        } else {
            requestGenreList();
            DbHelper.initDAO(this);
        }

    }

    private void requestGenreList() {
        @SuppressWarnings("unchecked") final GsonRequest gsonRequest = new GsonRequest(
                UrlComposer.getGenreListUrl(),
                GetGenreList.class,
                null,
                new Response.Listener<GetGenreList>() {
                    @Override
                    public void onResponse(GetGenreList getGennreList) {
                        List<Genre> genreList = getGennreList.genres;
                        for (Genre genre : genreList) {
                            GenreMapper.put(genre.getId(), genre.getName());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (volleyError != null) {
                            Logger.e("GetGenreList", volleyError.getMessage());
                        }
                    }
                });
        VolleySingleton.getInstance(this).addToRequestQueue(gsonRequest);
    }
}
