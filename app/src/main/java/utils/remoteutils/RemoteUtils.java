package utils.remoteutils;

import java.util.HashMap;

import remote.config.AIDLMethodName;
import utils.logutils.Print;
import yinkaiwenapp.ErrorCode;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 */
public class RemoteUtils {

    public static final String TAG = "RemoteUtils";

    /**
     * 用来封装TastProcess返回给主进程的数据封装
     * 一次性请求
     * @param methodName
     * @param params
     * @return
     */
    public static HashMap<String, Object> toReponseMap(String methodName, Object params) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(AIDLMethodName.METHOD_NAME, methodName);
        map.put(AIDLMethodName.METHOD_PARAMS, params);
        return map;
    }

    /**
     * 用来封装TastProcess返回给主进程的数据封装,添加一个code
     *
     * @param code
     * @return
     */
    public static HashMap<String, Object> addErrorCode(int code, HashMap<String, Object> map) {
        map.put(AIDLMethodName.METHOD_ERROR_CODE, code);
        return map;
    }

    /**
     * 用来封装TastProcess 主动上报给主进程的数据封装
     *
     * @param methodName
     * @param params
     * @return
     */
    public static HashMap<String, Object> toPostReponseMap(String methodName, Object params) {
        HashMap<String, Object> toReponseMap = toReponseMap(methodName, params);
        toReponseMap.put(AIDLMethodName.METHOD_POST, ErrorCode.SUCCESS);
        Print.i(TAG,toReponseMap.toString());
        return toReponseMap;
    }

    /**
     * 用来封装TastProcess 主动上报给主进程的数据封装
     * 解除主进程的回调监听
     *
     * @param methodName
     * @param params
     * @return
     */
    public static HashMap<String, Object> toPostFinish(String methodName, Object params) {
        HashMap<String, Object> toReponseMap = toPostReponseMap(methodName, params);
        toReponseMap.put(AIDLMethodName.METHOD_UNREGISTER, ErrorCode.SUCCESS);
        return toReponseMap;
    }
}
