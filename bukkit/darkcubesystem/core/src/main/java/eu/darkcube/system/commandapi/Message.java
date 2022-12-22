/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import eu.darkcube.system.version.VersionSupport;

public enum Message {

	NO_PERMISSION(VersionSupport.getVersion().getSpigotUnknownCommandMessage());

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
