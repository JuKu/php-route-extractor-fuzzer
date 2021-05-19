package com.jukusoft.route.extractor.parser;

public class Route {

    private String url;
    private String name;

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

}
