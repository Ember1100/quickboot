package cc.moreluck.quickboot.constant;

public interface Constants {

    String TRACE_ID = "TraceId";
    String APP = "quickboot";
    String SPAN_ID = "SpanId";
    String TX_ID = "TxId";
    String EMPTY = "";
    String TRACE_HOST_NAME = "HostName";
    String UNKNOWN = "unknown";
    String TRACE_URI = "UriPath";
    String COLON = ":";
    String UTF_8 = "UTF-8";
    String COMMA = ",";
    String[] HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    public static final String NONE_KEY = "NONE";

    public static final String DEFAULT_OWNER = "DEFAULT";

    public static final int DEFAULT_EXPIRE_TIME = -1;

    public static final int DEFAULT_WAIT_TIME = Integer.MAX_VALUE;

    public static final String PREFIX = "QUICKBOOT:";
}