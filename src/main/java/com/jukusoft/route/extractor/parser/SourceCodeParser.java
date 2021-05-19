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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility class to parse source code
 */
public class SourceCodeParser {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeParser.class);
    private static final Pattern pattern = Pattern.compile("@Route((.*\\)))", Pattern.MULTILINE);//"(?:filter=\\*\\*)(.*?)(?:&)"

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

        logger.debug("parse source directory: {}", srcDir.getAbsolutePath());

        List<Route> routes = Files.walk(Paths.get(srcDir.toURI()))
                .filter(Files::isRegularFile)
                .filter(file -> file.toFile().getAbsolutePath().endsWith(".php"))
                .map(file -> parseSourceCodeFile(file))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());

        logger.info("{} routes found in source code directory", routes.size());

        return routes;
    }

    public static List<Route> parseSourceCodeFile(Path path) {
        List<Route> routes = new ArrayList<>();

        logger.info("parse file: {}", path.toFile().getAbsolutePath());

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);

            //lite performance optimization
            if (content.contains("@Route")) {
                logger.debug("file contains @Route annotations: {}", path.toFile().getAbsolutePath());

                final Matcher m = pattern.matcher(content);

                int cnt = 0;
                while (m.find()) {
                    String line = m.group(0);
                    //System.out.println(++cnt + ": G1: " + m.group(1));

                    String innerBracesContent = m.group(1).replace("(", "").replace(")", "").replace("\"", "");
                    //logger.info("content: {}", innerBracesContent);

                    String[] params = innerBracesContent.split(", ");

                    String url = "";
                    String name = "";

                    for (String param : params) {
                        String[] array = param.split("=");

                        if (array.length == 1) {
                            //its the url
                            url = array[0];
                            logger.debug("endpoint url found: {}", url);
                        } else {
                            String[] array1 = new String[array.length - 1];
                            System.arraycopy(array, 1, array1, 0, array1.length);
                            array[1] = String.join("=", array1);
                            //logger.info("key-value found, key: {}, value: {}", array[0], array[1]);

                            if (array[0].equals("name")) {
                                name = array[1];
                            }
                        }
                    }

                    if (!name.isEmpty()) {
                        logger.debug("add entpoint url to list: {}", url);

                        Route route = new Route(url, name);
                        routes.add(route);
                    }
                }

                logger.debug("found {} occurrences of @Route annotations in this file", cnt);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR - Cannot parse file: " + path.toFile().getAbsolutePath() + " because of exception: " + e.getLocalizedMessage());
        }

        return routes;
    }

}
