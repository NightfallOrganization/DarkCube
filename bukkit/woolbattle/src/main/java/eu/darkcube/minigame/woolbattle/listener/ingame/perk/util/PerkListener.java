/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class PerkListener implements Listener {

	// to add a new Perk you need to add perk type, lore for the perk, perk item,
	// perk item lore, and then add the effect
	// lastly register unregister your listener in
	// eu.darkcube.minigame.woolbattle.game.Ingame

	/**
	 * @param perk the perk to check
	 *
	 * @return if the given perk is usable
	 */
	protected static boolean checkUsable(UserPerk perk) {
		WBUser user = perk.owner();
		Player p = user.getBukkitEntity();
		if (!p.getInventory().contains(Material.WOOL, perk.perk().cost()) || perk.cooldown() > 0) {
			Ingame.playSoundNotEnoughWool(user);
			new Scheduler(perk.currentPerkItem()::setItem).runTask();
			return false;
		}
		return true;
	}

	/**
	 * if the usedItem matches the perkItem and the perk given by the perkItem is usable
	 * <p>
	 * Also runs the itemMatchRunnable if the items match
	 *
	 * @param user              the {@link WBUser}
	 * @param usedItem          the used {@link ItemStack}
	 * @param itemMatchRunnable the runnable to execute if the perk is usable match
	 *
	 * @return if the checks are successful
	 */
	protected static boolean checkUsable(WBUser user, ItemStack usedItem, Perk perk,
			Consumer<UserPerk> itemMatchRunnable) {
		ItemBuilder builder = ItemBuilder.item(usedItem);
		if (!builder.persistentDataStorage().has(PerkItem.KEY_PERK_ID)) {
			return false;
		}
		int perkId =
				builder.persistentDataStorage().get(PerkItem.KEY_PERK_ID, PerkItem.TYPE_PERK_ID);
		UserPerk userPerk = user.perks().perk(perkId);
		if (!userPerk.perk().equals(perk)) {
			return false;
		}
		if (itemMatchRunnable != null)
			itemMatchRunnable.accept(userPerk);
		return checkUsable(userPerk);
	}

	protected static void payForThePerk(UserPerk perk) {
		ItemManager.removeItems(perk.owner(), perk.owner().getBukkitEntity().getInventory(),
				perk.owner().getSingleWoolItem(), perk.perk().cost());
	}

	protected static void payForThePerk(WBUser user, Perk perk) {
		ItemManager.removeItems(user, user.getBukkitEntity().getInventory(),
				user.getSingleWoolItem(), perk.cost());
	}

	protected static void startCooldown(UserPerk perk) {
		startCooldown(perk, null);
	}

	protected static void startCooldown(UserPerk perk, BooleanSupplier mayCountDown) {
		// if error add a check for 0 cooldown
		perk.cooldown(perk.perk().cooldown());
		new Scheduler() {

			int cd = perk.perk().cooldown();
			int waited = 0;

			@Override
			public void run() {
				if (mayCountDown != null && !mayCountDown.getAsBoolean()) {
					return;
				}
				waited++;
				waited = waited % 20;
				if (waited != 0) {
					return;
				}
				perk.cooldown(Math.max(0, --this.cd));
				if (cd <= 0) {
					cancel();
				}
			}

		}.runTaskTimer(1);
	}

}
