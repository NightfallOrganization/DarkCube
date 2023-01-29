/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.standard;

import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerEnderpearlLaunchable extends Listener<LaunchableInteractEvent> {

	public void enable() {
//		disable = false;
//		new Scheduler() {
//			@Override
//			public void run() {
//				if (disable) {
//					cancel();
//					return;
//				}
//				for (User user : Main.getInstance().getUserWrapper().getUsers()) {
//					if (user.getTeam().getType() != TeamType.SPECTATOR)
//						user.getEnderPearl().setItem();
//				}
//			}
//		}.runTaskTimer(1);
	}

	public void disable() {
//		disable = true;
	}

	@Override
	@EventHandler
	public void handle(LaunchableInteractEvent e) {
		if (e.getEntityType() == EntityType.ENDER_PEARL) {
			Player p = e.getPlayer();
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());

			ItemStack item = e.getItem();

			if (item == null)
				return;
			if (!Item.DEFAULT_PEARL.getItemId().equals(ItemManager.getItemId(item))
					&& !Item.DEFAULT_PEARL_COOLDOWN.getItemId().equals(ItemManager.getItemId(item))) {
				return;
			}

			if (user.getEnderPearl().getCooldown() > 0
					|| !p.getInventory().contains(Material.WOOL, user.getEnderPearl().getCost())) {
				if (e.getEntity() != null) {
					e.getEntity().remove();
					this.setItem(user);
				} else {
					this.deny(user);
				}
				e.setCancelled(true);
				return;
			}
			ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), user.getEnderPearl().getCost());

			if (e.getEntity() == null) {
				p.launchProjectile(EnderPearl.class);
				e.setCancelled(true);
			}
			new Scheduler() {

				int cd = user.getEnderPearl().getMaxCooldown();

				@Override
				public void run() {
					user.getEnderPearl().setCooldown(this.cd);
					if (this.cd <= 0) {
						this.cancel();
						return;
					}
					this.cd--;
				}

			}.runTaskTimer(20);
		}
	}

	private void deny(User user) {
		Ingame.playSoundNotEnoughWool(user);
		this.setItem(user);
	}

	private void setItem(User user) {
		new Scheduler() {

			@Override
			public void run() {
				user.getEnderPearl().setItem();
			}

		}.runTask();
	}

}
