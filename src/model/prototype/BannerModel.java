package model.prototype;

import java.awt.Dimension;
import java.util.HashMap;

import org.apache.commons.configuration.Configuration;

public class BannerModel {

	private String name;
	private String dirname;
	private Dimension dimSM;
	private Dimension dimMD;
	private Dimension dimLG;
	HashMap<String, Dimension> dimensions;
	private Boolean selectStatus;
	private Boolean matchSrcStatus;

	public BannerModel() {

	}

	public BannerModel(String name, Configuration props) {
		dimensions = new HashMap<String, Dimension>();

		this.name = name;
		this.dirname = props.getString("dirname");

		if (props.containsKey("sm.width") && props.containsKey("sm.height")) {
			dimSM = new Dimension(props.getInt("sm.width"), props.getInt("sm.height"));
			dimensions.put("sm", dimSM);
		}
		if (props.containsKey("md.width") && props.containsKey("md.height")) {
			dimMD = new Dimension(props.getInt("md.width"), props.getInt("md.height"));
			dimensions.put("md", dimMD);
		}
		if (props.containsKey("lg.width") && props.containsKey("lg.height")) {
			dimLG = new Dimension(props.getInt("lg.width"), props.getInt("lg.height"));
			dimensions.put("lg", dimLG);
		}
		setSelectStatus(false);
		setMatchSrcStatus(true);
	}

	public BannerModel(String name, Dimension srcImageSize, Configuration props) {
		dimensions = new HashMap<String, Dimension>();

		this.name = name;
		this.dirname = props.getString("dirname");

		if (props.containsKey("sm.width") && props.containsKey("sm.height")) {
			if (props.getProperty("sm.height").toString().equals(new String("auto"))
					|| props.getProperty("sm.height").toString() == "auto") {
				float factor = (float) props.getInt("sm.width") / (float) srcImageSize.width;
				int newHeight = Math.round((float) srcImageSize.height * factor);
				dimSM = new Dimension(props.getInt("sm.width"), newHeight);
			} else {
				dimSM = new Dimension(props.getInt("sm.width"), props.getInt("sm.height"));
			}
			dimensions.put("sm", dimSM);
		}
		if (props.containsKey("md.width") && props.containsKey("md.height")) {
			if (props.getProperty("md.height").toString().equalsIgnoreCase("auto")
					|| props.getProperty("md.height").toString().equals(new String("auto"))
					|| props.getProperty("md.height").toString() == "auto") {
				float factor = (float) props.getInt("md.width") / (float) srcImageSize.width;
				int newHeight = Math.round((float) srcImageSize.height * factor);
				dimMD = new Dimension(props.getInt("md.width"), newHeight);
			} else {
				System.out.println(props.getProperty("md.height").toString());
				dimMD = new Dimension(props.getInt("md.width"), props.getInt("md.height"));
			}
			dimensions.put("md", dimMD);
		}
		if (props.containsKey("lg.width") && props.containsKey("lg.height")) {
			if (props.getProperty("lg.height").toString().equals(new String("auto"))
					|| props.getProperty("lg.height").toString() == "auto") {
				float factor = (float) props.getInt("lg.width") / (float) srcImageSize.width;
				int newHeight = Math.round((float) srcImageSize.height * factor);
				dimLG = new Dimension(props.getInt("lg.width"), newHeight);
			} else {
				dimLG = new Dimension(props.getInt("lg.width"), props.getInt("lg.height"));
			}
			dimensions.put("lg", dimLG);

		}
		setSelectStatus(false);
		setMatchSrcStatus(true);
	}

	public String GetDimensionsKeys() {
		String keys = "output: " + dirname + "/[";
		for (String key : dimensions.keySet()) {
			keys += " " + key + " ";
		}
		keys += "]";
		return keys;
	}

	/**
	 * @return the dimSM
	 */
	public Dimension getDimSM() {
		return dimSM;
	}

	/**
	 * @param dimSM the dimSM to set
	 */
	public void setDimSM(Dimension dimSM) {
		this.dimSM = dimSM;
	}

	/**
	 * @return the dimMD
	 */
	public Dimension getDimMD() {
		return dimMD;
	}

	/**
	 * @param dimMD the dimMD to set
	 */
	public void setDimMD(Dimension dimMD) {
		this.dimMD = dimMD;
	}

	/**
	 * @return the dimLG
	 */
	public Dimension getDimLG() {
		return dimLG;
	}

	/**
	 * @param dimLG the dimLG to set
	 */
	public void setDimLG(Dimension dimLG) {
		this.dimLG = dimLG;
	}

	/**
	 * @return the dimensions
	 */
	public HashMap<String, Dimension> getDimensions() {
		return dimensions;
	}

	/**
	 * @param dimensions the dimensions to set
	 */
	public void setDimensions(HashMap<String, Dimension> dimensions) {
		this.dimensions = dimensions;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the dirname
	 */
	public String getDirname() {
		return dirname;
	}

	/**
	 * @param dirname the dirname to set
	 */
	public void setDirname(String dirname) {
		this.dirname = dirname;
	}

	/**
	 * @return the selectStatus
	 */
	public Boolean getSelectStatus() {
		return selectStatus;
	}

	/**
	 * @param selectStatus the selectStatus to set
	 */
	public void setSelectStatus(Boolean selectStatus) {
		this.selectStatus = selectStatus;
	}

	/**
	 * @return the matchSrcStatus
	 */
	public Boolean getMatchSrcStatus() {
		return matchSrcStatus;
	}

	/**
	 * @param matchSrcStatus the matchSrcStatus to set
	 */
	public void setMatchSrcStatus(Boolean matchSrcStatus) {
		this.matchSrcStatus = matchSrcStatus;
	}

}
