/**
 * 
 */
package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.singleton.MultipartUtility;

/**
 * @author troeder
 *
 */
public class SftpTester {

	private HttpURLConnection httpConn;

	public SftpTester() {
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);
		httpConn.setDoOutput(true); // indicates POST method
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SftpTester sftpTester = new SftpTester();
		sftpTester.getRequest();
	}

	public void HTTPMultipartPostRequest() {
		String charset = "UTF-8";
		File uploadFile = new File("img/logo.png");
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

	public void getRequest() {
		List<String> response = new ArrayList<String>();
		String requestURL = "https://img.gs/kzrbgzzbfm/full/https://promondo-dev-2.websale.biz/websale8_shop-promondo-dev-2/produkte/medien/bilder/720px/87857G.jpg";
		// checks server's status code first
		int status = 0;
		try {
			status = httpConn.getResponseCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (status == HttpURLConnection.HTTP_OK) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				System.out.println(HttpURLConnection.guessContentTypeFromStream(httpConn.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					response.add(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpConn.disconnect();
		} else {
			try {
				throw new IOException("Server returned non-OK status: " + status);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
