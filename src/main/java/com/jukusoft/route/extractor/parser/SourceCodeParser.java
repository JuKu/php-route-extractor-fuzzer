package com.jukusoft.route.extractor.parser;

import java.io.File;
import java.io.IOException;
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

    /**
     * private constructor, because this class is a utility class
     */
    private SourceCodeParser() {
        //
    }

    public static List<Route> parseSourceCodeDir(File srcDir) throws IOException {
        if (!srcDir.exists() || srcDir.isDirectory()) {
            throw new IllegalArgumentException("src directory does not exists or is not a directory: " + srcDir.getAbsolutePath());
        }

        List<Route> routes = Files.walk(Paths.get(srcDir.toURI()))
                .filter(Files::isRegularFile)
                .filter(file -> file.endsWith(".php"))
                .map(file -> parseSourceCodeFile(file.toFile()))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());

        return routes;
    }

    public static List<Route> parseSourceCodeFile(File file) {
        List<Route> routes = new ArrayList<>();

        return routes;
    }

}
