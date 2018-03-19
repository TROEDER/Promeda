package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.JCheckBox;

public class SettingsView extends JFrame {

	private SettingsController controller;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	public JTextField textFieldLocNetworkRes;
	public JTextField textFieldLocMediaBackup;
	public JTextField textFieldMediaBackupDirOriginals;
	public JTextField textFieldMediaBackupDirLive;
	public StoreList listStoreSettings;

	/**
	 * Create the frame.
	 */
	public SettingsView(SettingsController controller) {
		this.controller = controller;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 770, 633);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
		tabbedPane.setBounds(5, 5, 744, 322);
		contentPane.add(tabbedPane);
		
		JPanel panelSystemConfig = new JPanel();
		panelSystemConfig.setOpaque(false);
		tabbedPane.addTab("System Config", (Icon) null, panelSystemConfig, "zuiop");
		panelSystemConfig.setLayout(null);
		
		JLabel lblNewLabel_1 = new JLabel("locNetworkRes");
		lblNewLabel_1.setBounds(10, 11, 166, 14);
		panelSystemConfig.add(lblNewLabel_1);
		
		textFieldLocNetworkRes = new JTextField();
		textFieldLocNetworkRes.setBounds(186, 8, 221, 20);
		panelSystemConfig.add(textFieldLocNetworkRes);
		textFieldLocNetworkRes.setColumns(10);
		
		JLabel labelLocMediaBackup = new JLabel("locMediaBackup");
		labelLocMediaBackup.setBounds(10, 38, 166, 14);
		panelSystemConfig.add(labelLocMediaBackup);
		
		textFieldLocMediaBackup = new JTextField();
		textFieldLocMediaBackup.setColumns(10);
		textFieldLocMediaBackup.setBounds(186, 35, 221, 20);
		panelSystemConfig.add(textFieldLocMediaBackup);
		
		JLabel labelMediaBackupDirOriginals = new JLabel("mediaBackupDirOriginals");
		labelMediaBackupDirOriginals.setBounds(10, 66, 166, 14);
		panelSystemConfig.add(labelMediaBackupDirOriginals);
		
		textFieldMediaBackupDirOriginals = new JTextField();
		textFieldMediaBackupDirOriginals.setColumns(10);
		textFieldMediaBackupDirOriginals.setBounds(186, 63, 221, 20);
		panelSystemConfig.add(textFieldMediaBackupDirOriginals);
		
		JLabel labelMediaBackupDirLive = new JLabel("mediaBackupDirLive");
		labelMediaBackupDirLive.setBounds(10, 94, 166, 14);
		panelSystemConfig.add(labelMediaBackupDirLive);
		
		textFieldMediaBackupDirLive = new JTextField();
		textFieldMediaBackupDirLive.setColumns(10);
		textFieldMediaBackupDirLive.setBounds(186, 91, 221, 20);
		panelSystemConfig.add(textFieldMediaBackupDirLive);
		tabbedPane.setEnabledAt(0, true);
		
		JPanel panelStoreConfig = new JPanel();
		panelStoreConfig.setOpaque(false);
		tabbedPane.addTab("Store Config", (Icon) null, panelStoreConfig, "asdfghjkl");
		tabbedPane.setEnabledAt(1, true);
		panelStoreConfig.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 11, 208, 294);
		panelStoreConfig.add(scrollPane);
		
		listStoreSettings = new StoreList();
		scrollPane.setViewportView(listStoreSettings);
		
		JLabel lblNewLabel = new JLabel("URL/Name");
		lblNewLabel.setBounds(228, 13, 147, 14);
		panelStoreConfig.add(lblNewLabel);
		
		textField = new JTextField();
		textField.setBounds(385, 10, 259, 20);
		panelStoreConfig.add(textField);
		textField.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(228, 38, 416, 14);
		panelStoreConfig.add(separator);
		
		JLabel lblFtpServer = new JLabel("FTP Server");
		lblFtpServer.setBounds(228, 50, 147, 14);
		panelStoreConfig.add(lblFtpServer);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(385, 47, 259, 20);
		panelStoreConfig.add(textField_1);
		
		JLabel lblFtpPort = new JLabel("FTP Port");
		lblFtpPort.setBounds(228, 75, 147, 14);
		panelStoreConfig.add(lblFtpPort);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(385, 72, 259, 20);
		panelStoreConfig.add(textField_2);
		
		JLabel lblUser = new JLabel("User");
		lblUser.setBounds(228, 103, 147, 14);
		panelStoreConfig.add(lblUser);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(385, 100, 259, 20);
		panelStoreConfig.add(textField_3);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(228, 131, 147, 14);
		panelStoreConfig.add(lblPassword);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(385, 128, 259, 20);
		panelStoreConfig.add(textField_4);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(228, 157, 416, 14);
		panelStoreConfig.add(separator_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(385, 170, 259, 110);
		panelStoreConfig.add(scrollPane_1);
		
		JList list = new JList();
		scrollPane_1.setViewportView(list);
		
		JLabel lblImageSizes = new JLabel("Image Sizes");
		lblImageSizes.setBounds(228, 171, 147, 14);
		panelStoreConfig.add(lblImageSizes);
		
		JLabel lblUseSubfolders = new JLabel("Use Subfolders");
		lblUseSubfolders.setBounds(228, 291, 147, 14);
		panelStoreConfig.add(lblUseSubfolders);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("");
		chckbxNewCheckBox.setOpaque(false);
		lblUseSubfolders.setLabelFor(chckbxNewCheckBox);
		chckbxNewCheckBox.setBounds(385, 287, 21, 23);
		panelStoreConfig.add(chckbxNewCheckBox);
	}
}
