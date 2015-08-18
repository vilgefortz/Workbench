package org.workbench.exceptions;

public class MissingImageFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -867943158172306198L;

	public MissingImageFileException(String value) {
		super ("Missing image of tool : " +value);
	}

}
