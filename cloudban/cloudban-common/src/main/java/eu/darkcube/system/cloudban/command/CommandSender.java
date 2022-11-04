package eu.darkcube.system.cloudban.command;

import java.util.UUID;

public interface CommandSender {

	boolean hasPermission(String permission);

	void sendMessage(String message);

	void sendMessage(String... message);

	String getName();
	
	UUID getUniqueId();

}
