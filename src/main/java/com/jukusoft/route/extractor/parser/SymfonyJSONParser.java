package com.jukusoft.route.extractor.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
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
        LOGGER.info("");

        return routes;
    }

}
