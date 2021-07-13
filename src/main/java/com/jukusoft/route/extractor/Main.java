package com.jukusoft.route.extractor;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;
import com.jukusoft.route.extractor.writer.impl.TextListGenerator;
import com.jukusoft.route.extractor.writer.impl.openapi.OpenAPI20Generator;
import com.jukusoft.route.extractor.parser.Route;
import com.jukusoft.route.extractor.parser.SourceCodeParser;
import com.jukusoft.route.extractor.writer.FileFormatGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * main class
 */
public class Main {

    /**
     * the class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) throws IOException {
        try {
            //parse CLI arguments
            Map<String,String> params = CLIArgumentsParser.parseArguments(args);

            List<Route> routes = SourceCodeParser.parseSourceCodeDir(new File(params.get("src")));

            // a list with target file formats
            List<FileFormatGenerator> outputFileGenerators = new ArrayList<>();
            outputFileGenerators.add(new OpenAPI20Generator(Path.of(params.get("output"))));
            outputFileGenerators.add(new TextListGenerator(Path.of(params.get("output"))));

            LOGGER.info("generate output file formats...");

            //generate output files
            for (FileFormatGenerator generator : outputFileGenerators) {
                String filename = params.getOrDefault("filename", generator.getPreferredFileName());
                LOGGER.info("Generate output file: {} by generator: {}", filename, generator.getClass().getSimpleName());

                generator.generateOutputFile(routes, params.getOrDefault("host", "localhost:8080"), params.getOrDefault("basePath", "/"), filename);
            }

            LOGGER.info("Generation of output files finished!");
        } catch (IllegalArgumentException e) {
            //logging is already done by CLIArgumentsParser
            System.exit(1);
        }
    }

}
