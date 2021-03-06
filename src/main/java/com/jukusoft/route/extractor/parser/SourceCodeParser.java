package com.jukusoft.route.extractor.parser;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * utility class to parse source code
 */
public class SourceCodeParser implements Parser {

    //see also: https://stackoverflow.com/questions/18864509/how-to-java-regex-to-match-everything-but-specified-pattern
    // ^ means "except" (in braces)
    private static final Logger logger = LoggerFactory.getLogger(SourceCodeParser.class);
    private static final Pattern pattern = Pattern.compile("@Route\\(([^\\)]*)\\)", Pattern.MULTILINE | Pattern.DOTALL);//"(?:filter=\\*\\*)(.*?)(?:&)"
    //@Route\([^\)]*\), old: @Route((.*\))), https://www.freeformatter.com/java-regex-tester.html#ad-output
    //3. version: @Route\(([^\)]*)\) - with braces it matches the content as group

    /**
     * private constructor, because this class is a utility class
     */
    public SourceCodeParser() {
        //
    }

    /**
     * parse source code directory.
     *
     * @param srcDir source code directory
     * @return list with extracted routes
     * @throws IOException if IOException occurs
     */
    public static List<Route> parseSourceCodeDir(File srcDir) throws IOException {
        Objects.requireNonNull(srcDir);

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

    /**
     * parse a single source code file.
     *
     * @param path path to source code file
     * @return list with all routes extracted from source code file
     */
    public static List<Route> parseSourceCodeFile(Path path) {
        Objects.requireNonNull(path);

        List<Route> routes = new ArrayList<>();

        logger.info("parse file: {}", path.toFile().getAbsolutePath());

        try {
            String content = Files.readString(path, StandardCharsets.UTF_8);

            //lite performance optimization
            if (content.contains("@Route")) {
                logger.debug("file contains @Route annotations: {}", path.toFile().getAbsolutePath());

                final Matcher m = pattern.matcher(content);

                String baseUrl = "";
                int counter = 0;

                int cnt = 0;
                while (m.find()) {
                    String line = m.group(0);
                    //System.out.println(++cnt + ": G1: " + m.group(1));
                    //System.err.println(line);

                    String innerBracesContent = m.group(1).replace("(", "").replace(")", "").replace("\"", "");
                    //logger.info("content: {}", innerBracesContent);

                    String[] params = innerBracesContent.split(", ");

                    String url = "";
                    String name = "";

                    // map with additional params, e.q. "requirements" or "defaults"
                    Map<String,String> additionalParams = new HashMap<>();

                    // iterate through all params in the braces (comma-seperated)
                    for (String param : params) {
                        String[] array = param.split("=");

                        //is there only one "="?
                        if (array.length == 1) {
                            //its the url
                            url = array[0];
                            logger.debug("endpoint url found: {}", url);

                            if (counter == 0) {
                                //its the base url
                                baseUrl = url;
                            }
                        } else {
                            //join the second and all following values together
                            String[] array1 = new String[array.length - 1];
                            System.arraycopy(array, 1, array1, 0, array1.length);
                            array[1] = String.join("=", array1);
                            //logger.info("key-value found, key: {}, value: {}", array[0], array[1]);

                            if (array[0].equals("name")) {
                                name = array[1];
                            } else {
                                //add other parameter
                                logger.info("other parameter found: {}, value: {}", array[0], array[1]);
                                additionalParams.put(array[0], array[1]);
                            }
                        }
                    }

                    if (!name.isEmpty() && !url.equals(baseUrl)) {
                        //remove the first "/" before the url, because base url already contains this (else we get something like "//")
                        if (baseUrl.endsWith("/") && url.startsWith("/")) {
                            url = url.substring(1);
                        }

                        url = baseUrl + url;
                        logger.debug("add entpoint url to list: {}", url);

                        Route route = new Route(url, name);
                        RouteMethod method = new RouteMethod(Route.METHOD.GET);
                        route.addRouteMethod(Route.METHOD.GET, method);

                        if (additionalParams.containsKey("defaults")) {
                            String values = additionalParams.get("defaults");

                            //remove "{" and "}"
                            values = values.replace("{", "").replace("}", "");

                            for (String defaultPair : values.split(",")) {
                                String[] array2 = defaultPair.split("=");
                                String paramName = array2[0].trim();
                                String defaultValue = array2[1].trim();

                                logger.debug("add default value, key: {}, value: {}", paramName, defaultValue);
                                method.addDefaultValue(paramName, defaultValue);
                            }
                        }

                        if (additionalParams.containsKey("requirements")) {
                            String paramContent = additionalParams.get("requirements");

                            // remove "*" from multiline comments
                            paramContent = paramContent.replace("*", "");

                            // make the ini string to a json string
                            paramContent = paramContent.replace(" =", ":");

                            JSONObject json = new JSONObject(paramContent);

                            for (String key : json.keySet()) {
                                logger.info("add required parameter: {}", key);
                                method.addParameter(key, Parameter.IN_TYPE.PATH, true, "string", method.getDefaultValue(key).orElse(""));
                            }
                        }

                        //fix missing default parameters, which aren't required
                        for (Map.Entry<String, String> entry : method.getDefaultValues().entrySet()) {
                            String paramName = entry.getKey();
                            String defaultValue = entry.getValue();

                            //check, if parameter already exists in required parameters
                            if (!method.hasParameter(paramName)) {
                                //add parameter
                                logger.info("add parameter with default name: {}", paramName);

                                //NOTE: paramaters with default values are never required parameter (specified by specification)
                                method.addParameter(paramName, Parameter.IN_TYPE.PATH, false, "string", defaultValue);
                            }
                        }

                        routes.add(route);
                    } else {
                        logger.warn("endpoint without name: {}", url);
                    }

                    counter++;
                }

                logger.debug("found {} occurrences of @Route annotations in this file", cnt);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR - Cannot parse file: " + path.toFile().getAbsolutePath() + " because of exception: " + e.getLocalizedMessage());
        }

        return routes;
    }

    @Override
    public String getParameter() {
        return "src";
    }

    @Override
    public List<Route> parse(File srcFile, List<Route> routes) throws IOException {
        if (!routes.isEmpty()) {
            throw new IllegalStateException("this parser has to be the first one in the pipeline");
        }

        return SourceCodeParser.parseSourceCodeDir(srcFile);
    }

}
