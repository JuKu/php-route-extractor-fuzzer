package com.jukusoft.neo4j.openapi.importer.client;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseFactoryTest {

    @EnabledIfSystemProperty(named = "neo4jtests", matches = "true", disabledReason = "Works only with Neo4J Server")
    @Test
    @Tag("neo4j")
    public void testCreateConnection() {
        Driver driver = DatabaseFactory.createConnection("bolt://192.168.2.39:7687", "neo4j", "admin");
        assertNotNull(driver);
    }

    @EnabledIfSystemProperty(named = "neo4jtests", matches = "true", disabledReason = "Works only with Neo4J Server")
    @Test
    @Tag("neo4j")
    public void testCreateSession() {
        Driver driver = DatabaseFactory.createConnection("bolt://192.168.2.39:7687", "neo4j", "admin");
        Session session = driver.session();
        assertNotNull(session);
        assertTrue(session.isOpen());

        driver.close();
    }

}
