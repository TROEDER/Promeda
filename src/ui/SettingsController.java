package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import model.prototype.StoreDataModel;
import model.singleton.PropertiesModel;

public class SettingsController implements ActionListener {

	private SettingsView view;
	private PropertiesModel propApp = new PropertiesModel();
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
		propApp.loadAppProperties();
		view.textFieldLocNetworkRes.setText(propApp.get("locNetworkRes"));
		view.textFieldLocMediaBackup.setText(propApp.get("locMediaBackup"));
		view.textFieldMediaBackupDirOriginals.setText(propApp.get("mediaBackupDirOriginals"));
		view.textFieldMediaBackupDirLive.setText(propApp.get("mediaBackupDirLive"));
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
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == view.btnSettingsCancel) {
			view.dispose();
		} else if (ae.getSource() == view.btnSettingsSave) {
			
		}
	}
}
