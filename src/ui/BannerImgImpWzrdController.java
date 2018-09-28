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
import java.util.List;
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

import model.prototype.BannerModel;

// import com.enterprisedt.net.ftp.FTPException;

import model.prototype.ImageSize;
import model.prototype.StoreDataModel;
import model.singleton.FtpClientModel;
import model.singleton.ImageHandler;
import model.singleton.PropertiesModel;
import model.singleton.SFTPClientModel;
import psd.model.Layer;
import psd.model.Psd;

public class BannerImgImpWzrdController implements ActionListener, ComponentListener {

	private BannerImgImpWzrdView view;
	private PropertiesModel propApp;
	private Vector<StoreDataModel> stores;
	private Vector<StoreDataModel> selectedStores;
	private File srcFile;
	private Vector<File> psdFileList = new Vector<File>();
	private Vector<ImageSize> imageSizeList = new Vector<ImageSize>();
	private Vector<BannerModel> bannerTemplates = new Vector<BannerModel>();
	private Vector<BannerModel> selectedBannerTemplates = new Vector<BannerModel>();
	private BufferedImage srcImage;
	private FTPClient ftp = null;
	private SFTPClientModel sftp = null;

	public BannerImgImpWzrdController() {
		initProperties();
		initView();
		initBannerDim();
		initStores();
	}

	public BannerImgImpWzrdController(File psdFile) {
		initProperties();
		initBannerDim();
		initView();
		initStores();
		this.srcFile = psdFile;
		view.fileListSourceFiles.setText(psdFile.getAbsolutePath());
	}

	private void initProperties() {
		propApp = new PropertiesModel();
		propApp.loadAppProperties();
	}

	private void initView() {

		view = new BannerImgImpWzrdView(this);
		view.setVisible(true);
	}

