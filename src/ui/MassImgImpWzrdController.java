package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

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
import model.singleton.ImageHandler;
import model.singleton.MultipartUtility;
import model.singleton.PropertiesModel;
import model.singleton.SFTPClientModel;

public class MassImgImpWzrdController implements ActionListener, ComponentListener {

	private MassImgImpWzrdView view;
	private PropertiesModel propApp;
	private Vector<StoreDataModel> stores;
	private Vector<StoreDataModel> selectedStores;
	private File psdFilesPath;
	private Vector<File> psdFileList = new Vector<File>();
	private Vector<String> psdStringList = new Vector<String>();
	private Vector<ImageSize> imageSizeList = new Vector<ImageSize>();
	private FTPClient ftp = null;
	private SFTPClientModel sftp = null;
	private int psdParseError;

	public MassImgImpWzrdController() {
		initProperties();
		initView();
		initStores();
	}

	public MassImgImpWzrdController(Vector<File> psdFileList) {
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

		view = new MassImgImpWzrdView(this);
		view.setVisible(true);
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

	public void readCsvOld(File csvFile) {
		String productID;
		Scanner scanner;
		try {
			scanner = new Scanner(csvFile);
			while (scanner.hasNext()) {
				System.out.println("mark 1");
				productID = scanner.next();
				System.out.println(psdFilesPath.getAbsolutePath() + File.separatorChar + productID);
				File psdFile = new File(psdFilesPath.getAbsolutePath() + File.separatorChar + productID);
				System.out.println("mark 2");
				if (psdFile.isDirectory() && psdFile.exists()) {
					System.out.println("mark 3");
					psdFileList.add(psdFile);
					// System.out.println("mark 4");
					// psdFileList.addAll(Arrays.asList(getPsdFileAdditionals(productID,
					// psdFilesPath)));
					System.out.println("mark 4");
					for (File psdFileAdditional : getPsdFileAdditionals(productID, psdFilesPath)) {
						System.out.println("mark 5.1");
						psdFileList.add(psdFileAdditional);
						System.out.println("mark 5.2");
					}
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void readCsv(File csvFile) {
		String productID;
		Scanner scanner;
		psdStringList.clear();

		try {
			scanner = new Scanner(csvFile);
			while (scanner.hasNext()) {
				productID = scanner.next();
				psdStringList.add(productID);
				System.out.println(productID);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public File[] getPsdFileAdditionals(final String productID, File psdFilesPath) {
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.matches(".*(" + productID + "_)+.*");
			}
		};
		File[] files = psdFilesPath.listFiles(filter);
		return files;
	}

	public File[] sortByNumber(File[] files) {
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				int n1 = extractNumber(o1.getName());
				int n2 = extractNumber(o2.getName());
				return n1 - n2;
			}

			private int extractNumber(String name) {
				int i = 0;
				try {
					int s = name.indexOf('_') + 1;
					int e = name.lastIndexOf('.');
					String number = name.substring(s, e);
					i = Integer.parseInt(number);
				} catch (Exception e) {
					i = 0; // if filename does not match the format
							// then default to 0
				}
				return i;
			}
		});

		for (File f : files) {
			System.out.println(f.getName());
		}
		return files;
	}

	public File getLatestPSDFileVersion(File psdFiles, String productID) {
		// GET LATEST PSD VERSION (VIA FILENAME ATTACHMENT '_YYmmdd' (Date))
		// File[] psdFileVersionSort = sortByNumber(psdFiles.listFiles());
		File[] psdFileVersionSort = psdFiles.listFiles();
		Arrays.sort(psdFileVersionSort);
		if (psdFileVersionSort.length == 0 || psdFileVersionSort == null) {
			System.out.println(productID + "," + "no file");
			return null;
		} else if (psdFileVersionSort[psdFileVersionSort.length - 1].length() == 0) {
			System.out.println(productID + "," + "empty file");
			return null;
		} else {
			return psdFileVersionSort[psdFileVersionSort.length - 1];
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

		for (StoreDataModel store : selectedStores) {
			try {
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

				for (File psdFiles : psdFileList) {
					try {
						// GET LATEST PSD VERSION (VIA FILENAME ATTACHMENT '_YYmmdd' (Date))
						File[] psdFileVersionSort = sortByNumber(psdFiles.listFiles());
						File psdFile = psdFileVersionSort[psdFileVersionSort.length - 1];

						// COPY PSD FILE TO ORIGINALS FOLDER
						/*
						 * FileUtils.copyFile(psdFile, new File(propApp.get("locMediaBackup") +
						 * propApp.get("mediaBackupDirOriginals") +
						 * FilenameUtils.getBaseName(psdFiles.getName()) + "/" +
						 * FilenameUtils.getBaseName(psdFiles.getName()) + currentDate + "." +
						 * FilenameUtils.getExtension(psdFile.getName())));
						 */

						// GET BUFFEREDIMAGE FROM PSD FILE
						img = imgHandler.getImageFromPsd(psdFile);

						// Show 100x100px thumb of current file in wizard
						progressThumbUpdate(imgHandler.resizeImage(100, 100, img));

						for (ImageSize imgSize : store.getStoreImageSizeListNew()) {

							// RESIZE BUFFEREDIMAGE
							progressLabelUpdate("Resize " + FilenameUtils.getBaseName(psdFiles.getName()) + " to "
									+ imgSize.getWidth() + "px");
							BufferedImage scaledImage = imgHandler.resizeImage(imgSize.getWidth(), imgSize.getHeight(),
									img);

							// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
							progressLabelUpdate("Remove Alpha Channel from "
									+ FilenameUtils.getBaseName(psdFile.getName()) + " (" + imgSize.getWidth() + "px)");
							BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

							// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
							File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
									+ imgSize.getName());
							if (!directory.exists()) {
								directory.mkdirs();
							}

							imgFile = new File(
									directory.getPath() + "/" + FilenameUtils.getBaseName(psdFiles.getName()) + ".jpg");
							ImageIO.write(rgbImage, "jpg", imgFile);

							// UPLOAD TO (REMOTE-)WEBSERVER
							progressLabelUpdate("Upload " + FilenameUtils.getBaseName(psdFiles.getName()) + " ("
									+ imgSize.getName() + ") to " + store.getStoreName());

							File remoteFile = new File(imgSize.getName() + "/" + imgFile.getName());

							// USING FTP
							if (store.getStoreFtpProtocol().equals("ftp")) {
								if (!ftp.isConnected()) {
									ftp.connect(store.getStoreFtpServer());
								}
								InputStream input = new FileInputStream(imgFile);
								ftp.mkd(imgSize.getName());
								ftp.changeWorkingDirectory(imgSize.getName());
								ftp.storeFile(remoteFile.getName(), input);
								ftp.changeToParentDirectory();

								// USING SFTP
							} else if (store.getStoreFtpProtocol().equals("sftp")) {
								if (!sftp.session.isConnected()) {
									sftp.connect();
								}
								sftp.upload(imgFile, remoteFile);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					progressBarUpdate(progressStepSize);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		progressBarUpdate(100);
		progressLabelUpdate("complete");
		view.btnCardNext.setEnabled(true);
		view.btnCardNext.setText("Done");
	}

	public void processPsdFiles() {
		psdParseError = 0;
		File psdFile;
		view.progressBar.setValue(0);
		view.progressBar.setMaximum(psdStringList.size() * selectedStores.size());
		view.progressBar.setString(view.progressBar.getValue() + "/" + view.progressBar.getMaximum());

		for (StoreDataModel store : selectedStores) {

			for (String productID : psdStringList) {
				psdFileList.clear();
				psdFile = new File(psdFilesPath.getAbsolutePath() + File.separatorChar + productID);
				if (psdFile.isDirectory() && psdFile.exists()) {
					psdFileList.add(psdFile);
					// psdFileList.addAll(Arrays.asList(getPsdFileAdditionals(productID,
					// psdFilesPath)));
					/*
					 * for (File psdFileAdditional : getPsdFileAdditionals(productID, psdFilesPath))
					 * { psdFileList.add(psdFileAdditional); }
					 */
					int count = 1;
					File psdFileAdditional = new File(
							psdFilesPath.getAbsolutePath() + File.separatorChar + productID + "_" + count);
					System.out.println(productID + "," + "folder exists");
					while (psdFileAdditional.exists() && psdFileAdditional.isDirectory()) {
						psdFileList.add(psdFileAdditional);
						count++;
						psdFileAdditional = new File(
								psdFilesPath.getAbsolutePath() + File.separatorChar + productID + "_" + count);
					}
				}
				for (File psdFiles : psdFileList) {
					// GET LATEST PSD VERSION (VIA FILENAME ATTACHMENT '_YYmmdd' (Date))
					File psdFileLatestVersion = getLatestPSDFileVersion(psdFiles, productID);
					if (psdFileLatestVersion != null) {
						// START BUILDING IMAGE
						buildImage(psdFiles.getName(), psdFileLatestVersion, store);
					}
				}
				progressBarUpdate(1);
			}
		}
		progressBarUpdate(100);
		progressLabelUpdate("complete");
		view.btnCardNext.setEnabled(true);
		view.btnCardNext.setText("Done");
	}

	public void processJpegFiles() {
		File jpegFile;
		ImageHandler imgHandler = new ImageHandler();
		view.progressBar.setValue(0);
		view.progressBar.setMaximum(psdStringList.size() * selectedStores.size());
		view.progressBar.setString(view.progressBar.getValue() + "/" + view.progressBar.getMaximum());

		for (StoreDataModel store : selectedStores) {
			for (String productID : psdStringList) {

				jpegFile = new File(psdFilesPath.getAbsolutePath() + File.separatorChar + productID + ".jpg");
				try {
					BufferedImage srcImage = ImageIO.read(jpegFile);

					if (srcImage != null) {

						// Show 100x100px thumb of current file in wizard
						progressThumbUpdate(imgHandler.resizeImage(100, 100, srcImage));

						for (ImageSize imgSize : store.getStoreImageSizeListNew()) {

							// RESIZE BUFFEREDIMAGE
							progressLabelUpdate("Resize " + FilenameUtils.getBaseName(jpegFile.getName()) + " to "
									+ imgSize.getWidth() + "px");
							BufferedImage scaledImage = imgHandler.resizeImage(imgSize.getWidth(), imgSize.getHeight(),
									srcImage);

							// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
							File directory = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive")
									+ imgSize.getName());
							if (!directory.exists()) {
								directory.mkdirs();
							}

							File imgFile = new File(directory.getPath() + "/" + jpegFile.getName() + ".jpg");
							ImageIO.write(scaledImage, "jpg", imgFile);
						}
					}
					progressBarUpdate(1);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void buildImage(String fileName, File psdFile, StoreDataModel store) {
		System.out.println(fileName + " --> " + psdFile.getName());
		ImageHandler imgHandler = new ImageHandler();
		File imgFile = null;
		BufferedImage img;
		DateTimeFormatter fmt = DateTimeFormat.forPattern("_yyyyMMdd");
		String currentDate = LocalDate.now().toString(fmt);

		try {
			img = imgHandler.getImageFromPsd2(psdFile);
			if (img != null) {

				// Show 100x100px thumb of current file in wizard
				progressThumbUpdate(imgHandler.resizeImage(100, 100, img));

				for (ImageSize imgSize : store.getStoreImageSizeListNew()) {

					// RESIZE BUFFEREDIMAGE
					progressLabelUpdate(
							"Resize " + FilenameUtils.getBaseName(fileName) + " to " + imgSize.getWidth() + "px");
					BufferedImage scaledImage = imgHandler.resizeImage(imgSize.getWidth(), imgSize.getHeight(), img);

					// REMOVE ALPHA CHANNEL FROM BUFFEREDIMAGE ( ARGB -> RGB )
					progressLabelUpdate("Remove Alpha Channel from " + FilenameUtils.getBaseName(fileName) + " ("
							+ imgSize.getWidth() + "px)");
					BufferedImage rgbImage = imgHandler.removeAlphaChannel(scaledImage);

					// WRITE IMAGE FILE TO MEDIA/LIVE FOLDER
					File directory = new File(
							propApp.get("locMediaBackup") + propApp.get("mediaBackupDirLive") + imgSize.getName());
					if (!directory.exists()) {
						directory.mkdirs();
					}

					if (view.btnGrpImageFormat.getSelection().getActionCommand().equals("PSD")
							&& view.btnGrpImageFormat.getSelection().getActionCommand() != null) {
						imgFile = new File(directory.getPath() + "/" + fileName + ".jpg");
					} else if (view.btnGrpImageFormat.getSelection().getActionCommand().equals("JPEG")) {
						imgFile = new File(directory.getPath() + "/" + fileName);
					}

					ImageIO.write(rgbImage, "jpg", imgFile);
				}
			}
		} catch (IOException e) {
			psdParseError++;
			System.out.println(psdParseError);
			e.printStackTrace();
		} finally {
			System.out.println(psdParseError);
		}
	}

	public void imageOptim(File file) {
		String charset = "UTF-8";
		File uploadFile = file;
		String requestURL = "https://im2.io/kzrbgzzbfm/full";

		try {
			MultipartUtility multipart = new MultipartUtility(requestURL, charset);

			multipart.addHeaderField("User-Agent", "Promeda");
			multipart.addHeaderField("Test-Header", "Header-Value");

			multipart.addFormField("description", "Cool Pictures");
			multipart.addFormField("keywords", "image,compression,imagemin");

			multipart.addFilePart("fileUpload", uploadFile);

			List<String> response = multipart.finish();

			System.out.println("SERVER REPLIED:");

			for (String line : response) {
				System.out.println(line);
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public static String executePost(String targetURL, String urlParameters) {
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			// connection.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Type", "multipart/form-data");
			connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			System.out.println(response.toString());
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static void fireHTTPRequest(String request) {
		URL yahoo;
		try {
			yahoo = new URL("http://www.yahoo.com/");
			URLConnection yc = yahoo.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public File chooseFile() {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Select files (multiple selection possible)");
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileNameExtensionFilter(".txt, .csv", new String[] { "txt", "csv" }));
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

	public File chooseDir() {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Select the directory containing the PSD Files");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setLocation(100, 100);
		fileChooser
				.setCurrentDirectory(new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals")));
		int returnVal = fileChooser.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		}
		return file;
	}

	public void addFilesPath() {
		psdFilesPath = chooseDir();
		view.textFieldPsdFilesPath.setText(psdFilesPath.getAbsolutePath());
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
		view.progressBar.setString(view.progressBar.getValue() + "/" + view.progressBar.getMaximum());
		view.labelLoadManMoving.setLocation(view.progressBar.getValue(), 95);
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
		} else if (ae.getSource() == view.btnSelectAll) {
			view.checkBoxListStores.selectAll();
		} else if (ae.getSource() == view.btnDeselectAll) {
			view.checkBoxListStores.deselectAll();
		} else if (ae.getSource() == view.btnBrowseCsvFile) {
			File productsCsv = chooseFile();
			view.textFieldProductsCsv.setText(productsCsv.getAbsolutePath());
			readCsv(productsCsv);
		} else if (ae.getSource() == view.btnBrowsePsdFilesPath) {
			addFilesPath();
		}
	}

	@Override
	public void componentShown(ComponentEvent ce) {
		if (ce.getSource() == view.panelCardSourceFiles) {
			view.btnCardBack.setVisible(false);
		} else if (ce.getSource() == view.panelCardTargetStores) {
			view.checkBoxListStores.setListData(stores);
			System.out.println(view.btnGrpImageFormat.getSelection().getActionCommand());
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
			if (view.btnGrpImageFormat.getSelection().getActionCommand().equals("PSD")
					&& view.btnGrpImageFormat.getSelection().getActionCommand() != null) {
				Thread t = new Thread() {
					@Override
					public void run() {
						processPsdFiles();
					}
				};
				t.start();

			} else if (view.btnGrpImageFormat.getSelection().getActionCommand().equals("JPEG")) {
				Thread t = new Thread() {
					@Override
					public void run() {
						processJpegFiles();
					}
				};
				t.start();
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
