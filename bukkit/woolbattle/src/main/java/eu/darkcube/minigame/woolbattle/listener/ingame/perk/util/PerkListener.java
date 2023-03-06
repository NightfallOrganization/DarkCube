/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public abstract class PerkListener implements Listener {

	private final Perk perk;

	public PerkListener(Perk perk) {
		this.perk = perk;
	}

	/**
	 * @param perk the perk to check
	 *
	 * @return if the given perk is usable
	 */
	public static boolean checkUsable(UserPerk perk) {
		WBUser user = perk.owner();
		if (user.woolCount() < perk.perk().cost() || perk.cooldown() > 0) {
			WoolBattle.getInstance().getIngame().playSoundNotEnoughWool(user);
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
	public static boolean checkUsable(WBUser user, ItemStack usedItem, Perk perk,
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

	public static void payForThePerk(UserPerk perk) {
		payForThePerk(perk.owner(), perk.perk());
	}

	public static void payForThePerk(WBUser user, Perk perk) {
		user.removeWool(perk.cost());
	}

	public Perk perk() {
		return perk;
	}

}
