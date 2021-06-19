package com.jukusoft.neo4j.openapi.importer.client;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * a factory for the database connection instance.
 *
 * @author Justin Kuenzel
 */
public class DatabaseFactory {

    private DatabaseFactory() {
        //
    }

    /**
     * create a new Neo4j connection.
     *
     * @param uri neo4j server connection
     * @param username neo4j username
     * @param password neo4j password
     * @return neo4j connection
     */
    public static Driver createConnection(String uri, String username, String password) {
        return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
    }

}
