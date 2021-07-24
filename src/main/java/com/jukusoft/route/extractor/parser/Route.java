package com.jukusoft.route.extractor.parser;

import java.util.*;

public class Route {

    public enum METHOD {
        POST,
        GET,
        PUT,
        DELETE
    }

    private Map<METHOD,RouteMethod> methods = new HashMap<>();

    private String url;
    private String name;

    public Route(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public void addRouteMethod(METHOD method, RouteMethod routeMethod) {
        methods.put(method, routeMethod);
    }

    public Map<METHOD,RouteMethod> getMethods() {
        return methods;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

}
