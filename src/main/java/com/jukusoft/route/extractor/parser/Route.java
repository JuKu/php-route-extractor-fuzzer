package com.jukusoft.route.extractor.parser;

public class Route {

    public enum METHOD {
        POST,
        GET,
        PUT,
        DELETE
    }

    private String url;
    private String name;
    private METHOD method = METHOD.GET;

    public Route(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public METHOD getMethod() {
        return method;
    }

}
