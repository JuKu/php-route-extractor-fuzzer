package com.jukusoft.neo4j.openapi.importer.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void testCreateAndDeleteNode() {
        Neo4JClient client = createClient();
        long oldNodeCount = client.countNodes("junit");

        //create a new node
        Node node = client.createNode("junit");
        assertTrue(node.getNodeID() > 0);

        //check, that a new node exists
        assertTrue(client.countNodes("junit") > oldNodeCount);

        //delete the same node
        client.deleteNode(node);

        //check, that node was deleted
        assertEquals(oldNodeCount, client.countNodes("junit"));
    }

    @BeforeAll
    public static void beforeAll() {
        Neo4JClient client = createClient();
        client.deleteNodes("junit");
    }

    @AfterAll
    public static void afterAll() {
        Neo4JClient client = createClient();
        client.deleteNodes("junit");
    }

    private static Neo4JClient createClient() {
        return new Neo4JClient("bolt://192.168.2.39:7687", "neo4j", "admin");
    }

}
