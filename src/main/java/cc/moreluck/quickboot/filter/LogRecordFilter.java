package cc.moreluck.quickboot.filter;

import cc.moreluck.quickboot.constant.Constants;
import cc.moreluck.quickboot.domain.User;
import cc.moreluck.quickboot.utils.TTLUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Configuration
public class LogRecordFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 设置链路追踪信息
            setupTraceContext(request);
            TTLUtil.saveMdcContext();
            TTLUtil.ttl.set(getUser());
            // 记录请求信息
            logRequestInfo(request);

            // 创建响应包装器
            BodyLogResponseWrapper responseWrapper = new BodyLogResponseWrapper(response);

            // 执行过滤器链
            filterChain.doFilter(request, responseWrapper);

            // 记录响应信息
            logResponseInfo(responseWrapper, request.getCharacterEncoding());

            // 将响应内容写回原始响应
            response.getOutputStream().write(responseWrapper.getBytes());
        } finally {
            // 清理上下文
            cleanupContext();
        }
    }

    private void setupTraceContext(HttpServletRequest request) {
        String tid = request.getHeader(Constants.TRACE_ID);
        String spanId = request.getHeader(Constants.SPAN_ID);

        if (StringUtils.isBlank(tid)) {
            tid = RandomUtil.randomString(24);
        }
        if (StringUtils.isBlank(spanId)) {
            spanId = Constants.APP;
        }
        MDC.put(Constants.TRACE_ID, tid);
        MDC.put(Constants.SPAN_ID, spanId);
    }



    private void logRequestInfo(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        log.info("客户端IP = {}, 请求路径 = {}:{}, 查询参数 = {}",
                clientIp, request.getMethod(), request.getServletPath(), request.getQueryString());
    }

    private void logResponseInfo(BodyLogResponseWrapper responseWrapper, String encoding) throws IOException {
        byte[] responseData = responseWrapper.getBytes();
        String responseBody = new String(responseData, Charset.forName(encoding));
        log.info("响应报文: {}", responseBody);
    }

    private void cleanupContext() {
        MDC.clear();
        TTLUtil.clearContext();
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {

        String ip = null;
        for (String header : Constants.HEADERS) {
            ip = request.getHeader(header);
            if (isValidIp(ip)) {
                break;
            }
        }

        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private boolean isValidIp(String ip) {
        return ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip);
    }

    public User getUser(){
        User user = new User();
        user.setId(RandomUtil.randomNumbers(12));
        user.setName(RandomUtil.randomString(6));
        user.setAge(RandomUtil.randomInt(2));
        return user;
    }
}
