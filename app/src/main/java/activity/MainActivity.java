package activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kevin.yinkaiwenapp.R;

import org.json.JSONObject;

import proxyremote.BaseCallBack;
import proxyremote.ProxyUtils;
import remote.TaskServiceMgr;
import utils.logutils.Print;
import utils.logutils.PrintManager;
import yinkaiwenapp.BroadCastAction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private RelativeLayout mLoadingLayout, mMainLayout;
    private TextView mClientTextView, mServerTextView;

    private MyHandler mHandler = new MyHandler();
    private static final int LOADING_TIME = 3000;//加载界面3秒
    private long timeStamp;


    private static final int MSG_SHOW_MAIN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        registerBroadCast();
        PrintManager.getInstance().init(this);
        TaskServiceMgr.getInstance().startTaskService();
        ProxyUtils.getInstance();

        timeStamp = System.currentTimeMillis();
        mLoadingLayout = findViewById(R.id.layout_loading);
        mMainLayout = findViewById(R.id.layout_main);
        mClientTextView = findViewById(R.id.text_send);
        mServerTextView = findViewById(R.id.text_receiver);

        mClientTextView.setOnClickListener(this);
        mServerTextView.setOnClickListener(this);

        showMain();
    }

    private BaseCallBack mPostCallback = new BaseCallBack() {
        @Override
        public void onReponse(int code, Object obj) {
            Print.i(TAG, "code : " + code + " obj : " + obj);
        }
    };


    private void testLog() {
        ProxyUtils.getInstance().receiveDownLoadProcess(mPostCallback);

        ProxyUtils.getInstance().startDownload(new JSONObject(), new BaseCallBack() {
            @Override
            public void onReponse(int code, Object obj) {
                Print.i(TAG, "code : " + code + "obj : " + obj);
            }
        });
    }

    /**
     * 注册广播接收者
     */
    private void registerBroadCast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadCastAction.PROXY_SERVICE_BINDED);

        manager.registerReceiver(mBroadcastReceiver, filter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Print.i(TAG, "mBroadcastReceiver action : " + (intent == null ? " null " : intent.getAction()));
            if (intent != null) {
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action)) {
                    switch (action) {
                        case BroadCastAction.PROXY_SERVICE_BINDED:
                            showMain();
                            break;
                        default:
                            Print.i(TAG, "mBroadcastReceiver action : " + action + " is not deal.");
                            break;
                    }
                } else {
                    Print.i(TAG, "mBroadcastReceiver action is null.");
                }
            } else {
                Print.i(TAG, "mBroadcastReceiver intent is null");
            }
        }
    };

    private void showMain() {
        if(mLoadingLayout.getVisibility() == View.GONE){
            return;
        }
        long time = System.currentTimeMillis();
        if ((time - timeStamp) >= LOADING_TIME) {
            mLoadingLayout.setVisibility(View.GONE);
            mMainLayout.setVisibility(View.VISIBLE);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_MAIN, (LOADING_TIME - (time - timeStamp)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PrintManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            switch (v.getId()) {
                case R.id.text_send:
                    Print.i(TAG,getResources().getString(R.string.main_this_is_client));
                    break;
                case R.id.text_receiver:
                    Print.i(TAG,getResources().getString(R.string.main_this_is_server));
                    break;
                default:
                    Print.i(TAG, "v : " + v.getId() + " is not deal.");
                    break;
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case MSG_SHOW_MAIN:
                        showMain();
                        break;
                    default:
                        Print.i(TAG, "What : " + msg.what + " is not deal.");
                        break;
                }
            }
        }
    }
}
