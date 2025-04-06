package cc.moreluck.quickboot.utils;

import java.util.UUID;
public class UUIDUtil {
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
