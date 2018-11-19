/*
 * Die Klasse demonstriert den Einsatz von nicht-modalen von <code>JDialog</code> abgeleiteten Dialogen.
 *
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * @NonModalDemo.java     12.02.2010
 * build: 12.02.2010
 * Copyright 2010 javabeginners.de. All rights reserved
 * @author Joerg Czeschla
 * 
 * This file is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with this file;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
@SuppressWarnings("serial")
public class NonModalDemo extends JFrame implements ActionListener {

	private JButton showButt, exitButt;
	private final MenuDialog menu = new MenuDialog();

	public NonModalDemo() {
		this.setLayout(new FlowLayout());
		showButt = new JButton("MenuDialog zeigen");
		showButt.addActionListener(this);
		this.add(showButt);
		exitButt = new JButton("Exit");
		exitButt.addActionListener(this);
		this.add(exitButt);
		this.setUndecorated(true);
		this.setSize(this.getToolkit().getScreenSize());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	} // Ende Konstruktor

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == exitButt) {
			System.exit(0);
		}
		if (e.getSource() == showButt) {
			menu.setVisible(true);
		}
	}

	public static void main(String[] args) {
		new NonModalDemo();
	}
} // Ende class NonModalDemo

@SuppressWarnings("serial")
class DialogTemplate extends JDialog implements ActionListener {
	public DialogTemplate() {
		this.setLayout(new BorderLayout(5, 5));
		this.setAlwaysOnTop(true);
		this.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
} // Ende class DialogTemplate

@SuppressWarnings("serial")
class OKDialog extends DialogTemplate {
	private JButton okButt;
	Component parent;

	public OKDialog(Component parent) {
		super();
		this.parent = parent;
		okButt = new JButton("ok");
		okButt.addActionListener(this);
		this.add(okButt, BorderLayout.SOUTH);
		this.pack();
		this.setVisible(false);
	}

	public void actionPerformed(ActionEvent e) {
		parent.setVisible(false);
		this.setVisible(false);
	}
} // Ende class OKDialog

// MenuDialog-Dialog
@SuppressWarnings("serial")
class MenuDialog extends DialogTemplate {
	final FileSelector fileSelector = new FileSelector();

	public MenuDialog() {
		super();
		JButton showButt = new JButton("Datei-Auswahl-Dialog zeigen");
		showButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!fileSelector.isVisible())
					fileSelector.setVisible(true);
			}
		});
		JButton exitButt = new JButton("Abbruch");
		exitButt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				MenuDialog.this.setVisible(false);
			}
		});
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(showButt);
		panel.add(exitButt);
		this.add(panel, BorderLayout.SOUTH);
		this.setBounds(300, 300, 300, 300);
		this.pack();
	}
} // Ende class MenuDialog

// FileChooser-Dialog
@SuppressWarnings("serial")
class FileSelector extends DialogTemplate {

	private final JFileChooser chooser = new JFileChooser();
	private final OKDialog okDialog = new OKDialog(this);

	public FileSelector() {
		super();
		final JLabel pfadLabel = new JLabel("Abbruch?");
		okDialog.add(pfadLabel, BorderLayout.CENTER);
		chooser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File file = chooser.getSelectedFile();
				if (file != null
						&& e.getActionCommand().equals(
								JFileChooser.APPROVE_SELECTION)) {
					pfadLabel.setText(chooser.getSelectedFile()
							.getAbsolutePath());
				}
				if (okDialog != null) {
					okDialog.setBounds(400, 400, 200, 150);
					okDialog.pack();
					okDialog.setVisible(true);
				}
			}
		});
		this.setBounds(100, 100, 400, 300);
		this.add(chooser, BorderLayout.CENTER);
		this.setVisible(false);
	}
} // Ende class FileSelector
