/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.passive.ListenerDoubleJump;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerBlockBreak extends Listener<BlockBreakEvent> {

	@SuppressWarnings("deprecation")
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
			//			if (e.getBlock().getType() != Material.WOOL) {
			WoolBattle.getInstance().getIngame().placedBlocks.remove(e.getBlock());
			e.getBlock().setType(Material.AIR);
			return;
			//			}
		}
		Material type = block.getType();
		if (type == Material.WOOL) {
			Ingame ingame = WoolBattle.getInstance().getIngame();
			if (!ingame.placedBlocks.contains(block)) {
				ingame.breakedWool.put(block, block.getData());
			} else {
				ingame.placedBlocks.remove(block);
			}
			Ingame.resetBlockDamage(block);
			int count = ItemManager.countItems(type, p.getInventory());
			int tryadd = user.getWoolBreakAmount();
			int fullInv = user.getMaxWoolSize();
			int freeSpace = fullInv - count;
			int remaining = tryadd - freeSpace;
			boolean shallAdd = false;
			ItemStack addItem = null;
			if (remaining > 0) {
				if (tryadd - remaining > 0) {
					addItem = new ItemStack(Material.WOOL, tryadd - remaining,
							user.getTeam().getType().getWoolColorByte());
					shallAdd = true;
				}
			} else if (freeSpace > 0) {
				addItem = new ItemStack(Material.WOOL, tryadd,
						user.getTeam().getType().getWoolColorByte());
				shallAdd = true;
			}
			if (shallAdd) {
				p.getInventory().addItem(addItem);
				if (ItemManager.countItems(Material.WOOL, p.getInventory())
						>= ListenerDoubleJump.COST)
					ingame.listenerDoubleJump.refresh(p);
				playSound(p);
			}
			e.getBlock().setType(Material.AIR);
			return;
		}
		if (!WoolBattle.getInstance().getIngame().placedBlocks.contains(block)) {
			e.setCancelled(true);
		}
	}

	private void playSound(Player p) {
		p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 1, 1);
	}
}
