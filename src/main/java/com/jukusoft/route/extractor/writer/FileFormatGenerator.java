package com.jukusoft.route.extractor.writer;

import com.jukusoft.route.extractor.parser.Route;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * an interface for writters which generates a specific file format, like the OpenAPI format.
 *
 * @author Justin Kuenzel
 */
public interface FileFormatGenerator {

    /**
     * generate a output format file.
     *
     * @param routes list with all available routes
     * @param host the host of the tarhet system, q.e. "127.0.0.1:8080"
     * @param basePath the base uri of the target system, e.q. "/app/"
     * @param fileName the file name of the output file
     * @throws IOException
     */
    public void generateOutputFile(List<Route> routes, String host, String basePath, String fileName) throws IOException;

    /**
     * get the preferred file extension of the output file.
     *
     * @return preferred file extension
     */
    public String getFileExtension();

    /**
     * get the preferred file name without extension.
     *
     * @return preferred file name without extension
     */
    public String getPreferredFileName();

    /**
     * create a new file, if no exists or remove the content from the old file.
     *
     * @param outputDir output directory
     * @param fileName file name
     * @return file instance
     */
    public default File createOrOverrideFile(Path outputDir, String fileName) {
        File file = new File(outputDir.toFile(), fileName);

        //delete old file, if file already exists
        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

}
