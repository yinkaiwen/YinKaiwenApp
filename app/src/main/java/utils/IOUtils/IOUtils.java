package utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

import utils.logutils.Print;

/**
 * Created by kevin on 2018/8/12.
 * https://github.com/yinkaiwen
 */
public class IOUtils {

    private static final String TAG = "IOUtils";

    public static void write(OutputStream outputStream, byte[] data){
        try {
            outputStream.write(data);
        } catch (IOException e) {
            Print.i(TAG,e.getMessage());
        }
    }

    public static void flush(OutputStream outputStream){
        try {
            outputStream.flush();
        } catch (IOException e) {
            Print.i(TAG,e.getMessage());
        }
    }
}
