package remote;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import proxyremote.ProxyService;
import remote.bean.IService;
import remote.bean.IServiceCallback;
import utils.logutils.Print;
import yinkaiwenapp.AppConfigure;
import yinkaiwenapp.BaseApplication;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 * <p>
 * 用来管理 TaskService
 */
public class TaskServiceMgr {

    private static final String TAG = "TaskServiceMgr";

    private static final String TASK_SERVICE_ACTION = "yinkaiwen.intent.taskservice";
    public static final String CALLBACK_SERVICE_ACTION = "yinkaiwen.intent.interation";
    private static TaskServiceMgr INSTANCE = null;
    private IService mIService = null;

    public static TaskServiceMgr getInstance() {
        if (INSTANCE == null) {
            synchronized (TaskServiceMgr.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskServiceMgr();
                }
            }
        }
        return INSTANCE;
    }

    private TaskServiceMgr() {
    }

    /**
     * 用于跟CallBackService bind.
     */
    private ServiceConnection mInteractionConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (name == null) {
                Print.i(TAG, "");
                return;
            }

            if (!AppConfigure.PACKAGE_NAME.equals(name.getPackageName())) {
                Print.i(TAG, "No right package.");
                return;
            }
            mIService = IService.Stub.asInterface(service);
            if (mIService != null) {
                try {
                    mIService.registerCallBack(mInteractionCallBack);
                } catch (RemoteException e) {
                    Print.e(TAG, e.getMessage());
                }
            } else {
                Print.i(TAG, "iService == null");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Print.i(TAG, "mInteractionConnection : onServiceDisconnected");
        }
    };

    private IServiceCallback.Stub mInteractionCallBack = new IServiceCallback.Stub() {

        @Override
        public void execute(String hashParamsString) throws RemoteException {
            Print.i(TAG, "IServiceCallback onResponse : " + hashParamsString);
        }
    };

    public void init() {
        bindCallBackService();
    }

    public void startTaskService() {
        Intent intent = new Intent(BaseApplication.getBaseApplicationContext(), TaskService.class);
        intent.setAction(TASK_SERVICE_ACTION);
        BaseApplication.getBaseApplicationContext().startService(intent);
    }

    /**
     * taskProcess进程中.
     */
    private void bindCallBackService() {
        Intent intent = new Intent(BaseApplication.getBaseApplicationContext(), ProxyService.class);
        intent.setAction(CALLBACK_SERVICE_ACTION);
        BaseApplication.getBaseApplicationContext().bindService(intent, mInteractionConnection, Service.BIND_AUTO_CREATE);
    }

    public void unRegisterCallBack() {
        if (mIService != null) {
            try {
                mIService.unRegisterCallBack(mInteractionCallBack);
            } catch (RemoteException e) {
                Print.e(TAG, e.getMessage());
            }
        } else {
            Print.i(TAG, "unRegisterCallBack mIService is null.");
        }
    }


}
