package ui;

import javax.swing.*;
import javax.swing.border.*;

import model.prototype.StoreDataModel;

import java.awt.Component;
import java.io.File;

public class StoreSettingsList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public StoreSettingsList() {
		setCellRenderer(new CellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			StoreDataModel store = (StoreDataModel) value;
			JLabel label = new JLabel(store.getStoreName());
			return label;
		}
	}
}