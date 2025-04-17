package cc.moreluck.quickboot.utils;

import cc.moreluck.quickboot.constant.Constants;
import cn.hutool.core.util.RandomUtil;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
public class ThreadMdcUtil {
    public static void setTraceIdIfAbsent() {
        if (MDC.get(Constants.TRACE_ID) == null) {
            MDC.put(Constants.TRACE_ID, RandomUtil.randomString(24));
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent();
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context) {
        return new Runnable() {
            @Override
            public void run() {
                if (context == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(context);
                }
                setTraceIdIfAbsent();
                try {
                    runnable.run();
                } finally {
                    MDC.clear();
                }
            }
        };
    }
}
