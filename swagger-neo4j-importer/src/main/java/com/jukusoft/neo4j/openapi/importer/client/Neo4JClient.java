package com.jukusoft.neo4j.openapi.importer.client;

import org.neo4j.driver.*;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * an easy to use Neo4j client without the required use of cypher.
 *
 * @author Justin Kuenzel
 */
public class Neo4JClient implements AutoCloseable {

    /**
     * the neo4j driver
     */
    private Driver driver;

    /**
     * public constructor.
     *
     * @param uri neo4j server uri
     * @param username neo4j username
     * @param password neo4j password
     */
    public Neo4JClient(String uri, String username, String password) {
        //create new database connection
        this.driver = DatabaseFactory.createConnection(uri, username, password);
    }

    /**
     * create a new neo4j database - important: this only works in enterprise edition (!).
     * This method doesn't do any escaping.
     *
     * @param database database name
     */
    public void createDatabase(String database) {
        this.writeTransaction(tx -> tx.run("CREATE DATABASE " + database));
    }

    /**
     * open a new session and execute a query.
     *
     * @param func the function to execute
     */
    public <T> T executeInSession(Function<Session,T> func) {
        try (Session session = driver.session()) {
            return func.apply(session);
        }
    }

    /**
     * do something in transaction.
     *
     * @param func the function to execute
     */
    public <T> T writeTransaction(Function<Transaction,T> func) {
        return this.executeInSession(session -> {
            return session.writeTransaction(transaction -> {
                return func.apply(transaction);
            });
        });
    }

    /**
     * do something in transaction.
     *
     * @param func the function to execute
     */
    public <T> T readTransaction(Function<Transaction,T> func) {
        return this.executeInSession(session -> {
            return session.readTransaction(transaction -> {
                return func.apply(transaction);
            });
        });
    }

    /**
     * create a new root node.
     *
     * @param type node label
     * @return node instance
     */
    public Node createNode(String type) {
        long nodeID = this.writeTransaction(tx -> {
            Result result = tx.run("CREATE (n:" + type + ") RETURN ', from node ' + id(a)");
            return result.single().get(0).asLong();
        });

        return new Node(nodeID);
    }

    /**
     * close the database driver.
     */
    @Override
    public void close() {
        this.driver.close();
    }

}
