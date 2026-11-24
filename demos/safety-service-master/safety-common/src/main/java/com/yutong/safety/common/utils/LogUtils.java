package com.yutong.safety.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create gwcuiyx 23-07-06.
 */
public class LogUtils {
    private static final Logger INFO = LoggerFactory.getLogger("info");
    private static final Logger WARN = LoggerFactory.getLogger("warn");
    private static final Logger ERROR = LoggerFactory.getLogger("error");

    /**
     * info日志
     *
     * @param msg
     * @param objs
     */
    public static void info(String msg, Object... objs) {
        INFO.info(msg, objs);
    }

    /**
     * warn日志
     *
     * @param msg
     * @param objs
     */
    public static void warn(String msg, Object... objs) {
        WARN.warn(msg, objs);
    }

    /**
     * error日志
     *
     * @param msg
     * @param t
     */
    public static void error(String msg, Throwable t) {
        ERROR.error(msg, t);
    }

    /**
     * error日志
     *
     * @param msg
     * @param objs
     */
    public static void error(String msg, Object... objs) {
        ERROR.error(msg, objs);
    }
}
