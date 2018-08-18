package utils.logutils.Log2DiskImp;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.IOUtils.CloseableUtils;
import utils.IOUtils.FileUtils;
import utils.IOUtils.IOUtils;
import utils.logutils.Log2Disk;
import utils.logutils.PrintConfigure;
import utils.timeutils.TimeUtils;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class StringLog2Disk implements Log2Disk {

    private static final String TAG = StringLog2Disk.class.getSimpleName();
    private ExecutorService mService = Executors.newSingleThreadExecutor();

    @Override
    public void log2Disk(File dir, final String tag, final String msg, final int level) {
        if(dir == null || !dir.exists()){
            Log.e(TAG,"no dir");
            return;
        }


        File targetFile = new File(dir, PrintConfigure.LOG_FILE_NAMES[0]);
        boolean exists = FileUtils.createFileUnExists(targetFile);
        if (exists) {
            long length = targetFile.length();

            //log0.txt 已经满了，删除log4.txt,log0.txt,log1.txt,log2.txt,log3.txt重新命名
            if (length > PrintConfigure.LOG_FILE_MAX_LENGTH) {
                for (int i = PrintConfigure.LOG_FILE_NAMES.length - 1; i >= 0; i--) {
                    File file = new File(dir, PrintConfigure.LOG_FILE_NAMES[i]);
                    if (file.exists()) {
                        if (i == PrintConfigure.LOG_FILE_NAMES.length - 1) {
                            file.deleteOnExit();
                        } else {
                            File reNameFile = new File(dir, PrintConfigure.LOG_FILE_NAMES[i + 1]);
                            boolean isSuccess = file.renameTo(reNameFile);
                            if (!isSuccess) {
                                Log.w(TAG, String.format("ReName file fail : %s to %s.", file.getAbsolutePath(), reNameFile.getAbsolutePath()));
                            }
                        }
                    }
                }
                exists = FileUtils.createFileUnExists(targetFile);
            }
            if (exists) {
                final FileOutputStream fos = FileUtils.openFileOutputStream(targetFile,true);
                if (fos != null) {
                    mService.execute(new Runnable() {
                        @Override
                        public void run() {
                            write2IO(fos, msg, tag, level);
                        }
                    });
                } else {
                    Log.w(TAG, String.format("Open file outputStream fail : %s", targetFile.getAbsolutePath()));
                }
            } else {
                Log.w(TAG, String.format("After change name,Create log file fail : %s.", targetFile.getAbsolutePath()));
            }
        } else {
            Log.w(TAG, String.format("Create log file fail : %s.", targetFile.getAbsolutePath()));
        }

    }

    private void write2IO(FileOutputStream fos, String msg, String tag, int level) {
        String formatString = msg;
        Log.d(TAG,"formatString.length" + formatString.length());
        String outStr;
        //超过MAX_LINE_LENGTH的部分折行
        while(formatString.length() > PrintConfigure.MAX_LINE_LENGTH){
            outStr = wrapFormatString(tag,level,formatString.substring(0,PrintConfigure.MAX_LINE_LENGTH));
            Log.d(TAG,"OUT : " + outStr.length() + "    "+outStr);
            formatString = formatString.substring(PrintConfigure.MAX_LINE_LENGTH,formatString.length());
            IOUtils.write(fos,outStr.getBytes());
            Log.d(TAG,"formatString.length" + formatString.length());
        }
        Log.d(TAG,"formatString.length" + formatString.length());
        formatString = wrapFormatString(tag,level,formatString);
        IOUtils.write(fos,formatString.getBytes());
        IOUtils.flush(fos);
        CloseableUtils.close(fos);
    }

    private String wrapFormatString(String tag, int level, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(TimeUtils.getCurrentTimeString())
                .append(PrintConfigure.FIX_DEVICE_STR)
                .append(level2Str(level))
                .append(PrintConfigure.FIX_DEVICE_STR)
                .append(tag)
                .append(PrintConfigure.FIX_DEVICE_STR)
                .append(msg)
                .append(PrintConfigure.CHANGE_LINE);
        return sb.toString();
    }


    private String level2Str(int level) {
        String str;
        switch (level) {
            case Log.VERBOSE:
                str = "v";
                break;
            case Log.DEBUG:
                str = "d";
                break;
            case Log.INFO:
                str = "i";
                break;
            case Log.WARN:
                str = "w";
                break;
            case Log.ERROR:
                str = "e";
                break;
            default:
                Log.w(TAG, "Unknown log level");
                str = "e";
                break;
        }
        return str;
    }

}
