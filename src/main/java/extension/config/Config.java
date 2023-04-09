package extension.config;

import java.util.HashMap;
import java.util.Map;

public class Config {
    public static final String[] ForbiddenPaths = {
            ""
    };
    public static final String[] ForbiddenHeaders = {
            "X-Custom-IP-Authorization",
            "X-Forward-For",
            "X-Remote-IP",
            "X-Originating-IP",
            "X-Remote-Addr",
            "X-Client-IP",
            "X-Real-IP",
            "X-Forwarded-For",
            "X-Forwarded",
            "Forwarded-For",
            "X-ProxyUser-Ip",
            "X-Original-URL",
            "Client-IP",
            "True-Client-IP",
            "Cluster-Client-IP",
            "X-ProxyUser-Ip",
            "Host",
            "Redirect",
            "Referer",
            "X-Forwarded-By",
            "X-Forwarded-Host",
            "X-True-IP",
            "Proxy-Host",
            "Request-Uri",
            "X-Forwarded-For-Original",
            "X-Forwarded-Server",
            "X-Forwarder-For",
            "X-Forward-For",
            "Base-Url",
            "Http-Url",
            "Proxy-Url",
            "Real-Ip",
            "Referrer",
            "Refferer",
            "Uri",
            "Url",
            "X-Host",
            "X-Http-Destinationurl",
            "X-Http-Host-Override",
            "X-Original-Remote-Addr",
            "X-Proxy-Url",
            "X-Rewrite-Url"
    };
    public static final String[] ForbiddenHeaderValues = {
            "localhost",
            "localhost:80",
            "localhost:443",
            "127.0.0.1",
            "127.0.0.1:80",
            "127.0.0.1:443",
            "2130706433",
            "0x7F000001",
            "0177.0000.0000.0001",
            "0",
            "127.1",
            "10.0.0.0",
            "10.0.0.1",
            "172.16.0.0",
            "172.16.0.1",
            "192.168.1.0",
            "192.168.1.1",
    };
    public static final Map<String,String> ForbiddenFixedHeaders = new HashMap<String,String>(Map.of(
            "X-Original-URL","/",
            "X-Rewrite-URL","/$path"
    ));

}
