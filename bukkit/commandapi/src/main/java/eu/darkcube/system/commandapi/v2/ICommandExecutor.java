package eu.darkcube.system.commandapi.v2;

public interface ICommandExecutor {
	
	boolean hasPermission(String permission);
	
	String getName();

	default void sendMessage(String... messages) {
		for(String message : messages) {
			sendMessage(message);
		}
	}

	void sendMessage(String message);
	
	void setMessageFormat(String format);
	
	String getMessageFormat();

}
