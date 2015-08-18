package org.workbench.tool.res;

import org.w3c.dom.Element;

public class Misc {
	public static String xmlGetString(String string, Element attrib) {
		String s = "";
		try {
			s = attrib.getAttribute(string);
		} catch (Exception e) {
		}
		return s;
	}

	public static Boolean xmlGetBoolean(Boolean def, String string, Element attrib) {
		Boolean s = def;
		try {
			s = Boolean.parseBoolean(attrib.getAttribute(string));
		} catch (Exception e) {
		}
		return s;
	}
}
