package com.jukusoft.route.extractor.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class SymfonyJSONParser implements Parser {

    /**
     * the class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SymfonyJSONParser.class);

    @Override
    public String getParameter() {
        return "symfonyJSON";
    }

    @Override
    public List<Route> parse(File srcFile, List<Route> routes) throws IOException {
        LOGGER.info("parse symfony console file: {}", srcFile.getAbsolutePath());

        if (!srcFile.exists()) {
            LOGGER.warn("symfony console file doesn't exists: {}", srcFile.getAbsolutePath());
            return routes;
        }

        String content = Files.readString(srcFile.toPath(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(content);

        //iterate through all routes found
        for (String routeName : json.keySet()) {
            JSONObject routeJSON = json.getJSONObject(routeName);

            //get url
            String url = routeJSON.getString("path");

            //replace "\/" with "/" to get same results
            url = url.replace("\\/", "/");

            Route newRoute = new Route(url, routeName);

            //check, if route already exists, if yes, return the already existing route
            Route route = routes.stream().filter(route1 -> route1.getName().equals(newRoute.getName())).findFirst().orElse(newRoute);

            if (!routes.stream().anyMatch(route1 -> route1.getName().equals(newRoute.getName()))) {
                //add new route
                LOGGER.info("add new route: {}, url: {}", routeName, url);
                routes.add(route);
            }

            //TODO: if any, create multiple routes
            String method = routeJSON.getString("method");
            method = method.replace("ANY", "GET|POST|PUT|DELETE");

            //Quick & Dirty Fix, because "|" is a special character and splits interprets this as regex, instead as a character
            method = method.replace("|", ",");

            for (String method1 : method.split(",")) {
                LOGGER.info("HTTP method found: {}", method1);
                Route.METHOD method2 = Route.METHOD.valueOf(method1);

                RouteMethod routeMethod = null;

                if (route.getMethods().containsKey(method)) {
                    routeMethod = route.getMethods().get(method);
                } else {
                    routeMethod = new RouteMethod(method2);
                    route.addRouteMethod(method2, routeMethod);
                }

                try {
                    //add default parameters
                    for (String defaultParamKey : routeJSON.getJSONObject("defaults").keySet()) {
                        String defaultValue = routeJSON.getJSONObject("defaults").getString(defaultParamKey);
                        routeMethod.addDefaultValue(defaultParamKey, defaultValue);
                        routeMethod.addParameter(defaultParamKey, Parameter.IN_TYPE.FORM, false, "string", defaultValue);
                    }
                } catch (JSONException e) {
                    //don't do anything here, because this is expected, if no default paramaters are set
                    LOGGER.debug("route doesn't contains any default parameters: '{}'", url);
                }

                try {
                    //add required parameters
                    for (String requiredParam : routeJSON.getJSONObject("requirements").keySet()) {
                        //check, if it is a path or a query / form parameter
                        Parameter.IN_TYPE paramType = url.contains("{" + requiredParam + "}") ? Parameter.IN_TYPE.PATH : Parameter.IN_TYPE.FORM;

                        LOGGER.debug("add route required parameter: {}, url: {}", requiredParam, url);
                        String varType = routeJSON.getJSONObject("requirements").getString(requiredParam).toLowerCase().equals("\\\\d+") ? "integer" : "string";
                        routeMethod.addParameter(requiredParam, paramType, true, varType, "");
                    }
                } catch (JSONException e) {
                    //don't do anything here, because this is expected, if no required paramaters exists
                    LOGGER.debug("route doesn't contains any required parameters: '{}'", url);
                }
            }
        }

        return routes;
    }

}