	public void initBannerDim() {
		System.out.println("asdasd" + propApp.get("locNetworkRes"));
		File filePropBanner = new File(propApp.get("locNetworkRes") + "banner" + "/" + "banner.properties");
		try {
			Configuration config = new PropertiesConfiguration(filePropBanner);
			List<Object> templates = config.getList("template");
			Configuration templateProps;
			for (Object template : templates) {
				templateProps = config.subset(template.toString());
				bannerTemplates.add(new BannerModel(template.toString(), templateProps));
				System.out.println(bannerTemplates.lastElement().getDimSM().width);
			}
			view.listBannerModels.setListData(bannerTemplates);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void initStores() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();
		stores = new Vector<StoreDataModel>();

		try {
			for (File file : files) {
				if (!file.isDirectory() && FilenameUtils.isExtension(file.getName(), "properties")) {
					Configuration config = new PropertiesConfiguration(file);
					/*
					 * String[] imgSizeParams = config.getStringArray("banner.image.size"); for
					 * (String param : imgSizeParams) { imageSizeList.add(new
					 * ImageSize(param.split(","))); System.out.println(new
					 * ImageSize(param.split(",")).getName()); System.out.println( "Groesse: " +
					 * imageSizeList.size() + " - " + imageSizeList.lastElement().getWidth()); }
					 */
					// System.out.println("imageSizeList.size() " + imageSizeList.size() + " - " +
					// imageSizeList.get(0).getName());
					/*
					 * stores.add(new StoreDataModel(config.getString("url"),
					 * config.getString("ftp.host"), Integer.parseInt(config.getString("ftp.port")),
					 * config.getString("ftp.user"), config.getString("ftp.pswd"), imageSizeList));
					 */
					stores.add(new StoreDataModel(config.getString("url"), config.getString("ftp.host"),
							Integer.parseInt(config.getString("ftp.port")), config.getString("ftp.protocol"),
							config.getString("ftp.user"), config.getString("ftp.pswd"),
							config.getString("ftp.dir.default"), config.getList("product.image.size")));
				}
			}
		} catch (ConfigurationException cex) {
			// Something went wrong
		}
	}

	public void parsePsdLayers() {

		// view.labelPreviewPsdImage.setIcon(new
		// ImageIcon(Sanselan.getBufferedImage(psdFile)));
		Psd psd;
		Layer layer;
		try {
			psd = new Psd(srcFile);
			for (int i = 0; i <= psd.getLayersCount(); i++) {
				layer = psd.getLayer(i);
				view.labelPreviewPsdImage.setIcon(new ImageIcon(layer.getImage()));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void process() {

		String bannerName = view.textFieldBannerFileName.getText();
		ImageHandler imgHandler = new ImageHandler();
		File imgFile;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("_yyyyMMdd");
		String currentDate = LocalDate.now().toString(fmt);

		double progressBarMax = 100;
		double progressSteps = 3;
		double progressStepSizef = progressBarMax / progressSteps;
		int progressStepSize = (int) Math.round(progressStepSizef);

		try {

			for (StoreDataModel store : selectedStores) {

				if (store.getStoreFtpProtocol().equals("ftp")) {
					ftp = new FTPClient();
					ftp.connect(store.getStoreFtpServer());
					ftp.login(store.getStoreFtpUser(), store.getStoreFtpPass());
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
				} else if (store.getStoreFtpProtocol().equals("sftp")) {
					sftp = new SFTPClientModel(store.getStoreFtpServer(), store.getStoreFtpPort(),
							store.getStoreFtpUser(), store.getStoreFtpPass(), store.getDirDefault());
					sftp.connect();
				}

				// COPY PSD FILE TO ORIGINALS FOLDER
				copyFile(srcFile, new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals")
						+ bannerName + currentDate + "." + FilenameUtils.getExtension(srcFile.getName())));

				// GET BUFFEREDIMAGE FROM PSD FILE
				// img = imgHandler.getImageFromPsd(psdFile);

				progressThumbUpdate(imgHandler.resizeImage(100, 100, srcImage));
				for (BannerModel banner : selectedBannerTemplates) {

					Vector<BufferedImage> scaledImages = new Vector<BufferedImage>();
					scaledImages.clear();
					
					// RESIZE BUFFEREDIMAGE
					progressLabelUpdate("Resize " + FilenameUtils.getBaseName(srcFile.getName()) + " to "
							+ banner.getDimSM().width + " " + banner.getDimSM().height + " px");
					
					if(banner.getDimSM()!=null || banner.getDimSM().width >= 0) {
						BufferedImage scaledImageSM = imgHandler.resizeImage(banner.getDimSM().width,
						banner.getDimSM().height, srcImage);
						scaledImages.add(scaledImageSM);
					}
					if(banner.getDimMD()!=null || banner.getDimMD().width >= 0) {
						BufferedImage scaledImageMD = imgHandler.resizeImage(banner.getDimMD().width,
						banner.getDimMD().height, srcImage);
					scaledImages.add(scaledImageMD);
					}
					if(banner.getDimLG()!=null || banner.getDimLG().width >= 0) {
						BufferedImage scaledImageLG = imgHandler.resizeImage(banner.getDimLG().width,
							banner.getDimLG().height, srcImage);
						scaledImages.add(scaledImageLG);
					}
					//BufferedImage[] scaledImages = { scaledImageSM, scaledImageMD, scaledImageLG };
					
					// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
					File[] directories = {
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
									+ banner.getDirname() + "/" + "sm" + "/"),
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
									+ banner.getDirname() + "/" + "md" + "/"),
							new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
									+ banner.getDirname() + "/" + "lg" + "/") };
					for (int i = 0; i <= scaledImages.size(); i++) {
						System.out.println(directories[i].getPath());
						if (!directories[i].exists()) {
							directories[i].mkdirs();
						}

						imgFile = new File(directories[i].getPath() + "/" + bannerName + ".jpg");
						ImageIO.write(scaledImages.get(i), "jpg", imgFile);

						// UPLOAD TO (REMOTE-)WEBSERVER
						progressLabelUpdate("Upload " + FilenameUtils.getBaseName(srcFile.getName()) + " ("
								+ banner.getName() + ") to " + store.getStoreName());
						String remoteDir = banner.getDirname() + "/" + directories[i].getName();
						File remoteFile = new File(remoteDir + "/" + imgFile.getName());

						// USING FTP
						if (store.getStoreFtpProtocol().equals("ftp")) {
							if (!ftp.isConnected()) {
								ftp.connect(store.getStoreFtpServer());
							}
							InputStream input = new FileInputStream(imgFile);
							ftp.mkd(remoteDir);
							ftp.changeWorkingDirectory(remoteDir);
							ftp.storeFile(remoteFile.getName(), input);
							ftp.changeToParentDirectory();
							ftp.changeToParentDirectory();

							// USING SFTP
						} else if (store.getStoreFtpProtocol().equals("sftp")) {
							if (!sftp.session.isConnected()) {
								sftp.connect();
							}
							sftp.upload(imgFile, remoteFile);
						}
					}
				}
				progressBarUpdate(progressStepSize);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		progressBarUpdate(100);
		progressLabelUpdate("complete");
		view.btnCardNext.setEnabled(true);
		view.btnCardNext.setText("Done");
	}

	public File openFile() {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Select files (multiple selection possible)");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setLocation(100, 100);

		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		}
		return file;
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

	public void initSelectedBannerList() {
		selectedBannerTemplates = new Vector<BannerModel>();
		for (BannerModel banner : bannerTemplates) {
			if (banner.getSelectStatus())
				selectedBannerTemplates.add(banner);
		}
	}

	public void initSelectedStoreList() {
		selectedStores = new Vector<StoreDataModel>();
		for (StoreDataModel store : stores) {
			if (store.getSelectStatus())
				selectedStores.add(store);
		}
	}

	public void initSrcFile(File srcFile) {
		System.out.println(srcFile.getName());
		String fileExt = FilenameUtils.getExtension(srcFile.getName());
		System.out.println(fileExt);
		try {
			if (fileExt == "psd") {
				Psd psd = new Psd(srcFile);
				srcImage = psd.getImage();
				view.labelPreviewPsdImage.setIcon(new ImageIcon(srcImage));
			} else if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg")) {
				System.out.println(srcFile.getName());
				srcImage = ImageIO.read(srcFile);
				view.labelPreviewPsdImage.setIcon(new ImageIcon(srcImage));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			srcFile = openFile();
			System.out.println(srcFile.getName());
			initSrcFile(srcFile);
			view.fileListSourceFiles.setText(srcFile.getAbsolutePath());
			view.textFieldBannerFileName.setText(FilenameUtils.getBaseName(srcFile.getName()));
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
			initSelectedBannerList();
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
