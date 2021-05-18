package com.jukusoft.route.extractor;

import com.jukusoft.route.extractor.cli.CLIArgumentsParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class CLIArgumentsParserTest {

    @Test
    public void testParseEmptyArguments() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CLIArgumentsParser.parseArguments(new String[0]);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            CLIArgumentsParser.parseArguments(new String[]{"-i", "test"});
        });
    }

    @Test
    public void testParseValidArguments() {
        Map<String,String> params = CLIArgumentsParser.parseArguments(new String[]{"-s", "src/", "-o", "/output"});
        Assertions.assertFalse(params.isEmpty());

        //check values
        Assertions.assertEquals("src/", params.get("src"));
        Assertions.assertEquals("/output", params.get("output"));
    }

}
