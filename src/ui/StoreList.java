package ui;

import javax.swing.*;
import javax.swing.border.*;

import model.prototype.StoreDataModel;

import java.awt.Color;
import java.awt.Component;

public class StoreList extends JList {
	
	public int index;
	public StoreList() {
		setCellRenderer(new CellRenderer());
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			StoreDataModel store = (StoreDataModel) value;
			JLabel label = new JLabel(store.getStoreName());
			if(isSelected) {
				label.setOpaque(true);
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}
			return label;
		}
	}
}