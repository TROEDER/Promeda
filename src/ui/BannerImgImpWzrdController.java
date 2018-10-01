package ui;

import static org.apache.commons.io.FileUtils.copyFile;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.ListModel;

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
	private Vector<File> srcFileList = new Vector<File>();
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
		File filePropBanner = new File(propApp.get("locNetworkRes") + "banner" + File.separator + "banner.properties");
		try {
			Configuration config = new PropertiesConfiguration(filePropBanner);
			List<Object> templates = config.getList("template");
			Configuration templateProps;
			for (Object template : templates) {
				templateProps = config.subset(template.toString());
				bannerTemplates.add(new BannerModel(template.toString(), templateProps));
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

					for (Entry<String, Dimension> dim : banner.getDimensions().entrySet()) {
						// RESIZE BUFFEREDIMAGE
						progressLabelUpdate("Resize " + FilenameUtils.getBaseName(srcFile.getName()) + " to "
								+ dim.getValue().width + " " + dim.getValue().height + " px");

						// RESIZE BUFFEREDIMAGE
						progressLabelUpdate("Resize " + FilenameUtils.getBaseName(srcFile.getName()) + " to "
								+ dim.getValue().width + " " +  dim.getValue().height + " px");
						BufferedImage scaledImage = imgHandler.resizeImage(dim.getValue().width,
								dim.getValue().height, srcImage);

						// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
						File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
								+ banner.getDirname() + File.separator + dim.getKey());
						if (!directory.exists()) {
							directory.mkdirs();
						}

						imgFile = new File(directory.getPath() + File.separator + bannerName + ".jpg");
						ImageIO.write(scaledImage, "jpg", imgFile);

						// UPLOAD TO (REMOTE-)WEBSERVER
						progressLabelUpdate("Upload " + bannerName + " ("
								+ banner.getName() + ") to " + store.getStoreName());

						File remoteFile = new File(banner.getDirname() + "/" + dim.getKey() + "/" + imgFile.getName());

						// USWING FTP
						if (store.getStoreFtpProtocol().equals("ftp")) {
							if (!ftp.isConnected()) {
								ftp.connect(store.getStoreFtpServer());
							}
							InputStream input = new FileInputStream(imgFile);
							ftp.mkd(banner.getDirname());
							ftp.changeWorkingDirectory(banner.getDirname());
							ftp.mkd(dim.getKey());
							ftp.changeWorkingDirectory(dim.getKey());
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

		} catch (

		IOException e) {
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
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
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
			srcFileList.add(srcFile);
			// srcFileList = new Vector<File>(Arrays.asList(openFiles()));
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
			view.fileListSourceFilesSummary.setListData(srcFileList);
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
