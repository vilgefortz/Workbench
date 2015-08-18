package org.workbench.tool.panel;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.workbench.exceptions.MissingImageFileException;
import org.workbench.tool.Tool;
import org.workbench.window.ThumbnailsPane;

public class ToolThumbnail extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	BufferedImage image;

	private Tool t;
	private ThumbnailsPane thumbnailsPane;
	private boolean dragging;
	public ToolThumbnail(Tool t) {
		this.setComponentPopupMenu(new ToolPopupMenu(t));
		t.thumbnail = this;
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED), t.getName()));
		this.t = t;
		try {
			image = ImageIO.read(new File(t.imageName));
		} catch (IOException ex) {
			image = null;
		}
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED), this.t.getName()));
		this.revalidate();
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON1) {
		if (t.isDummy()){
			JFrame f = t.getNewToolCreationWindow();
			if (f!=null)f.setVisible(true);
		}
		else {
			
			JFrame f = t.getToolEditWindow();
			if (f!=null)f.setVisible(true);
		}
		
		}
		
	} 

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED), t.getName()));

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED), t.getName()));
		setDraggingCursor(false);
		
		if (isDragging ()) {
			setDragging (false);
			Point p = new Point ();
			p.x = (int) (e.getPoint().getX() + this.getBounds().getLocation().getX());
			p.y = (int) (e.getPoint().getY() + this.getBounds().getLocation().getY());
			ToolThumbnail dest = thumbnailsPane.getThumbnailAt(p);
			if (dest!=null && dest != this) { //destination exist and is not the same object reference
				thumbnailsPane.switchThumbs (this,dest);
			}
		}
	}

	private boolean isDragging() {
		// TODO Auto-generated method stub
		return dragging;
	}
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		if (image != null) { 
			try{
			BufferedImage scImg;
			double centerx = (this.getWidth() - Tool.GAP) / 2;
			double centery = (this.getHeight() - Tool.GAP - Tool.GAPH) / 2;
			if (((double) this.getWidth() - Tool.GAP)
					/ (double) (this.getHeight() - Tool.GAP - Tool.GAPH) < (double) image
					.getWidth() / (double) image.getHeight())
				scImg = Scalr.resize(image, Method.BALANCED, this.getWidth()
						- Tool.GAP);
			else
				scImg = Scalr.resize(image, Method.BALANCED, this.getHeight()
						- Tool.GAP - Tool.GAPH);
			g.drawImage(scImg,
					(int) (Tool.GAP / 2 + centerx - scImg.getWidth() / 2),
					(int) (Tool.GAP / 2 + Tool.GAPH + centery - scImg
							.getHeight() / 2), null); // see
			} catch (Exception e) {
				System.out.println ("supressed exception at resizing");
			}
		}
	}

	public void refreshImage() {
		try {

			image = ImageIO.read(new File(t.imageName));
		} catch (IOException ex) {
			System.out.println("No image at " + t.windowName + " "
					+ t.getName());
			image = null;
		}
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createBevelBorder(BevelBorder.RAISED), t.getName()));
		this.revalidate();
	}

	public void setContainerHandler(ThumbnailsPane thumbnailsPane) {
		this.thumbnailsPane=thumbnailsPane;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		setDraggingCursor(true);
		setDragging(true);
		
	}

	private void setDragging(boolean b) {
		this.dragging = b;
		
	}
	private void setDraggingCursor(boolean b) {
		if (b) thumbnailsPane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else thumbnailsPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public String getName () {
		return t.getName();
	}
	public Tool getTool() {
		return t;
	}
	public ThumbnailsPane getMainPane() {
		return thumbnailsPane;
	}
	public BufferedImage getImage() {
		return image;
	}
}
