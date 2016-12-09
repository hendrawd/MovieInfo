package hendrawd.ganteng.movieinfo.network;

import android.text.TextUtils;

/**
 * Url composer for themoviedb.org APIs
 * Documentation can be found at https://developers.themoviedb.org/3/movies
 *
 * @author hendrawd on 11/17/16
 */

public class UrlComposer {

    private static final String API_KEY = "";//please add your API KEY from themoviedb.org here
    private static final String BASE_URL = "http://api.themoviedb.org/3";

    public static final String CATEGORY_LATEST = "latest";//the response is not list, but just 1 last movie object
    public static final String CATEGORY_NOW_PLAYING = "now_playing";//not support regional id, so it will not used
    public static final String CATEGORY_POPULAR = "popular";
    public static final String CATEGORY_TOP_RATED = "top_rated";
    public static final String CATEGORY_UPCOMING = "upcoming";

    public static boolean isApiKeyEmpty() {
        return TextUtils.isEmpty(API_KEY);
    }

    private static String getApiKey() {
        return "api_key=" + API_KEY;
    }

    /**
     * Compose API url for movie list based on category
     *
     * @param category String
     * @param page     page you want to get
     * @return Movie list url String
     * example: "http://api.themoviedb.org/3/movie/popular?api_key=API_KEY"
     */
    public static String getMovieUrl(String category, int page) {
        return BASE_URL + "/movie/" + category + "?" + getApiKey() + "&page=" + page;
    }

    /**
     * Compose API url for genre list
     *
     * @return Genre list url String
     * example: "http://api.themoviedb.org/3/genre/movie/list?api_key=API_KEY&language=en-US"
     */
    public static String getGenreListUrl() {
        return BASE_URL + "/genre/movie/list?" + getApiKey() + "&language=en-US";
    }

    /**
     * Compose API url for movie list based on search query of movieName
     *
     * @param movieName part of the name of the movie
     * @param page      page you want to get
     * @return Movie list url String with search query
     */
    public static String getSearchUrl(String movieName, int page) {
        return BASE_URL + "/search/movie?&query=" + movieName + "&" + getApiKey() + "&page=" + page;
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
    public static String getVideoUrl(String movieId) {
        return BASE_URL + "/movie/" + movieId + "/videos?" + getApiKey();
    }

    /**
     * Compose API url for review list based on movieId
     *
     * @param movieId String
     * @param page    page you want to get
     * @return Review list url String
     * example: "https://api.themoviedb.org/3/movie/76341/reviews?api_key=API_KEY&language=en-US&page=1"
     */
    public static String getReviewUrl(String movieId, int page) {
        return BASE_URL + "/movie/" + movieId + "/reviews?" + getApiKey() + "&language=en-US" + "&page=" + page;
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
