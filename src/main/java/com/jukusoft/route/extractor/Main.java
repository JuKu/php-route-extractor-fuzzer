package com.jukusoft.route.extractor;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;
import com.jukusoft.route.extractor.parser.Route;
import com.jukusoft.route.extractor.parser.SourceCodeParser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * main class
 */
public class Main {

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) throws IOException {
        try {
            //parse CLI arguments
            Map<String,String> params = CLIArgumentsParser.parseArguments(args);

            List<Route> routes = SourceCodeParser.parseSourceCodeDir(new File(params.get("src")));
        } catch (IllegalArgumentException e) {
            //logging is already done by CLIArgumentsParser
            System.exit(1);
        }
    }

}
