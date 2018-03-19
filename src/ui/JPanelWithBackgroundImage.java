package ui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class JPanelWithBackgroundImage extends JPanel {

	private Image backgroundImage;

	public JPanelWithBackgroundImage() {

	}

	public JPanelWithBackgroundImage(String fileName) {
		try {
			backgroundImage = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Draw the background image.
		g.drawImage(backgroundImage, 0, 0, this);
	}

}
