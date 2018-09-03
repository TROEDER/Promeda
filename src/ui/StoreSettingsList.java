package ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import model.prototype.StoreDataModel;

public class StoreSettingsList extends JList {

	public StoreSettingsList() {
		setCellRenderer(new CellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			StoreDataModel store = (StoreDataModel) value;
			JLabel label = new JLabel(store.getStoreName());
			return label;
		}
	}
}