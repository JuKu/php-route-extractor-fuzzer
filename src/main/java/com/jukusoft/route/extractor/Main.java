package com.jukusoft.route.extractor;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;
import com.jukusoft.route.extractor.parser.Parser;
import com.jukusoft.route.extractor.writer.impl.CSVGenerator;
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
import java.util.stream.Collectors;

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

            //list with parsers
            List<Parser> parsers = new ArrayList<>();

            //call all parsers, if activated
            for (Parser parser : parsers) {
                if (parser.isActivated(params)) {
                    List<Route> parsedRoutes = parser.parse(new File(params.get(parser.getParameter())));

                    //don't add duplicate routes
                    routes.addAll(parsedRoutes.stream()
                            .filter(route -> !routes.stream().anyMatch(route1 -> route1.getUrl().equals(route.getUrl())))
                            .collect(Collectors.toList()));
                }
            }

            // a list with target file formats
            List<FileFormatGenerator> outputFileGenerators = new ArrayList<>();
            outputFileGenerators.add(new OpenAPI20Generator(Path.of(params.get("output"))));
            outputFileGenerators.add(new CSVGenerator(Path.of(params.get("output"))));

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
