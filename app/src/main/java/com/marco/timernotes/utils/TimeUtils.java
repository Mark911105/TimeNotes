package com.marco.timernotes.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Administrator
 * Date: 2016/5/25
 * Time: 0:28
 * 格式化时间
 */
public class TimeUtils {
    public static final SimpleDateFormat JDF = new SimpleDateFormat("HH:mm");

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String fixTime(long time) {
        return JDF.format(new Date(time));
    }
}
