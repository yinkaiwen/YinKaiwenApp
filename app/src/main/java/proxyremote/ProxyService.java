package proxyremote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import remote.TaskServiceMgr;
import remote.bean.IService;
import remote.bean.IServiceCallback;
import utils.logutils.Print;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 * <p>
 * 代理服务： 用来跟 TaskService 交互的服务
 * <p>
 * 每个Activity都跟TaskService交互，会有大量重复的代码；使用代理交互，可以降低耦合.
 */
public class ProxyService extends Service {

    private static final String TAG = "ProxyService";
    RemoteCallbackList<IServiceCallback> mCallbackList = new RemoteCallbackList<>();

    private Map<String, BaseCallBack> executeCallBackMap = new HashMap<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Print.i(TAG, "action : " + action);
            if (TaskServiceMgr.CALLBACK_SERVICE_ACTION.equals(action)) {
                return mInteraction;
            }
            if (ProxyUtils.CALLBACK_SERVICE_PROCY_ACTION.equals(action)) {
                //todo 返回代理IBinder对象;都在主进程中，不需要AIDL
                return mBinder;
            }
        }

        return null;
    }

    /**
     * 用来跟TaskService交互用的AIDL.
     */
    private IService.Stub mInteraction = new IService.Stub() {

        @Override
        public void registerCallBack(IServiceCallback callback) throws RemoteException {
            if (callback != null) {
                Print.i(TAG, "mCallbackList.register  ");
                mCallbackList.register(callback);
            } else {
                Print.i(TAG, "registerCallBack callback is null.");
            }
        }

        @Override
        public void unRegisterCallBack(IServiceCallback callback) throws RemoteException {
            if (callback != null) {
                Print.i(TAG, "mCallbackList.unregister  ");
                mCallbackList.unregister(callback);
            } else {
                Print.i(TAG, "unRegisterCallBack callback is null.");
            }
        }

        //item.execute(jsonObject.toString()); 执行后，taskProcess中会回调该方法.
        @Override
        public void onReponse(String hashParamsString) throws RemoteException {
            //todo hashParamsString中包含 code,methodName,params等参数，需要根据methodName找到
            //对应的callback,再将code和params解析出来.
            Print.i(TAG,"onReponse : " + hashParamsString);
        }


    };

    /**
     * 用来跟主进程交互的Binder.
     */
    private MyBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {

        MyBinder() {
            Print.i(TAG, "MyBinder onCreate.");
        }

        public ProxyService getCallBackService() {
            return ProxyService.this;
        }
    }


    /**
     * 主进程中调用的方法
     *
     * @param jsonObject
     */
    public void execute(JSONObject jsonObject, BaseCallBack callBack, String methodName) {
        if (mCallbackList == null) {
            Print.i(TAG, "execute mCallbackList == null");
            return;
        }
        if (jsonObject == null) {
            Print.i(TAG, "execute jsonObject == null");
            return;
        }

        BaseCallBack back = executeCallBackMap.get(methodName);
        if (back == null) {
            executeCallBackMap.put(methodName, callBack);
        } else {
            Print.i(TAG, "executeCallBackMap had " + methodName + " not add.");
        }

        mCallbackList.beginBroadcast();
        int count = mCallbackList.getRegisteredCallbackCount();
        for (int i = 0; i < count; i++) {
            IServiceCallback item = mCallbackList.getBroadcastItem(i);
            try {
                Print.i(TAG, "item.execute");
                item.execute(jsonObject.toString());
            } catch (RemoteException e) {
                Print.e(TAG, "index : " + i + "  " + e.getMessage());
            }
        }


        mCallbackList.finishBroadcast();
    }


}
