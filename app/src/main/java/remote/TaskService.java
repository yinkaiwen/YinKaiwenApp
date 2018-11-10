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
