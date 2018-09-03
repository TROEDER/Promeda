package ui;

public class PageImgImpWzrdController {

	private PageImgImpWzrdView view;
	
	public PageImgImpWzrdController() {
		initView();
	}
	private void initView() {

		view = new PageImgImpWzrdView(this);
		view.setVisible(true);
	}
}
