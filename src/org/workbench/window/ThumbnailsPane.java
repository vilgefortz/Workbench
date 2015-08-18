package org.workbench.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import org.workbench.exceptions.MissingImageFileException;
import org.workbench.tool.Tool;
import org.workbench.tool.panel.ToolThumbnail;

public class ThumbnailsPane extends JPanel {
	private static final long serialVersionUID = 8447810144814992066L;
	private static final double PADDING = 4;
	private int cHeight;
	private int cWidth;
	ToolContainer tc;
	ToolThumbnail[] thumbs;

	public ThumbnailsPane(ToolContainer toolContainer) {
		this.setLayout(null);
		tc = toolContainer;
	}

	public void createThumbnails()  {
		this.removeAll();
		thumbs = new ToolThumbnail[tc.size()];
		for (int i = 0; i < tc.size(); i++) {
			thumbs[i] = tc.get(i).getThumbnail();
			thumbs[i].setContainerHandler(this);
			this.add(thumbs[i]);
		}
		this.repaint();
	}

	private Rectangle generateBounds(int x, int y, int width, int height) {
		double xPos = cWidth * x;
		double yPos = cHeight * y;
		return new Rectangle((int) (xPos + PADDING), (int) (yPos + PADDING),
				(int) (cWidth - PADDING), (int) (cHeight - PADDING));
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return tc.getName();
	}
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		resized();
		super.paintComponent(g);
	}

	public void resized() {
		int width = ToolContainer.tabbedPane.getWidth()-28;
		int height = ToolContainer.tabbedPane.getHeight()-32;
		cWidth = width / tc.grid.getWidth();
		cHeight = height / tc.grid.getHeight();
				for (int i = 0; i < thumbs.length; i++)
					thumbs[i].setBounds(generateBounds(i % tc.grid.getWidth(),
							i / tc.grid.getHeight(), width, height));
		this.setPreferredSize(new Dimension((int) width, (int) Math
				.ceil(cHeight * (tc.size() / tc.grid.getWidth()))));
	}

	public ToolThumbnail getThumbnailAt(Point point) {
		for (ToolThumbnail th : this.thumbs) {
			if (th.getBounds().contains(point)) return th;
		}
		return null;
	}
	public void switchThumbs(ToolThumbnail src, ToolThumbnail dest)  {

		ToolContainer tc = ToolContainer.containerList.get(this.getName());
		System.out.println ("MOVING!! Size = " + tc.size() );
		for (Tool t : tc) {
			System.out.println ("SWITCHING THUMBS : " + tc.indexOf(t) + " " + t.getName());
		}
		System.out.println(src.getName() + " " +  dest.getName());
		int srcIndex = ToolContainer.main.indexOf(src.getTool());
		int desIndex = ToolContainer.main.indexOf(dest.getTool());
		System.out.println ("SWITCH INDEX " + srcIndex + " " + desIndex);
		ToolContainer.main.set(srcIndex,dest.getTool());
		ToolContainer.main.set(desIndex,src.getTool());
		ToolContainer.refreshTabbedPane();	
	}
}
