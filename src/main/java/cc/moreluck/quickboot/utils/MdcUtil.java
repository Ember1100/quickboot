package cc.moreluck.quickboot.utils;

import cc.moreluck.quickboot.constant.Constants;
import cc.moreluck.quickboot.constant.TraceTypeEnum;
import cn.hutool.core.util.IdUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Objects;

import static cc.moreluck.quickboot.constant.Constants.*;

public class MdcUtil {

    protected MdcUtil() {
    }

    public static String getTrace() {
        return getMDC(TRACE_ID);
    }

    public static String getSpan() {
        return getMDC("SpanId");
    }

    public static void setTrace() {
        putMDC(TRACE_ID, IdUtil.fastSimpleUUID());
    }

    public static String getMDC(String key) {
        try {
            return MDC.get(key);
        } catch (Exception var2) {
            return null;
        }
    }

    public static void putMDC(String key, String val) {
        try {
            MDC.put(key, val);
        } catch (Exception var3) {
        }

    }

    public static String getTxId() {
        return getMDC(TX_ID);
    }

    public static void setTxId() {
        putMDC(TX_ID, IdUtil.fastSimpleUUID());
    }

    public static void setTxAndSpanId(TraceTypeEnum type) {
        String ID;
        if (!Objects.isNull(type) && TraceTypeEnum.TRACE_16 != type) {
            if (TraceTypeEnum.TRACE_32 == type) {
                ID = IdUtil.fastSimpleUUID();
            } else {
                ID = IdUtil.fastSimpleUUID();
            }
        } else {
            ID = IdUtil.fastSimpleUUID();
        }

        putMDC(TX_ID, ID);
        putMDC(SPAN_ID, ID);
    }

    public static void setSpan(TraceTypeEnum type) {
        String spanId;
        if (!Objects.isNull(type) && TraceTypeEnum.TRACE_16 != type) {
            if (TraceTypeEnum.TRACE_32 == type) {
                spanId = IdUtil.fastSimpleUUID();
            } else {
                spanId = IdUtil.fastSimpleUUID();
            }
        } else {
            spanId = IdUtil.fastSimpleUUID();
        }

        putMDC(SPAN_ID, spanId);
    }

    public static void setSpan() {
        setSpan(TraceTypeEnum.TRACE_16);
    }

    public static void setTrace(TraceTypeEnum type) {
        String traceId;
        if (!Objects.isNull(type) && TraceTypeEnum.TRACE_16 != type) {
            if (TraceTypeEnum.TRACE_32 == type) {
                traceId = IdUtil.fastSimpleUUID();
            } else {
                traceId = IdUtil.fastSimpleUUID();
            }
        } else {
            traceId = IdUtil.fastSimpleUUID();
        }

        putMDC(TRACE_ID, traceId);
    }

    public static void clear() {
        try {
            MDC.clear();
        } catch (Exception var1) {
        }

    }

    public static void setModuleName(String moduleName) {
        putMDC("ModuleName", moduleName);
    }

    public static String getModuleName() {
        return getMDC("ModuleName");
    }

    public static void setTargetSignature(String targetSignature) {
        putMDC("TargetSignature", targetSignature);
    }

    public static String getTargetSignature() {
        return getMDC("TargetSignature");
    }

    public static void setWebTraceMsg(TraceTypeEnum type, HttpServletRequest request) {
        String traceId = WebUtil.getTraceHeader(request);
        if(StringUtils.isBlank(traceId)){
            setTrace(type);
        }else {
            putMDC(TRACE_ID, traceId);
        }
        String spanId = WebUtil.getSpanHeader(request);
        if(StringUtils.isBlank(spanId)){
            setSpan(type);
        }else{
            putMDC(SPAN_ID, spanId);
        }


        setTxId();
        putMDC(TRACE_ID, WebUtil.getIP(request));
        putMDC(TRACE_URI, WebUtil.getMethod(request) + Constants.COLON + WebUtil.getServletPath(request));
    }

    public static void setWebTraceMsg(TraceTypeEnum type) {
        HttpServletRequest request = WebUtil.getRequest();
        setWebTraceMsg(type,request);
    }

    public static void setWebTraceMsg(HttpServletRequest request) {

    }

    /**
     * 获取Map值
     * @return
     */
    public static Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }

}
