package remote;

import java.util.HashMap;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 * <p>
 * TaskProcess 主动向主进程发送消息
 */
public interface PostTaskInfoInterface {
    void post(int code, Object info,String methodName);

    void finish(int code, Object info,String methodName);
}
