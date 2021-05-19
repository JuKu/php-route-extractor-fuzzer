package com.jukusoft.route.extractor.parser;

public class Parameter {

    private String name;
    private String in;
    private boolean required;
    private String type;
    private String defaultStr;

    public Parameter(String name, String in, boolean required, String type, String defaultStr) {
        this.name = name;
        this.in = in;
        this.required = required;
        this.type = type;
        this.defaultStr = defaultStr;
    }

    public String getName() {
        return name;
    }

    public String getIn() {
        return in;
    }

    public boolean getRequired() {
        return required;
    }

    public String getType() {
        return type;
    }

    public String getDefaultStr() {
        return defaultStr;
    }

}
