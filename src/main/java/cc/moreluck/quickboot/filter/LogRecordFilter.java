package cc.moreluck.quickboot.filter;

import cc.moreluck.quickboot.constant.TraceTypeEnum;
import cc.moreluck.quickboot.utils.LogLocalUtil;
import cc.moreluck.quickboot.utils.MdcUtil;
import cn.hutool.core.text.CharSequenceUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class LogRecordFilter extends OncePerRequestFilter {
    private final String[] EXCLUDE_PATHS = {".js",".css"};
    private final TraceTypeEnum traceType;
    private final boolean sessionEnabled;
    private final boolean debugRequestPath;
    private final boolean debugRequestPathOnlyApi;

    public LogRecordFilter(TraceTypeEnum traceType, boolean sessionEnabled, boolean debugRequestPath, boolean debugRequestPathOnlyApi) {
        this.traceType = traceType;
        this.sessionEnabled = sessionEnabled;
        this.debugRequestPath = debugRequestPath;
        this.debugRequestPathOnlyApi = debugRequestPathOnlyApi;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (debugRequestPath) {
            if(debugRequestPathOnlyApi){
                if(!CharSequenceUtil.containsAny(request.getServletPath(),EXCLUDE_PATHS)){
                    log.info("请求路径 = {}:{},查询参数 = {}", request.getMethod(), request.getServletPath(), request.getQueryString());
                }
            }else {
                log.info("请求路径 = {}:{},查询参数 = {}", request.getMethod(), request.getServletPath(), request.getQueryString());
            }
        }
        MdcUtil.setWebTraceMsg(traceType, request);
        try {
            if (sessionEnabled) {
                filterChain.doFilter(request, response);
            } else {
                NoSessionRequestWrapper noSessionRequestWrapper = new NoSessionRequestWrapper(request);
                filterChain.doFilter(noSessionRequestWrapper, response);
            }
        } finally {
            LogLocalUtil.clear();
            MdcUtil.clear();
        }
    }


    /**
     * 前后端分离不需要session,每个请求创建，浪费内存
     */
    class NoSessionRequestWrapper extends HttpServletRequestWrapper {
        public NoSessionRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }
    }
}
