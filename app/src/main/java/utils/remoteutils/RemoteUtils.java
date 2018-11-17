package utils.remoteutils;

import java.util.HashMap;

import remote.config.AIDLMethodName;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 */
public class RemoteUtils {

    /**
     * 用来封装TastProcess返回给主进程的数据封装
     * @param methodName
     * @param params
     * @return
     */
    public static HashMap<String,Object> toReponseMap(String methodName,Object params){
        HashMap<String,Object> map = new HashMap<>();
        map.put(AIDLMethodName.METHOD_NAME,methodName);
        map.put(AIDLMethodName.METHOD_PARAMS,params);
        return map;
    }

    /**
     * 用来封装TastProcess返回给主进程的数据封装,添加一个code
     * @param code
     * @return
     */
    public static HashMap<String,Object> addErrorCode(int code,HashMap<String,Object> map){
        map.put(AIDLMethodName.METHOD_ERROR_CODE,code);
        return map;
    }
}
