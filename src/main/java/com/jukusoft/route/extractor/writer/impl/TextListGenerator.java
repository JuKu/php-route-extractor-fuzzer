package com.jukusoft.route.extractor.writer.impl;

import com.jukusoft.route.extractor.parser.Route;
import com.jukusoft.route.extractor.writer.FileFormatGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

/**
 * a file format generator to only write the route paths to a specific file.
 *
 * @author Justin Kuenzel
 */
public class TextListGenerator implements FileFormatGenerator {

    /**
     * the output directory.
     */
    private final Path outputDir;

    /**
     * constructor
     *
     * @param outputDir output directory, where generated OpenAPI files should be stored
     */
    public TextListGenerator(Path outputDir) {
        this.outputDir = outputDir;

        if (!outputDir.toFile().exists() || !outputDir.toFile().isDirectory()) {
            throw new IllegalArgumentException("output directory doesn't exists or is not a directory: " + outputDir.toString());
        }
    }

    @Override
    public void generateOutputFile(List<Route> routes, String host, String basePath, String fileName) throws IOException {
        File file = createOrOverrideFile(outputDir, fileName);

        //create new file writer
        try (Writer fileWriter = new FileWriter(file)) {
            for (Route route : routes) {
                fileWriter.write(route.getUrl() + System.lineSeparator());
            }
        }
    }

    @Override
    public String getFileExtension() {
        return ".txt";
    }

    @Override
    public String getPreferredFileName() {
        return "routes.txt";
    }

}
