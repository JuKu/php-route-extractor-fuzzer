package com.jukusoft.route.extractor.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * an interface for a parser, e.q. a source code parser.
 *
 * @author Justin Kuenzel
 */
public interface Parser {

    /**
     * check, if this parser is activated.
     *
     * @param cliParams commandline interface params
     * @return true, if parser is activated
     */
    public default boolean isActivated(Map<String,String> cliParams) {
        return cliParams.containsKey(getParameter());
    }

    /**
     * get commandline interface parameter.
     *
     * @return commandline interface parameter
     */
    public String getParameter();

    /**
     * parse directory or file
     *
     * @param srcFile directory or file
     * @param routes routes list of already existing routes, so that the parser can add additional information to existing routes
     * @return list of routes
     * @throws IOException if a file exception occurs
     */
    public List<Route> parse(File srcFile, List<Route> routes) throws IOException;

}
