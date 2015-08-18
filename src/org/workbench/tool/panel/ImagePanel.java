package org.workbench.tool.panel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;

/**
 * @author Lukasz Zielinski
 * 
 */
/*
 * TODO polish zoom and grabbing features
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	BufferedImage img;
	BufferedImage scImg;

	public ImagePanel() {
		super();
		//this.setPreferredSize(new Dimension (200,200));
		

	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (img != null) {
			double centerx = this.getWidth() / 2;
			double centery = this.getHeight() / 2;
			if (((double) this.getWidth()) / (double) (this.getHeight()) < (double) img
					.getWidth() / (double) img.getHeight())
				scImg = Scalr.resize(img, Method.BALANCED, this.getWidth());
			else
				scImg = Scalr.resize(img, Method.BALANCED, this.getHeight());
			g.drawImage(scImg, (int) (centerx - scImg.getWidth() / 2),
					(int) (centery - scImg.getHeight() / 2), null);
													
		}
	}

	public void setImage(String imageName) throws IOException {
		img = ImageIO.read(new File(imageName));
		this.repaint();
	}
}
