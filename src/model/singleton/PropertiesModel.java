package model.singleton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertiesModel {

	private Properties prop;

	public PropertiesModel() {
		prop = new Properties();
	}

	public final void load() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("props/app.properties");
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(PropertiesModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public final void loadAppProperties() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("props/config/app.properties");
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(PropertiesModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public final void load(String propFile) {
		try {
			InputStream input = new FileInputStream(propFile);
			prop.load(input);
		} catch (IOException ex) {
			Logger.getLogger(PropertiesModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public String get(String key) {
		return prop.getProperty(key);
	}

	public void write(String[] propInputData) {

		try {
			// writes previous properties to file "resource\\config_chronicle.properties"
			OutputStream outputPropChronicle = new FileOutputStream("resource\\config_chronicle.properties");
			prop.store(outputPropChronicle, null);

			OutputStream output = new FileOutputStream("resource\\config.properties");
			// set the properties value
			// prop.setProperty("database", "app-archives.com");
			// prop.setProperty("dbuser", "xd122_db1");
			// prop.setProperty("dbpassword", "enterWeltfrieden1337!");
			// write properties to file
			prop.store(output, null);
		} catch (IOException ex) {
			Logger.getLogger(PropertiesModel.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void write() {

		try {
			// writes previous properties to file "resource\\config_chronicle.properties"
			OutputStream outputPropChronicle = new FileOutputStream("resource\\app_chronicle.properties");
			prop.store(outputPropChronicle, null);

			OutputStream output = new FileOutputStream("resource\\app.properties");
			// set the properties value
			prop.setProperty("database", "app-archives.com");
			prop.setProperty("dbuser", "xd122_db1");
			prop.setProperty("dbpassword", "enterWeltfrieden1337!");
			// write properties to file
			prop.store(output, null);
		} catch (IOException ex) {
			Logger.getLogger(PropertiesModel.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
