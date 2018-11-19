/*
 * 
 * Copyright (C) 2006 Enterprise Distributed Technologies Ltd
 * 
 * www.enterprisedt.com
 */
package model.singleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.util.debug.Level;
import com.enterprisedt.util.debug.Logger;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public final class FtpClientModel {

	private String host;
	private int port;
	private String user;
	private String password;

	public FileTransferClient ftp;

	public Session session = null;
	private Channel channel = null;
	private ChannelSftp channelSftp = null;

	/**
	 * Logger for Debugging/Output for Log-File set up logger so that we get some
	 * output
	 **/
	private Logger log = Logger.getLogger(FtpClientModel.class);

	/*******************************************************************************
	 * Constructor - Creates FTP for given host data
	 * 
	 * @param ftpHost
	 * @param ftpUser
	 * @param ftpPwd
	 ******************************************************************************/
	public FtpClientModel(String ftpHost, int ftpPort, String ftpUser, String ftpPwd) {

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

		// create client
		log.info("Creating FTP client");
		ftp = new FileTransferClient();
		// set remote host
		log.info("Setting remote host");
		try {
			ftp.setRemoteHost(host);
			ftp.setUserName(user);
			ftp.setPassword(password);
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
//		connect();
	}

	/*******************************************************************************
	 * public method connect
	 ******************************************************************************/
	public void connect() {
		// connect to the server
		log.info("Connecting to server " + host);
		try {
			ftp.connect();
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
		log.info("Connected and logged in to server " + host);
	}

	/*******************************************************************************
	 * public method changeDir
	 * 
	 * @param dir
	 ******************************************************************************/
	public void changeDir(String dir) {
		try {
			log.info("Current dir: " + ftp.getRemoteDirectory());
			log.info("Changing directory");
			ftp.changeDirectory(dir);
			log.info("Current dir: " + ftp.getRemoteDirectory());
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
	}

	/*******************************************************************************
	 * public method upload
	 * 
	 * @param localFile
	 * @param remoteFile
	 ******************************************************************************/
	public void upload(String localFile, String remoteFile) {
		try {
			log.info("Uploading file");
			ftp.uploadFile(localFile, remoteFile);
			log.info("File uploaded");
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
	}

	/*******************************************************************************
	 * public method download
	 * 
	 * @param localFileName
	 * @param remoteFileName
	 ******************************************************************************/
	public void download(String localFileName, String remoteFileName) {
		try {
			log.info("downloading file");
			ftp.downloadFile(localFileName, remoteFileName);
			log.info("File downloaded");
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
	}

	/*******************************************************************************
	 * public method disconnect
	 ******************************************************************************/
	public void disconnect() {
		try {
			// Shut down client
			log.info("Quitting client");
			ftp.disconnect();
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		} catch (FTPException ex) {
			java.util.logging.Logger.getLogger(FtpClientModel.class.getName()).log(java.util.logging.Level.SEVERE, null,
					ex);
		}
	}

	public void sftpConnect() {

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sftpUpload(File localFile, File remoteFile) {
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
//            if(!remoteFile.getParentFile().exists()) channelSftp.mkdir(remoteFile.getParentFile().getName());
			channelSftp.cd(remoteFile.getParentFile().getName());
			System.out.println(remoteFile.getParentFile().getName());
			channelSftp.put(new FileInputStream(localFile), remoteFile.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void sftpDisconnect() {
		channelSftp.disconnect();
		channel.disconnect();
		session.disconnect();
	}

}
