package com.vendavo.support.prototype;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Takes given folder and list of remote folders and copy all files from the folder into all remote folder. 
 * Files at remote folders are deleted first, only first level of files are copied from source folder (i.e. no recursion)
 *
 */
public class DeployTool {
	public static void main(String[] args) throws IOException {
		System.out.println("DeployTool, version 0.1.0");
		System.out.println("Usage: DeployTool folderToCopy fileWithFoldersToCopy");
		if (args.length == 2) {
			File file = new File(args[0]);
			ArrayList list = new ArrayList();
			
			if (file.exists() && file.isDirectory()) {
				File foldersToCopy = new File(args[1]);
				BufferedReader br = new BufferedReader(new FileReader(foldersToCopy));
				while(true) {
					String line = br.readLine();
					if (line == null) break;
					
					list.add(line.trim());
				}
				
				br.close();
			}
			
			for (int i = 0; i < list.size(); i++) {
				System.out.println("Processing deploy to:" + list.get(i));
				File destFolder = new File((String)list.get(i));
				if (!destFolder.exists()) {
					if (!destFolder.mkdirs()) {
						System.out.println("Cannot create deploy folder:" + file.getAbsolutePath());
					}
				}
				
				File[] filesToDelete = destFolder.listFiles();
				for (int j = 0; j < filesToDelete.length; ++j) {
					if (filesToDelete[j].exists() && filesToDelete[j].isFile()) {
						filesToDelete[j].delete();
					}
				}
				
				File[] filesToCopy = file.listFiles();
				for (int j = 0; j < filesToCopy.length; ++j) {
					File copyFromFile = filesToCopy[j];
					if (copyFromFile.isFile() & copyFromFile.exists()) {
						copy(copyFromFile, destFolder);
					}
				}
			}
		}
	}

	private static void copy(File copyFromFile, File destFolder) {
		File copyToFile = new File(destFolder, copyFromFile.getName());
		System.out.println("Copy " + copyFromFile.getAbsolutePath() + " into file " + copyToFile.getAbsolutePath());
		
		BufferedInputStream is = null;
		BufferedOutputStream os = null;
		try {
			is = new BufferedInputStream(new FileInputStream(copyFromFile));
			os = new BufferedOutputStream(new FileOutputStream(copyToFile));
			byte[] buffer = new byte[1024 * 128];
			while (true) {
				int count = is.read(buffer);
				if (count <= 0) break;
				
				os.write(buffer, 0, count);
			}
			
			os.close();
			is.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Cannot copy " + copyFromFile.getAbsolutePath() + " into file " + copyToFile.getAbsolutePath());
		} catch (IOException e) {
			try {
				if (is != null)	is.close();
				if (os != null) os.close();
			} catch (IOException e1) {
			}
		}
		
		
	}
}
