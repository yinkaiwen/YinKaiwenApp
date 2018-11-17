// IService.aidl
package remote.bean;

import remote.bean.IServiceCallback;


interface IService {

    //用来注册RemoteCallbackList
    void registerCallBack(IServiceCallback callback);

    //用来解注册RemoteCallbackList
    void unRegisterCallBack(IServiceCallback callback);

    //TaskService中回调时使用.
    void onReponse(in Map reponse);
}
