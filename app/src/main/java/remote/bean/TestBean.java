package remote.bean;

import java.io.Serializable;

/**
 * Created by kevin on 2018/11/17.
 * https://github.com/yinkaiwen
 */
public class TestBean implements Serializable {
    public String arg = "success";

    @Override
    public String toString() {
        return "TestBean{" +
                "arg='" + arg + '\'' +
                '}';
    }
}
