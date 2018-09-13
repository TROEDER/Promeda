/**
 * 
 */
package examples;

import java.io.File;

import model.singleton.SFTPClientModel;

/**
 * @author troeder
 *
 */
public class SftpTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File fily = new File("websale8_shop-promondo-dev-2/produkte/medien/bilder");
		System.out.println(fily.getPath());
		
		// VIA SFTP
		SFTPClientModel sftp = new SFTPClientModel("ftp.c1.websale.net", 22022, "promondo-dev", "ohn1gooX", "websale8_shop-promondo-dev-2/produkte/medien/bilder");
		sftp.sftpConnect();
		
	}

}
