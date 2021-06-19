package com.jukusoft.neo4j.openapi.importer.client;

/**
 * a neo4j database object.
 *
 * @author Justin Kuenzel
 */
public class Database {

    private String name;
    private String address;
    private String role;
    private String requestedStatus;
    private String currentStatus;
    private String error;
    private boolean defaultDB;
    private boolean homeDB;

    public Database(String name, String address, String role, String requestedStatus, String currentStatus, String error, boolean defaultDB, boolean homeDB) {
        this.name = name;
        this.address = address;
        this.role = role;
        this.requestedStatus = requestedStatus;
        this.currentStatus = currentStatus;
        this.error = error;
        this.defaultDB = defaultDB;
        this.homeDB = homeDB;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRole() {
        return role;
    }

    public String getRequestedStatus() {
        return requestedStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getError() {
        return error;
    }

    public boolean isDefaultDB() {
        return defaultDB;
    }

    public boolean isHomeDB() {
        return homeDB;
    }

    @Override
    public String toString() {
        return "Database{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", role='" + role + '\'' +
                ", requestedStatus='" + requestedStatus + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", error='" + error + '\'' +
                ", defaultDB=" + defaultDB +
                ", homeDB=" + homeDB +
                '}';
    }

}
