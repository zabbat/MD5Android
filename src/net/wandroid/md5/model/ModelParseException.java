package net.wandroid.md5.model;

/**
 * Exception thrown when parsing a 3d model fails.
 * This exception is unchecked, since the code that load should be able to be runned in
 * functions that does not allow checked exceptions 
 * @author Jungbeck
 *
 */
public class ModelParseException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	/**
	 * Sets the message
	 * @param msg the error message
	 */
	public ModelParseException(String msg) {
		super(msg);
	}
	
}
