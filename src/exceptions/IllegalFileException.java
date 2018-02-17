package exceptions;

/**
 * Exception to be thrown if a file does not exist or is bad formatted
 */
public class IllegalFileException extends Exception {
	private static final long serialVersionUID = 5600512118798798164L;

	public IllegalFileException() {
		super("The file does not exist or is bad formatted");
	}
}
