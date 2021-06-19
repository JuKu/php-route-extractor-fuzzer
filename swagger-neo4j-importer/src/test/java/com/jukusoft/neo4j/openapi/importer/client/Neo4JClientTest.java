package com.jukusoft.neo4j.openapi.importer.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Neo4JClientTest {

    @Test
    public void testConstructor() {
        Neo4JClient client = new Neo4JClient("bolt://192.168.2.39:7687", "neo4j", "admin");
        client.close();
    }

    @Test
    public void testListDatabases() {
        Neo4JClient client = createClient();
        List<Database> databases = client.listDatabases();
        Assertions.assertNotNull(databases);
        assertFalse(databases.isEmpty());

        assertTrue(databases.stream().anyMatch(db -> db.getName().equals("moleweb")));
    }

    private Neo4JClient createClient() {
        return new Neo4JClient("bolt://192.168.2.39:7687", "neo4j", "admin");
    }

}
