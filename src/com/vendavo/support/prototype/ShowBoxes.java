package com.vendavo.support.prototype;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Looks into the folder 'incoming' and processing all CSV files started with HC (HostCheck).
 * File are deleted after processed.
 * At the beginning previously processed list of boxes is loaded from boxes.csv
 * At the end, current status of boxes is stored into boxes.csv
 * Output of the program is online-boxes.html with current status of boxes.
 */
public class ShowBoxes {

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");

    public static void main(String[] args) throws IOException {
        File file = new File("incoming");
        if (file.isDirectory() && file.exists()) {
            HashMap instances = loadBoxes(new File("boxes.csv"));

            File[] files = file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().startsWith("HC") && pathname.getName().endsWith(".csv");
                }
            });

            for (int i = 0; i < files.length; ++i) {
                processFile(instances, files[i]);
                files[i].delete();
            }

            saveBoxes(instances, new File("boxes.csv"));

            File outputFile = new File("online-boxes.html");
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
            bw.write("<html><head/><title>Vendavo Global Support - online boxes, 0.2.0</title><body>");
            bw.write("<h1>List of Vendavo Global Support Boxes</h1>");
            bw.write("<div>Updated: " + df.format(Calendar.getInstance().getTime()) + "</div>");
            bw.write("<table border=\"1\" width=\"100%\">");
            bw.write("<tr>");
            bw.write("<td>#</td><td><b>Last Online</b></td><td><b>Host</b></td> <td><b>OS</b></td> <td><b>CPU Freq.</b></td> <td><b>#Cores</b></td> <td><b>Phys. Memory(MB)</b></td> <td><b>Drive C - Free (GB)</b></td>" +
                    "<td><b>Drive D - Free (GB)</b></td> <td><b>Drive E - Free(GB)</b></td> <td><b>Excel</b></td>");
            bw.write("</tr>");

            Box[] list = (Box[]) instances.values().toArray(new Box[0]);
            Arrays.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    Box i1 = (Box) o1;
                    Box i2 = (Box) o2;
                    return i1.getHost().compareTo(i2.getHost());
                }
            });

            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < list.length; ++i) {
                Box box = list[i];
                long delta = currentTime - box.getLastUpdated();
                String lastOnline;

                if (delta < 1000 * 60) {
                    lastOnline = "Before " + (delta / 1000) + " second(s)";
                } else if (delta < 1000 * 60 * 60) {
                    lastOnline = "Before " + (delta / (1000 * 60)) + " minute(s)";
                } else if (delta < 1000 * 60 * 60 * 24) {
                    lastOnline = "Before " + (delta / (1000 * 60 * 60)) + " hour(s)";
                } else lastOnline = "Before " + (delta / (1000 * 60 * 60 * 24)) + " hour(s)";

                String host = box.getHost();
                String os = box.getOs();
                String cpuSpeed = box.getCpuSpeed();
                String numberOfCores = box.getNumberOfCores();
                String physMemory = box.getPhysicalMemorySize();

                String cColor = "FFFFFF";
                if (!box.getcFree().equalsIgnoreCase("null")) {
                    if (Float.parseFloat(box.getcPerc()) <= 5) cColor = "FF000"; else cColor = "00FF00";
                }

                String dColor = "FFFFFF";
                if (!box.getdFree().equalsIgnoreCase("null")) {
                    if (Float.parseFloat(box.getdPerc()) <= 5) dColor = "FF000"; else dColor = "00FF00";
                }

                String eColor = "FFFFFF";
                if (!box.geteFree().equalsIgnoreCase("null")) {
                    if (Float.parseFloat(box.getePerc()) <= 5) eColor = "FF000"; else eColor = "00FF00";
                }

                String cFree = "<td bgcolor=\"#" + cColor + "\"  nowrap=\"nowrap\">" + (box.getcFree().equalsIgnoreCase("null") ? "-" : (box.getcFree() + " GB / " + box.getcPerc() + "%")) + "</td>";
                String dFree = "<td bgcolor=\"#" + dColor + "\"  nowrap=\"nowrap\">" + (box.getdFree().equalsIgnoreCase("null") ? "-" : (box.getdFree() + " GB / " + box.getdPerc() + "%")) + "</td>";
                String eFree = "<td bgcolor=\"#" + eColor + "\"  nowrap=\"nowrap\">" + (box.geteFree().equalsIgnoreCase("null") ? "-" : (box.geteFree() + " GB / " + box.getePerc() + "%")) + "</td>";
                String excel = box.getExcelVersion().equalsIgnoreCase("null") ? "-" : box.getExcelVersion();

//                String folderHtml = (instance.isFolderExists() ? "<td nowrap=\"nowrap\">" : "<td bgcolor=\"#EE0000\" nowrap=\"nowrap\">") + folder + "</td>";
//                String schemaHtml = (instance.isSchemaExists() ? "<td nowrap=\"nowrap\">" : "<td bgcolor=\"#EE0000\" nowrap=\"nowrap\">") + schema + "</td>";

                bw.write("<tr bgcolor=\"#EEEEEE\">");
                bw.write("<td>" + (i + 1) + "</td>");
                bw.write("<td>" + lastOnline + "</td>");
                bw.write("<td nowrap=\"nowrap\">" + host + "</td> <td nowrap=\"nowrap\">" + os + "</td> <td nowrap=\"nowrap\">" + cpuSpeed + "</td> <td nowrap=\"nowrap\">" + numberOfCores + "</td> <td nowrap=\"nowrap\">" + physMemory + "</td> " + cFree + dFree + eFree + "<td nowrap=\"nowrap\">" + excel + "</td>");
                bw.write("</tr>\n");
            }

            bw.write("</table>");
            bw.write("</body></html>");


            bw.close();
        }
    }

    private static void saveBoxes(HashMap boxes, File outputFile) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        Box[] list = (Box[]) boxes.values().toArray(new Box[0]);
        for (int i = 0; i < list.length; ++i) {
            Box box = list[i];
            bw.write(Long.toString(box.getLastUpdated()));
            bw.write(',');
            bw.write(box.getHost());
            bw.write(',');
            bw.write(box.getOs());
            bw.write(',');
            bw.write(box.getIe());
            bw.write(',');
            bw.write(box.getCpu());
            bw.write(',');
            bw.write(box.getCpuSpeed());
            bw.write(',');
            bw.write(box.getNumberOfCores());
            bw.write(',');
            bw.write(box.getPhysicalMemorySize());
            bw.write(',');
            bw.write(box.getcSize());
            bw.write(',');
            bw.write(box.getcFree());
            bw.write(',');
            bw.write(box.getcPerc());
            bw.write(',');
            bw.write(box.getdSize());
            bw.write(',');
            bw.write(box.getdFree());
            bw.write(',');
            bw.write(box.getdPerc());
            bw.write(',');
            bw.write(box.geteSize());
            bw.write(',');
            bw.write(box.geteFree());
            bw.write(',');
            bw.write(box.getePerc());
            bw.write(',');
            bw.write(box.getExcelVersion());
            bw.write("\n");
        }

        bw.close();
    }

    private static HashMap loadBoxes(File boxesFile) throws IOException {
        HashMap boxes = new HashMap();
        if (boxesFile.exists() && boxesFile.isFile()) {
            BufferedReader br = new BufferedReader(new FileReader(boxesFile));
            while (true) {
                String line = br.readLine();
                if (line == null) break;

                String[] parts = line.split(",");

                if (parts.length == 18) {
                    long lastUpdated = Long.parseLong(parts[0].trim());
                    String host = parts[1].trim();
                    String os = parts[2].trim();
                    String ie = parts[3].trim();
                    String cpu = parts[4].trim();
                    String cpuSpeed = parts[5].trim();
                    String numberOfCores = parts[6].trim();
                    String physicalMemorySize = parts[7].trim();
                    String cSize = parts[8].trim();
                    String cFree = parts[9].trim();
                    String cPerc = parts[10].trim();
                    String dSize = parts[11].trim();
                    String dFree = parts[12].trim();
                    String dPerc = parts[13].trim();
                    String eSize = parts[14].trim();
                    String eFree = parts[15].trim();
                    String ePerc = parts[16].trim();
                    String excelVersion = parts[17].trim();

                    Box box = new Box(host, os, ie, cpu, cpuSpeed, numberOfCores, physicalMemorySize, cSize, cFree, cPerc, dSize, dFree, dPerc, eSize, eFree, ePerc, excelVersion);
                    box.setLastUpdated(lastUpdated);
                    boxes.put(box, box);
                }
            }

            br.close();
        }

        return boxes;
    }

