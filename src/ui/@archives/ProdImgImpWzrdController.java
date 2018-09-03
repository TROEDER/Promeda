package ui.archives;

import static org.apache.commons.io.FileUtils.copyFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import model.prototype.ImageSize;
import model.prototype.StoreDataModel;
import model.singleton.FtpClientModel;
import model.singleton.ImageHandler;
import model.singleton.PropertiesModel;

public class ProdImgImpWzrdController implements ActionListener, ComponentListener {

	private ProdImgImpWzrdView view;
	private FtpClientModel ftp;
	private PropertiesModel propApp;
	private Vector<StoreDataModel> stores;
	private Vector<StoreDataModel> selectedStores;
	private File[] psdFiles;
	private Vector<File> psdFileList = new Vector<File>();

	public ProdImgImpWzrdController() {
		initProperties();
		initView();
		initStores();
	}

	public ProdImgImpWzrdController(Vector<File> psdFileList) {
		initProperties();
		initView();
		initStores();
		this.psdFileList = psdFileList;
		view.fileListSourceFiles.setListData(psdFileList);
	}

	private void initProperties() {
		propApp = new PropertiesModel();
		propApp.loadAppProperties();
	}

	private void initView() {

		view = new ProdImgImpWzrdView(this);
		view.setVisible(true);
	}

	public void initStores() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();

		PropertiesModel propStore = new PropertiesModel();
		stores = new Vector<StoreDataModel>();

