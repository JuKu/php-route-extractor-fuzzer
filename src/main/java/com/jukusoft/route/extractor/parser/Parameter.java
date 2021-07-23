package com.jukusoft.route.extractor.parser;

public class Parameter {

    //see also: https://swagger.io/docs/specification/2-0/describing-parameters/
    public enum IN_TYPE {
        QUERY,
        PATH,
        HEADER,
        FORM
    }

    private String name;
    private IN_TYPE in;//possible values: "query" or "path", see also: https://swagger.io/docs/specification/2-0/describing-parameters/
    private boolean required;
    private String type;
    private String defaultStr;

    public Parameter(String name, IN_TYPE in, boolean required, String type, String defaultStr) {
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
        return in.name().toLowerCase();
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
