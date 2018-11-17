package remote.taskimp;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import proxyremote.BaseCallBack;
import remote.PostTaskInfoInterface;
import remote.TaskInterface;
import remote.bean.TestBean;
import remote.config.AIDLMethodName;
import utils.logutils.Print;
import utils.remoteutils.RemoteUtils;
import yinkaiwenapp.ErrorCode;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 * <p>
 * 下载任务
 */
public class DownLoadTask implements TaskInterface {
    private static final String TAG = "DownLoadTask";

    private static DownLoadTask INSTANCE = null;

    public static DownLoadTask getInstance() {
        if (INSTANCE == null) {
            synchronized (DownLoadTask.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownLoadTask();
                }
            }
        }
        return INSTANCE;
    }

    private DownLoadTask() {
    }


    public void executeStartDonwLoad(JSONObject params, BaseCallBack callBack) {
        Print.i(TAG, "params : " + params);

        callBack.onReponse(ErrorCode.SUCCESS, RemoteUtils.toReponseMap(AIDLMethodName.START_DOWN_LOAD, new TestBean()));


        final PostTaskInfoInterface post = new PostTaskInfoImp();
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 100; i = i + 10) {
                    try {
                        Thread.sleep(500);
                        if (i >= 100) {
                            post.finish(ErrorCode.SUCCESS, i, AIDLMethodName.DOWN_LOAD_PROCESS);
                        } else {
                            post.post(ErrorCode.SUCCESS, i, AIDLMethodName.DOWN_LOAD_PROCESS);
                        }
                    } catch (InterruptedException e) {
                        Print.i(TAG, e.getMessage());
                    }
                }
            }
        });
    }
}
