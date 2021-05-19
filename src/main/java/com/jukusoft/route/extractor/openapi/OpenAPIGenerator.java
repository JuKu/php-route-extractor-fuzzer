package com.jukusoft.route.extractor.openapi;

import com.jukusoft.route.extractor.parser.Route;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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
        String content = generateJSON(routes, host, basePath).toString();

        byte[] strToBytes = content.getBytes();
        Files.write(file.toPath(), strToBytes);
    }

    private JSONObject generateJSON(List<Route> routes, String host, String basePath) {
        JSONObject json = new JSONObject();

        json.put("swagger", "2.0");
        json.put("basePath", basePath);
        json.put("host", host);

        JSONObject paths = new JSONObject();

        //

        json.put("paths", paths);

        return json;
    }

}
