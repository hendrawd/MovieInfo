package hendrawd.ganteng.movieinfo.util;

import android.util.Log;

import hendrawd.ganteng.movieinfo.BuildConfig;

/**
 * Auto log based on BuildConfig.DEBUG
 * Will not show any log on release version
 *
 * @author hendrawd on 10/24/16
 */

public class Logger {

    public static void v(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.v(tag, message);
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message);
    }

    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.i(tag, message);
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.w(tag, message);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message);
    }
}
