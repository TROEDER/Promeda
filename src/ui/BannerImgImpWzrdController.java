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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
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
import model.singleton.ImageHandler;
import model.singleton.PropertiesModel;
import model.singleton.SFTPClientModel;
import psd.model.Layer;
import psd.model.Psd;

public class BannerImgImpWzrdController extends ImportController implements ActionListener, ComponentListener  {

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
	private ImageHandler imgHandler = new ImageHandler();

	public BannerImgImpWzrdController() {
		initProperties();
		initView();
		// initBannerDim();
		initStores();
	}

	public BannerImgImpWzrdController(File psdFile) {
		initProperties();
		// initBannerDim();
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
		Dimension srcImageSize = new Dimension(srcImage.getWidth(), srcImage.getHeight());
		System.out.println("asdasd" + propApp.get("locNetworkRes"));
		File filePropBanner = new File(propApp.get("locNetworkRes") + "banner" + File.separator + "banner.properties");
		try {
			Configuration config = new PropertiesConfiguration(filePropBanner);
			List<Object> templates = config.getList("template");
			Configuration templateProps;
			for (Object template : templates) {
				templateProps = config.subset(template.toString());
				bannerTemplates.add(new BannerModel(template.toString(), srcImageSize, templateProps));
			}
			updateBannerTemplateList();
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
							config.getString("ftp.dir.banner"), config.getList("product.image.size")));
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
		imgHandler = new ImageHandler();
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
					ftp.cwd(store.getDirDefault());
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
						BufferedImage scaledImage = imgHandler.resizeImage(dim.getValue().width, dim.getValue().height,
								srcImage);

						// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
						progressLabelUpdate("Remove Alpha Channel from " + FilenameUtils.getBaseName(srcFile.getName())
								+ dim.getValue().width + " " + dim.getValue().height + " px");
						BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

						// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
						File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
								+ banner.getDirname() + File.separator + dim.getKey());
						if (!directory.exists()) {
							directory.mkdirs();
						}

						imgFile = new File(directory.getPath() + File.separator + bannerName + ".jpg");
						// ImageIO.write(scaledImage, "jpg", imgFile);

						// COMPRESS and WRITE JPEG FILE
						compress(rgbImage, imgFile);

						// UPLOAD TO (REMOTE-)WEBSERVER
						progressLabelUpdate(
								"Upload " + bannerName + " (" + banner.getName() + ") to " + store.getStoreName());

						File remoteFile = new File(imgFile.getName());

						// USING FTP
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
							sftp.makeDir(banner.getDirname());
							sftp.changeDir(banner.getDirname());
							sftp.makeDir(dim.getKey());
							sftp.changeDir(dim.getKey());
							sftp.upload(imgFile, remoteFile);
							sftp.changeDir("..");
							sftp.changeDir("..");
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

	public void compress(BufferedImage srcImage, File destFile) throws IOException {
		OutputStream os = new FileOutputStream(destFile);
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
		ImageWriter writer = (ImageWriter) writers.next();

		ImageOutputStream ios = ImageIO.createImageOutputStream(os);
		writer.setOutput(ios);

		ImageWriteParam param = writer.getDefaultWriteParam();

		if (param.canWriteProgressive()) {
			param.setProgressiveMode(ImageWriteParam.MODE_DEFAULT);
		}

		if (param.canWriteCompressed()) {
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// param.setCompressionType("JPEG-LS");
			param.setCompressionQuality(0.85f); // Change the quality value you prefer
		}

		writer.write(writer.getDefaultStreamMetadata(param), new IIOImage(srcImage, null, null), param);

		os.close();
		ios.close();
		writer.dispose();
	}

	public void libJpegTurboCompress() {

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
		String fileExt = FilenameUtils.getExtension(srcFile.getName());
		try {
			if (fileExt.equalsIgnoreCase("psd") || fileExt.equalsIgnoreCase("psb")) {
				Psd psd = new Psd(srcFile);
				srcImage = psd.getImage();
				float factor = (float) 300 / (float) srcImage.getHeight();
				int newWidth = Math.round((float) srcImage.getWidth() * factor);
				ImageIcon iconHelper = new ImageIcon(imgHandler.resizeImage(newWidth, 300, srcImage));
				view.labelPreviewPsdImage.setIcon(iconHelper);
			} else if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("jpeg")) {
				srcImage = ImageIO.read(srcFile);
				float factor = (float) 300 / (float) srcImage.getHeight();
				int newWidth = Math.round((float) srcImage.getWidth() * factor);
				ImageIcon iconHelper = new ImageIcon(imgHandler.resizeImage(newWidth, 300, srcImage));
				view.labelPreviewPsdImage.setIcon(iconHelper);
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
			initBannerDim();
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
		} else if (ce.getSource() == view.panelCardImageOptions) {
//			updateBannerTemplateList();
//			view.listBannerModels.setListData(bannerTemplates);
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

	private void updateBannerTemplateList() {
		System.out.println("updateBannerList");
		for (BannerModel banner : bannerTemplates) {
			banner.setMatchSrcStatus(null);
			if (banner.getMatchSrcStatus() == null && banner.getDimensions().get("lg") != null) {
				System.out.println("updateBannerList");
				if (banner.getDimensions().get("lg").getWidth() == srcImage.getWidth()
						&& banner.getDimensions().get("lg").getHeight() == srcImage.getHeight()) {
					System.out.println("updateBannerList");
					banner.setMatchSrcStatus(true);
				} else {
					banner.setMatchSrcStatus(false);
				}
			} else if ((banner.getMatchSrcStatus() == null && banner.getDimensions().get("md") != null)) {
				if (banner.getDimensions().get("md").getWidth() == srcImage.getWidth()
						&& banner.getDimensions().get("md").getHeight() == srcImage.getHeight()) {
					banner.setMatchSrcStatus(true);
				} else {
					banner.setMatchSrcStatus(false);
				}
			} else if (banner.getMatchSrcStatus() == null && banner.getDimensions().get("sm") != null) {
				if (banner.getDimensions().get("sm").getWidth() == srcImage.getWidth()
						&& banner.getDimensions().get("sm").getHeight() == srcImage.getHeight()) {
					banner.setMatchSrcStatus(true);
				} else {
					banner.setMatchSrcStatus(false);
				}
			}
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
