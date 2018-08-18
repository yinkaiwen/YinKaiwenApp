package utils.logutils;

import java.io.File;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public interface Log2Disk {

    void log2Disk(File dir, String TAG, String msg,int level);

}
