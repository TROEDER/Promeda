package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.Component;
import java.io.File;

public class FileList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	public FileList() {
		setCellRenderer(new CellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			File file = (File) value;
			JLabel label = new JLabel(file.getName());
			return label;
		}
	}
}