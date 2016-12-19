package hendrawd.ganteng.movieinfo.util;

import android.util.SparseArray;

/**
 * @author hendrawd on 11/21/16
 */

public class GenreMapper {
    private static SparseArray<String> genreMapper = new SparseArray<>();

    public static void put(int key, String genre) {
        genreMapper.put(key, genre);
    }

    public static String get(int key) {
        return genreMapper.get(key);
    }

    public static boolean isInitialized() {
        return genreMapper.size() > 0;
    }
}
