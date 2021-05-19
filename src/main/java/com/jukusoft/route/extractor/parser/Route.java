package com.jukusoft.route.extractor.parser;

import java.util.ArrayList;
import java.util.List;

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
    private String produces = "application/xml";
    private List<Parameter> parameters = new ArrayList<>();

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

    public String getProduces() {
        return produces;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(String name, String in, boolean required, String type, String defaultStr) {
        parameters.add(new Parameter(name, in, required, type, defaultStr));
    }

}
