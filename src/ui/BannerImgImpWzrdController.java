package ui;

import static org.apache.commons.io.FileUtils.copyFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ListIterator;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

// import com.enterprisedt.net.ftp.FTPException;

import model.prototype.ImageSize;
import model.prototype.StoreDataModel;
import model.singleton.FtpClientModel;
import model.singleton.ImageHandler;
import model.singleton.PropertiesModel;

public class BannerImgImpWzrdController implements ActionListener, ComponentListener {

	private BannerImgImpWzrdView view;
	private FtpClientModel ftp;
	private PropertiesModel propApp;
	private Vector<StoreDataModel> stores;
	private Vector<StoreDataModel> selectedStores;
	private File[] psdFiles;
	private Vector<File> psdFileList = new Vector<File>();
	Vector<ImageSize> imageSizeList = new Vector<ImageSize>();

	public BannerImgImpWzrdController() {
		initProperties();
		initView();
		initStores();
	}

	public BannerImgImpWzrdController(Vector<File> psdFileList) {
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

		view = new BannerImgImpWzrdView(this);
		view.setVisible(true);
	}

	public void initStores() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();
		stores = new Vector<StoreDataModel>();
		
		try
		{
			for (File file : files) {
				if (!file.isDirectory() && FilenameUtils.isExtension(file.getName(), "properties")) {
					Configuration config = new PropertiesConfiguration(file);
					String[] imgSizeParams = config.getStringArray("banner.image.size");
					for(String param : imgSizeParams) {
						imageSizeList.add(new ImageSize(param.split(",")));
						System.out.println(new ImageSize(param.split(",")).getName());
						System.out.println("Groesse: " + imageSizeList.size() + " - " + imageSizeList.lastElement().getWidth());
					}
					//System.out.println("imageSizeList.size() " + imageSizeList.size() + " - " + imageSizeList.get(0).getName());
					/*stores.add(new StoreDataModel(config.getString("url"), config.getString("ftp.host"),
							Integer.parseInt(config.getString("ftp.port")), config.getString("ftp.user"),
							config.getString("ftp.pswd"), imageSizeList));*/
					stores.add(new StoreDataModel(config.getString("url"), config.getString("ftp.host"),
							Integer.parseInt(config.getString("ftp.port")), config.getString("ftp.protocol"), config.getString("ftp.user"),
							config.getString("ftp.pswd"), config.getList("banner.image.size")));
				}
				imageSizeList.clear();
			}
		}
		catch (ConfigurationException cex)
		{
		    // Something went wrong
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
				FTPClient f = new FTPClient();
			    f.connect(store.getStoreFtpServer());
			    f.login(store.getStoreFtpUser(), store.getStoreFtpPass());
			    f.setFileType(FTP.BINARY_FILE_TYPE);
			    
				for (File psdFile : psdFileList) {
					
				// COPY PSD FILE TO ORIGINALS FOLDER
					copyFile(psdFile,
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals")
									+ view.textFieldBannerFileName.getText() + "/"
									+ view.textFieldBannerFileName.getText() + currentDate + "."
									+ FilenameUtils.getExtension(psdFile.getName())));
				
				// GET BUFFEREDIMAGE FROM PSD FILE
					img = imgHandler.getImageFromPsd(psdFile);
					
					progressThumbUpdate(imgHandler.resizeImage(100, 100, img));
					for (ImageSize imgSize : store.getStoreImageSizeListNew()) {

					// RESIZE BUFFEREDIMAGE
						progressLabelUpdate("Resize " + FilenameUtils.getBaseName(psdFile.getName()) + " to "
								+ imgSize.getWidth() + "px");
						BufferedImage scaledImage = imgHandler.resizeImage(imgSize.getWidth(),
								imgSize.getHeight(), img);

					// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
						progressLabelUpdate("Remove Alpha Channel from " + FilenameUtils.getBaseName(psdFile.getName())
								+ " (" + imgSize.getWidth() + "px)");
						BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

					// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
						File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
								+ imgSize.getName() + "/" + view.textFieldBannerFileName.getText() + "/");
						if (!directory.exists()) {
							directory.mkdirs();
						}

						imgFile = new File(
								directory.getPath() + "/" + view.textFieldBannerFileName.getText() + ".jpg");
						ImageIO.write(rgbImage, "jpg", imgFile);

					// UPLOAD TO WEBSERVER 
						progressLabelUpdate("Upload " + FilenameUtils.getBaseName(psdFile.getName()) + " ("
								+ imgSize.getName() + ") to " + store.getStoreName());

						File remoteFile = new File(imgSize.getName() + "/" + imgFile.getName());
						
						// VIA FTP
						if (store.getStoreFtpProtocol().equals("ftp")) {
							if (!f.isConnected()) {
								f.connect(store.getStoreFtpServer());
							}
							System.out.println("here");
							InputStream input = new FileInputStream(imgFile);
							f.mkd(imgSize.getName());
							f.changeWorkingDirectory(imgSize.getName());
							f.storeFile(remoteFile.getName(), input);
							f.changeToParentDirectory();
							
						// VIA SFTP
						} else if (store.getStoreFtpProtocol().equals("sftp")) {
							if (!ftp.session.isConnected()) {
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
