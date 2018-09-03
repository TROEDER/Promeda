package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class AppView extends JFrame {

	private AppController controller;
	private JPanelWithBackgroundImage contentPane;
	public JButton btnProdImgImpWzrd;
	public JButton btnSearch;
	public JButton btnSettings;
	public JButton btnExit;
	public JTextField textFieldProdNr;
	public JButton btnPageImgImpWzrd;

	/**
	 * Create the frame.
	 */
	public AppView(AppController controller) {

		this.controller = controller;

		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 251, 473);
		contentPane = new JPanelWithBackgroundImage(getClass().getResource("/img/promeda-app-bg3.jpg"));
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

		textFieldProdNr = new JTextField();
		textFieldProdNr.setBounds(0, 0, 152, 40);
		textFieldProdNr.addActionListener(controller);
		panel.setLayout(null);
		textFieldProdNr.setFont(new Font("Open Sans", Font.BOLD, 14));
		panel.add(textFieldProdNr);
		textFieldProdNr.setColumns(10);

		btnSearch = new JButton("Search");
		btnSearch.setBounds(149, 0, 76, 40);
		btnSearch.addActionListener(controller);
		panel.add(btnSearch);

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
		btnSettings.setBounds(10, 200, 225, 40);
		contentPane.add(btnSettings);

		btnExit = new JButton("");
		btnExit.setContentAreaFilled(false);
		btnExit.setBorderPainted(false);
		btnExit.setIcon(new ImageIcon(AppView.class.getResource("/img/Power.png")));
		btnExit.addActionListener(controller);
		btnExit.setSize(new Dimension(225, 40));
		btnExit.setPreferredSize(new Dimension(300, 23));
		btnExit.setAlignmentX(0.5f);
		btnExit.setBounds(94, 367, 64, 64);
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
		
		btnPageImgImpWzrd = new JButton("Page Image Import Wizard");
		btnPageImgImpWzrd.addActionListener(controller);
		btnPageImgImpWzrd.setSize(new Dimension(225, 40));
		btnPageImgImpWzrd.setPreferredSize(new Dimension(300, 23));
		btnPageImgImpWzrd.setAlignmentX(0.5f);
		btnPageImgImpWzrd.setBounds(10, 294, 225, 40);
		contentPane.add(btnPageImgImpWzrd);
	}
}
