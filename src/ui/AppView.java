package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JTextField;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import java.awt.Font;

public class AppView extends JFrame {

	private AppController controller;
	private JPanelWithBackgroundImage contentPane;
	public JButton btnProdImgImpWzrd;
	public JButton btnSearch;
	public JButton btnSettings;
	public JButton btnExit;
	public JTextField textFieldProdNr;

	/**
	 * Create the frame.
	 */
	public AppView(AppController controller) {

		this.controller = controller;

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 251, 372);
		contentPane = new JPanelWithBackgroundImage("resource/img/promeda-app-bg3.jpg");
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("PROMEDA");
		lblNewLabel.setVerticalAlignment(SwingConstants.BOTTOM);
		lblNewLabel.setFont(new Font("Montserrat", Font.BOLD, 18));
		lblNewLabel.setBounds(72, 11, 163, 40);
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(lblNewLabel);

		JPanel panel = new JPanel();
		panel.setBounds(10, 96, 225, 40);
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		textFieldProdNr = new JTextField();
		textFieldProdNr.addActionListener(controller);
		textFieldProdNr.setFont(new Font("Open Sans", Font.BOLD, 14));
		panel.add(textFieldProdNr);
		textFieldProdNr.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.addActionListener(controller);
		panel.add(btnSearch, BorderLayout.EAST);

		btnProdImgImpWzrd = new JButton("Product Image Import Wizard");
		btnProdImgImpWzrd.addActionListener(controller);
		btnProdImgImpWzrd.setLocation(10, 147);
		btnProdImgImpWzrd.setSize(new Dimension(225, 40));
		btnProdImgImpWzrd.setPreferredSize(new Dimension(300, 23));
		btnProdImgImpWzrd.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPane.add(btnProdImgImpWzrd);

		btnSettings = new JButton("Settings");
		btnSettings.addActionListener(controller);
		btnSettings.setSize(new Dimension(225, 40));
		btnSettings.setPreferredSize(new Dimension(300, 23));
		btnSettings.setAlignmentX(0.5f);
		btnSettings.setBounds(10, 198, 225, 40);
		contentPane.add(btnSettings);

		btnExit = new JButton("Exit");
		btnExit.addActionListener(controller);
		btnExit.setSize(new Dimension(225, 40));
		btnExit.setPreferredSize(new Dimension(300, 23));
		btnExit.setAlignmentX(0.5f);
		btnExit.setBounds(10, 282, 225, 40);
		contentPane.add(btnExit);
		
		JLabel lblNewLabel_1 = new JLabel("Promondo Media Administration");
		lblNewLabel_1.setFont(new Font("Gill Sans MT", Font.PLAIN, 12));
		lblNewLabel_1.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel_1.setBounds(72, 49, 163, 36);
		contentPane.add(lblNewLabel_1);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setIcon(new ImageIcon(AppView.class.getResource("/img/logo.png")));
		label.setBounds(10, 5, 52, 80);
		contentPane.add(label);
	}
}
