package org.workbench.tool.res;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Icons {
	
	public static final String BROWSE = "browse";
	private static final String EXTENSION = "png";
	private static final String RES = "data/res/";

	
	protected static ImageIcon createImageIcon(String path, String description) {
			return new ImageIcon(path, description);
	}
	public static Icon getIcon (String name) {
		return createImageIcon (RES+name+"."+EXTENSION,name);
	}
}

