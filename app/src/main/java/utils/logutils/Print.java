package utils.logutils;

import android.util.Log;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class Print {
    private static final String TAG = Print.class.getSimpleName();

    public static void v(String tag, String msg) {
        log2Cat(tag,msg,Log.VERBOSE);
    }

    public static void d(String tag, String msg) {
        log2Cat(tag,msg,Log.DEBUG);
    }

    public static void i(String tag, String msg) {
        log2Cat(tag,msg,Log.INFO);
    }

    public static void w(String tag, String msg) {
        log2Cat(tag,msg,Log.WARN);
    }

    public static void e(String tag, String msg) {
        log2Cat(tag,msg,Log.ERROR);
    }

    private static void log2Cat(String tag, String msg, int level) {
        switch (level) {
            case Log.VERBOSE:
                Log.v(tag,msg);
                break;
            case Log.DEBUG:
                Log.d(tag,msg);
                break;
            case Log.INFO:
                Log.i(tag,msg);
                break;
            case Log.WARN:
                Log.w(tag,msg);
                break;
            case Log.ERROR:
                Log.e(tag,msg);
                break;
            default:
                Log.w(TAG,"Unknown log level");
                break;
        }
    }
}
