package utils.apputils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.Iterator;
import java.util.List;

import yinkaiwenapp.BaseApplication;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 *
 * APP的一些工具方法
 */
public class AppUtils {
    /**
     * 获取版本名称
     *
     * @return
     */
    public static String getVersionName() {
        return getPackageInfo() == null ? "0" : getPackageInfo().versionName;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        return getPackageInfo() == null ? 0 : getPackageInfo().versionCode;
    }


    private static PackageInfo getPackageInfo() {
        PackageInfo pInfo = null;
        Context context = BaseApplication.getBaseApplicationContext();
        try {
            //通过PackageManager可以得到PackageInfo
            PackageManager pManager = context.getPackageManager();
            pInfo = pManager.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pInfo;
    }

    /**
     * 获取当前进程名称
     *
     * @return
     */
    public static String getAppName() {
        Context context = BaseApplication.getBaseApplicationContext();
        int pid = android.os.Process.myPid(); // Returns the identifier of this process
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
        if (list == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo info : list) {
            try {
                if (info.pid == pid) {
                    return info.processName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
