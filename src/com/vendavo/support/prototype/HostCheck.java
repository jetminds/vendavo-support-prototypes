package com.vendavo.support.prototype;

/** VERSION INFO
 * v 0.1.0 - initial version
 * v 0.2.0 - added MB to GB conversion
 *         - Office parser updated to match 2003/2007/2010
 *
 */


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HostCheck {
	// Kernel version:            Microsoft Windows Server 2003 R2, Multiprocessor Free
	private static final Pattern OSPattern =
		Pattern.compile("^Kernel version:\\s*([a-zA-z0-9\\s]+),.*");
	// IE version:                8.0000
	private static final Pattern IEPattern = 
		Pattern.compile("^IE version:\\s*([0-9].[0-9]+)");
	// Processor type:            Intel(R) Xeon(TM) CPU
	private static final Pattern CPUPattern = 
		Pattern.compile("^Processor type:\\s*(Intel\\(R\\)\\s+Xeon).*\\s+CPU.*");
	// Processor speed:           2.8 GHz
	private static final Pattern CPUSpdPattern = 
		Pattern.compile("^Processor speed:\\s+([0-9].[0-9])\\s+GHz");
	// Processors:                4
	private static final Pattern CPUCoresPattern = 
		Pattern.compile("^Processors:\\s+([0-9]+)");
	// Physical memory:           4096 MB
	private static final Pattern MemPattern = 
		Pattern.compile("^Physical memory:\\s+([0-9]{1,5})\\sMB");
	// C: Fixed      NTFS                              12.00 GB    2.76 GB  23.0%
	private static final Pattern CDrivePattern = 
		Pattern.compile("^C:\\s+Fixed(\\s+[a-zA-Z\\d]+)+\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)%");
	// D: Fixed      NTFS                             149.01 GB   13.25 GB   8.9%
	private static final Pattern DDrivePattern = 
		Pattern.compile("^D:\\s+Fixed(\\s+[a-zA-Z\\d]+)+\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)%");
	// E: Fixed      NTFS       Local Disk            136.95 GB   15.93 GB  11.6%
	private static final Pattern EDrivePattern = 
		Pattern.compile("^E:\\s+Fixed(\\s+[a-zA-Z\\d]+)+\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)\\s(GB|MB)\\s+([0-9]+.[0-9]+)%");
	// Microsoft Office Excel MUI (English) 2007 12.0.4518.1014
	private static final Pattern ExcelPattern = 
		Pattern.compile("^Microsoft\\s+(Office)\\s+(Excel).*(\\s\\d{4}).*");
	// Microsoft Office Professional Edition 2003 11.0.5614.0
	private static final Pattern OfficePattern = 
		Pattern.compile("^Microsoft\\s+(Office)\\s+Professional\\s+Edition\\s+(\\d{4}).*");
	
	
	public static void main(String[] args) {
		
		System.out.println("HostCheck, version 0.2.0\nRelevant CSV lines starts with SUCCESS.");
		System.out.println("#SUCCESS, HOST, OS, IE, CPU speed, # of cores, MEM, "
				+ "C: size, C: free, C: perc, "
				+ "D: size, D: free, D: perc, "
				+ "E: size, E: free, E: perc, Excel");

		processFile();
	}

	private static void processFile() {
		//File file = new File (fileName);
		String[] host = new String[17];
		BufferedReader br;
		host[0]=getHostName();
		String line="";
		try {
			br = new BufferedReader(new FileReader("host.txt"));
			while (true){
				try {
					line = br.readLine();
				} catch (IOException e) {
					System.out.println("Error reading line: " + line + e.getMessage());
					continue;
				}
				if (line==null){
					break;
				} else
				processLine(line.trim(), host);
			}
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("Unable to close BufferedReader:" + e.getMessage());
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found error: " + e.getMessage());
		}
		printInfo(host);
	}
	
	private static void processLine(String line, String[] host){
		//OS
		Matcher OSMatch = OSPattern.matcher(line);
		if(OSMatch.matches()){
			host[1]=OSMatch.group(1).trim();
			return;
		}
		
		//IE
		Matcher IEMatch = IEPattern.matcher(line);
		if(IEMatch.matches()){
			host[2]=IEMatch.group(1).trim();
			return;
		}
		
		//CPU
		Matcher CPUMatch = CPUPattern.matcher(line);
		if(CPUMatch.matches()){
			host[3]=CPUMatch.group(1).trim();
			return;
		}
		
		//CPUSpd
		Matcher CPUSpdMatch = CPUSpdPattern.matcher(line);
		if(CPUSpdMatch.matches()){
			host[4]=CPUSpdMatch.group(1).trim();
			return;
		}

		//CPUCores
		Matcher CPUCoresMatch = CPUCoresPattern.matcher(line);
		if(CPUCoresMatch.matches()){
			host[5]=CPUCoresMatch.group(1).trim();
			return;
		}
		
		//CPUCores
		Matcher MemMatch = MemPattern.matcher(line);
		if(MemMatch.matches()){
			host[6]=MemMatch.group(1).trim();
			return;
		}
		
		//C: Drive data
		Matcher CDriveMatch = CDrivePattern.matcher(line);
		if(CDriveMatch.matches()){
			// populate C: drive capacity
			if (CDriveMatch.group(3).equals("GB")){
				host[7]=CDriveMatch.group(2).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(CDriveMatch.group(2))/1024 );
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				host[7]= bd.toString();
			}
			// populate C: drive free space
			if (CDriveMatch.group(5).equals("GB")){
				host[8]=CDriveMatch.group(4).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(CDriveMatch.group(4))/1024 );
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				host[8]= bd.toString();
			}
			// populate C: drive free percentage
			host[9]=CDriveMatch.group(6).trim();
			return;
		}
		
		//D: Drive data
		Matcher DDriveMatch = DDrivePattern.matcher(line);
		if(DDriveMatch.matches()){
			//populate D: drive capacity
			if (DDriveMatch.group(3).equals("GB")){
				host[10]=DDriveMatch.group(2).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(DDriveMatch.group(2))/1024 );
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				host[10]= bd.toString();
			}
			//populate D: drive free space
			if (DDriveMatch.group(5).equals("GB")){
				host[11]=DDriveMatch.group(4).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(DDriveMatch.group(4))/1024 );
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				host[11]= bd.toString();
			}
			//populate D: drive free percentage
			host[12]=DDriveMatch.group(6).trim();
			return;
		}
		
		//E: Drive data
		Matcher EDriveMatch = EDrivePattern.matcher(line);
		if(EDriveMatch.matches()){
			// populate E: drive capacity
			if (EDriveMatch.group(3).equals("GB")){
				host[13]=EDriveMatch.group(2).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(EDriveMatch.group(2))/1024 );
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				host[13]= bd.toString();
			}
			// populate E: drive free space
			if (EDriveMatch.group(5).equals("GB")){
				host[14]=EDriveMatch.group(4).trim();
			}else {
				BigDecimal bd = new BigDecimal( Float.parseFloat(CDriveMatch.group(4))/1024 );
				bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
				host[14]= bd.toString();
			}
			host[15]=EDriveMatch.group(6).trim();
			// populate E: drive free percentage
			return;
		}
		
		// Excel
		Matcher ExcelMatch = ExcelPattern.matcher(line);
		if(ExcelMatch.matches()){
			host[16]=ExcelMatch.group(1).trim()+" "+ExcelMatch.group(3).trim();
			return;
		}
		
		// Office
		Matcher OfficeMatch = OfficePattern.matcher(line);
		if(OfficeMatch.matches()){
			host[16]=OfficeMatch.group(1).trim()+" "+OfficeMatch.group(2).trim();
			return;
		}
	}
	
	private static void printInfo(String[] host) {
		if (host[0] != null) {
			System.out.print("SUCCESS");
			for (int i=0; i<host.length; i++){
				System.out.print(", "+ host[i]);
			}
			System.out.println();
		} else
			System.out.println("No information available");
	}

	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.out.println("Could not resolve hostname. " + e.getMessage());
		}
		return "UNKNOWN HOST";
	}
}
