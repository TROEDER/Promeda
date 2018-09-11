/**
 * 
 */
package examples;

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

		// VIA SFTP
		SFTPClientModel sftp = new SFTPClientModel("ftp.c1.websale.net", 22022, "promondo-dev", "ohn1gooX");
		sftp.sftpConnect();
		
	}

}
