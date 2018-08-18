package utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by kevin on 2018/8/12.
 * https://github.com/yinkaiwen
 */
public class IOUtils {

    public static void write(OutputStream outputStream, byte[] data){
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flush(OutputStream outputStream){
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
