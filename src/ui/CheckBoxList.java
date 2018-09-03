package ui;

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

import model.prototype.StoreDataModel;

public class CheckBoxList extends JList {
	
	private final ImageIcon checked = new ImageIcon(CheckBoxList.class.getResource("/img/checkbox-checked.png"));
	private final ImageIcon unchecked = new ImageIcon(CheckBoxList.class.getResource("/img/checkbox-unchecked.png"));

	public CheckBoxList() {
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
					StoreDataModel store = (StoreDataModel) getModel().getElementAt(index);
					store.setSelectStatus(!store.getSelectStatus());
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
			StoreDataModel store = (StoreDataModel) value;
			JLabel label = new JLabel(store.getStoreName());
			if (store.getSelectStatus()) {
				label.setIcon(checked);
			} else {
				label.setIcon(unchecked);
			}
			return label;
		}
	}
}