package io.xka.jlite.web.serv.options;

public class CORSOptions {
    //create
    private boolean enable = false;

    private String allowOrigin = "*";

    private String allowMethods = "GET, POST, PUT, DELETE, OPTIONS";

    private String allowHeaders = "Content-Type, Authorization, Content-Length, X-Requested-With";

    private String exposeHeaders = "Content-Type, Authorization, Content-Length, X-Requested-With";

    private String allowCredentials = "true";

    private String maxAge = "1800";

    public static CORSOptions create() {
        return new CORSOptions();
    }

    //copy
    public static CORSOptions copy(CORSOptions corsOptions) {
        return CORSOptions.create()
                .enable(corsOptions.enable)
                .allowOrigin(corsOptions.allowOrigin)
                .allowMethods(corsOptions.allowMethods)
                .allowHeaders(corsOptions.allowHeaders)
                .exposeHeaders(corsOptions.exposeHeaders)
                .allowCredentials(corsOptions.allowCredentials)
                .maxAge(corsOptions.maxAge);
    }

    //settings
    public CORSOptions enable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public CORSOptions allowOrigin(String allowOrigin) {
        this.allowOrigin = allowOrigin;
        return this;
    }

    public CORSOptions allowMethods(String allowMethods) {
        this.allowMethods = allowMethods;
        return this;
    }

    public CORSOptions allowHeaders(String allowHeaders) {
        this.allowHeaders = allowHeaders;
        return this;
    }

    public CORSOptions exposeHeaders(String exposeHeaders) {
        this.exposeHeaders = exposeHeaders;
        return this;
    }

    public CORSOptions allowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
        return this;
    }

    public CORSOptions maxAge(String maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    //getters

    public boolean isEnable() {
        return enable;
    }

    public String getAllowOrigin() {
        return allowOrigin;
    }

    public String getAllowMethods() {
        return allowMethods;
    }

    public String getAllowHeaders() {
        return allowHeaders;
    }

    public String getExposeHeaders() {
        return exposeHeaders;
    }

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public String getMaxAge() {
        return maxAge;
    }
}
