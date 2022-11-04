package eu.darkcube.system.commandapi;

public class InvalidCommandException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidCommandException() {
	}

	public InvalidCommandException(Throwable throwable) {
		super(throwable);
	}
	
	public InvalidCommandException(String msg) {
		super(msg);
	}
}
