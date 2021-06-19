package com.jukusoft.neo4j.openapi.importer.client;

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
     * default constructor.
     * @param nodeID nodeID
     */
    protected Node(long nodeID) {
        this.nodeID = nodeID;
    }

    public long getNodeID() {
        return nodeID;
    }

}
