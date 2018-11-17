package proxyremote;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.json.JSONObject;

import remote.config.AIDLMethodName;
import utils.logutils.Print;
import yinkaiwenapp.AppConfigure;
import yinkaiwenapp.BaseApplication;


/**
 * Created by kevin on 2018/11/11.
 * https://github.com/yinkaiwen
 * <p>
 * 用来操作ProxyService 的工具类
 */
public class ProxyUtils {

    private static final String TAG = "ProxyUtils";
    private static ProxyUtils INSTANCE = null;
    public static final String CALLBACK_SERVICE_PROCY_ACTION = "yinkaiwen.intent.proxy";
    private ProxyService mCallBackService = null;

    public static ProxyUtils getInstance() {
        Print.i(TAG, "CallBackUtils.getInstance.");
        if (INSTANCE == null) {
            synchronized (ProxyUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProxyUtils();
                }
            }
        }
        return INSTANCE;
    }

    private ProxyUtils() {
        bindCallBackService();
    }

    /**
     * 主进程中
     * 绑定CallBackService.
     */
    private void bindCallBackService() {
        Intent intent = new Intent(BaseApplication.getBaseApplicationContext(), ProxyService.class);
        intent.setAction(CALLBACK_SERVICE_PROCY_ACTION);
        BaseApplication.getBaseApplicationContext().bindService(intent, mProxyConnection, Service.BIND_AUTO_CREATE);
    }

    /**
     * 代理ServiceConnection.
     */
    private ServiceConnection mProxyConnection = new ServiceConnection() {

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

            ProxyService.MyBinder mBinder = (ProxyService.MyBinder) service;
            mCallBackService = mBinder.getCallBackService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Print.i(TAG, "ProxyService onServiceDisconnected");
        }

    };


    public void startDownload(JSONObject jsonObject, BaseCallBack callBack) {
        if (mCallBackService != null && callBack != null) {
            Print.i(TAG, "start down load.");
            mCallBackService.execute(jsonObject, callBack, AIDLMethodName.START_DOWN_LOAD);
        } else {
            Print.i(TAG, "startDownload mCallBackService == null or callBack is null");
        }
    }

    public void receiveDownLoadProcess(BaseCallBack callBack) {
        if (mCallBackService != null && callBack != null) {
            Print.i(TAG, "receiveDownLoadProcess.");
            mCallBackService.registerPostReceiver(AIDLMethodName.DOWN_LOAD_PROCESS, callBack);
        } else {
            Print.i(TAG, "receiveDownLoadProcess mCallBackService == null or callBack is null");
        }
    }
}
