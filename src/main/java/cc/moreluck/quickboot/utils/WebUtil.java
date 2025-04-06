package cc.moreluck.quickboot.utils;

import cc.moreluck.quickboot.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static cc.moreluck.quickboot.constant.Constants.EMPTY;

public class WebUtil {
    public static final String USER_AGENT_HEADER = "user-agent";
    private static final String[] IP_HEADER_NAMES = new String[]{
            "x-forwarded-for",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };
    private static final Predicate<String> IP_PREDICATE = (ip) -> !StringUtils.hasText(ip) || Constants.UNKNOWN.equalsIgnoreCase(ip);
    private static final Predicate<String> IP_LOCAL_PREDICATE = (ip) -> Objects.equals("127.0.0.1", ip) || Objects.equals("0:0:0:0:0:0:0:1", ip);


    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes) requestAttributes).getRequest();
        //return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        //        .map(x -> (ServletRequestAttributes) x)
        //        .map(ServletRequestAttributes::getRequest)
        //        .orElse(null);
    }


    public static HttpServletResponse getResponse() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(x -> (ServletRequestAttributes) x)
                .map(ServletRequestAttributes::getResponse)
                .orElse(null);
    }

    public static String getIP() {
        return Optional.ofNullable(WebUtil.getRequest())
                .map(WebUtil::getIP)
                .orElse(null);
    }


    private static String getAddr(String ip) {
        if (!StringUtils.hasText(ip)) {
            return null;
        }
        if (!IP_LOCAL_PREDICATE.test(ip)) {
            return ip;
        }
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
        }
        return null;
    }


    /**
     * 获取ip
     */
    public static String getIP(HttpServletRequest request) {
        if (request == null) {
            return EMPTY;
        }
        String ip = null;
        for (String ipHeader : IP_HEADER_NAMES) {
            ip = request.getHeader(ipHeader);
            if (!IP_PREDICATE.test(ip)) {
                break;
            }
        }
        if (IP_PREDICATE.test(ip)) {
            ip = request.getRemoteAddr();
        }
        String ipStr = !StringUtils.hasText(ip) ? null : splitTrim(ip, Constants.COMMA)[0];
        return getAddr(ipStr);
    }

    /**
     * 分割 字符串 删除常见 空白符
     *
     * @param str       字符串
     * @param delimiter 分割符
     * @return 字符串数组
     */
    private static String[] splitTrim(String str, String delimiter) {
        return StringUtils.delimitedListToStringArray(str, delimiter, " \t\n\n\f");
    }


    public static String getServletPath() {
        HttpServletRequest request = getRequest();
        return getServletPath(request);
    }

    public static String getServletPath(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return EMPTY;
        }
        return request.getServletPath();
    }


    public static String getMethod(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return EMPTY;
        }
        return request.getMethod();
    }


    public static String getTraceHeader() {
        HttpServletRequest request = getRequest();
        return getTraceHeader(request);
    }

    public static String getTraceHeader(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return EMPTY;
        }
        return request.getHeader(Constants.TRACE_ID);
    }


    public static String getSpanHeader(HttpServletRequest request) {
        if (Objects.isNull(request)) {
            return EMPTY;
        }
        return request.getHeader(Constants.SPAN_ID);
    }


    /***
     * 获取 request 中 json 字符串的内容
     *
     * @param request request
     * @return 字符串内容
     */
    public static String getRequestParamString(HttpServletRequest request) {
        try {
            return getRequestStr(request);
        } catch (Exception ex) {
            return EMPTY;
        }
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request) throws IOException {
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            return new String(queryString.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8).replaceAll("&amp;", "&").replaceAll("%22", "\"");
        }
        return getRequestStr(request, getRequestBytes(request));
    }

    /**
     * 获取 request 请求的 byte[] 数组
     *
     * @param request request
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readLen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readLen == -1) {
                break;
            }
            i += readLen;
        }
        return buffer;
    }

    /**
     * 获取 request 请求内容
     *
     * @param request request
     * @param buffer  buffer
     * @return String
     * @throws IOException IOException
     */
    public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null) {
            charEncoding = Constants.UTF_8;
        }
        String str = new String(buffer, charEncoding).trim();
        if (!StringUtils.hasText(str)) {
            StringBuilder sb = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                String value = request.getParameter(key);
                appendBuilder(sb, key, "=", value, "&");
            }
            str = removeSuffix(sb.toString(), "&");
        }
        return str.replaceAll("&amp;", "&");
    }

    public static StringBuilder appendBuilder(StringBuilder sb, CharSequence... strs) {
        for (CharSequence str : strs) {
            sb.append(str);
        }

        return sb;
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }

        final String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());// 截取前半段
        }
        return str2;
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String subPre(CharSequence string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    /**
     * 改进JDK subString<br>
     * index从0开始计算，最后一个字符为-1<br>
     * 如果from和to位置一样，返回 "" <br>
     * 如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length<br>
     * 如果经过修正的index中from大于to，则互换from和to example: <br>
     * abcdefgh 2 3 =》 c <br>
     * abcdefgh 2 -3 =》 cde <br>
     *
     * @param str              String
     * @param fromIndexInclude 开始的index（包括）
     * @param toIndexExclude   结束的index（不包括）
     * @return 字串
     */
    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();

        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0) {
                fromIndexInclude = 0;
            }
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }

        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0) {
                toIndexExclude = len;
            }
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }

        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }

        if (fromIndexInclude == toIndexExclude) {
            return EMPTY;
        }

        return str.toString().substring(fromIndexInclude, toIndexExclude);
    }

    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }
}