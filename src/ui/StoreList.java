package ui;

import javax.swing.*;
import javax.swing.border.*;

import model.prototype.StoreDataModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class StoreList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	public int index;
	public StoreList() {
		setCellRenderer(new CellRenderer());
		
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			StoreDataModel store = (StoreDataModel) value;
			JLabel label = new JLabel(store.getStoreName());
			if(list.getSelectedIndex() == index) {
				label.setBorder(new LineBorder(new Color(0, 0, 255), 2));
			}
			return label;
		}
	}
}