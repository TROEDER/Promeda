import java.awt.EventQueue;

import ui.AppController;

public class Launch {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new AppController();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
