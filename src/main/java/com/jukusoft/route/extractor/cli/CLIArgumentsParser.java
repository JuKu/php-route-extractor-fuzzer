package com.jukusoft.route.extractor.cli;

import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * this utility class is responsible for parsing the CLI parameters into a specific schema
 * @author Justin Kuenzel
 */
public class CLIArgumentsParser {

    /**
     * private constructor, because this is a utility class
     */
    private CLIArgumentsParser() {
        //
    }

    /**
     * parse command line arguments into a specific schema
     * @param args string array with parameters
     * @return key-value-map with options
     */
    public static Map<String,String> parseArguments(String[] args) {
        Map<String,String> res = new HashMap<>();

        Options options = new Options();

        Option input = new Option("s", "src", true, "src code directory to parse");
        input.setRequired(true);
        options.addOption(input);

        Option output = new Option("o", "output", true, "output code directory where swagger / OpenAPI specs will be generated");
        output.setRequired(true);
        options.addOption(output);

        Option fileName = new Option("f", "filename", true, "output filename (optional)");
        fileName.setRequired(false);
        options.addOption(fileName);

        Option host = new Option("h", "host", true, "host address, e.q. localhost:8888");
        host.setRequired(false);
        options.addOption(host);

        Option basePath = new Option("b", "basePath", true, "base path, e.q. /api");
        basePath.setRequired(false);
        options.addOption(basePath);

        Option symfonyJSON = new Option("j", "symfonyJSON", true, "symfony json source file to parse");
        symfonyJSON.setRequired(false);
        options.addOption(symfonyJSON);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            for (String key : Arrays.stream(cmd.getOptions()).map(option -> option.getLongOpt()).toList()) {
                String value = cmd.getOptionValue(key);
                res.put(key, value);
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("php-route-extractor-fuzzer", options);

            throw new IllegalArgumentException("Cannot parse arguments correctly", e);
        }

        return res;
    }

}
