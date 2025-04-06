package cc.moreluck.quickboot.utils;
    import ch.qos.logback.core.util.SystemInfo;
    import java.util.Objects;

    public class SystemUtil {
        private static final SystemInfo systemInfo = new SystemInfo();

        public SystemUtil() {
        }

        public static String get(String name) {
            return get(name, (String)null);
        }

        public static String get(String name, String defaultValue) {
            Objects.requireNonNull(name);
            String value = null;

            try {
                value = System.getProperty(name);
            } catch (Exception var4) {
            }

            return value == null ? defaultValue : value;
        }

        public static boolean getBoolean(String name, boolean defaultValue) {
            String value = get(name);
            if (value == null) {
                return defaultValue;
            } else {
                return switch (value.trim().toLowerCase()) {
                    case "true", "yes", "1", "on" -> true;
                    case "false", "no", "0", "off" -> false;
                    default -> defaultValue;
                };
            }
        }

        public static long getInt(String name, int defaultValue) {
            String value = get(name);
            if (value == null) {
                return (long)defaultValue;
            } else {
                value = value.trim().toLowerCase();

                try {
                    return (long)Integer.parseInt(value);
                } catch (NumberFormatException var4) {
                    return (long)defaultValue;
                }
            }
        }

        public static long getLong(String name, long defaultValue) {
            String value = get(name);
            if (value == null) {
                return defaultValue;
            } else {
                value = value.trim().toLowerCase();

                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException var5) {
                    return defaultValue;
                }
            }
        }

        public static SystemInfo info() {
            return systemInfo;
        }
    }