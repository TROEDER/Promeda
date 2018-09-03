package ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JPanelWithBackgroundImage extends JPanel {

	private Image backgroundImage;

	public JPanelWithBackgroundImage() {

	}

	public JPanelWithBackgroundImage(URL fileURL) {
		try {
			backgroundImage = ImageIO.read(fileURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw the background image.
		g.drawImage(backgroundImage, 0, 0, this);
	}

}
