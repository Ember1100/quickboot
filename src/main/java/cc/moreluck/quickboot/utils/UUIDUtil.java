package cc.moreluck.quickboot.utils;

import java.util.UUID;
public class UUIDUtil {
    public static String generateUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        String ss = "%s %%%s%";

        System.out.println(String.format(ss, "a", "b"));
    }
}