//#SUCCESS, HOST, OS, IE, CPU speed, # of cores, MEM, C: size, C: free, C: perc, D: size, D: free, D: perc, E: size, E: free, E: perc, Excel
//SUCCESS, Supwin01, Microsoft Windows Server 2003, 7.0000, Intel(R) Xeon, 3.2, 4, 4096, 29.99, 1.99, 6.6, 80.88, 697.53, 0.8, null, null, null, null

    private static void processFile(HashMap boxes, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        long updated = file.lastModified();
        while (true) {
            String line = br.readLine();
            if (line == null) break;

            line = line.trim().toUpperCase();
            if (line.startsWith("SUCCESS")) {
                String[] parts = line.split(",");
                if (parts.length != 18) {
                    System.out.println("Line ignored in file: " + file.getAbsolutePath());
                    System.out.println("Ignored line: " + line);
                    continue;
                }

                String host = parts[1].trim();
                String os = parts[2].trim();
                String ie = parts[3].trim();
                String cpu = parts[4].trim();
                String cpuSpeed = parts[5].trim();
                String numberOfCores = parts[6].trim();
                String physicalMemory = parts[7].trim();
                String cSize = parts[8].trim();
                String cFree = parts[9].trim();
                String cPerc = parts[10].trim();
                String dSize = parts[11].trim();
                String dFree = parts[12].trim();
                String dPerc = parts[13].trim();
                String eSize = parts[14].trim();
                String eFree = parts[15].trim();
                String ePerc = parts[16].trim();
                String excel = parts[17].trim();

                Box box = new Box(host, os, ie, cpu, cpuSpeed, numberOfCores, physicalMemory, cSize, cFree, cPerc, dSize, dFree, dPerc, eSize, eFree, ePerc, excel);
                box.setLastUpdated(updated);

                Box existingBox = (Box) boxes.get(box);
                if (existingBox != null) {
                    long time = existingBox.getLastUpdated();
                    if (time < box.getLastUpdated()) {
                        boxes.remove(existingBox);
                        boxes.put(box, box);
                    }
                } else {
                    boxes.put(box, box);
                }
            }
        }

        br.close();
    }
}
