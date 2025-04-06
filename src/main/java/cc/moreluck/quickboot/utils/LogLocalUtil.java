package cc.moreluck.quickboot.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@UtilityClass
public class LogLocalUtil {
    private static final ThreadLocal<Map<String, Object>> LOCAL = ThreadLocal.withInitial(HashMap::new);

    /**
     * @return threadLocal中的全部值
     */
    public static Map<String, Object> getAll() {
        return new HashMap<>(LOCAL.get());
    }

    /**
     * 设置一个值到ThreadLocal
     *
     * @param key   键
     * @param value 值
     * @param <T>   值的类型
     * @return 被放入的值
     * @see Map#put(Object, Object)
     */
    public static <T> T put(String key, T value) {
        try {
            LOCAL.get().put(key, value);
        } catch (Exception e) {
            log.error("设置一个Value到ThreadLocal异常:", e);
        }
        return value;
    }

    /**
     * 设置一个Map到ThreadLocal
     *
     * @param map map
     * @return 被放入的值
     * @see Map#putAll(Map)
     */
    public static void put(Map<String, Object> map) {
        try {
            LOCAL.get().putAll(map);
        } catch (Exception e) {
            log.error("设置一个Map到ThreadLocal异常:", e);
        }
    }

    /**
     * 删除参数对应的值
     *
     * @param key
     * @see Map#remove(Object)
     */
    public static void remove(String key) {
        LOCAL.get().remove(key);
    }

    /**
     * 清空ThreadLocal
     *
     * @see Map#clear()
     */
    public static void clear() {
        LOCAL.remove();
    }

    /**
     * 从ThreadLocal中获取值
     *
     * @param key 键
     * @param <T> 值泛型
     * @return 值, 不存在则返回null, 如果类型与泛型不一致, 可能抛出{@link ClassCastException}
     * @see Map#get(Object)
     * @see ClassCastException
     */
    public static <T> T get(String key) {
        return ((T) LOCAL.get().get(key));
    }

    /**
     * 从ThreadLocal中获取值,并指定一个当值不存在的提供者
     *
     * @see Supplier
     */
    public static <T> T getIfAbsent(String key, Supplier<T> supplierOnNull) {
        return ((T) LOCAL.get().computeIfAbsent(key, k -> supplierOnNull.get()));
    }

    /**
     * 获取一个值后然后删除掉
     *
     * @param key 键
     * @param <T> 值类型
     * @return 值, 不存在则返回null
     * @see this#get(String)
     * @see this#remove(String)
     */
    public static <T> T getAndRemove(String key) {
        try {
            return get(key);
        } finally {
            remove(key);
        }
    }

}
