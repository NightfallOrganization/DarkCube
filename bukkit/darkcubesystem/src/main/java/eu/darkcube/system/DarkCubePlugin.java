/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

import org.bukkit.event.Listener;

public class DarkCubePlugin extends Plugin implements Listener {
	
	@Override
	public String getCommandPrefix() {
		return "§5Dark§dCube";
	}
}
