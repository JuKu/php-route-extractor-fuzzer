package com.jukusoft.route.extractor.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility class to parse source code
 */
public class SourceCodeParser {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeParser.class);

    /**
     * private constructor, because this class is a utility class
     */
    private SourceCodeParser() {
        //
    }

    public static List<Route> parseSourceCodeDir(File srcDir) throws IOException {
        if (!srcDir.exists() || !srcDir.isDirectory()) {
            throw new IllegalArgumentException("src directory does not exists or is not a directory: " + srcDir.getAbsolutePath());
        }

        logger.info("parse source directory: {}", srcDir.getAbsolutePath());

        List<Route> routes = Files.walk(Paths.get(srcDir.toURI()))
                .filter(Files::isRegularFile)
                .filter(file -> file.toFile().getAbsolutePath().endsWith(".php"))
                .map(file -> parseSourceCodeFile(file))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());

        return routes;
    }

    public static List<Route> parseSourceCodeFile(Path path) {
        List<Route> routes = new ArrayList<>();

        logger.info("parse file: {}", path.toFile().getAbsolutePath());

        try {
            Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR - Cannot parse file: " + path.toFile().getAbsolutePath() + " because of exception: " + e.getLocalizedMessage());
        }

        return routes;
    }

}
