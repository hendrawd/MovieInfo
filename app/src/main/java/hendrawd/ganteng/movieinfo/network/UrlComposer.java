package hendrawd.ganteng.movieinfo.network;

import android.content.Context;
import android.text.TextUtils;

import hendrawd.ganteng.movieinfo.R;

/**
 * Url composer for themoviedb.org APIs
 * Documentation can be found at https://developers.themoviedb.org/3/movies
 *
 * @author hendrawd on 11/17/16
 */

public class UrlComposer {

    private static final String BASE_URL = "http://api.themoviedb.org/3";

    public static final String CATEGORY_LATEST = "latest";//the response is not list, but just 1 last movie object
    public static final String CATEGORY_NOW_PLAYING = "now_playing";//not support regional id, so it will not used
    public static final String CATEGORY_POPULAR = "popular";
    public static final String CATEGORY_TOP_RATED = "top_rated";
    public static final String CATEGORY_UPCOMING = "upcoming";

    public static boolean isApiKeyEmpty(Context context) {
        return TextUtils.isEmpty(context.getString(R.string.movie_db_api_key));
    }

    private static String getApiKey(Context context) {
        return "api_key=" + context.getString(R.string.movie_db_api_key);
    }

    /**
     * Compose API url for movie list based on category
     *
     * @param category String
     * @param page     page you want to get
     * @return Movie list url String
     * example: "http://api.themoviedb.org/3/movie/popular?api_key=API_KEY"
     */
    public static String getMovieUrl(Context context, String category, int page) {
        return BASE_URL + "/movie/" + category + "?" + getApiKey(context) + "&page=" + page;
    }

    /**
     * Compose API url for genre list
     *
     * @return Genre list url String
     * example: "http://api.themoviedb.org/3/genre/movie/list?api_key=API_KEY&language=en-US"
     */
    public static String getGenreListUrl(Context context) {
        return BASE_URL + "/genre/movie/list?" + getApiKey(context) + "&language=en-US";
    }

    /**
     * Compose API url for movie list based on search query of movieName
     *
     * @param movieName part of the name of the movie
     * @param page      page you want to get
     * @return Movie list url String with search query
     */
    public static String getSearchUrl(Context context, String movieName, int page) {
        return BASE_URL + "/search/movie?&query=" + movieName + "&" + getApiKey(context) + "&page=" + page;
    }

    /**
     * Get poster url(portrait) with size 185x278
     *
     * @param posterPath poster path that provided by movieList
     * @return String of poster url
     */
    public static String getPosterUrl(String posterPath) {
        return "https://image.tmdb.org/t/p/w185" + posterPath;
    }

    /**
     * Get backdrop url(landscape) with size 600x338
     *
     * @param backdropPath backdrop path that provided by movieList
     * @return String of backdrop url
     */
    public static String getBackdropUrl(String backdropPath) {
        return "https://image.tmdb.org/t/p/w600" + backdropPath;
    }

    /**
     * Compose API url for video list based on movieId
     *
     * @param movieId String
     * @return Video list url String
     * example: "http://api.themoviedb.org/3/movie/76341/videos?api_key=API_KEY"
     */
    public static String getVideoUrl(Context context, String movieId) {
        return BASE_URL + "/movie/" + movieId + "/videos?" + getApiKey(context);
    }

    /**
     * Compose API url for review list based on movieId
     *
     * @param movieId String
     * @param page    page you want to get
     * @return Review list url String
     * example: "https://api.themoviedb.org/3/movie/76341/reviews?api_key=API_KEY&language=en-US&page=1"
     */
    public static String getReviewUrl(Context context, String movieId, int page) {
        return BASE_URL + "/movie/" + movieId + "/reviews?" + getApiKey(context) + "&language=en-US" + "&page=" + page;
    }

    /**
     * Get youtube thumbnail preview of the video
     *
     * @param videoId key of youtube video
     * @return thumbnail url
     * <p>
     * It is possible to change the resolution of the thumbnail, but beware of unavailable thumb
     * we can use:
     * default.jpg
     * hqdefault.jpg
     * mqdefault.jpg
     * sddefault.jpg
     * maxresdefault.jpg
     */
    public static String getYoutubeThumbnail(String videoId) {
        return "https://img.youtube.com/vi/" + videoId + "/default.jpg";
    }
}
