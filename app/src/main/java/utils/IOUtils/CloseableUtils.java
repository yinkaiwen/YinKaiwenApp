package utils.IOUtils;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;

import utils.logutils.Print;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class CloseableUtils {

    private static final String TAG = CloseableUtils.class.getSimpleName();

    public static void close(Closeable target) {
        if (target != null) {
            try {
                target.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Print.i(TAG,"Target is null.");
        }
    }
}
