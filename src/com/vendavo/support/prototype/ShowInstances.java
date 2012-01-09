package com.vendavo.support.prototype;

import oracle.jdbc.pool.OracleDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
/**
 * 
 * Looks into 'incoming' folder in the current folder and processed all CSV files starting with VC (like Vendavo Check)
 * All files are deleted after processed.
 * Also (before processing) it loads previously processed instances from instances.csv.
 * At all files are processed, current status is saved  to this file.
 * Output is online-temp.html file generated with current status of instances.
 */
public class ShowInstances {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

    public static void main(String[] args) throws IOException {
        File file = new File("incoming");
        if (file.isDirectory() && file.exists()) {
            HashMap instances = loadInstances(new File("instances.csv"));

            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith("VC") && pathname.getName().endsWith(".csv");
                }
            });

            for (int i = 0; i < files.length; ++i) {
                processFile(instances, files[i]);
                files[i].delete();
            }

            saveInstances(instances, new File("instances.csv"));

            File outputFile = new File("online-temp.html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write("<html><head/><title>Vendavo Global Support - online instances, 0.5.0</title><body>");
            bw.write("<h1>List of Vendavo Global Support Instances</h1>");
            bw.write("<div>Updated: " + df.format(Calendar.getInstance().getTime()) + "</div>");
            bw.write("<table border=\"1\" width=\"100%\">");
            bw.write("<tr>");
            bw.write("<td>#</td><td><b>Last Online</b></td><td><b>World</b></td><td><b>Version</b></td><td><b>URL</b></td><td><b>User</b></td>" +
                    "<td><b>Box</b></td><td><b>Folder</b></td><td><b>App Server</b></td><td><b>Schema</b></td><td><b>Cluster</b></td>");
            bw.write("</tr>");

            Instance[] list = (Instance[]) instances.values().toArray(new Instance[0]);
            Arrays.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Instance i1 = (Instance) o1;
                    Instance i2 = (Instance) o2;
                    return i1.getWorld().compareTo(i2.getWorld());
                }
            });

            for (int i = 0; i < list.length; ++i) {
                String folder = list[i].getFolder();
                folder = folder.substring(2);

                String box = list[i].getBox();
                folder = "\\\\" + box + folder;
                File fileFolder = new File(folder);
                list[i].setFolderExists(fileFolder.exists() && fileFolder.isDirectory());

                updateSchemaExists(list[i]);
            }

            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < list.length; ++i) {
                Instance instance = list[i];
                long delta = currentTime - instance.getLastOnline();
                String lastOnline;
                boolean online = delta < 1000 * 60 * 10;

                if (delta < 1000 * 60) {
                    lastOnline = "Before " + (delta / 1000) + " second(s)";
                } else if (delta < 1000 * 60 * 60) {
                    lastOnline = "Before " + (delta / (1000 * 60)) + " minute(s)";
                } else if (delta < 1000 * 60 * 60 * 24) {
                    lastOnline = "Before " + (delta / (1000 * 60 * 60)) + " hour(s)";
                } else lastOnline = "Before " + (delta / (1000 * 60 * 60 * 24)) + " hour(s)";

                String world = instance.getWorld();
                String version = instance.getBranch();
                String URL = "<a href=\"" + instance.getURL() + "\">" + instance.getURL() + "</a>";
                String user = instance.getUsername();
                String box = instance.getBox();
                String folder = instance.getFolder();
                String appServer = instance.getAppServer();
                String schema = instance.getSchema() + "@" + instance.getDb();
                String cluster = instance.isCluster() ? "Yes" : "No";

                if (online) {
                    bw.write("<tr bgcolor=\"#00DD00\">");
                } else {
                    if (!instance.isFolderExists() || !instance.isSchemaExists()) {
                        bw.write("<tr bgcolor=\"#EEAAAA\">");
                    } else {
                        bw.write("<tr>");
                    }
                }

                String folderHtml = (instance.isFolderExists() ? "<td nowrap=\"nowrap\">" : "<td bgcolor=\"#EE0000\" nowrap=\"nowrap\">") + folder + "</td>";
                String schemaHtml = (instance.isSchemaExists() ? "<td nowrap=\"nowrap\">" : "<td bgcolor=\"#EE0000\" nowrap=\"nowrap\">") + schema + "</td>";

                bw.write("<td>" + (i + 1) + "</td>");
                bw.write("<td nowrap=\"nowrap\">" + lastOnline + "</td><td nowrap=\"nowrap\">" + world + "</td><td nowrap=\"nowrap\">" + version + "</td><td nowrap=\"nowrap\">" + URL.toLowerCase() + "</td><td nowrap=\"nowrap\">" + user + "</td>" +
                        "<td nowrap=\"nowrap\">" + box + "</td>" + folderHtml + "<td nowrap=\"nowrap\">" + appServer + "</td>" + schemaHtml + "<td nowrap=\"nowrap\">" + cluster + "</td>");
                bw.write("</tr>");
            }

            bw.write("</table>");
            bw.write("</body></html>");


            bw.close();
        }
    }

    private static void updateSchemaExists(Instance instance) {
        String url = "???";
        System.out.println("Checking schema: " + instance.getSchema() + "@" + instance.getDb());
        Connection conn = null;
        try {
            String schema = instance.getSchema();
            String password = instance.getDbPassword();
            String dbServer = instance.getDbServer();
            String dbPort = instance.getDbPort();
            String dbInstance = instance.getDbInstance();

            url = "jdbc:oracle:thin:" + schema + "/" + password + "@" + dbServer + ":" + dbPort + "/" + dbInstance;

            OracleDataSource ds = new OracleDataSource();
            ds.setURL(url);
            conn = ds.getConnection();
            Statement stm = conn.createStatement();
            stm.execute("select 'Test' from Dual");
            conn.close();

            instance.setSchemaExists(true);
        } catch (SQLException e) {
            System.out.println("Cannot find schema with url:" + url);
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException e) {

            }
        }
    }

    private static void saveInstances(HashMap instances, File outputFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        Instance[] list = (Instance[]) instances.values().toArray(new Instance[0]);
        for (int i = 0; i < list.length; ++i) {
            Instance instance = list[i];
            bw.write(Long.toString(instance.getLastOnline()));
            bw.write(',');
            bw.write(instance.getUsername());
            bw.write(',');
            bw.write(instance.getBox());
            bw.write(',');
            bw.write(instance.getPort());
            bw.write(',');
            bw.write(instance.getWorld());
            bw.write(',');
            bw.write(instance.getBranch());
            bw.write(',');
            bw.write(instance.getAppServer());
            bw.write(',');
            bw.write(instance.getURL());
            bw.write(',');
            bw.write(instance.getFolder());
            bw.write(',');
            bw.write(instance.getSchema());
            bw.write(',');
            bw.write(instance.getDb());
            bw.write(',');
            bw.write(instance.getJava());
            bw.write(',');
            bw.write(instance.isCluster() ? "true" : "false");
            bw.write(',');
            bw.write(instance.getDbServer());
            bw.write(',');
            bw.write(instance.getDbInstance());
            bw.write(',');
            bw.write(instance.getDbPort());
            bw.write(',');
            bw.write(instance.getDbPassword());
            bw.write("\n");
        }

        bw.close();
    }

    private static HashMap loadInstances(File instancesFile) throws IOException {
        HashMap instances = new HashMap();
        if (instancesFile.exists() && instancesFile.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(instancesFile));
            while (true) {
                String line = br.readLine();
                if (line == null) break;

                String[] parts = line.split(",");

                // Format without dbServer, dbInstance, dbPort, dbPassword at the end
                if (parts.length == 17) {
                    long lastOnline = Long.parseLong(parts[0].trim());
                    String username = parts[1].trim();
                    String box = parts[2].trim();
                    String port = parts[3].trim();
                    String world = parts[4].trim();
                    String branch = parts[5].trim();
                    String appServer = parts[6].trim();
                    String URL = parts[7].trim();
                    String folder = parts[8].trim();
                    String schema = parts[9].trim();
                    String db = parts[10].trim();
                    String java = parts[11].trim();
                    boolean cluster = parts[12].trim().equalsIgnoreCase("true");
                    String dbServer = parts[13].trim();
                    String dbInstance = parts[14].trim();
                    String dbPort = parts[15].trim();
                    String dbPassword = parts[16].trim();

                    Instance instance = new Instance(username, box, port, world, branch, appServer, URL, folder, schema, db, java, cluster);
                    instance.setDbServer(dbServer);
                    instance.setDbInstance(dbInstance);
                    instance.setDbPort(dbPort);
                    instance.setDbPassword(dbPassword);
                    instance.setLastOnline(lastOnline);
                    instances.put(instance, instance);
                }
            }

            br.close();
        }

        return instances;
    }

    private static void processFile(HashMap instances, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        while (true) {
            String line = br.readLine();
            if (line == null) break;

            line = line.trim().toUpperCase();
            if (line.startsWith("SUCCESS")) {
                String[] parts = line.split(",");
                if (parts.length != 19) {
                    System.out.println("Line ignored in file: " + file.getAbsolutePath());
                    System.out.println("Ignored line: " + line);
                    continue;
                }

                String username = parts[1].trim();
                String box = parts[2].trim();
                String port = parts[3].trim();
                String world = parts[4].trim();
                String branch = parts[5].trim();
                String appServer = parts[6].trim();
                String URL = parts[7].trim();
                String folder = parts[8].trim();
                String schema = parts[9].trim();
                String db = parts[10].trim();
                String java = parts[12].trim();
                boolean cluster = parts[13].trim().equalsIgnoreCase("true");
                String dbServer = parts[14].trim();
                String dbInstance = parts[15].trim();
                String dbPort = parts[16].trim();
                String dbPassword = parts[17].trim();
                long lastOnline = Long.parseLong(parts[18].trim());

                Instance instance = new Instance(username, box, port, world, branch, appServer, URL, folder, schema, db, java, cluster);
                instance.setLastOnline(lastOnline);
                instance.setDbServer(dbServer);
                instance.setDbInstance(dbInstance);
                instance.setDbPort(dbPort);
                instance.setDbPassword(dbPassword);

                Instance existingInstance = (Instance) instances.get(instance);
                if (existingInstance != null) {
                    long time = existingInstance.getLastOnline();
                    if (time < instance.getLastOnline()) {
                        instances.remove(existingInstance);
                        instances.put(instance, instance);
                    }
                } else {
                    instances.put(instance, instance);
                }
            }
        }

        br.close();
    }
}
