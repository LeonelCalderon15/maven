package utility;

import java.io.File;

public class Browser {
	
	 public static boolean DeleteOldFile(String downloadPath, String fileName) {
		  File dir = new File(downloadPath);
		  File[] dirContents = dir.listFiles();
		  
		  for (int i = 0; i < dirContents.length; i++) {
		      if (dirContents[i].getName().equals(fileName)) {
		          // File has been found, it can now be deleted:
		          dirContents[i].delete();
		          return true;
		      }
		          }
		      return false;
		  }
	 
	 public static boolean IsFileDownloaded(String downloadPath, String fileName) {
		  File dir = new File(downloadPath);
		  File[] dirContents = dir.listFiles();

		  for (int i = 0; i < dirContents.length; i++) {
		      if (dirContents[i].getName().equals(fileName)) {
		          return true;
		      }
		   }
		      return false;
		  }
	 
}
