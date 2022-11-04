package eu.darkcube.system.commandapi;

public class CommandNotFoundException extends Exception {
	
	private static final long serialVersionUID = -2980710899154740069L;
	
	
	private String message;
	public CommandNotFoundException(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return getMessage();
	}
	
	@Override
	public String getMessage() {
		return message + "\nValue: " + super.getMessage();
	}
	
}