		for (File file : files) {
			if (!file.isDirectory()) {
				propStore.load(file.getPath());
				String[] imgSizes = propStore.get("img.sizes").split(",");
				stores.add(new StoreDataModel(propStore.get("url"), propStore.get("ftp.host"),
						Integer.parseInt(propStore.get("ftp.port")), propStore.get("ftp.user"),
						propStore.get("ftp.pswd"), imgSizes));
			}

		}
	}

	public void initStores2() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();

		PropertiesModel propStore = new PropertiesModel();
		stores = new Vector<StoreDataModel>();
		Vector<ImageSize> imgSizes = new Vector<ImageSize>();
		for (File file : files) {
			if (!file.isDirectory()) {
				propStore.load(file.getPath());
				for (String imgSize : propStore.get("img.sizes").split("|")) {
					String[] imgSizeValues = imgSize.split(",");
					imgSizes.add(new ImageSize(Integer.parseInt(imgSizeValues[0]), imgSizeValues[1]));
				}
				stores.add(new StoreDataModel(propStore.get("url"), propStore.get("ftp.host"),
						Integer.parseInt(propStore.get("ftp.port")), propStore.get("ftp.user"),
						propStore.get("ftp.pswd"), imgSizes));
				imgSizes.clear();
			}
		}
	}

	public void process() {

		ImageHandler imgHandler = new ImageHandler();
		File imgFile;
		BufferedImage img;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("_yyyyMMdd");
		String currentDate = LocalDate.now().toString(fmt);

		double progressBarMax = 100;
		double progressSteps = psdFileList.size() * selectedStores.size();
		double progressStepSizef = progressBarMax / progressSteps;
		int progressStepSize = (int) Math.round(progressStepSizef);

		try {
			for (StoreDataModel store : selectedStores) {

				ftp = new FtpClientModel(store.getStoreFtpServer(), store.getStoreFtpPort(), store.getStoreFtpUser(),
						store.getStoreFtpPass());

				if (store.getStoreFtpProtocol() == "ftp") {
					ftp.connect();
				} else if (store.getStoreFtpProtocol() == "sftp") {
					ftp.sftpConnect();
				}

				for (File psdFile : psdFileList) {

					// String[] fileNameParts = psdFile.getName().split("\\.(?=[^\\.]+$)");
					// Not used

					// COPY PSD FILE TO ORIGINALS FOLDER
					copyFile(psdFile,
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals")
									+ FilenameUtils.getBaseName(psdFile.getName()) + "/"
									+ FilenameUtils.getBaseName(psdFile.getName()) + currentDate + "."
									+ FilenameUtils.getExtension(psdFile.getName())));

					// GET BUFFEREDIMAGE FROM PSD FILE
					img = imgHandler.getImageFromPsd(psdFile);

					progressThumbUpdate(imgHandler.resizeImage(100, 100, img));
					for (String imgSize : store.getStoreImageSizes()) {

						// RESIZE BUFFEREDIMAGE
						progressLabelUpdate(
								"Resize " + FilenameUtils.getBaseName(psdFile.getName()) + " to " + imgSize + "px");
						int imgSizeInt = Integer.parseInt(imgSize);
						BufferedImage scaledImage = imgHandler.resizeImage(imgSizeInt, imgSizeInt, img);

						// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
						progressLabelUpdate("Remove Alpha Channel from " + FilenameUtils.getBaseName(psdFile.getName())
								+ " (" + imgSize + "px)");
						BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

						// WRITE IMAGE FILE TO TEMPORARY FOLDER
						File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
								+ imgSize + "px/" + FilenameUtils.getBaseName(psdFile.getName()));
						if (!directory.exists()) {
							directory.mkdirs();
						}

						imgFile = new File(
								directory.getPath() + "/" + FilenameUtils.getBaseName(psdFile.getName()) + ".jpg");
						ImageIO.write(rgbImage, "jpg", imgFile);

						// UPLOAD TO WEBSERVER
						progressLabelUpdate("Upload " + FilenameUtils.getBaseName(psdFile.getName()) + " (" + imgSize
								+ "px) to " + store.getStoreName());
						if (store.getStoreImageSizes().length == 1) {
							ftp.connect();
							ftp.upload(imgFile.getPath(), imgFile.getName());
							System.out.println("Uploaded: " + imgFile.getName());
						} else if (store.getStoreImageSizes().length > 1) {
							File remoteFile;
							if (imgSize == "66") {
								remoteFile = new File("thumb/" + imgFile.getName());
							} else if (imgSize == "130") {
								remoteFile = new File("klein130/" + imgFile.getName());
							} else {
								remoteFile = new File(imgSize + "px/" + imgFile.getName());
							}

							ftp.sftpUpload(imgFile, remoteFile);
							System.out.println("Uploaded: " + remoteFile.getName());
						}

					}
					progressBarUpdate(progressStepSize);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		progressBarUpdate(100);
		progressLabelUpdate("complete");
		view.btnCardNext.setEnabled(true);
		view.btnCardNext.setText("Done");
	}

	public void process2() {

		ImageHandler imgHandler = new ImageHandler();
		File imgFile;
		BufferedImage img;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("_yyyyMMdd");
		String currentDate = LocalDate.now().toString(fmt);

		double progressBarMax = 100;
		double progressSteps = psdFileList.size() * selectedStores.size();
		double progressStepSizef = progressBarMax / progressSteps;
		int progressStepSize = (int) Math.round(progressStepSizef);

		try {
			for (StoreDataModel store : selectedStores) {

				ftp = new FtpClientModel(store.getStoreFtpServer(), store.getStoreFtpPort(), store.getStoreFtpUser(),
						store.getStoreFtpPass());

				for (File psdFile : psdFileList) {

					// String[] fileNameParts = psdFile.getName().split("\\.(?=[^\\.]+$)");
					// Not used

					// COPY PSD FILE TO ORIGINALS FOLDER
					copyFile(psdFile,
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals")
									+ FilenameUtils.getBaseName(psdFile.getName()) + "/"
									+ FilenameUtils.getBaseName(psdFile.getName()) + currentDate + "."
									+ FilenameUtils.getExtension(psdFile.getName())));

					// GET BUFFEREDIMAGE FROM PSD FILE
					img = imgHandler.getImageFromPsd(psdFile);

					progressThumbUpdate(imgHandler.resizeImage(100, 100, img));
					for (ImageSize imgSize : store.getStoreImageSizeList()) {

						// RESIZE BUFFEREDIMAGE
						progressLabelUpdate("Resize " + FilenameUtils.getBaseName(psdFile.getName()) + " to "
								+ imgSize.getSideLength() + "px");
						BufferedImage scaledImage = imgHandler.resizeImage(imgSize.getSideLength(),
								imgSize.getSideLength(), img);

						// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
						progressLabelUpdate("Remove Alpha Channel from " + FilenameUtils.getBaseName(psdFile.getName())
								+ " (" + imgSize.getSideLength() + "px)");
						BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

						// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
						File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
								+ imgSize.getName() + "/" + FilenameUtils.getBaseName(psdFile.getName()));
						if (!directory.exists()) {
							directory.mkdirs();
						}

						imgFile = new File(
								directory.getPath() + "/" + FilenameUtils.getBaseName(psdFile.getName()) + ".jpg");
						ImageIO.write(rgbImage, "jpg", imgFile);

						// UPLOAD TO WEBSERVER
						progressLabelUpdate("Upload " + FilenameUtils.getBaseName(psdFile.getName()) + " ("
								+ imgSize.getName() + ") to " + store.getStoreName());

						File remoteFile = new File( imgSize.getName() + "/" + imgFile.getName());
						
						// VIA FTP
						if (store.getStoreFtpProtocol() == "ftp") {
							if (!ftp.ftp.isConnected()) {
								ftp.connect();
							}
							ftp.upload(imgFile.getPath(), imgFile.getName());
						
						// VIA SFTP
						} else if (store.getStoreFtpProtocol() == "sftp") {
							if(!ftp.session.isConnected()) {
								ftp.sftpConnect();
							}
							ftp.sftpUpload(imgFile, remoteFile);
						}
					}
					progressBarUpdate(progressStepSize);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		progressBarUpdate(100);
		progressLabelUpdate("complete");
		view.btnCardNext.setEnabled(true);
		view.btnCardNext.setText("Done");
	}

	public File[] openFiles() {
		File[] files = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Select files (multiple selection possible)");
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setLocation(100, 100);

		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			files = fileChooser.getSelectedFiles();
		}
		return files;
	}

	public void addFiles() {
		psdFiles = openFiles();

		for (File file : psdFiles) {
			psdFileList.add(file);
		}
		view.fileListSourceFiles.setListData(psdFileList);
	}

	public void removeFiles() {
		psdFileList.remove(view.fileListSourceFiles.getSelectedIndex());
		view.fileListSourceFiles.setListData(psdFileList);
	}

	public void clearList() {
		psdFileList.clear();
		view.fileListSourceFiles.setListData(psdFileList);
	}

	public void initSelectedStoreList() {
		selectedStores = new Vector<StoreDataModel>();
		for (StoreDataModel store : stores) {
			if (store.getSelectStatus())
				selectedStores.add(store);
		}
	}

	public void progressBarUpdate(int progressStepSize) {
		view.progressBar.setValue(view.progressBar.getValue() + progressStepSize);
		view.labelLoadManMoving.setLocation(view.progressBar.getValue() * 4, 95);
	}

	public void progressLabelUpdate(String LabelText) {
		view.labelProgressStep.setText(LabelText);
	}

	public void progressThumbUpdate(BufferedImage previewThumb) {
		view.labelProgressThumb.setIcon(new ImageIcon(previewThumb));
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == view.btnCardBack) {
			view.cardLayoutContentContainer.previous(view.panelContentContainer);
			view.btnCardNext.setText("Next");
		} else if (ae.getSource() == view.btnCardNext) {
			if (view.btnCardNext.getText() == "Done") {
				view.dispose();
			} else {
				view.btnCardBack.setVisible(true);
				view.cardLayoutContentContainer.next(view.panelContentContainer);
			}
		} else if (ae.getSource() == view.btnAddFiles) {
			addFiles();
		} else if (ae.getSource() == view.btnRemoveFiles) {
			removeFiles();
		} else if (ae.getSource() == view.btnClearFileList) {
			clearList();
		} else if (ae.getSource() == view.btnSelectAll) {
			view.checkBoxListStores.selectAll();
		} else if (ae.getSource() == view.btnDeselectAll) {
			view.checkBoxListStores.deselectAll();
		}
	}

	@Override
	public void componentShown(ComponentEvent ce) {
		if (ce.getSource() == view.panelCardSourceFiles) {
			view.btnCardBack.setVisible(false);
		} else if (ce.getSource() == view.panelCardTargetStores) {
			view.checkBoxListStores.setListData(stores);
		} else if (ce.getSource() == view.panelCardSummary) {
			view.btnCardNext.setText("Import");
			initSelectedStoreList();
			view.fileListSourceFilesSummary.setListData(psdFileList);
			view.fileListSourceFilesSummary.setEnabled(false);
			view.storeListTargetStoresSummary.setListData(selectedStores);
			view.storeListTargetStoresSummary.setEnabled(false);
		} else if (ce.getSource() == view.panelCardProcessing) {
			view.btnCardBack.setVisible(false);
			view.btnCardNext.setEnabled(false);
			Thread t = new Thread() {
				@Override
				public void run() {
					process();
				}
			};
			t.start();
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

}
