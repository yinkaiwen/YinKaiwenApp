package proxyremote;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import remote.TaskServiceMgr;
import remote.bean.IService;
import remote.bean.IServiceCallback;
import remote.config.AIDLMethodName;
import utils.logutils.Print;
import yinkaiwenapp.ErrorCode;

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
    private Map<String, List<BaseCallBack>> postCallBackMap = new HashMap<>();

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
        public void onReponse(Map response) throws RemoteException {
            //todo hashParamsString中包含 code,methodName,params等参数，需要根据methodName找到
            //对应的callback,再将code和params解析出来.
            Print.i(TAG, "onReponse : " + (response == null ? " null" : response.toString()));

            if (response == null) {
                Print.i(TAG, "response is null");
            } else {
                String methodName = (String) response.get(AIDLMethodName.METHOD_NAME);
                Object obj = response.get(AIDLMethodName.METHOD_PARAMS);
                int code = (int) response.get(AIDLMethodName.METHOD_ERROR_CODE);

                //TaskProcess主动上报数据的处理
                Object post = response.get(AIDLMethodName.METHOD_POST);
                if (post != null) {
                    Print.i(TAG, "post != null");
                    //主动上报的处理
                    if (ErrorCode.SUCCESS == (int) post) {
                        Print.i(TAG, "ErrorCode.SUCCESS == (int) post");
                        Object isUnRegister = response.get(AIDLMethodName.METHOD_UNREGISTER);
                        if (isUnRegister != null && ErrorCode.SUCCESS == (int) isUnRegister) {
                            // 需要解除监听
                            post(code, obj, methodName);
                            unRegisterPostReceiverAll(methodName);
                        } else {
                            // 主动上报
                            post(code, obj, methodName);
                        }
                    } else {
                        Print.i(TAG, "post is not 1.");
                    }
                } else {
                    Print.i(TAG, "post == null");
                    //一次性请求回调
                    BaseCallBack callBack = executeCallBackMap.remove(methodName);
                    if (callBack != null) {
                        callBack.onReponse(code, obj);
                    } else {
                        Print.i(TAG, "onResponse callback is null.");
                    }
                }
            }
        }

        //callback中代码如果有异常，在这里会捕获到对应的异常
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                return super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                Print.e(TAG, e.getMessage());
            }
            return false;
        }

        /**
         * 主动上报的数据回调post
         * @param code
         * @param obj
         * @param methodName
         */
        private void post(int code, Object obj, String methodName) {
            List<BaseCallBack> postCallBacks = postCallBackMap.get(methodName);
            if (postCallBacks != null) {
                synchronized (postCallBackMap.getClass()) {
                    for (BaseCallBack callBack : postCallBacks) {
                        callBack.onReponse(code, obj);
                    }
                }
            } else {
                Print.i(TAG, "NO callback for : " + methodName);
            }
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
     * @param callBack
     * @param methodName
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
                //用来封装AIDL的参数
                Map<String, Object> params = new HashMap<>();
                params.put(AIDLMethodName.METHOD_NAME, methodName);
                params.put(AIDLMethodName.METHOD_PARAMS, jsonObject.toString());
                Print.i(TAG, "item.execute");
                item.execute(params);
            } catch (RemoteException e) {
                Print.e(TAG, "index : " + i + "  " + e.getMessage());
            }
        }

        mCallbackList.finishBroadcast();
    }

    /**
     * 注册监听TaskProcess主动上报的监听
     *
     * @param methodName
     * @param callBack
     */
    public void registerPostReceiver(String methodName, BaseCallBack callBack) {
        if (TextUtils.isEmpty(methodName) || callBack == null) {
            Print.i(TAG, "registerPostReceiver TextUtils.isEmpty(methodName) || callBack == null");
        } else {
            synchronized (postCallBackMap.getClass()) {
                List<BaseCallBack> baseCallBacks = postCallBackMap.get(methodName);
                if (baseCallBacks == null) {
                    List<BaseCallBack> list = new ArrayList<>();
                    list.add(callBack);
                    postCallBackMap.put(methodName, list);
                } else {
                    baseCallBacks.add(callBack);
                    postCallBackMap.put(methodName, baseCallBacks);
                }
            }
        }
    }

    /**
     * 解除监听TaskProcess主动上报的某个监听
     *
     * @param methodName
     * @param callBack
     */
    public void unRegisterPostReceiver(String methodName, BaseCallBack callBack) {
        if (TextUtils.isEmpty(methodName) || callBack == null) {
            Print.i(TAG, "unRegisterPostReceiver TextUtils.isEmpty(methodName) || callBack == null");
        } else {
            synchronized (postCallBackMap.getClass()) {
                List<BaseCallBack> baseCallBacks = postCallBackMap.get(methodName);
                if (baseCallBacks == null) {
                    Print.i(TAG, "no need unRegisterPostReceiver : " + methodName);
                } else {
                    boolean remove = baseCallBacks.remove(callBack);
                    Print.i(TAG, "unRegisterPostReceiver   remove : " + remove);
                    postCallBackMap.put(methodName, baseCallBacks);
                }
            }
        }
    }

    /**
     * 移除所有的监听 关于methodName
     *
     * @param methodName
     */
    public void unRegisterPostReceiverAll(String methodName) {
        if (!TextUtils.isEmpty(methodName)) {
            synchronized (postCallBackMap.getClass()) {
                postCallBackMap.remove(methodName);
                Print.i(TAG, "postCallBackMap.remove : " + methodName);
            }
        } else {
            Print.i(TAG, "unRegisterPostReceiverAll methodName is null: " + methodName);
        }
    }


}
