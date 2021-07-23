package com.jukusoft.route.extractor.parser;

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

            
        }

        return routes;
    }

}
