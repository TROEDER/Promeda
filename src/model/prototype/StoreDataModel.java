/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.prototype;

import java.util.ArrayList;
import model.singleton.DatabaseModel;

/**
 *
 * @author troeder
 */
public class StoreDataModel {

	private String storeName;
	private String storeFtpServer;
	private int storeFtpPort;
	private String storeFtpUser;
	private String storeFtpPass;
	private String storeFtpProtocol;
	private String[] storeImageSizes;
	private ImageSize[] imageSizes;
	private Boolean selectStatus;

	public StoreDataModel(String storeName, String storeFtpServer, int storeFtpPort, String storeFtpUser,
			String storeFtpPass, String[] storeImageSizes) {

		this.storeName = storeName;
		this.storeFtpServer = storeFtpServer;
		this.storeFtpPort = storeFtpPort;
		this.storeFtpUser = storeFtpUser;
		this.storeFtpPass = storeFtpPass;
		this.storeImageSizes = storeImageSizes;
		selectStatus = false;
	}

	public Boolean getSelectStatus() {
		return selectStatus;
	}

	public void setSelectStatus(Boolean selectStatus) {
		this.selectStatus = selectStatus;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public void setStoreFtpServer(String storeFtpServer) {
		this.storeFtpServer = storeFtpServer;
	}

	public void setStoreFtpPort(int storeFtpPort) {
		this.storeFtpPort = storeFtpPort;
	}

	public void setStoreFtpUser(String storeFtpUser) {
		this.storeFtpUser = storeFtpUser;
	}

	public void setStoreFtpPass(String storeFtpPass) {
		this.storeFtpPass = storeFtpPass;
	}

	public String getStoreFtpServer() {
		return storeFtpServer;
	}

	public int getStoreFtpPort() {
		return storeFtpPort;
	}

	public String getStoreFtpUser() {
		return storeFtpUser;
	}

	public String getStoreFtpPass() {
		return storeFtpPass;
	}

	public String getStoreFtpProtocol() {
		return storeFtpProtocol;
	}

	public void setStoreFtpProtocol(String storeFtpProtocol) {
		this.storeFtpProtocol = storeFtpProtocol;
	}

	public String[] getStoreImageSizes() {
		return storeImageSizes;
	}

	public void setStoreImageSizes(String[] storeImageSizes) {
		this.storeImageSizes = storeImageSizes;
	}

}
