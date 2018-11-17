package remote.taskimp;

import java.util.HashMap;
import java.util.Map;

import remote.PostTaskInfoInterface;
import remote.TaskServiceMgr;
import utils.remoteutils.RemoteUtils;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 */
public class PostTaskInfoImp implements PostTaskInfoInterface {

    private static PostTaskInfoImp INSTANCE = null;

    public static PostTaskInfoImp getInstance(){
        if(INSTANCE == null){
            synchronized (PostTaskInfoImp.class){
                if(INSTANCE == null){
                    INSTANCE = new PostTaskInfoImp();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void post(int code, Object info, String methodName) {
        TaskServiceMgr.getInstance().post(code, RemoteUtils.toPostReponseMap(methodName,info));
    }

    @Override
    public void finish(int code, Object info, String methodName) {
        TaskServiceMgr.getInstance().post(code, RemoteUtils.toPostFinish(methodName,info));
    }
}
