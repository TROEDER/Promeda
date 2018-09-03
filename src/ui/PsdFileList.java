package ui;

import java.awt.Color;
import java.awt.Component;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class PsdFileList extends JList {
	
	private final ImageIcon psdFileIcon = new ImageIcon(CheckBoxList.class.getResource("/img/psd-file-32px.png"));

	public PsdFileList() {
		setCellRenderer(new CellRenderer());

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	protected class CellRenderer implements ListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			File file = (File) value;
			JLabel label = new JLabel(file.getName(), psdFileIcon, SwingConstants.LEFT);
			if(isSelected) {
				label.setOpaque(true);
				label.setBackground(Color.BLUE);
				label.setForeground(Color.WHITE);
			}
			return label;
		}
	}
}