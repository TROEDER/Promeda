package ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MassImgImpWzrdView extends JFrame {

	private MassImgImpWzrdController controller;

	private JPanel contentPane;
	public JButton btnCardBack;
	public JButton btnCardNext;
	public JPanel panelContentContainer;
	public CardLayout cardLayoutContentContainer;
	public JPanel panelCardSourceFiles;
	public JPanel panelCardTargetStores;
	public JPanel panelCardSummary;
	public JPanel panelCardProcessing;
	public JLabel labelProgressStep;
	public JProgressBar progressBar;
	public JLabel labelProgressThumb;
	public CheckBoxList checkBoxListStores;
	public JButton btnSelectAll;
	public JButton btnDeselectAll;
	public FileList fileListSourceFiles;
	public JButton btnAddFiles;
	public JButton btnRemoveFiles;
	public JButton btnClearFileList;
	public FileList fileListSourceFilesSummary;
	public StoreList storeListTargetStoresSummary;
	public JLabel labelLoadManMoving;
	public JTextField textFieldProductsCsv;
	private JPanel panelCardImageOptions;
	private JLabel lblImageOptions;
	private JLabel lblSetupYourPreferred;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;

	/**
	 * Create the frame.
	 */
	public MassImgImpWzrdView(MassImgImpWzrdController controller) {
		setTitle("Mass Image Import Wizard");

		this.controller = controller;

		setResizable(false);
		setSize(new Dimension(436, 567));
		setPreferredSize(new Dimension(436, 426));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 436, 426);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelContentContainer = new JPanel();
		panelContentContainer.setBounds(0, 0, 430, 330);
		contentPane.add(panelContentContainer);
		cardLayoutContentContainer = new CardLayout();
		panelContentContainer.setLayout(cardLayoutContentContainer);

		panelCardSourceFiles = new JPanel();
		panelCardSourceFiles.addComponentListener(controller);
		panelContentContainer.add(panelCardSourceFiles, "panelCardSourceFiles");
		panelCardSourceFiles.setBackground(new Color(255, 255, 255));
		panelCardSourceFiles.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 74, 260, 91);
		panelCardSourceFiles.add(scrollPane);

		fileListSourceFiles = new FileList();
		fileListSourceFiles.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		fileListSourceFiles.setFont(new Font("Segoe UI", Font.BOLD, 12));
		fileListSourceFiles.setBackground(new Color(240, 248, 255));
		scrollPane.setViewportView(fileListSourceFiles);

		btnAddFiles = new JButton("Add");
		btnAddFiles.addActionListener(controller);
		btnAddFiles.setBounds(280, 74, 119, 23);
		panelCardSourceFiles.add(btnAddFiles);

		btnRemoveFiles = new JButton("Remove");
		btnRemoveFiles.addActionListener(controller);
		btnRemoveFiles.setBounds(280, 108, 119, 23);
		panelCardSourceFiles.add(btnRemoveFiles);

		JLabel lblSelectImages = new JLabel("Select Images");
		lblSelectImages.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblSelectImages.setBounds(10, 11, 410, 27);
		panelCardSourceFiles.add(lblSelectImages);

		JLabel lblAddYourSourcefiles = new JLabel("Add your source-files (.psd) to the import-list");
		lblAddYourSourcefiles.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblAddYourSourcefiles.setBounds(10, 40, 410, 23);
		panelCardSourceFiles.add(lblAddYourSourcefiles);

		btnClearFileList = new JButton("Clear List");
		btnClearFileList.setEnabled(false);
		btnClearFileList.addActionListener(controller);
		btnClearFileList.setBounds(280, 142, 119, 23);
		panelCardSourceFiles.add(btnClearFileList);
		
		textFieldProductsCsv = new JTextField();
		textFieldProductsCsv.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Products Import CSV", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		textFieldProductsCsv.setBounds(10, 189, 260, 39);
		panelCardSourceFiles.add(textFieldProductsCsv);
		textFieldProductsCsv.setColumns(10);
		
		JLabel lblForNameConventions = new JLabel("For name conventions please visit:");
		lblForNameConventions.setForeground(new Color(102, 102, 102));
		lblForNameConventions.setBounds(10, 289, 389, 14);
		panelCardSourceFiles.add(lblForNameConventions);
		
		JLabel lblHttpdokuwikidokuphpidresponsiveshopbilder = new JLabel("http://192.168.25.17/dokuwiki/doku.php?id=responsive_shop#bilder");
		lblHttpdokuwikidokuphpidresponsiveshopbilder.setForeground(new Color(51, 0, 255));
		lblHttpdokuwikidokuphpidresponsiveshopbilder.setBounds(10, 305, 389, 14);
		panelCardSourceFiles.add(lblHttpdokuwikidokuphpidresponsiveshopbilder);
		
		textField_2 = new JTextField();
		textField_2.setBorder(new TitledBorder(null, "Banner Foldername", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		textField_2.setColumns(10);
		textField_2.setBounds(10, 239, 389, 39);
		panelCardSourceFiles.add(textField_2);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnBrowse.setBounds(280, 201, 119, 23);
		panelCardSourceFiles.add(btnBrowse);

		panelCardTargetStores = new JPanel();
		panelCardTargetStores.addComponentListener(controller);
		
		panelCardImageOptions = new JPanel();
		panelCardImageOptions.setLayout(null);
		panelCardImageOptions.setBackground(Color.WHITE);
		panelContentContainer.add(panelCardImageOptions, "name_1656368493048464");
		
		lblImageOptions = new JLabel("Image options");
		lblImageOptions.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblImageOptions.setBounds(10, 11, 410, 27);
		panelCardImageOptions.add(lblImageOptions);
		
		lblSetupYourPreferred = new JLabel("Setup your preferred image sizes.");
		lblSetupYourPreferred.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblSetupYourPreferred.setBounds(10, 40, 410, 23);
		panelCardImageOptions.add(lblSetupYourPreferred);
		
		JLabel lblDim = new JLabel("Custom Dimension");
		lblDim.setBounds(20, 89, 102, 20);
		panelCardImageOptions.add(lblDim);
		
		textField = new JTextField();
		textField.setBounds(132, 89, 77, 20);
		panelCardImageOptions.add(textField);
		textField.setColumns(10);
		
		JLabel lblPx = new JLabel("px");
		lblPx.setBounds(212, 89, 21, 20);
		panelCardImageOptions.add(lblPx);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(243, 89, 77, 20);
		panelCardImageOptions.add(textField_1);
		
		JLabel label = new JLabel("px");
		label.setBounds(325, 89, 21, 20);
		panelCardImageOptions.add(label);
		
		JLabel lblBreite = new JLabel("Breite");
		lblBreite.setBounds(132, 74, 46, 14);
		panelCardImageOptions.add(lblBreite);
		
		JLabel lblHhe = new JLabel("H\u00F6he");
		lblHhe.setBounds(243, 74, 46, 14);
		panelCardImageOptions.add(lblHhe);
		
		JButton btnNewButton = new JButton("Add");
		btnNewButton.setBounds(356, 89, 51, 20);
		panelCardImageOptions.add(btnNewButton);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBorder(null);
		scrollPane_4.setBounds(20, 136, 387, 172);
		panelCardImageOptions.add(scrollPane_4);
		
		JList list = new JList();
		list.setBorder(new TitledBorder(null, "Sizes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		scrollPane_4.setViewportView(list);
		panelCardTargetStores.setLayout(null);
		panelCardTargetStores.setBackground(new Color(255, 255, 255));
		panelContentContainer.add(panelCardTargetStores, "panelCardTargetStores");

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(10, 74, 260, 210);
		panelCardTargetStores.add(scrollPane_1);

		checkBoxListStores = new CheckBoxList();
		scrollPane_1.setViewportView(checkBoxListStores);
		checkBoxListStores.setBackground(new Color(240, 248, 255));
		checkBoxListStores.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		btnSelectAll = new JButton("Select All");
		btnSelectAll.addActionListener(controller);
		btnSelectAll.setBounds(280, 74, 119, 23);
		panelCardTargetStores.add(btnSelectAll);

		btnDeselectAll = new JButton("Deselect All");
		btnDeselectAll.addActionListener(controller);
		btnDeselectAll.setBounds(280, 108, 119, 23);
		panelCardTargetStores.add(btnDeselectAll);

		JLabel lblSelectStores = new JLabel("Select Stores");
		lblSelectStores.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblSelectStores.setBounds(10, 11, 389, 27);
		panelCardTargetStores.add(lblSelectStores);

		JLabel lblTheImagesWill = new JLabel("The images will be uploaded to selected stores.");
		lblTheImagesWill.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTheImagesWill.setBounds(10, 40, 389, 23);
		panelCardTargetStores.add(lblTheImagesWill);

		panelCardSummary = new JPanel();
		panelCardSummary.addComponentListener(controller);
		panelCardSummary.setLayout(null);
		panelCardSummary.setBackground(Color.WHITE);
		panelContentContainer.add(panelCardSummary, "panelCardSummary");

		JLabel lblSummary = new JLabel("Summary");
		lblSummary.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblSummary.setBounds(10, 11, 389, 27);
		panelCardSummary.add(lblSummary);

		JLabel label_2 = new JLabel("");
		label_2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		label_2.setBounds(10, 40, 389, 23);
		panelCardSummary.add(label_2);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBackground(new Color(255, 255, 255));
		scrollPane_2.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Files", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 128)));
		scrollPane_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_2.setBounds(10, 74, 186, 206);
		panelCardSummary.add(scrollPane_2);

		fileListSourceFilesSummary = new FileList();
		scrollPane_2.setViewportView(fileListSourceFilesSummary);

		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Stores", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 128)));
		scrollPane_3.setBackground(new Color(255, 255, 255));
		scrollPane_3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_3.setBounds(215, 74, 205, 204);
		panelCardSummary.add(scrollPane_3);

		storeListTargetStoresSummary = new StoreList();
		scrollPane_3.setViewportView(storeListTargetStoresSummary);

		panelCardProcessing = new JPanel();
		panelCardProcessing.addComponentListener(controller);
		panelCardProcessing.setLayout(null);
		panelCardProcessing.setBackground(Color.WHITE);
		panelContentContainer.add(panelCardProcessing, "name_427830841893322");
		
		labelLoadManMoving = new JLabel("");
		labelLoadManMoving.setBounds(10, 87, 48, 48);
		labelLoadManMoving.setIcon(new ImageIcon(getClass().getResource("/img/load-man.png")));
		panelCardProcessing.add(labelLoadManMoving);

		JLabel lblProcessing = new JLabel("Processing...");
		lblProcessing.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblProcessing.setBounds(10, 11, 410, 27);
		panelCardProcessing.add(lblProcessing);

		JLabel lblTheWizard = new JLabel("The wizard is generating and uploading the images.");
		lblTheWizard.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblTheWizard.setBounds(10, 40, 410, 23);
		panelCardProcessing.add(lblTheWizard);

		labelProgressStep = new JLabel("");
		labelProgressStep.setBounds(10, 95, 410, 14);
		panelCardProcessing.add(labelProgressStep);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 109, 410, 27);
		panelCardProcessing.add(progressBar);

		labelProgressThumb = new JLabel("");
		labelProgressThumb.setBorder(new BevelBorder(BevelBorder.RAISED, new Color(224, 255, 255),
				new Color(175, 238, 238), new Color(0, 128, 128), new Color(72, 209, 204)));
		labelProgressThumb.setBounds(10, 147, 100, 100);
		panelCardProcessing.add(labelProgressThumb);

		JPanel panelButtonBar = new JPanel();
		panelButtonBar.setBounds(0, 331, 430, 66);
		contentPane.add(panelButtonBar);
		panelButtonBar.setLayout(null);

		btnCardBack = new JButton("BACK");
		btnCardBack.addActionListener(this.controller);
		btnCardBack.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCardBack.setBounds(12, 12, 98, 43);
		btnCardBack.setVisible(false);
		panelButtonBar.add(btnCardBack);

		btnCardNext = new JButton("NEXT");
		btnCardNext.addActionListener(controller);
		btnCardNext.setOpaque(false);
		btnCardNext.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btnCardNext.setBounds(320, 12, 98, 43);
		panelButtonBar.add(btnCardNext);
	}
}
