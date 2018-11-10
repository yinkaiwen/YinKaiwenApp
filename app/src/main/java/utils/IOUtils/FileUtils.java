package utils.IOUtils;

import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.logutils.Print;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();

    public static boolean createDirUnExists(File dir) {
        boolean flag = false;
        if (dir != null) {
            if (!dir.exists()) {
                boolean isSuccess = dir.mkdirs();
                if (isSuccess) {
                    flag = true;
                    Log.i(TAG, String.format("Make dir success : %s", dir.getAbsolutePath()));
                } else {
                    Log.i(TAG, String.format("Make dir fail : %s", dir.getAbsolutePath()));
                }
            } else {
                if (!dir.isDirectory()) {
                    Print.w(TAG, String.format("Warning :You want to create a dir,but %s has exists and target is file.", dir.getAbsolutePath()));
                } else {
                    flag = true;
                }
            }
        } else {
            Print.w(TAG, String.format("Target dir is null."));
        }
        return flag;
    }

    public static boolean createFileUnExists(File file) {
        boolean flag = false;
        if (file != null) {
            if (!file.exists()) {
                try {
                    boolean isSuccess = file.createNewFile();
                    if (isSuccess) {
                        flag = true;
                        Log.i(TAG, String.format("Create file success : %s.", file.getAbsolutePath()));
                    } else {
                        Log.i(TAG, String.format("Create file fail : %s.", file.getAbsolutePath()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (!file.isFile()) {
                    Print.w(TAG, String.format("Warning :You want to create a file,but %s has exists and target is dir.", file.getAbsolutePath()));
                } else {
                    flag = true;
                }
            }
        } else {
            Print.w(TAG, "Target file is null.");
        }

        return flag;
    }

    public static FileOutputStream openFileOutputStream(File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fos;
    }

    public static FileOutputStream openFileOutputStream(File file,boolean append) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fos;
    }
}
