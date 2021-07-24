package com.jukusoft.route.extractor.parser;

import java.util.*;

public class RouteMethod {

    private Route.METHOD method = Route.METHOD.GET;
    private String produces = "application/xml";
    private List<Parameter> parameters = new ArrayList<>();
    private Map<String,String> defaultValues = new HashMap<>();

    public RouteMethod(Route.METHOD method) {
        this.method = method;
    }

    public Route.METHOD getMethod() {
        return method;
    }

    public String getProduces() {
        return produces;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(String name, Parameter.IN_TYPE in, boolean required, String type, String defaultStr) {
        if (hasParameter(name)) {
            //override values
            Parameter parameter = parameters.stream().filter(param -> param.getName().equals(name)).findFirst().get();
            parameter.setIn(in);
            parameter.setRequired(required);
            parameter.setType(type);
            parameter.setDefaultStr(defaultStr);

            return;
        }

        parameters.add(new Parameter(name, in, required, type, defaultStr));
    }

    public boolean hasParameter(String name) {
        return parameters.stream().anyMatch(param -> param.getName().equals(name));
    }

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    public void addDefaultValue(String paramName, String defaultValue) {
        defaultValues.put(paramName, defaultValue);
    }

    public Optional<String> getDefaultValue(String param) {
        return defaultValues.containsKey(param) ? Optional.of(defaultValues.get(param)) : Optional.empty();
    }

}
