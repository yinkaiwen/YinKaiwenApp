package remote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import utils.logutils.Print;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 */
public class TaskService extends Service {

    private static final String TAG = "TaskService";

    @Override
    public void onCreate() {
        Print.i(TAG,"on create.");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
