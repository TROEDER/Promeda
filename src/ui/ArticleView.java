package ui;

import java.awt.Cursor;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class ArticleView extends JFrame {

	private JPanel contentPane;
	public PsdFileList listPsdFiles;
	public JLabel labelPsdPreview;
	public JButton btnSavePsdFile;
	public JLabel labelSearchQuery;
	public JButton btnRestoreFromPsd;
	public JButton btnRemoveFromLive;

	/**
	 * Create the frame.
	 */
	public ArticleView(ArticleController controller) {
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 776, 467);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 349, 430);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 41, 320, 257);
		panel.add(scrollPane_1);
		
		listPsdFiles = new PsdFileList();
		listPsdFiles.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		listPsdFiles.addListSelectionListener(controller);
		scrollPane_1.setViewportView(listPsdFiles);
		
		JLabel lblMatchesFor = new JLabel("Matches for:");
		lblMatchesFor.setBounds(10, 11, 80, 19);
		panel.add(lblMatchesFor);
		
		labelSearchQuery = new JLabel("XXXXX");
		labelSearchQuery.setFont(new Font("Tahoma", Font.BOLD, 12));
		labelSearchQuery.setBounds(100, 11, 288, 19);
		panel.add(labelSearchQuery);
		
		btnSavePsdFile = new JButton("Save psd-file");
		btnSavePsdFile.addActionListener(controller);
		btnSavePsdFile.setBounds(10, 309, 100, 100);
		panel.add(btnSavePsdFile);
		btnSavePsdFile.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnSavePsdFile.setHorizontalTextPosition(SwingConstants.CENTER);
		btnSavePsdFile.setIconTextGap(10);
//		btnSavePsdFile.setIcon(new ImageIcon(ArticleView.class.getResource("/javax/swing/plaf/metal/icons/ocean/floppy.gif")));
		btnSavePsdFile.setBorder(null);
		
		btnRestoreFromPsd = new JButton("Restore from psd");
		btnRestoreFromPsd.addActionListener(controller);
		btnRestoreFromPsd.setBounds(120, 309, 100, 100);
		panel.add(btnRestoreFromPsd);
		btnRestoreFromPsd.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRestoreFromPsd.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnRestoreFromPsd.setIconTextGap(10);
//		btnRestoreFromPsd.setIcon(new ImageIcon(ArticleView.class.getResource("/javax/swing/plaf/metal/icons/ocean/upFolder.gif")));
		btnRestoreFromPsd.setBorder(null);
		
		btnRemoveFromLive = new JButton("Remove from live");
		btnRemoveFromLive.addActionListener(controller);
		btnRemoveFromLive.setBounds(230, 309, 100, 100);
		panel.add(btnRemoveFromLive);
		btnRemoveFromLive.setIconTextGap(10);
		btnRemoveFromLive.setVerticalTextPosition(SwingConstants.BOTTOM);
//		btnRemoveFromLive.setIcon(new ImageIcon(ArticleView.class.getResource("/javax/swing/plaf/metal/icons/ocean/close.gif")));
		btnRemoveFromLive.setHorizontalTextPosition(SwingConstants.CENTER);
		btnRemoveFromLive.setBorder(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(359, 0, 404, 430);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		labelPsdPreview = new JLabel("");
		labelPsdPreview.setHorizontalAlignment(SwingConstants.CENTER);
		labelPsdPreview.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		labelPsdPreview.setBounds(10, 40, 384, 378);
		panel_1.add(labelPsdPreview);
		
		JLabel lblPreview = new JLabel("Preview");
		lblPreview.setBounds(10, 11, 384, 21);
		panel_1.add(lblPreview);
	}
}
