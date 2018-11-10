package proxyremote;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 *
 * 代理服务： 用来跟 TaskService 交互的服务
 */
public class CallBackService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
