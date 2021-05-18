package com.jukusoft.route.extractor;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;

import java.util.Map;

/**
 * main class
 */
public class Main {

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        //parse CLI arguments
        Map<String,String> params = CLIArgumentsParser.parseArguments(args);
    }

}
