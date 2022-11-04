package eu.darkcube.system.commandapi;

import eu.darkcube.system.Reflection;

public enum Message {

	NO_PERMISSION(Reflection.getFieldValue(
			Reflection.getField(Reflection.getClass("org.spigotmc.SpigotConfig"), "unknownCommandMessage"), null)
			.toString());

	private String msg;

	Message(String msg) {
		this.msg = msg;
	}

	public void setMessage(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
}
