// IService.aidl
package remote.bean;

import remote.bean.IServiceCallback;


//ProxyService 和 TaskService 交互时使用
interface IService {

    void registerCallBack(IServiceCallback callback);

    void unRegisterCallBack(IServiceCallback callback);

    void onReponse(String hashParamsString);
}
