package remote.config;

/**
 * Created by kevin on 2018/11/11.
 * https://github.com/yinkaiwen
 */
public class AIDLMethodName {

    /*通用参数*/
    public static final String METHOD_NAME = "method_name";
    public static final String METHOD_PARAMS = "method_params";
    public static final String METHOD_ERROR_CODE = "method_error_code";
    /*通用参数*/


    /*TaskInterface的方法名称，用来给主进程调用，不可重复*/
    //TaskInterface中的用来给主进程调用的方法名的开头，固定值execute，可以使用注解的方式，之后升级后使用
    public static final String FIX_START_METHOD_NAME = "execute";
    public static final String START_DOWN_LOAD = "executeStartDonwLoad";
    /*TaskInterface的方法名称，用来给主进程调用，不可重复*/
}
