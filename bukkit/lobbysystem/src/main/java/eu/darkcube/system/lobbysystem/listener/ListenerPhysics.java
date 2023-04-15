/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerPhysics extends BaseListener {

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL) {
			e.setCancelled(true);
			if (e.getClickedBlock()
					.equals(Lobby.getInstance().getDataManager().getJumpAndRunPlate().getBlock())) {
				if (Lobby.getInstance().getDataManager().isJumpAndRunEnabled())
					UserWrapper.fromUser(UserAPI.getInstance().getUser(e.getPlayer())).startJaR();
			}
		}
	}

	@EventHandler
	public void handle(BlockPhysicsEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFadeEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFormEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockSpreadEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockFromToEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockGrowEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(LeavesDecayEvent e) {
		e.setCancelled(true);
	}
}
