package edu.tum.sse.jtec;

public class JTeCExtension {
    private static final String DEFAULT_VERSION = "0.0.3-SNAPSHOT";

    private String version = DEFAULT_VERSION;
    private String options = "";

    public String getVersion(){
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
