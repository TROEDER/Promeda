/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.prototype;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author troeder
 */
/**
 * @author troeder
 *
 */
public class StoreDataModel {

	private String storeName;
	private String storeFtpServer;
	private int storeFtpPort;
	private String storeFtpUser;
	private String storeFtpPass;
	private String storeFtpProtocol;
	private String dirDefault;
	private ImageSize[] storeImageSizes;
	private Vector<ImageSize> storeImageSizeList = new Vector<ImageSize>();;
	private List<ImageSize> storeImageSizeListNew = new Vector<ImageSize>();
	private Boolean selectStatus;

	public StoreDataModel(String storeName, String storeFtpServer, int storeFtpPort, String storeFtpUser,
			String storeFtpPass, ImageSize[] storeImageSizes) {

		this.storeName = storeName;
		this.storeFtpServer = storeFtpServer;
		this.storeFtpPort = storeFtpPort;
		this.storeFtpUser = storeFtpUser;
		this.storeFtpPass = storeFtpPass;
		this.storeImageSizes = storeImageSizes;
		List<ImageSize> lList = Arrays.asList(storeImageSizes);
		selectStatus = false;
	}

	public StoreDataModel(String storeName, String storeFtpServer, int storeFtpPort, String storeFtpUser,
			String storeFtpPass, Vector<ImageSize> storeImageSizeList) {

		this.storeName = storeName;
		this.storeFtpServer = storeFtpServer;
		this.storeFtpPort = storeFtpPort;
		this.storeFtpUser = storeFtpUser;
		this.storeFtpPass = storeFtpPass;
		this.storeImageSizeList = storeImageSizeList;
		selectStatus = false;
	}

	public StoreDataModel(String storeName, String storeFtpServer, int storeFtpPort, String storeFtpProtocol,
			String storeFtpUser, String storeFtpPass, List<Object> storeImageSizeList) {

		this.storeName = storeName;
		this.storeFtpServer = storeFtpServer;
		this.storeFtpPort = storeFtpPort;
		this.storeFtpProtocol = storeFtpProtocol;
		this.storeFtpUser = storeFtpUser;
		this.storeFtpPass = storeFtpPass;
		for (Object imageSizeParams : storeImageSizeList) {
			storeImageSizeListNew.add(new ImageSize(imageSizeParams.toString().split(",")));
			System.out.println("xyz" + new ImageSize(imageSizeParams.toString().split(",")).getHeight());
			
		}
		selectStatus = false;
	}

	public StoreDataModel(String storeName, String storeFtpServer, int storeFtpPort, String storeFtpProtocol,
			String storeFtpUser, String storeFtpPass, String dirDefault, List<Object> storeImageSizeList) {

		this.storeName = storeName;
		this.storeFtpServer = storeFtpServer;
		this.storeFtpPort = storeFtpPort;
		this.storeFtpProtocol = storeFtpProtocol;
		this.storeFtpUser = storeFtpUser;
		this.storeFtpPass = storeFtpPass;
		this.dirDefault = dirDefault;
		for (Object imageSizeParams : storeImageSizeList) {
			storeImageSizeListNew.add(new ImageSize(imageSizeParams.toString().split(",")));
			System.out.println("xyz" + new ImageSize(imageSizeParams.toString().split(",")).getHeight());
		}
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

	public String getDirDefault() {
		return dirDefault;
	}

	public void setDirDefault(String dirDefault) {
		this.dirDefault = dirDefault;
	}

	public ImageSize[] getStoreImageSizes() {
		return storeImageSizes;
	}

	public void setStoreImageSizes(ImageSize[] storeImageSizes) {
		this.storeImageSizes = storeImageSizes;
	}

	public Vector<ImageSize> getStoreImageSizeList() {
		return storeImageSizeList;
	}

	public void setStoreImageSizeList(Vector<ImageSize> storeImageSizeList) {
		this.storeImageSizeList = storeImageSizeList;
	}

	public List<ImageSize> getStoreImageSizeListNew() {
		return storeImageSizeListNew;
	}

	public void setStoreImageSizeListNew(List<ImageSize> storeImageSizeListNew) {
		this.storeImageSizeListNew = storeImageSizeListNew;
	}

}
