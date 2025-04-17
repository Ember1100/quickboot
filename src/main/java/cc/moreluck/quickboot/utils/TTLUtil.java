package cc.moreluck.quickboot.utils;

import cc.moreluck.quickboot.domain.User;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @Author: huangqi
 * @CreateTime: 2025-04-17
 * @Description:
 */
public class TTLUtil {

    public static final TransmittableThreadLocal<User> ttl = new TransmittableThreadLocal<>();

    // 使用 TransmittableThreadLocal 存储 MDC 上下文
    public static final TransmittableThreadLocal<Map<String, String>> ttlMdcContext = new TransmittableThreadLocal<>();

    // 保存 MDC 上下文
    public static void saveMdcContext() {
        ttlMdcContext.set(MDC.getCopyOfContextMap());
    }

    // 恢复 MDC 上下文
    public static void restoreMdcContext() {
        Map<String, String> contextMap = ttlMdcContext.get();
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
    }

    // 清理 MDC 和 TTL 上下文
    public static void clearContext() {
        MDC.clear();
        ttlMdcContext.remove();
        ttl.remove();
    }
}
