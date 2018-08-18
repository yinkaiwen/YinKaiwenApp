package utils.logutils;

import android.Manifest;

import yinkaiwenapp.AppConfigure;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class PrintConfigure {

    public static final String LOG_DIR_NAME = "yinkaiwen.app";
    public static final String[] LOG_FILE_NAMES = {"log0.txt", "log1.txt", "log2.txt", "log3.txt", "log4.txt"};


    //测试版每个Log文件最大值5M,正式版2M.
    private static final int RELEASE_LENGTH = 2;
    private static final int DEBUG_LENGTH = 5;
    public static final int LOG_FILE_MAX_LENGTH = AppConfigure.isRelease ? RELEASE_LENGTH * 1024 * 1024 : DEBUG_LENGTH * 1024 * 1024;

    //每行Log的最大字数
    public static final int MAX_LINE_LENGTH = 500;

    public static final String FIX_DEVICE_STR = "|";

    public static final String[] LOG_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static final int LOG_REQUEST_PERMISSION_TAG = 1001;
    public static final String CHANGE_LINE = "\r\n";

}
