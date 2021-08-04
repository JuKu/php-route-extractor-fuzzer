package com.jukusoft.route.extractor.writer.impl.openapi;

import com.jukusoft.route.extractor.parser.Parameter;
import com.jukusoft.route.extractor.parser.Route;
import com.jukusoft.route.extractor.parser.RouteMethod;
import com.jukusoft.route.extractor.writer.FileFormatGenerator;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * this class is responsible for generating OpenAPI specification files (swagger json)
 */
public class OpenAPI20Generator implements FileFormatGenerator {

    /**
     * the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAPI20Generator.class);

    private final Path outputDir;

    /**
     * constructor
     *
     * @param outputDir output directory, where generated OpenAPI files should be stored
     */
    public OpenAPI20Generator(Path outputDir) {
        this.outputDir = outputDir;

        if (!outputDir.toFile().exists() || !outputDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("output directory doesn't exists or is not a directory: " + outputDir.toString());
        }
    }

    /**
     * generate OpenAPI 2.x JSON files
     *
     * @param routes list with all possible routes
     * @param host host address, e.q. localhost:8888
     * @param basePath base path
     * @param fileName
     */
    @Override
    public void generateOutputFile(List<Route> routes, String host, String basePath, String fileName) throws IOException {
        File file = createOrOverrideFile(outputDir, fileName);

        //generate OpenAPI spec
        int spacesToIndentEachLevel = 2;
        String content = generateJSON(routes, host, basePath).toString(spacesToIndentEachLevel);

        LOGGER.info("write swagger specification: {}", file.getAbsolutePath());
        byte[] strToBytes = content.getBytes();
        Files.write(file.toPath(), strToBytes);

        //validate generated swagger file, see also: https://github.com/swagger-api/swagger-parser
        LOGGER.info("validate swagger file...");
        SwaggerParseResult result = new OpenAPIParser().readContents(file.toString(), null, null);

        //check for validation errors and warnings
        if (result.getMessages() != null) {
            LOGGER.error("Validation error or warnings occured: ");
            result.getMessages().forEach(LOGGER::error);
        } else {
            LOGGER.info("Swagger validation succeeded");
        }

        if (result.getOpenAPI() == null) {
            LOGGER.error("Validation of Swagger / OpenAPI file failed");
        }
    }

    @Override
    public String getFileExtension() {
        return ".json";
    }

    @Override
    public String getPreferredFileName() {
        return "result.json";
    }

