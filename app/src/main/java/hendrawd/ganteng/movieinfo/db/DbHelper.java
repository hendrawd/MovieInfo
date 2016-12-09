package hendrawd.ganteng.movieinfo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import hendrawd.ganteng.movieinfo.BuildConfig;
import hendrawd.ganteng.movieinfo.R;

/**
 * Created by hendrawd on 11/28/2016
 * Caution! call initDAO first when application started or when you want to use this helper
 * (just need to call once)
 * if you not call it, it will cause null pointer exception
 */
public class DbHelper {

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static SQLiteDatabase db;
    private static String dbName;

    /**
     * @param context application / activity
     */
    public static void initDAO(Context context) {
        QueryBuilder.LOG_SQL = BuildConfig.DEBUG;
        QueryBuilder.LOG_VALUES = BuildConfig.DEBUG;

        dbName = context.getString(R.string.app_name) + "-db";
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, dbName, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public static SQLiteDatabase getDb() {
        return db;
    }

    public static DaoMaster getDaoMaster() {
        return daoMaster;
    }

    public static DaoSession getDaoSession() {
        return daoSession;
    }

    public static String getDbName() {
        return dbName;
    }

    public static final int ERROR_TOO_MANY_DATA = -1;
    public static final int ERROR_DUPLICATE_DATA = -2;
    public static final int SUCCESS = 1;

    public static boolean movieInDb(hendrawd.ganteng.movieinfo.network.response.Movie responseMovieObject) {
        MovieDao movieDao = daoSession.getMovieDao();
        QueryBuilder<Movie> movieQueryBuilder = movieDao.queryBuilder();
        movieQueryBuilder.where(movieDao.getPkProperty().eq(responseMovieObject.getId()));
        return movieQueryBuilder.count() >= 1;
    }

    public static int insertFavoriteMovie(hendrawd.ganteng.movieinfo.network.response.Movie responseMovieObject) {
        MovieDao movieDao = daoSession.getMovieDao();
        QueryBuilder<Movie> movieQueryBuilder = movieDao.queryBuilder();
        if (movieQueryBuilder.count() >= 20) {
            return ERROR_TOO_MANY_DATA;
        }
        movieQueryBuilder.where(movieDao.getPkProperty().eq(responseMovieObject.getId()));
        if (movieQueryBuilder.count() >= 1) {
            return ERROR_DUPLICATE_DATA;
        }
        hendrawd.ganteng.movieinfo.db.Movie daoMovieObject = new Movie();
        daoMovieObject.setPoster_path(responseMovieObject.getPoster_path());
        daoMovieObject.setOriginal_title(responseMovieObject.getOriginal_title());
        daoMovieObject.setTitle(responseMovieObject.getTitle());
        daoMovieObject.setOverview(responseMovieObject.getOverview());
        daoMovieObject.setRelease_date(responseMovieObject.getRelease_date());
        daoMovieObject.setBackdrop_path(responseMovieObject.getBackdrop_path());
        daoMovieObject.setVote_average(responseMovieObject.getVote_average());
        daoMovieObject.setId(responseMovieObject.getId());

        StringBuilder genreIdsBuilder = new StringBuilder();
        int[] genreIdArray = responseMovieObject.getGenre_ids();
        int genreIdLength = genreIdArray.length;
        for (int i = 0; i < genreIdLength; i++) {
            genreIdsBuilder.append(genreIdArray[i]);
            if (i != genreIdLength - 1)
                genreIdsBuilder.append(" ");
        }
        daoMovieObject.setGenre_ids(genreIdsBuilder.toString());

        movieDao.insertOrReplaceInTx(daoMovieObject);
        return SUCCESS;
    }

    public static boolean deleteFavoriteMovie(String id) {
        try {
            MovieDao movieDao = daoSession.getMovieDao();
//        QueryBuilder<Movie> queryBuilder = movieDao.queryBuilder();
//        queryBuilder.where(MovieDao.Properties.Id.eq(id));
//        queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities();
            movieDao.deleteByKey(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<hendrawd.ganteng.movieinfo.network.response.Movie> getFavoriteMovies() {
        MovieDao movieDao = daoSession.getMovieDao();
        QueryBuilder<Movie> movieQueryBuilder = movieDao.queryBuilder();
        List<Movie> listDbMovie = movieQueryBuilder.list();
        List<hendrawd.ganteng.movieinfo.network.response.Movie> listResponseMovie = new ArrayList<>();
        for (Movie dbMovie : listDbMovie) {
            hendrawd.ganteng.movieinfo.network.response.Movie responseMovie =
                    new hendrawd.ganteng.movieinfo.network.response.Movie();
            responseMovie.setPoster_path(dbMovie.getPoster_path());
            responseMovie.setOriginal_title(dbMovie.getOriginal_title());
            responseMovie.setTitle(dbMovie.getTitle());
            responseMovie.setOverview(dbMovie.getOverview());
            responseMovie.setRelease_date(dbMovie.getRelease_date());
            responseMovie.setBackdrop_path(dbMovie.getBackdrop_path());
            responseMovie.setVote_average(dbMovie.getVote_average());
            responseMovie.setId(dbMovie.getId());

            String[] genreIdArrayString = dbMovie.getGenre_ids().split(" ");
            int[] genreIdArrayInt = new int[genreIdArrayString.length];
            int genreIdLength = genreIdArrayString.length;
            for (int i = 0; i < genreIdLength; i++) {
                genreIdArrayInt[i] = Integer.parseInt(genreIdArrayString[i]);
            }
            responseMovie.setGenre_ids(genreIdArrayInt);
            listResponseMovie.add(responseMovie);
        }
        return listResponseMovie;
    }
}
