/*
 * 
 * Copyright (C) 2006 Enterprise Distributed Technologies Ltd
 * 
 * www.enterprisedt.com
 */
package model.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public final class SFTPClientModel {

	private String host;
	private int port;
	private String user;
	private String password;
	private String dirDefault;

	public Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	/**
	 * Logger for Debugging/Output for Log-File set up logger so that we get some
	 * output
	 **/
	// private Logger log = Logger.getLogger(SFTPClientModel.class);

	/*******************************************************************************
	 * Constructor - Creates FTP for given host data
	 * 
	 * @param ftpHost
	 * @param ftpUser
	 * @param ftpPwd
	 ******************************************************************************/
	public SFTPClientModel(String ftpHost, int ftpPort, String ftpUser, String ftpPwd, String ftpDirDefault) {

		/**
		 * Logger for Debugging/Output for Log-File set up logger so that we get some
		 * output
		 **/
		Logger.setLevel(Level.INFO);

		// extract arguments
		host = ftpHost;
		port = ftpPort;
		user = ftpUser;
		password = ftpPwd;
		dirDefault = ftpDirDefault;

	}

	public void connect() {

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			System.out.println(channelSftp.pwd());
			channelSftp.cd(dirDefault);
			System.out.println(channelSftp.pwd());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void changeDir(File remoteFile) {
		try {
			channelSftp.cd(remoteFile.getParentFile().getName());
			channelSftp.cd(remoteFile.getParentFile().getName());
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void changeDir(String dir) {
		try {
			channelSftp.cd(dir);
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void makeDir(String dir) {
		SftpATTRS attrs;
		try {
			attrs = channelSftp.stat(dir);
		} catch (Exception e) {
			try {
				channelSftp.mkdir(dir);
			} catch (SftpException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void upload(File localFile, File remoteFile) {

		try {
			channelSftp.put(new FileInputStream(localFile), remoteFile.getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void disconnect() {
		channelSftp.disconnect();
		channel.disconnect();
		session.disconnect();
	}

}
