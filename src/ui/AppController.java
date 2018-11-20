package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AppController implements ActionListener {

	private AppView view;

	public AppController() {
		initView();
	}

	private void initView() {
		view = new AppView(this);
		view.setVisible(true);
	}

	public void initProdImgImpWzrd() {
		new ProdImgImpWzrdController();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == view.btnExit) {
			System.exit(0);
		} else if (ae.getSource() == view.btnProdImgImpWzrd) {
			new ProdImgImpWzrdController();
		} else if (ae.getSource() == view.btnPageImgImpWzrd) {
			new BannerImgImpWzrdController();
		} else if (ae.getSource() == view.btnNewButton) {
			new MassImgImpWzrdController();
		} else if (ae.getSource() == view.btnMenuImageImport) {
			view.popupMenuImageImport.show(view.getContentPane(), view.btnMenuImageImport.getX(), view.btnMenuImageImport.getY() + view.btnMenuImageImport.getHeight());
		} else if (ae.getSource() == view.btnSettings) {
			new SettingsController();
		} else if (ae.getSource() == view.btnSearch || ae.getSource() == view.textFieldProdNr) {
			new ArticleController(view.textFieldProdNr.getText());
		}
	}
}
