package remote.taskimp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import proxyremote.BaseCallBack;
import remote.TaskInterface;
import remote.bean.TestBean;
import remote.config.AIDLMethodName;
import utils.logutils.Print;
import utils.remoteutils.RemoteUtils;
import yinkaiwenapp.ErrorCode;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 */
public class DownLoadTask implements TaskInterface {
    private static final String TAG = "DownLoadTask";

    private static DownLoadTask INSTANCE = null;

    public static DownLoadTask getInstance(){
        if(INSTANCE == null){
            synchronized (DownLoadTask.class){
                if(INSTANCE == null){
                    INSTANCE = new DownLoadTask();
                }
            }
        }
        return INSTANCE;
    }

    private DownLoadTask() {
    }

    public void executeStartDonwLoad(JSONObject params, BaseCallBack callBack){
        Print.i(TAG,"params : " + params);

        callBack.onReponse(ErrorCode.SUCCESS, RemoteUtils.toReponseMap(AIDLMethodName.START_DOWN_LOAD,new TestBean()));
    }
}
