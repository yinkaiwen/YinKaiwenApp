package utils.timeutils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kevin on 2018/8/12.
 * https://github.com/yinkaiwen
 */
public class TimeUtils {

    /**
     * 获取当前的日期 格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentTimeString(){
        long currentTime = System.currentTimeMillis();
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date(currentTime));
    }
}
