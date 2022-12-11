/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;

public class ListenerVoidWorld extends Listener<WorldInitEvent> {

	@Override
	@EventHandler
	public void handle(WorldInitEvent e) {
		WoolBattle.getInstance().loadWorld(e.getWorld());
	}
}
