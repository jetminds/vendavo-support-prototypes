package com.vendavo.support.prototype;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Renames given file with given prefix so that the resulting filename is PREFIX_HOSTNAME_DATE.csv
 */

public class RenameWisely {
	private static SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	public static void main(String[] args) {
		System.out.println("RenameWisely, version 0.1.0 - renames given file to HOST_DATE_TIME format. ");
		System.out.println("Usage: RenameWisely fileToRename prefixToAdd");
		if (args.length == 2) {
			String name = args[1] + "_" + getHostName() + "_" + date.format(Calendar.getInstance().getTime()) + ".csv";
			File file = new File(args[0]);
			if (file.renameTo(new File(name))) {
				System.out.println("File " + file.getAbsolutePath() + " renamed to " + name );
			}
		}
	}
	
	private static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}	
		
		return "Unknown";
	}

}

