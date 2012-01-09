package com.vendavo.support.prototype;

public class Instance {
    private long lastOnline;
    private String username;
    private String box;
    private String port;
    private String world;
    private String branch;
    private String appServer;
    private String URL;
    private String folder;
    private String schema; // username
    private String db;    // TNS NAME
    private String java;
    private boolean cluster;
    private boolean folderExists;
    private String dbServer;
    private String dbInstance;
    private String dbPort;
    private String dbPassword;
    private boolean schemaExists;

    public Instance(String username, String box, String port, String world, String branch, String appServer, String URL, String folder, String schema, String db, String java, boolean cluster) {
        this.username = username;
        this.box = box;
        this.port = port;
        this.world = world;
        this.branch = branch;
        this.appServer = appServer;
        this.URL = URL;
        this.folder = folder;
        this.schema = schema;
        this.db = db;
        this.java = java;
        this.cluster = cluster;
        this.folderExists = false;
    }

    public String getUsername() {
        return username;
    }

    public String getBox() {
        return box;
    }

    public String getPort() {
        return port;
    }

    public String getWorld() {
        return world;
    }

    public String getBranch() {
        return branch;
    }

    public String getAppServer() {
        return appServer;
    }

    public String getURL() {
        return URL;
    }

    public String getFolder() {
        return folder;
    }

    public String getSchema() {
        return schema;
    }

    public String getDb() {
        return db;
    }

    public String getJava() {
        return java;
    }

    public boolean isCluster() {
        return cluster;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instance)) return false;

        Instance instance = (Instance) o;

        if (box != null ? !box.equals(instance.box) : instance.box != null) return false;
        if (folder != null ? !folder.equals(instance.folder) : instance.folder != null) return false;

        return true;
    }

    public boolean isFolderExists() {
        return folderExists;
    }

    public void setFolderExists(boolean folderExists) {
        this.folderExists = folderExists;
    }

    public int hashCode() {
        int result = box != null ? box.hashCode() : 0;
        result = 31 * result + (folder != null ? folder.hashCode() : 0);
        return result;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(long lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getDbServer() {
        return dbServer;
    }

    public void setDbServer(String dbServer) {
        this.dbServer = dbServer;
    }

    public String getDbInstance() {
        return dbInstance;
    }

    public void setDbInstance(String dbInstance) {
        this.dbInstance = dbInstance;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public boolean isSchemaExists() {
        return schemaExists;
    }

    public void setSchemaExists(boolean schemaExists) {
        this.schemaExists = schemaExists;
    }
}
