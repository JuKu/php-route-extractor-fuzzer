package com.jukusoft.route.extractor.parser;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SourceCodeParserTest {

    /**
     * check, that a NPE is thrown, if the method is executed with null paramater.
     */
    @Test
    public void parseNullSourceCodeFile() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            SourceCodeParser.parseSourceCodeFile(null);
        });
    }

    /**
     * check that source parser for a single file works as expected.
     */
    @Test
    public void testSourceCodeFile() {
        List<Route> routes = SourceCodeParser.parseSourceCodeFile(new File("../mole-web/src/TUD/INF/MoleWebBundle/Controller/CatalogController.php").toPath());
        Assertions.assertNotNull(routes);
        Assertions.assertFalse(routes.isEmpty());

        //check, if the first route is specified correctly
        assertTrue(routes.stream().anyMatch(route -> route.getUrl().contains("/catalogs/semester/{_locale}")));

        assertEquals(16, routes.size());

        //verify, that the first @Route annotation is not parsed ("/catalogs")
        assertEquals(0, routes.stream().filter(route -> route.getUrl().equals("/catalogs")).count());
    }

}
