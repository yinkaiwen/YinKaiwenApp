package remote;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import proxyremote.BaseCallBack;
import proxyremote.ProxyService;
import remote.bean.IService;
import remote.bean.IServiceCallback;
import remote.config.AIDLMethodName;
import remote.taskimp.DownLoadTask;
import utils.logutils.Print;
import utils.remoteutils.RemoteUtils;
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

    private List<TaskInterface> cacheTasks = new LinkedList<>();
    //以 methodName : TaskInterface 的方式来保存任务对象
    private Map<String, TaskInterface> tasks = new HashMap<>();

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
        //初始化TaskInterface,用来反射调用
        DownLoadTask downLoadTask = DownLoadTask.getInstance();
        cacheTasks.add(downLoadTask);


        while (!cacheTasks.isEmpty()) {
            TaskInterface task = cacheTasks.remove(0);
            Method[] methods = task.getClass().getDeclaredMethods();
            for (Method m : methods) {
                String methodName = m.getName();
                if (methodName.startsWith(AIDLMethodName.FIX_START_METHOD_NAME)) {
                    Print.i(TAG, "put Method : " + methodName);
                    tasks.put(methodName, task);
                }
            }
        }
    }

    /**
     * 用来执行TaskInterface的CallBack
     */
    private BaseCallBack mCallBack = new BaseCallBack() {
        @Override
        public void onReponse(int code, Object obj) {
            if (obj != null) {
                if (obj instanceof HashMap) {
                    Print.i(TAG, "obj : " + obj.toString());
                    HashMap<String, Object> param = RemoteUtils.addErrorCode(code, (HashMap<String, Object>) obj);

                    Print.i(TAG, "param : " + param.toString());
                    try {
                        mIService.onReponse(param);
                    } catch (RemoteException e) {
                        Print.e(TAG, e.getMessage());
                    }
                } else {
                    Print.e(TAG, "TaskInterface execute method must onResponse arg is HashMap");
                }
            } else {
                Print.i(TAG, "obj is null.");
            }
        }
    };

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
        public void execute(Map params) throws RemoteException {
            Print.i(TAG, "IServiceCallback onResponse : " + (params == null ? " null" : params.toString()));
            //根据params的方法名称，使用反的方式去调用对应类的方法

            if (params == null) {
                Print.i(TAG, "execute(Map params)  params is null.");
            } else {
                try {
                    String executeMethodName = (String) params.get(AIDLMethodName.METHOD_NAME);
                    //参数1
                    String param = (String) params.get(AIDLMethodName.METHOD_PARAMS);
                    JSONObject arg = new JSONObject(param);
                    TaskInterface taskInterface = tasks.get(executeMethodName);
                    Method method = taskInterface.getClass().getMethod(executeMethodName, JSONObject.class, BaseCallBack.class);
                    method.invoke(taskInterface, arg, mCallBack);

                } catch (NoSuchMethodException e) {
                    Print.e(TAG, e.getMessage());
                } catch (IllegalAccessException e) {
                    Print.e(TAG, e.getMessage());
                } catch (InvocationTargetException e) {
                    Print.e(TAG, e.getMessage());
                } catch (JSONException e) {
                    Print.e(TAG, e.getMessage());
                }
            }

        }

    };

    void init() {
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

    /**
     * TaskProcess主动上报数据给主进程
     *
     * @param code
     * @param info
     */
    public void post(int code, HashMap<String, Object> info) {
        try {
            if (mIService != null && info != null) {
                info = RemoteUtils.addErrorCode(code, info);
                Print.i(TAG, "info : " + info.toString());
                mIService.onReponse(info);
            }
        } catch (RemoteException e) {
            Print.e(TAG, e.getMessage());
        }
    }

}
