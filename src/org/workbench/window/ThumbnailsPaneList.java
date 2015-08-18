package org.workbench.window;

import java.util.ArrayList;

public class ThumbnailsPaneList extends ArrayList<ThumbnailsPane> {

	private static final long serialVersionUID = 3906685910127209132L;

	public ThumbnailsPane get(String name) {
		for (ThumbnailsPane t : this) {
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}
}
