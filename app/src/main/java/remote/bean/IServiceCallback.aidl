// IServiceCallback.aidl
package remote.bean;


// ProxyService 跟 TaskService 交互的回调接口
interface IServiceCallback {

    //ProxyService中用来调用TaskService中的方法
    void execute(String params);
}
