package org.workbench.window;

import java.util.ArrayList;

public class ToolContainerList extends ArrayList<ToolContainer> {

	private static final long serialVersionUID = -7281366466124175143L;

	public ToolContainer get (String name) {
		for (ToolContainer t: this) {
			if (t.getName().equals(name)) return t;
		}
		return null;
	}
}
