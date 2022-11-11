package eu.darkcube.system.commandapi;

import eu.darkcube.system.ReflectionUtils;

public enum Message {

//	NO_PERMISSION(ReflectionUtils.getValue(
//			Reflection.getField(ReflectionUtils.getClass("org.spigotmc.SpigotConfig"), "unknownCommandMessage"), null)
//			.toString());
	NO_PERMISSION(
			ReflectionUtils.getValue(null, "org.spigotmc.SpigotConfig", false, "unknownCommandMessage").toString());

	private String msg;

	Message(String msg) {
		this.msg = msg;
	}

	public void setMessage(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return this.msg;
	}

}
