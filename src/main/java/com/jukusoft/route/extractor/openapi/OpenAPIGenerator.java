package com.jukusoft.route.extractor.openapi;

import com.jukusoft.route.extractor.parser.Parameter;
import com.jukusoft.route.extractor.parser.Route;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * this class is responsible for generating OpenAPI specification files (swagger json)
 */
public class OpenAPIGenerator {

    private final Path outputDir;

    /**
     * constructor
     *
     * @param outputDir output directory, where generated OpenAPI files should be stored
     */
    public OpenAPIGenerator(Path outputDir) {
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
    public void generateJSON(List<Route> routes, String host, String basePath, String fileName) throws IOException {
        File file = new File(outputDir.toFile(), fileName);
        file.createNewFile();

        //generate OpenAPI spec
        int spacesToIndentEachLevel = 2;
        String content = generateJSON(routes, host, basePath).toString(spacesToIndentEachLevel);

        byte[] strToBytes = content.getBytes();
        Files.write(file.toPath(), strToBytes);
    }

    private JSONObject generateJSON(List<Route> routes, String host, String basePath) {
        JSONObject json = new JSONObject();

        json.put("swagger", "2.0");
        json.put("basePath", basePath);
        json.put("host", host);

        //produces
        JSONArray producesArray = new JSONArray();
        producesArray.put("application/xml");
        json.put("produces", producesArray);

        Map<String,List<Route>> pathMap = convertToPathMap(routes);

        JSONObject paths = new JSONObject();

        for (Map.Entry<String,List<Route>> entry : pathMap.entrySet()) {
            JSONObject methodJSON = new JSONObject();

            for (Route route : entry.getValue()) {
                JSONObject path = new JSONObject();

                //TODO: add code here
                path.put("description", route.getName());

                JSONArray producesArr = new JSONArray();
                producesArr.put(route.getProduces());
                path.put("produces", producesArr);

                if (!route.getParameters().isEmpty()) {
                    JSONArray parametersArray = new JSONArray();

                    for (Parameter param : route.getParameters()) {
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

                    path.put("parameters", parametersArray);
                }

                methodJSON.put(route.getMethod().toString().toLowerCase(Locale.ROOT), path);
            }

            paths.put(entry.getKey(), methodJSON);
        }

        json.put("paths", paths);

        return json;
    }

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
