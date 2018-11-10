package utils.logutils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import utils.IOUtils.FileUtils;
import utils.apputils.AppUtils;
import utils.logutils.Log2DiskImp.StringLog2Disk;
import yinkaiwenapp.BaseApplication;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class PrintManager {

    private static PrintManager INSTANCE = null;
    private static final String TAG = PrintManager.class.getSimpleName();
    private static final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private File LOG_DIR = null;

    private boolean isHasPermission = false;
    private boolean isSDCardRegular = false;

    private Log2Disk sLog2Disk = null;

    public synchronized static PrintManager getInstance() {
        if (INSTANCE == null) {
            synchronized (PrintManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PrintManager();
                }
            }
        }
        return INSTANCE;
    }


    private PrintManager() {

    }

    public void init(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isHasPermission = checkPermission();
        }else{
            isHasPermission = true;
        }

        if(!isHasPermission){
            requestSDCardPermission(activity);
        }
        initConfig();
    }

    /**
     * 在 Service中直接调用
     */
    public void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isHasPermission = checkPermission();
        }else{
            isHasPermission = true;
        }
        initConfig();
    }

    private void initConfig(){
        initLogDir();
        sLog2Disk = new StringLog2Disk();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean checkPermission() {
        int checkSelfPermission = BaseApplication.getBaseApplicationContext().checkSelfPermission(PERMISSION);
        Log.i(TAG,"checkSelfPermission : " + checkSelfPermission);
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void initLogDir() {
        if (isHasPermission) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                isSDCardRegular = true;
                File logDir = new File(Environment.getExternalStorageDirectory(), PrintConfigure.LOG_DIR_NAME);
                boolean exists = FileUtils.createDirUnExists(logDir);
                if (exists) {
                    //根据进程名称创建相应的目录
                    String processName = AppUtils.getAppName();
                    if(TextUtils.isEmpty(processName)){
                        processName = "mainProcess";
                    }
                    if("com.example.kevin.yinkaiwenapp".equals(processName)){
                        processName = "main";
                    }else if("com.example.kevin.yinkaiwenapp:taskProcess".equals(processName)){
                        processName = "taskProcess";
                    }
                    File targetDir = new File(logDir, processName);
                    Log.d(TAG,"targetDir : " + targetDir.getAbsolutePath());
                    FileUtils.createDirUnExists(targetDir);
                    LOG_DIR = targetDir;
                    Log.d(TAG,"LOG_DIR : " + LOG_DIR.getAbsolutePath());
                }
            } else {
                isSDCardRegular = false;
                Print.i(TAG, "SD Card is wrong.");
            }
        } else {
            Log.i(TAG, "No permission : android.permission.WRITE_EXTERNAL_STORAGE");
        }
    }

    public void log2Disk(String tag, String msg, int level) {
        if(!isHasPermission){
            boolean flag = checkPermission();
            isHasPermission = flag;
        }
        if(isHasPermission){
            sLog2Disk.log2Disk(LOG_DIR, tag, msg, level);
        }
    }

    private void requestSDCardPermission(Activity activity){
        ActivityCompat.requestPermissions(activity,
                PrintConfigure.LOG_PERMISSION,
                PrintConfigure.LOG_REQUEST_PERMISSION_TAG);
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){
        if(requestCode == PrintConfigure.LOG_REQUEST_PERMISSION_TAG){
            int length = grantResults.length;
            for (int i = 0; i < length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "log 权限授予成功:" + permissions[i]);
                    isHasPermission = true;
                    init();
                } else {
                    Log.e(TAG, "log 权限授予失败:" + permissions[i]);
                    isHasPermission = false;
                }
            }
        }else {
            Log.d(TAG,"NO request log permission");
        }
    }


}
