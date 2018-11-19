/**
 * Die Klasse stellt ein Panel zur Darstellung von Bildern bereit, auf dem am oberen Bildrand  bei
 * &Uuml;berfahren mit der Maus ein Menu eingeblendet wird, das beim Entfernen der Maus wieder
 * ausgeblendet wird.
 */
package de.yourwebs.test.transparentPanel;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/*
 * @JB_BlurMenu.java     14.02.2010
 * build: 14.02.2010
 * Copyright 2010 yourwebs.de. All rights reserved
 * @author J&ouml;rg Czeschla
 * 
 * This file is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along with Test;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA. 
 */
@SuppressWarnings("serial")
public class JB_BlurMenu extends JPanel implements MouseListener,
		MouseMotionListener {

	private float alpha = 0f;
	private ImgLabel nextLabel, previousLabel;
	
	// Geschwindigkeit der Menu-Einblendung. Je hoeher, desto langsamer
	private int delay = 10;

	public JB_BlurMenu() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setOpaque(false);
		
		// Menu-Icons
		nextLabel = new ImgLabel(
				getImage("previous.png"));
		nextLabel.addMouseListener(this);
		add(nextLabel);
		previousLabel = new ImgLabel(
				getImage("next.png"));
		previousLabel.addMouseListener(this);
		add(previousLabel);
		// Event Handling
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * Steuert die Transparenz des Panels
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		Composite alphaComp = AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, alpha);

		g2d.setComposite(alphaComp);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getAlpha() {
		System.out.println(alpha);
		return alpha;
	}

	/**
	 * L&auml;dt die Image-Objekte der Bilder
	 * 
	 * @param name
	 *            String, Pfad der Bilddatei im CLASSPATH (mit Package-Struktur)
	 * @return
	 */
	private Image getImage(String name) {
		Image img = null;
		try {
			img = ImageIO.read(getClass().getClassLoader().getResource(name));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		// Intergrundbild
		ImageIcon icon = new ImageIcon("test.jpg");
		final Dimension dimension = new Dimension(icon.getIconWidth(), icon
				.getIconHeight());

		// Hintergrundbild
		JLabel bildLabel = new JLabel(icon);
		bildLabel.setIcon(icon);
		bildLabel.setSize(dimension);
		bildLabel.setOpaque(true);

		// Transparenzpanel
		final JB_BlurMenu transparentPanel = new JB_BlurMenu();
		transparentPanel.setSize(dimension.width, 50);
		
		// LayeredPane erzeugen und mit HinterGrund-Bild und Panel belegen
		JLayeredPane layeredPane = new JLayeredPane();
		frame.setPreferredSize(dimension);
		layeredPane.setSize(dimension);
		layeredPane.setPreferredSize(dimension);
		layeredPane.add(bildLabel, new Integer(0));
		layeredPane.add(transparentPanel, new Integer(1));

		// alles zusammenbasteln
		frame.setLayeredPane(layeredPane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	} // Ende main()
	
	/************ Event Handling *************/

	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == nextLabel) System.out.println("nextLabel");
		if(e.getSource() == previousLabel) System.out.println("previousLabel");
	}

	public void mouseEntered(MouseEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (float f = alpha; f < 1.0f; f += 0.01f) {
					JB_BlurMenu.this.setAlpha(f);
					JB_BlurMenu.this.update(JB_BlurMenu.this
							.getGraphics());
					try {
						Thread.sleep(delay);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	public void mouseExited(MouseEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				for (float f = alpha; f > 0f; f -= 0.001f) {
					JB_BlurMenu.this.setAlpha(f);
					JB_BlurMenu.this.repaint();
				}
			}
		});
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}
}

@SuppressWarnings( { "serial" })
class ImgLabel extends JLabel {

	public ImgLabel(Image im) {
		super(new ImageIcon(im));
		setSize(im.getWidth(this), im.getWidth(this));
		setOpaque(false);
	}
}
