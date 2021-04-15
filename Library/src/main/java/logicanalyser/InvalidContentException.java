package logicanalyser;

public class InvalidContentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidContentException() {
		
	}
	
	public InvalidContentException(String message) {
		super(message);
	}
	
	public InvalidContentException(Throwable cause) {
		super(cause);
	}
	
	public InvalidContentException(String message, Throwable cause) {
		super(message, cause);
	}
}
