package org.workbench.window.misc;

public class Grid {

	private int height;
	private int width;

	public Grid(int i, int j) {
		width = i;
		height = j;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
