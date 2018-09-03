package ui;

import static org.apache.commons.io.FileUtils.copyFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import model.singleton.ImageHandler;
import model.singleton.PropertiesModel;

public class ArticleController implements ListSelectionListener, ActionListener {

	private ArticleView view;
	private PropertiesModel propApp = new PropertiesModel();

	public ArticleController(String articleNr) {
		propApp.loadAppProperties();
		initView();
		initPsdFiles(articleNr);

	}

	private void initView() {
		view = new ArticleView(this);
		view.setVisible(true);
	}

	public void initPsdFiles(final String filename) {
		view.labelSearchQuery.setText(filename);
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.matches(".*(" + filename + ")+.*");
			}
		};
		File f = new File(propApp.get("locMediaBackup") + propApp.get("mediaBackupDirOriginals"));
		File[] files = f.listFiles(filter);
		System.out.println(files.length);
		Vector<File> filesList = new Vector<File>();

		for (File file : files) {

			if (file.isDirectory()) {
				System.out.println(file.getName());
				File[] files2 = file.listFiles(filter);
				for (File file2 : files2) {

					if (!file2.isDirectory()) {
						filesList.add(file2);
					}
				}
			}
		}
		view.listPsdFiles.setListData(filesList);
		view.listPsdFiles.setSelectedIndex(0);
	}

	public void initThumbSlider() {

		String articleNr = "18909G";
		File liveImage = new File("media/live/100px/" + articleNr + "/" + articleNr + ".jpg");
		if (liveImage.isFile()) {
			Vector<File> imageFiles = new Vector<File>();
			imageFiles.add(liveImage);

			int count = 1;
			liveImage = new File(
					"media/live/100px/" + articleNr + "_" + count + "/" + articleNr + "_" + count + ".jpg");
			while (liveImage.isFile()) {
				imageFiles.add(liveImage);
				count++;
				liveImage = new File(
						"media/live/100px/" + articleNr + "_" + count + "/" + articleNr + "_" + count + ".jpg");
			}
			// view.listThumbs.setListData(imageFiles);
		}

	}

	public File saveFile() {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile((File) view.listPsdFiles.getSelectedValue());
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Save file");
		fileChooser.setLocation(100, 100);

		int returnVal = fileChooser.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
		}
		return file;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == view.btnRestoreFromPsd) {
			File psdFile = (File) view.listPsdFiles.getSelectedValue();
			Vector<File> psdFileList = new Vector<File>();
			psdFileList.add(psdFile);
			new ProdImgImpWzrdController(psdFileList);
		} else if (ae.getSource() == view.btnSavePsdFile) {
			try {
				copyFile((File) view.listPsdFiles.getSelectedValue(), saveFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (ae.getSource() == view.btnRemoveFromLive) {

		}
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		if (lse.getSource() == view.listPsdFiles) {
			File psdFile = (File) view.listPsdFiles.getSelectedValue();
			System.out.println(psdFile.getName());
			ImageHandler imgHandler = new ImageHandler();
			BufferedImage img;
			try {
				img = imgHandler.getImageFromPsd(psdFile);
				view.labelPsdPreview.setIcon(new ImageIcon(imgHandler.resizeImage(378, 378, img)));
				view.repaint();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
