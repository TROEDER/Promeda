package ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import java.awt.Component;
import javax.swing.JEditorPane;
import javax.swing.JTabbedPane;
import java.awt.List;

public class PageImgImpWzrdView extends JFrame {

	private PageImgImpWzrdController controller;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public PageImgImpWzrdView(PageImgImpWzrdController controller) {

		this.controller = controller;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 607, 508);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2);
	}
}
