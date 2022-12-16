/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.command;

import java.util.UUID;

public interface CommandSender {

	boolean hasPermission(String permission);

	void sendMessage(String message);

	void sendMessage(String... message);

	String getName();
	
	UUID getUniqueId();

}
