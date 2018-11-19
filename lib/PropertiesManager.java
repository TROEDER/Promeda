/*
 * Die Klasse erm&ouml;glicht die Verwaltung einer Properties-Datei, die z.B.
 * zum Speichern von Programm-Optionen verwendet werden kann. Die Default-Werte
 * werden als String-Array in der Klasse abgelegt. Sie werden als Felder deklariert und k&ouml;nnen den eigenen
 * Bed&uuml;rfnissen angepasst werden:
 * <ul>
 * <li>rootPath = System.getProperty("user.home")</li>
 * <li>dirName = ".configDir"</li>
 * <li>fileName = "config"</li>
 * <li>header = ""</li>
 * <li>saveXML = true</li>
 * </ul>
 * 
 */
/*
 * @PropertiesManager.java     1.12.2010
 * Copyright 2010 yourwebs.de. All rights reserved
 * @author J&ouml;rg Czeschla
 *
 * This file is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with Sudoku;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
 */
package grundlagen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertiesManager {

	/**
	 * Pfad zum Ablageort des Konfigurationsverzeichnisses Standard:
	 * Heimatverzeichnis des Users
	 */
	private String rootPath = System.getProperty("user.home");

	/**
	 * Verzeichnis, in dem die Speicherdatei abgelegt wird. Es wird im
	 * Home-Verzeichnis des Users angelegt. Um das Verzeichnis direkt im
	 * Home-Verzeichnis des Users abzulegen muss hier ein Leerstring ("")
	 * angegeben werden. Unter Windows wird der f&uum;hrende Punkt entfernt
	 */
	private String dirName = ".configDir";

	/**
	 * Dateiname der Properties-Dateiname Unter Windows wird ein evtl.
	 * f&uum;hrender Punkt entfernt
	 */
	private String fileName = "config";

	/**
	 * kompletter Pfad zur Konfigurationsdatei
	 */
	private String configFilePath;

	/**
	 * Kommentar-Header der Properties-Datei
	 */
	private final String header = "";

	/**
	 * Wenn true wird die Properties-Datei als *.xml angelegt ansonsten als
	 * Plain-Text-Datei mit Paaren in der Form Key:Value
	 */
	private boolean saveXML = true;

	/**
	 * Default-Werte Key und Values werden hier als identische Strings
	 * abgespeichert Wenn leer wird eine leere Speicherdatei angelegt
	 */
	private final String[] prop = { "eins" };

	private Properties defaultProperties = new Properties();
	private Properties properties;

	public PropertiesManager() {
		// Initialisierung der Default-Properties
		loadDefaultProperties(prop);

		// Als Default-Werte werden die Default-Properties geladen
		properties = new Properties(defaultProperties);

		if (!makeConfigFile()) {
			System.out.println("Pfad der Speicherdatei ungueltig!");
			return;
		}
		loadProperties();
	}

	/**
	 * Legt den Pfad und die Konfigurationsdatei des Users an. Dies geschieht
	 * per default im home-Verzeichnis des Users &uuml;ber die Variable
	 * rootPath. Ist rootPath leer, wird das Konfigurationsverzeichnis mit Datei
	 * in dem Verzeichnis angelegt, aus dem heraus die Application gestartet
	 * wurde. Bei der Neuanlage werden die Default-Werte der Properties
	 * gespeichert.
	 * 
	 * @return true wenn die Datei vorhanden ist oder angelegt werden konnte,
	 *         sonst false
	 */
	private boolean makeConfigFile() {

		// Abruch wenn Datei existiert
		if (checkConfigPath())
			return true;

		File file = new File(configFilePath);
		File dir = file.getParentFile();

		// Falls Ablageverzeichnis nicht existiert, versuche es zu erstellen
		if (!dir.isDirectory() && !dir.mkdirs()) {
			System.err.println(dir
					+ " ist kein Verzeichnis und kann nicht erstellt werden!");
			return false;
		}

		try {
			// Falls nicht vorhanden Ablagedatei erstellen und
			// Default-Properties speichern
			if (file.createNewFile()) {
				saveProperties(defaultProperties);
				System.out.println(file + " erstellt.");
				return true;
			}
		} catch (IOException e) {
			System.err
					.println("Konfigurationsdatei konnte nicht angelegt werden.");
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * pr&uuml;ft den Pfad zur Properties-Datei auf seine Existenz und ob die
	 * Datei beschrieben werden kann
	 * 
	 * @return true wenn die Konfigurationsdatei existiert und beschrieben
	 *         werden kann, sonst false
	 */
	private boolean checkConfigPath() {
		String dir = dirName;
		String fs = System.getProperty("file.separator");

		// Windows mag keine Dot-Files
		if (System.getProperty("os.name").contains("Windows")) {
			dir = dir.indexOf('.') == 0 ? dir.substring(1) : dir;
			fileName = fileName.indexOf('.') == 0 ? fileName.substring(1)
					: fileName;
		}

		// wenn der Verzeichnisname nicht leer ist, '/' an Verzeichnisnamen
		// haengen falls nicht vorhanden
		dir = dir.lastIndexOf(fs) == dirName.length() || dirName.equals("") ? dir
				: dir + fs;

		// wenn der Pfadname nicht leer ist, '/' an Pfadnamen haengen
		// falls nicht vorhanden
		rootPath = rootPath.lastIndexOf(fs) == rootPath.length()
				|| rootPath.equals("") ? rootPath : rootPath + fs;

		// Pfad endgueltig zusammensetzen
		configFilePath = rootPath + dir + fileName;

		File configFile = new File(configFilePath);

		// Schreibrechte pruefen
		if (configFile.isFile() && configFile.canWrite())
			return true;

		return false;
	}

	/**
	 * Speichert die Properties prop im eingestellten Verzeichnis Sind diese
	 * null werden die bereits vorhandenen Properties gespeichert. Das Speichern
	 * kann je nach Angabe des Feldes saveXML wahlweise als XML-Datei oder als
	 * Plain-Text-Datei mit Key:Value-Paaren erfolgen.
	 * 
	 * @param prop
	 *            das zu speichernde Properties-Objekt
	 */
	private void saveProperties(Properties prop) {
		prop = prop == null ? properties : prop;

		// Abbruch wenn falscher Pfad gesetzt ist
		if (configFilePath == null || configFilePath.length() == 0) {
			return;
		}

		FileOutputStream propOut = null;
		try {
			// Properties speichern
			propOut = new FileOutputStream(new File(configFilePath));
			properties.putAll(prop);
			if (saveXML)
				prop.storeToXML(propOut, header);
			else
				prop.store(propOut, header);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (propOut != null)
					propOut.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Liest die Properties aus der zugeh&ouml;rigen Datei. Wenn diese nicht
	 * lesbar ist oder nicht existiert, wird versucht, diese anzulegen und mit
	 * den Default-Werten zu belegen.
	 */
	private void loadProperties() {
		File cf = new File(configFilePath);
		if (cf.length() == 0) {
			System.out.println("Properties k\u00F6nnen nicht geladen werden, "
					+ cf + " ist leer.\nDefault Properties werden geladen.");
			return;
		}

		Enumeration<?> propEnum = properties.propertyNames();
		String prop;

		// Properties aus Datei auslesen
		if (cf.canRead() && cf.isFile() && cf.length() > 0) {
			FileInputStream propIn = null;
			try {
				propIn = new FileInputStream(configFilePath);
				if (saveXML)
					properties.loadFromXML(propIn);
				else
					properties.load(propIn);
				// durchlaeuft die Keys der default Properties und prueft, ob
				// diese in
				// den ausgelesenen Properties belegt sind. Falls nicht wird ein
				// Leerstring geladen.
				while (propEnum.hasMoreElements()) {
					prop = (String) propEnum.nextElement();
					if (properties.getProperty(prop).equals("")) {
						properties.setProperty(prop,
								defaultProperties.getProperty(prop));
					}
				}
				// Wenn Auslesen erfolgreich Abbruch
				return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (propIn != null)
						propIn.close();
				} catch (IOException e) {
				}
			}
		}
		// falls Datei nicht existiert, versuche sie zu erstellen
		makeConfigFile();
	}

	/**
	 * Erzeugt aus den String-Angaben das Default-Properties-Objekt. Es wird
	 * beim Erzeugen der Properties-Arbeitsversion als Parameter &uuml;bergeben.
	 * 
	 * @param s
	 *            String[], das Array der Keys/Values. Die Values werden unter
	 *            dem gleichnamigen Key gespeichert.
	 */
	private void loadDefaultProperties(String[] s) {
		loadDefaultProperties(s, s);
	}

	/**
	 * Setzt die Default-Properties mit Hilfe zweier Arrays Die Methode kann
	 * verwendet werden um zur Laufzeit die Default-Werte der Properties zu
	 * &auml;ndern
	 * 
	 * @param keys
	 * @param values
	 */
	public void loadDefaultProperties(Object[] keys, Object[] values) {
		if (keys.length != values.length) {
			System.err
					.println("Keys und Values der Default-Arrays sind ungleich lang.");
			return;
		}
		String key, value;
		for (int i = 0; i < keys.length; i++) {
			key = keys[i].toString();
			value = values[i].toString();
			defaultProperties.put(key, value);
		}
	}

	/**
	 * Setzt den Wert value zur Eigenschaft key der Properties. Wenn keine
	 * Properties gesetzt sind, werden die Default-Properties geladen und
	 * anschliessend der Wert value an der Position key &uuml;berschrieben. Die
	 * Default-Properties bleiben grunds&auml;tzlich unber&uuml;hrt.
	 * 
	 * @param key
	 *            String der Schl&uuml;ssel, unter dem value gespeichert wird
	 * @param value
	 *            String der Wert, der unter key gespeichert wird
	 */
	public void setProperty(String key, String value) {
		if (properties == null)
			properties = defaultProperties;
		properties.setProperty(key, value);
		saveProperties(properties);
	}

	/**
	 * Liefert den Wert der zu key geh&ouml;renden Eigenschaft
	 * 
	 * @param key
	 *            Schl&uuml;ssel der gefragten Eigenschaft
	 * @return String, der Wert der gefragten Eigenschaft
	 */
	public String getProperty(String key) {
		if (properties == null)
			properties = defaultProperties;
		return properties.getProperty(key);
	}

	/**
	 * Gibt das aktuelle Properties-Objekt zur&uuml;ck. Wenn dies null ist,
	 * werden die Default-Properties zur&uuml;ck gegeben.
	 * 
	 * @return das aktuelle Properties-Objekt
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Hier besteht die M&ouml;glichkeit, auch von ausserhalb der Klasse die
	 * Default-Werte einzusehen und zu manipulieren, etwa f&uuml;r Vergleiche,
	 * etc.
	 * 
	 * @return das Properties-Objekt der Default-Properties
	 */
	public Properties getDefaultProperties() {
		return defaultProperties;
	}

	/**
	 * Druckt die eingetragenen Properties zeilenweise aus
	 */
	public void printProperties() {
		Enumeration<?> e = getProperties().propertyNames();
		while (e.hasMoreElements()) {
			String s = (String) e.nextElement();
			System.out.println(s + ": " + getProperty(s));
		}
	}

	public static void main(String[] args) {
		PropertiesManager pm = new PropertiesManager();
		pm.printProperties();
		pm.setProperty("eins", "sechs");
		pm.printProperties();
	}
}
