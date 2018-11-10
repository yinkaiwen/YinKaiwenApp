package remote;

import android.content.Intent;

import yinkaiwenapp.BaseApplication;

/**
 * Created by kevin on 2018/11/10.
 * https://github.com/yinkaiwen
 */
public class TaskServiceMgr {

    private static final String ACTION = "yinkaiwen.intent.taskservice";
    private static TaskServiceMgr INSTANCE = null;

    public static TaskServiceMgr getInstance(){
        if(INSTANCE == null){
            synchronized (TaskServiceMgr.class){
                if(INSTANCE == null){
                    INSTANCE = new TaskServiceMgr();
                }
            }
        }
        return INSTANCE;
    }

    private TaskServiceMgr() {
    }

    public void startTaskService(){
        Intent intent = new Intent(BaseApplication.getBaseApplicationContext(),TaskService.class);

        BaseApplication.getBaseApplicationContext().startService(intent);
    }
}
