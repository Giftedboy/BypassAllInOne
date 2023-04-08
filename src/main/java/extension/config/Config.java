package extension.config;

public class Config {
    public static final String[] ForbiddenPaths = {
            ""
    };
    public static final String[] ForbiddenHeaders = {
            ""
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
    public static final String[] ForbiddenFixedHeaders = {
            ""
    };
}
