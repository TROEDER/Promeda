/**
* Created on July 1, 2010 Copyright(c) https://kodehelp.com All Rights Reserved.
*/
package ui.archives;

import java.io.File;
import java.io.FileInputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 * @author https://kodehelp.com
 *
 */
public class SFTPinJava {

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*String SFTPHOST = "10.20.30.40";
        int SFTPPORT = 22;
        String SFTPUSER = "username";
        String SFTPPASS = "password";
        String SFTPWORKINGDIR = "/export/home/kodehelp/";*/

        /*String SFTPHOST = "ftp.c1.websale.net";
		int SFTPPORT = 22022;
		String SFTPUSER = "promondo-bilder";
		String SFTPPASS = "wSoupscM5k4E";
		String SFTPWORKINGDIR = "/";*/
		
		String SFTPHOST = "9153-2.whserv.de";
		int SFTPPORT = 21;
		String SFTPUSER = "web3f2";
		String SFTPPASS = "#XSoRg3a!L7cD9";
		String SFTPWORKINGDIR = "/";
		
        String filename = "10272G_2_20131119.jpg";
        String localDirectory = "media/live/100px/10272G_2_20131119/";
        String filepath = localDirectory + filename;
		File file = new File(filepath);
		
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            channelSftp.put(new FileInputStream(file), file.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
