package com.jukusoft.neo4j.openapi.importer.client;

import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     * delete a neo4j database - important: this only works in enterprise edition (!).
     * This method doesn't do any escaping.
     *
     * @param database database name
     */
    public void dropDatabase(String database) {
        this.writeTransaction(tx -> tx.run("DROP DATABASE " + database + " IF EXISTS"));
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
    public Node createNode(String... type) {
        long nodeID = this.writeTransaction(tx -> {
            String typeStr = (type != null) ? ":" + String.join(":", type) : "";
            Result result = tx.run("CREATE (n" + typeStr + ") RETURN id(n)");
            return result.single().get("id(n)").asLong();
        });

        return getNodeById(nodeID);
    }

    /**
     * reload a node from database.
     *
     * @param node neo4j node
     * @return reloaded node
     */
    public Node reload(Node node) {
        if (node.getNodeID() == 0) {
            throw new IllegalStateException("node is not persisted");
        }

        return getNodeById(node.getNodeID());
    }

    /**
     * get node from database.
     *
     * @param nodeID id of node
     * @return node from database
     */
    public Node getNodeById(long nodeID) {
        return this.readTransaction(tx -> {
            Node node = new Node(nodeID);

            Result result = tx.run("MATCH(n)\n" +
                    "WHERE ID(n) = " + nodeID + "\n" +
                    "RETURN labels(n) as labels");

            for (String label : result.single().get("labels").asList(label -> label.isNull() || label.isEmpty() ? null : label.asString())) {
                if (label != null && !label.equals("0")) {
                    node.addLabel(label);
                }
            }

            return node;
        });
    }

    /**
     * delete a specific node.
     * @param node the persistent neo4j node
     */
    public void deleteNode(Node node) {
        this.writeTransaction(tx -> {
            Result result = tx.run("MATCH (n) where ID(n)=" + node.getNodeID() + "\n" +
                    "OPTIONAL MATCH (n)-[r]-() //drops p's relations\n" +
                    "DELETE r,n");
            return null;
        });
    }

    /**
     * delete all nodes with this label.
     * @param type node label
     */
    public void deleteNodes(String type) {
        this.writeTransaction(tx -> {
            Result result = tx.run("MATCH (n:" + type + ")\n" +
                    "OPTIONAL MATCH (n)-[r]-() //drops p's relations\n" +
                    "DELETE r,n");
            return null;
        });
    }

    /**
     * count all nodes of a specific type
     * @return number of nodes in database
     */
    public long countNodes() {
        return countNodes(null);
    }

    /**
     * count all nodes of a specific type
     * @param type node label
     * @return number of nodes
     */
    public long countNodes(String type) {
        return this.readTransaction(tx -> {
            //only append ":", if a type is requested
            String typeStr = (type != null && !type.isEmpty()) ? ":" + type : "";

            Result result = tx.run("MATCH (n" + typeStr + ")\n" +
                    "RETURN count(n) as count");
            return result.single().get("count").asLong();
        });
    }

    /**
     * persist data of a neo4j node.
     * @param node neo4j node
     */
    public Node save(Node node) {
        return this.writeTransaction(tx -> {
            long nodeID = node.getNodeID();

            //create new node, if not exists
            if (nodeID == 0) {
                String labels = node.listLabels().stream().count() > 1 ? String.join(":", node.listLabels()) : null;
                Node node1 = this.createNode(labels);

                node.setNodeID(node1.getNodeID());
                nodeID = node1.getNodeID();
            }

            //update labels
            updateLabels(tx, node);

            return reload(node);
        });
    }

    private void updateLabels(Transaction tx, Node node) {
        //get old labels
        Node oldNode = getNodeById(node.getNodeID());

        List<String> labelsToRemove = oldNode.listLabels().stream().filter(label -> !node.listLabels().contains(label)).collect(Collectors.toList());
        List<String> labelsToAdd = node.listLabels().stream().filter(label -> !oldNode.listLabels().contains(label)).collect(Collectors.toList());

        String query = "MATCH (n)\n" +
                "WHERE ID(n) = " + node.getNodeID() + "\n";

        //remove old labels
        for (String label : labelsToRemove) {
            String execQuery = query + "REMOVE n:" + label;
            tx.run(execQuery);
        }

        //add new labels
        for (String label : labelsToAdd) {
            tx.run(query + "SET n:" + label);
        }
    }

    /**
     * list all databases.
     *
     * @return list of databases
     */
    public List<Database> listDatabases() {
        return this.readTransaction(tx -> {
            List<Database> databases = new ArrayList<>();

            Result result = tx.run("SHOW DATABASES");

            for (Record row : result.list()) {
                Database db = new Database(
                        row.get("name").asString(),
                        row.get("address").asString(),
                        row.get("role").asString(),
                        row.get("requestedStatus").asString(),
                        row.get("currentStatus").asString(),
                        row.get("terror").asString(),
                        row.get("default").asBoolean(),
                        row.get("home").asBoolean()
                );
                databases.add(db);
            }

            return databases;
        });
    }

    /**
     * close the database driver.
     */
    @Override
    public void close() {
        this.driver.close();
    }

}
