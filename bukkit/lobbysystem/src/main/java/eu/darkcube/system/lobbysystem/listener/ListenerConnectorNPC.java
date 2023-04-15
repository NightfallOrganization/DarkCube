/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.libs.com.github.juliarn.npc.NPC;
import eu.darkcube.system.libs.com.github.juliarn.npc.event.PlayerNPCInteractEvent;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import org.bukkit.event.EventHandler;

public class ListenerConnectorNPC extends BaseListener {
	@EventHandler
	public void handle(PlayerNPCInteractEvent e) {
		NPC npc = e.getNPC();
		ConnectorNPC n = ConnectorNPC.get(npc);
		if (n == null)
			return;
		n.connect(e.getPlayer());
	}
}