    /**
     * generate JSON for OpenAPI Spec Version 2.0 .
     *
     * @param routes all available routes
     * @param host the server host
     * @param basePath base path
     *
     * @return OpenAPI 2.0 spec as JSON Object
     */
    private JSONObject generateJSON(List<Route> routes, String host, String basePath) {
        JSONObject json = new JSONObject();

        //add general information on top layer
        json.put("swagger", "2.0");
        json.put("basePath", basePath);
        json.put("host", host);

        //produces
        JSONArray producesArray = new JSONArray();
        producesArray.put("application/xml");
        json.put("produces", producesArray);

        //info-tag
        JSONObject infoJSON = new JSONObject();
        infoJSON.put("title", "auto-generated swagger specification");
        infoJSON.put("description", "auto-generated swagger specification");
        infoJSON.put("version", "1.0.0");
        json.put("info", infoJSON);

        Map<String,List<Route>> pathMap = convertToPathMap(routes);

        JSONObject paths = new JSONObject();

        // "paths" object
        for (Map.Entry<String,List<Route>> entry : pathMap.entrySet()) {
            JSONObject methodsJSON = new JSONObject();

            String url = entry.getKey();
            List<Route> methodsForRoute = entry.getValue();

            //iterate through all available routes (URLs)
            for (Route route : methodsForRoute) {
                //note: every url can have multiple route-objects, if there are more than one HTTP methods for this route
                for (Map.Entry<Route.METHOD, RouteMethod> methodEntry : route.getMethods().entrySet()) {
                    Route.METHOD method = methodEntry.getKey();
                    RouteMethod routeMethod = methodEntry.getValue();

                    JSONObject pathMethod = new JSONObject();

                    pathMethod.put("summary", route.getName());
                    pathMethod.put("description", route.getName());
                    pathMethod.put("operationId", route.getName());

                    JSONArray producesArr = new JSONArray();
                    producesArr.put(routeMethod.getProduces());
                    pathMethod.put("produces", producesArr);

                    //add default responses
                    JSONObject respJSON = new JSONObject();
                    JSONObject resp200JSON = new JSONObject();
                    resp200JSON.put("description", "Success");
                    respJSON.put("200", resp200JSON);
                    pathMethod.put("responses", respJSON);

                    if (!routeMethod.getParameters().isEmpty()) {
                        JSONArray parametersArray = new JSONArray();

                        for (Parameter param : routeMethod.getParameters()) {
                            if (param.getIn().equals("path")) {
                                //we already add path-variables in another section
                                continue;
                            }

                            JSONObject param1 = new JSONObject();

                            param1.put("name", param.getName());
                            param1.put("in", param.getIn());
                            param1.put("required", param.getRequired());
                            param1.put("type", param.getType());
                            param1.put("description", param.getName());
                            param1.put("operationId", param.getName());

                            if (!param.getDefaultStr().isEmpty()) {
                                param1.put("default", param.getDefaultStr());
                            }

                            parametersArray.put(param1);
                        }

                        pathMethod.put("parameters", parametersArray);
                    }

                    methodsJSON.put(routeMethod.getMethod().toString().toLowerCase(Locale.ROOT), pathMethod);
                }

                //add url parameters, if neccessary
                if (url.contains("{") && url.contains("}")) {
                    //url contains path variables
                    JSONArray parametersJSON = new JSONArray();

                    //get the first route-method and extract the path variables
                    List<Parameter> parameters = methodsForRoute.stream()
                            .findFirst()//find first route obkect
                            .map(route1 -> route1.getMethods())//map to list with route-methods
                            .filter(map -> !map.isEmpty())//verify, that route has minimum one HTTP method (this is a MUST-HAVE requirement)
                            .map(map -> map.values().stream().findFirst())//map to first route-method (all methods contains the same path parameters, so we only need the first one
                            .map(opt -> opt.get())//remove the optional
                            .map(routeMethod -> routeMethod.getParameters())//map to route methods
                            .get();

                    //iterate through all parameters for a specific url, filter the path parameters and add them to JSON
                    for (Parameter parameter : parameters) {
                        //only add path-variables
                        if (parameter.getIn().equals("path")) {
                            JSONObject parameterJSON = new JSONObject();

                            parameterJSON.put("name", parameter.getName());
                            parameterJSON.put("in", parameter.getIn());
                            parameterJSON.put("required", parameter.getRequired());
                            parameterJSON.put("type", parameter.getType());
                            parameterJSON.put("description", parameter.getName());
                            parameterJSON.put("operationId", parameter.getName());

                            if (!parameter.getDefaultStr().isEmpty()) {
                                parameterJSON.put("default", parameter.getDefaultStr());
                            }

                            parametersJSON.put(parameterJSON);
                        }
                    }

                    //add path parameters to json
                    methodsJSON.put("parameters", parametersJSON);
                }
            }

            //add the route-methods to the URL
            paths.put(entry.getKey(), methodsJSON);
        }

        json.put("paths", paths);

        return json;
    }

    /**
     * merges different route-methods with the same URL to a map like URL - route-list.
     *
     * @param routes all available routes for a specific site
     * @return map "URL - route-list" for mapping URLs to routes with different methods
     */
    private Map<String,List<Route>> convertToPathMap(List<Route> routes) {
        Map<String,List<Route>> pathMap = new HashMap<>();

        for (Route route : routes) {
            if (!pathMap.containsKey(route.getUrl())) {
                pathMap.put(route.getUrl(), new ArrayList<>());
            }

            pathMap.get(route.getUrl()).add(route);
        }

        return pathMap;
    }

}
