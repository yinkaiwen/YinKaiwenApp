package remote.taskimp;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import proxyremote.BaseCallBack;
import remote.TaskInterface;
import remote.config.AIDLMethodName;
import utils.logutils.Print;
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

        Map<String,String> onReponseParams = new HashMap<>();
        onReponseParams.put(AIDLMethodName.METHOD_NAME,AIDLMethodName.START_DOWN_LOAD);
        onReponseParams.put(AIDLMethodName.METHOD_PARAMS,"success");

        callBack.onReponse(ErrorCode.SUCCESS,onReponseParams);
    }
}
