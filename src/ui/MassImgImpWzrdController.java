package ui;

import static org.apache.commons.io.FileUtils.copyFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.csv.CSVParser;
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
import model.singleton.SFTPClientModel;

public class MassImgImpWzrdController implements ActionListener, ComponentListener {

	private MassImgImpWzrdView view;
	private FtpClientModel ftp;
	private PropertiesModel propApp;
	private Vector<StoreDataModel> stores;
	private Vector<StoreDataModel> selectedStores;
	private File psdFilesPath;
	private Vector<File> psdFileList = new Vector<File>();
	Vector<ImageSize> imageSizeList = new Vector<ImageSize>();

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

	public void initBannerDim() {

	}

	public void initStores() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();
		stores = new Vector<StoreDataModel>();

		try {
			for (File file : files) {
				if (!file.isDirectory() && FilenameUtils.isExtension(file.getName(), "properties")) {
					Configuration config = new PropertiesConfiguration(file);
					/*String[] imgSizeParams = config.getStringArray("banner.image.size");
					for (String param : imgSizeParams) {
						imageSizeList.add(new ImageSize(param.split(",")));
						System.out.println(new ImageSize(param.split(",")).getName());
						System.out.println(
								"Groesse: " + imageSizeList.size() + " - " + imageSizeList.lastElement().getWidth());
					}*/
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
							config.getList("product.image.size")));
				}
				imageSizeList.clear();
			}
		} catch (ConfigurationException cex) {
			// Something went wrong
		}
	}

	public void readCsv(File csvFile) {
		String productID;
		Scanner scanner;
		try {
			scanner = new Scanner(csvFile);
			while (scanner.hasNext()) {
				productID = scanner.next();
				File psdFile = new File(psdFilesPath.getAbsolutePath() + File.separatorChar + productID);
				if (psdFile.isDirectory() && psdFile.exists()) {
					psdFileList.add(psdFile);
					for (File psdFileAdditional : getPsdFileAdditionals(productID, psdFilesPath)) {
						psdFileList.add(psdFileAdditional);
					}
				}
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
				
				FTPClient ftp = null;
				SFTPClientModel sftp = null;
				
				if(store.getStoreFtpProtocol().equals("ftp")) {
	            		ftp = new FTPClient();
	            		ftp.connect(store.getStoreFtpServer());
	    				ftp.login(store.getStoreFtpUser(), store.getStoreFtpPass());
	    				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				} else if(store.getStoreFtpProtocol().equals("sftp")) {
	            	sftp = new SFTPClientModel(store.getStoreFtpServer(), store.getStoreFtpPort(), store.getStoreFtpUser(), store.getStoreFtpPass(), store.getDirDefault());
				}
				
				for (File psdFiles : psdFileList) {
						File[] psdFileVersionSort = sortByNumber(psdFiles.listFiles());
						File psdFile = psdFileVersionSort[psdFileVersionSort.length - 1];
						// COPY PSD FILE TO ORIGINALS FOLDER
						/*
						 * copyFile(psdFile, new File(propApp.get("locMediaBackup") +
						 * propApp.get("mediaBackupDirOriginals") +
						 * FilenameUtils.getBaseName(psdFile.getName()) + "/" +
						 * FilenameUtils.getBaseName(psdFile.getName()) + currentDate + "." +
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

							// UPLOAD TO WEBSERVER
							progressLabelUpdate("Upload " + FilenameUtils.getBaseName(psdFiles.getName()) + " ("
									+ imgSize.getName() + ") to " + store.getStoreName());

							File remoteFile = new File(imgSize.getName() + "/" + imgFile.getName());

							// VIA FTP
							if (store.getStoreFtpProtocol().equals("ftp")) {
								if (!ftp.isConnected()) {
									ftp.connect(store.getStoreFtpServer());
								}
								InputStream input = new FileInputStream(imgFile);
								ftp.mkd(imgSize.getName());
								ftp.changeWorkingDirectory(imgSize.getName());
								ftp.storeFile(remoteFile.getName(), input);
								ftp.changeToParentDirectory();

								// VIA SFTP
							} else if (store.getStoreFtpProtocol().equals("sftpDEAKT")) {
								if (!sftp.session.isConnected()) {
									sftp.sftpConnect();
								}
								sftp.sftpUpload(imgFile, remoteFile);
							} else if (store.getStoreFtpProtocol().equals("sftp")) {
								if (!sftp.session.isConnected()) {
									sftp.sftpConnect();
								}
								sftp.sftpUpload(imgFile, remoteFile);
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
			addFilesPath();
		} else if (ae.getSource() == view.btnRemoveFiles) {
			removeFiles();
		} else if (ae.getSource() == view.btnClearFileList) {
			clearList();
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
