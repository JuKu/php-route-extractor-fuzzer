package com.jukusoft.neo4j.openapi.importer.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * a typical graph database node.
 *
 * @author Justin Kuenzel
 */
public class Node {

    /**
     * a uuid
     */
    private long nodeID = 0l;

    /**
     * list with labels of the node;
     */
    private List<String> labels = new ArrayList<>();

    /**
     * default constructor
     */
    public Node() {
        //
    }

    /**
     * default constructor.
     * @param nodeID nodeID
     */
    protected Node(long nodeID) {
        this.nodeID = nodeID;
    }

    public long getNodeID() {
        return nodeID;
    }

    protected void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }

    public List<String> listLabels() {
        return labels;
    }

    public void addLabel(String label) {
        this.labels.add(label);
    }

    public void removeLabel(String label) {
        this.labels.remove(label);
    }

}
