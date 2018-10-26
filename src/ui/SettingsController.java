package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;

import com.jcraft.jsch.ConfigRepository.Config;

import model.prototype.StoreDataModel;
import model.singleton.PropertiesModel;

public class SettingsController implements ActionListener {

	private SettingsView view;
	private PropertiesModel propApp = new PropertiesModel();
	private Configuration configApp;
	private Vector<StoreDataModel> stores;
	
	public SettingsController() {
		initView();
		initAppProperties();
		initStoreProperties();
		
	}
	
	private void initView() {
		view = new SettingsView(this);
		view.setVisible(true);
	}
	
	private void initAppProperties() {
		//configApp = propApp.loadAppProperties2();
		propApp.loadAppProperties();
		
		view.textFieldLocNetworkRes.setText(propApp.get("locNetworkRes"));
		view.textFieldLocMediaBackup.setText(propApp.get("locMediaBackup"));
		view.textFieldMediaBackupDirOriginals.setText(propApp.get("mediaBackupDirOriginals"));
		view.textFieldMediaBackupDirLive.setText(propApp.get("mediaBackupDirLive"));
		
		view.textFieldLocNetworkRes.setText(configApp.getString("locNetworkRes"));
		view.textFieldLocMediaBackup.setText(configApp.getString("locMediaBackup"));
		view.textFieldMediaBackupDirOriginals.setText(configApp.getString("mediaBackupDirOriginals"));
		view.textFieldMediaBackupDirLive.setText(configApp.getString("mediaBackupDirLive"));
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
	
	private void initStoreProperties() {
		File f = new File(propApp.get("locNetworkRes") + "stores");
		File[] files = f.listFiles();

		PropertiesModel prop = new PropertiesModel();
		stores = new Vector<StoreDataModel>();

		/*for (File file : files) {
			if (!file.isDirectory()) {
				prop.load(file.getPath());
				String[] imgSizes = prop.get("img.sizes").split(",");
				for (String size : prop.get("img.sizes").split(",")) {
					int imgSizeInt = Integer.parseInt(size);
				}
				stores.add(new StoreDataModel(prop.get("url"), prop.get("ftp.host"), Integer.parseInt(prop.get("ftp.port")),
					prop.get("ftp.user"), prop.get("ftp.pswd"), imgSizes));
			}
		}*/
		view.listStoreSettings.setListData(stores);
		view.listStoreSettings.setSelectedIndex(0);
	}
	
	public File chooseDir() {
		File file = null;

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Select the directory");
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
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == view.btnSettingsCancel) {
			view.dispose();
		} else if (ae.getSource() == view.btnSettingsSave) {
			
		} else if (ae.getSource() == view.btnBrowseNetworkRes) {
			try {
				configApp.setProperty("locNetworkRes", chooseDir().getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			view.textFieldLocNetworkRes.setText(configApp.getString("locNetworkRes"));
		} else if (ae.getSource() == view.btnBrowseNetworkMedia) {
			try {
				configApp.setProperty("locMediaBackup", chooseDir().getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			view.textFieldLocMediaBackup.setText(configApp.getString("locMediaBackup"));
		}
	}
}
