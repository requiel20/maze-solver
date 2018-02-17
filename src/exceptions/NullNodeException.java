package exceptions;

/**
 * Exception to be thrown if a method which do not allow null nodes as a
 * parameter is called with null nodes as parameters
 */
public class NullNodeException extends Exception {
	private static final long serialVersionUID = 1150093819353493851L;

	public NullNodeException() {
		super("Null nodes are not permitted");
	}

}
