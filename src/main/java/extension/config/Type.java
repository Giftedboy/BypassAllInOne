package extension.config;

public enum Type {
    Bypass40X("Bypass403"),
    CSRF("CSRF"),
    SSRF("SSRF"),
    CORS("CORS");

    private final String TypeName;

    private Type(String TypeName) {
        this.TypeName = TypeName;
    }

    public String toolName() {
        return this.TypeName;
    }

    public String toString() {
        return this.TypeName;
    }
}
