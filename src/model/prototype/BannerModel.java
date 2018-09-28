package model.prototype;

import java.awt.Dimension;

import org.apache.commons.configuration.Configuration;

public class BannerModel {
	private Dimension dimSM;
	private Dimension dimMD;
	private Dimension dimLG;
	private String name;
	private String dirname;
	private Boolean selectStatus;
	
	public BannerModel() {

	}

	public BannerModel(String name, Configuration props) {
		this.name = name;
		this.dirname = props.getString("dirname");
		dimSM = new Dimension(props.getInt("sm.width"), props.getInt("sm.height"));
		dimMD = new Dimension(props.getInt("md.width"), props.getInt("md.height"));
		dimLG = new Dimension(props.getInt("lg.width"), props.getInt("lg.height"));
		setSelectStatus(false);
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
}
