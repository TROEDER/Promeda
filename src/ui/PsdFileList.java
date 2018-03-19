package ui;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

public class PsdFileList extends JList {
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
	private final ImageIcon psdFileIcon = new ImageIcon(CheckBoxList.class.getResource("/img/psd-file-32px.png"));

	public PsdFileList() {
		setCellRenderer(new CellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			File file = (File) value;
			JLabel label = new JLabel(file.getName(), psdFileIcon, SwingConstants.LEFT);
			if(list.getSelectedIndex() == index) {
				label.setOpaque(true);
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}
			return label;
		}
	}
}