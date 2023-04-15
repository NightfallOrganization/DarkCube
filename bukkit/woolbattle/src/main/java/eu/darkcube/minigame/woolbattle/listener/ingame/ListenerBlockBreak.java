/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class ListenerBlockBreak extends Listener<BlockBreakEvent> {

	@Override
	@EventHandler
	public synchronized void handle(BlockBreakEvent e) {
		Player p = e.getPlayer();
		WBUser user = WBUser.getUser(p);
		Block block = e.getBlock();
		e.setExpToDrop(0);
		if (!user.isTrollMode()) {
			if (user.getTeam().getType() == TeamType.SPECTATOR) {
				e.setCancelled(true);
				return;
			}
		} else {
			WoolBattle.instance().getIngame().destroy(block, true);
			return;
		}
		Material type = block.getType();
		if (type == Material.WOOL) {
			WoolBattle.instance().getIngame().destroy(block, true);
			int tryadd = user.getWoolBreakAmount();
			int added = user.addWool(tryadd);
			if (added != 0) {
				playSound(user.getBukkitEntity());
			}
			return;
		}
		if (!WoolBattle.instance().getIngame().placedBlocks.contains(block)) {
			e.setCancelled(true);
		}
	}

	private void playSound(Player p) {
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
	}
}
