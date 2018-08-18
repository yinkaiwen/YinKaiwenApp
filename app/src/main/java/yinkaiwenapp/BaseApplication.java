package yinkaiwenapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by kevin on 2018/7/1.
 * https://github.com/yinkaiwen
 */
public class BaseApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }

    public static Context getBaseApplicationContext(){
        return mContext;
    }


}
