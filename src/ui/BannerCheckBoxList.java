package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import model.prototype.BannerModel;
import model.prototype.StoreDataModel;

public class BannerCheckBoxList extends JList {
	
	private final ImageIcon checked = new ImageIcon(BannerCheckBoxList.class.getResource("/img/checkbox-checked.png"));
	private final ImageIcon unchecked = new ImageIcon(BannerCheckBoxList.class.getResource("/img/checkbox-unchecked.png"));

	public BannerCheckBoxList() {
		setCellRenderer(new CellRenderer());

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int index = locationToIndex(e.getPoint());

				if (index != -1) {
					// JCheckBox checkbox = (JCheckBox)
					// getModel().getElementAt(index);
					// checkbox.setSelected(
					// !checkbox.isSelected());
					BannerModel banner = (BannerModel) getModel().getElementAt(index);
					banner.setSelectStatus(!banner.getSelectStatus());
					//store = checkFixedStores(store);
					
					repaint();
				}
			}
		});
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	public void selectAll() {
		ListModel currentList = getModel();
		Vector<StoreDataModel> storeList = new Vector<StoreDataModel>();
		for (int i = 0; i < currentList.getSize(); i++) {
			StoreDataModel store = (StoreDataModel) currentList.getElementAt(i);
			store.setSelectStatus(true);
			storeList.add(store);
		}
		setListData(storeList);
	}
	public void deselectAll() {
		ListModel currentList = getModel();
		Vector<StoreDataModel> storeList = new Vector<StoreDataModel>();
		for (int i = 0; i < currentList.getSize(); i++) {
			StoreDataModel store = (StoreDataModel) currentList.getElementAt(i);
			store.setSelectStatus(false);
			store = checkFixedStores(store);
			storeList.add(store);
		}
		setListData(storeList);
	}
	
	public StoreDataModel checkFixedStores(StoreDataModel store) {
		// stores, which are always checked/selected
		if(store.getStoreName() == "media.promondo-produkte.de") {
			store.setSelectStatus(true);
		}
		return store;
	}
	/*
	 * public void addCheckbox(JCheckBox checkBox) { currentList = this.getModel();
	 * JCheckBox[] newList = new JCheckBox[currentList.getSize() + 1]; for (int i =
	 * 0; i < currentList.getSize(); i++) { newList[i] = (JCheckBox)
	 * currentList.getElementAt(i); } newList[newList.length - 1] = checkBox;
	 * setListData(newList); }
	 */

	protected class CellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			BannerModel banner = (BannerModel) value;
			JLabel label = new JLabel(banner.getName() + " - " + banner.GetDimensionsKeys());
			if (banner.getSelectStatus()) {
				label.setIcon(checked);
			} else {
				label.setIcon(unchecked);
			}
			if (banner.getMatchSrcStatus() != null) {
				if (banner.getMatchSrcStatus()) {
					label.setEnabled(true);
					
				} else {
					label.setEnabled(false);
					label.setForeground(Color.DARK_GRAY);
					label.setBackground(Color.LIGHT_GRAY);
				}
			}
			return label;
		}
	}
}