/**
 * 
 */
package examples;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

import model.singleton.MultipartUtility;

/**
 * @author troeder
 *
 */
public class SftpTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SftpTester sftpTester = new SftpTester();
		// sftpTester.sendMultipartRequest();
		sftpTester.exec();
	}

	public int exec() {
		int exitValue = 0;
		//CommandLine cmdLine = new CommandLine("D:\\Benutzer\\Projekte\\eclipse-workspace\\Promeda\\imagemin\\node_modules\\npm\\bin\\npm");
		CommandLine cmdLine = new CommandLine("D:\\Benutzer\\Projekte\\eclipse-workspace\\Promeda\\imagemin\\node_modules\\gulp\\node_modules\\.bin\\gulp");
		//cmdLine.addArgument("start");
		DefaultExecutor executor = new DefaultExecutor();
		try {
			exitValue = executor.execute(cmdLine);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return exitValue;
		}

	}
	public void sendMultipartRequest() {
		String charset = "UTF-8";
		File uploadFile = new File("D:\\Benutzer\\Projekte\\eclipse-workspace\\Promeda\\bin\\examples\\logo.png");
		String requestURL = "https://im2.io/kzrbgzzbfm/full";

		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);

			/*
			 * multipart.addHeaderField("User-Agent", "Promeda");
			 * multipart.addHeaderField("Test-Header", "Header-Value");
			 * 
			 * multipart.addFormField("description", "Cool Pictures");
			 * multipart.addFormField("keywords", "image,compression,imagemin");
			 */

			multipart.addFilePart("fileUpload", uploadFile);

			List<String> response = multipart.finish();

			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	
}
