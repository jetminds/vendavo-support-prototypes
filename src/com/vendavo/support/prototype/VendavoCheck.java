package com.vendavo.support.prototype;

/** VERSION INFO - PROTOTYPE!
 * v 0.3.0 - username printout added
 *		   - output format changed
 * v 0.4.0 - cluster TRUE/FALSE flag added
 * 		   - header added
 * v 0.5.0 - added DB hostname (server) and DB instance parameters to output and port and password
 * v 0.6.0 - added datime when the instance was online. 0 for offline instances.
 * v 0.7.0 - minor changes...
 * Known problems: Sometimes HANDLE.EXE hangs and prevents other calls to continue... 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/*
 * The class simply parser output from HANDLE.EXE tools and NETSTAT. Main point is to see with process locked vendavo.log 
 * and ports that are used by specific processes.
 * Lines considered for automated parsin starts with 'SUCCESS' 
 */
public class VendavoCheck {
	public static void main(String[] args) {
		try {
			System.out.println("VendavoCheck, version 0.7.0\nRelevant CSV lines starts with SUCCESS.");
			System.out.println("#STATUS,USER, HOST, HTTP PORT, WORLD, VERSION, APP SERVER, URL, PATH"
					+ ", SCHEMA, TNS NAME, BUILD TAG, JDK, CLUSTER, DB server, DB instance, DB port, DB password, Online DateTime");
			BufferedReader br = new BufferedReader(new FileReader("handle.txt"));
			while (true) {
				try {
					String line = br.readLine();
					if (line == null) break;

					if (line.indexOf("type: File")>= 0) {
						String[] parts = line.split("\\s+");
						if (parts.length != 8 || !parts[1].equals("pid:") || !parts[3].equals("type:") || !parts[4].equals("File")) {
							System.out.println("Line ignored. Bad format: " + line);
							continue;
						}
						
						String process = parts[0];
						if (process.equals("java.exe") || process.equals("jstart.exe")) {
							processFileWithPid(parts[7], parts[2], parts[5], process.equals("jstart.exe"));
						}
					}
				} catch (IOException e) {
					System.out.println("Exception during reading 'handles.txt': " + e.getMessage());
				}
			}
			
			br.close();
		} catch (IOException e) {
			System.out.println("Cannot read 'handles.txt'");
		}
		
	}

	private static void processFileWithPid(String fileName, String pid, String user, boolean SAP) {
		File file = new File (fileName);
		File parent = file.getParentFile();
		if (!parent.getName().equalsIgnoreCase("log")) {
			System.out.println("Ignoring possible vendavo root - vendavo.log in bad location: " + fileName);
			return;
		}
		
		parent = parent.getParentFile();
		if (!parent.getName().equalsIgnoreCase("app")) {
			System.out.println("Ignoring possible vendavo root - vendavo.log in bad location: " + fileName);
			return;
		}

		parent = parent.getParentFile();
		if (!parent.getName().equalsIgnoreCase("web")) {
			System.out.println("Ignoring possible vendavo root - vendavo.log in bad location: " + fileName);
			return;
		}
	
		File vendavoFile = parent.getParentFile();
		File configProperties = new File(vendavoFile, "config/users/demo/config.properties");
		try {
			Properties properties = loadProperties(configProperties);
			String httpPort = (String)properties.get("vr2m.appserver.http.port");
			if (httpPort == null) {
				System.out.println("There is no vr2m.appserver.http.port property defined in : " + configProperties.getAbsolutePath());
				return;
			}
			
			if (SAP || ensurePortIsBound(pid, httpPort)) {
				Properties autoBuild = getAutobuildProperties(vendavoFile);
				String vendavoTag = (String)autoBuild.get("autobuild.branch");
				vendavoTag = vendavoTag != null ? vendavoTag : "???";
				
				String javaJdk = (String)autoBuild.get("autobuild.jdk.version");
				javaJdk = javaJdk != null ? javaJdk : "???";

				String tnsName = (String)properties.get("vr2m.db.tnsname");
				tnsName = tnsName != null ? tnsName : "???";

                String dbServer = (String)properties.get("vr2m.db.server");
                dbServer = dbServer != null ? dbServer : "???";

                String dbInstance = (String)properties.get("vr2m.db.instance");
                dbInstance = dbInstance != null ? dbInstance : "???";

                String dbPort = (String)properties.get("vr2m.db.port");
                dbPort = dbPort != null ? dbPort : "1521";

				String schema = (String)properties.get("vr2m.db.username");
				schema = schema != null ? schema : "???";
			
				String password = (String)properties.get("vr2m.db.password");
				password = password != null ? password : "???";
				
				String world = (String)properties.get("vr2m.world");
				world = world != null ? world : "???";
				
				String cluster = (String)properties.get("appserver.runmode.IsClustered");
				cluster = cluster != null ? cluster : "false";
				
				String appServer = (String)properties.get("vr2m.appserver");
				appServer = appServer != null ? appServer : "???";
				String host = getHostName();
				String version = vendavoTag;
				System.out.println();

                long onlineDateTime = System.currentTimeMillis();
                System.out.println("SUCCESS, " + user.replaceFirst("VENDAVO\\\\", "").toUpperCase() + ", "
						+ host + ", " + httpPort + ", " + world + ", " + version + ", " + appServer
						+ ", http://" + host + ":" + httpPort + "/vendavo , " 
						+ vendavoFile.getAbsolutePath() + ", " + schema + ", "
						+ tnsName + ", " + vendavoTag + ", " + javaJdk + ", " + cluster + ", "
                        + dbServer + ", " + dbInstance + ", " + dbPort + ", " + password + ", "
                        + onlineDateTime);
			}
		} catch (IOException e) {
			System.out.println("Error loading config.properties from location: " + configProperties.getAbsolutePath());
		}
	}

	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}	
		
		return "???";
	}
	
    private static Properties getAutobuildProperties(File vendavoFile) {
		File fileAuto = new File(vendavoFile, "_AUTOBUILD/autobuild.properties");
		try {
			return loadProperties(fileAuto);
		} catch (IOException e) {
			System.out.println("Error when loading autobuild.properties from: " + fileAuto.getAbsolutePath());
		}
		
		return new Properties();
	}

	private static boolean ensurePortIsBound(String pid, String httpPort) {
    	BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("netstat.txt"));
	    	while (true) {
				//TCP    0.0.0.0:53300          0.0.0.0:0              LISTENING       3224
	    		String line = br.readLine();
				if (line == null) break;

				line = line.trim();
				if (!line.	startsWith("TCP")) continue;
				String[] parts= line.split("\\s+");
				if (parts.length == 5 && parts[3].equalsIgnoreCase("LISTENING") && parts[4].equals(pid)) {
					//System.out.println(pid + " " + httpPort);
					String baseUrl = parts[1];
					String[] p = baseUrl.split(":");
					if (p.length == 2 && p[1].trim().equals(httpPort)) {
						br.close();
						return true;
					}
				}
	    	}	
	    	br.close();
		} catch (IOException e) {
			System.out.println("Error when reading NETSTAT: " + e.getMessage());
		}
		
		return false;
	}

	private static Properties loadProperties(File propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(propsFile);
        props.load(fis);    
        fis.close();
        return props;
    }	
}