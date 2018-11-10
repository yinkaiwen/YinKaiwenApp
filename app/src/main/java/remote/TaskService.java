package remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.logutils.Print;
import utils.logutils.PrintManager;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 *
 * 当耗操作程放在主进程时，主进程的内存可能会吃紧，会造成主进程卡顿；
 *
 * 将部分耗时操作放在远程服务中，可以最大程度地利用手机的性能。
 *
 *
 *
 *
 */
public class TaskService extends Service {

    private static final String TAG = "TaskService";

    @Override
    public void onCreate() {
        PrintManager.getInstance().init();
        Print.i(TAG,"on Create.");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
