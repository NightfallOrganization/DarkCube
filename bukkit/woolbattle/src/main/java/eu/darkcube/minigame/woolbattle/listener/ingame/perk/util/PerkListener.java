package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import java.util.function.BooleanSupplier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public abstract class PerkListener implements Listener {

	// to add a new Perk you need to add perktype, lore for the perk, perk item,
	// perk item lore, and then add the effect
	// lastly register unregister your listener in
	// eu.darkcube.minigame.woolbattle.game.Ingame

	/**
	 * @param user
	 * @param perk
	 * @return if the given perk is usable
	 */
	protected boolean checkUsable(Perk perk) {
		return this.checkUsable(perk, null, null);
	}

	/**
	 * @param user
	 * @param perk
	 * @param successRunnable
	 * @param failureRunnable
	 * @return if the given perk is usable
	 */
	protected boolean checkUsable(Perk perk, Runnable successRunnable, Runnable failureRunnable) {
		User user = perk.getOwner();
		Player p = user.getBukkitEntity();
		if (!p.getInventory().contains(Material.WOOL, perk.getCost()) || perk.getCooldown() > 0) {
			Ingame.playSoundNotEnoughWool(user);
			new Scheduler() {

				@Override
				public void run() {
					perk.setItem();
				}

			}.runTask();
			if (failureRunnable != null)
				failureRunnable.run();
			return false;
		}
		if (successRunnable != null)
			successRunnable.run();
		return true;
	}

	/**
	 * if the perk given by the item is usable
	 * 
	 * @param user
	 * @param perkItem
	 * @return if the check is successful
	 */
	protected boolean checkUsable(User user, Item perkItem) {
		Perk perk = user.getPerkByItemId(perkItem.getItemId());
		return perk != null ? this.checkUsable(perk) : false;
	}

	/**
	 * if the usedItem matches the perkItem and the perk given by the perkItem is usable
	 * 
	 * @param user
	 * @param perkType
	 * @param usedItem
	 * @return if the checks are successful
	 */
	protected boolean checkUsable(User user, PerkType perkType, ItemStack usedItem) {
		return this.checkUsable(user, perkType, usedItem, null);
	}

	/**
	 * if the usedItem matches the perkItem and the perk given by the perkItem is usable
	 * 
	 * Also runs the itemMatchRunnable if the items match
	 * 
	 * @param user
	 * @param perkItem
	 * @param usedItem
	 * @param itemMatchRunnable
	 * @return if the checks are successful
	 */
	protected boolean checkUsable(User user, PerkType perkType, ItemStack usedItem,
			Runnable itemMatchRunnable) {
		String usedItemId = ItemManager.getItemId(usedItem);
		if (perkType.getItem().getItemId().equals(usedItemId)
				|| perkType.getCooldownItem().getItemId().equals(usedItemId)) {
			if (itemMatchRunnable != null)
				itemMatchRunnable.run();
			return this.checkUsable(user, perkType.getItem());
		}
		return false;
	}

	/**
	 * if the perk in the user's hand is usable
	 * 
	 * @param user
	 * @return if the check is successful
	 */
	protected boolean checkPerkInHandUsable(User user) {
		Perk perk =
				user.getPerkByItemId(ItemManager.getItemId(user.getBukkitEntity().getItemInHand()));
		if (perk != null) {
			return this.checkUsable(perk);
		}
		return false;
	}

	protected void payForThePerk(Perk perk) {
		ItemManager.removeItems(perk.getOwner(), perk.getOwner().getBukkitEntity().getInventory(),
				perk.getOwner().getSingleWoolItem(), perk.getCost());
	}

	protected void payForThePerk(User user, PerkType perkType) {
		ItemManager.removeItems(user, user.getBukkitEntity().getInventory(),
				user.getSingleWoolItem(), perkType.getCost());
	}

	protected void startCooldown(User user, PerkType perkType, BooleanSupplier mayCountDown) {
		this.startCooldown(user.getPerkByItemId(perkType.getItem().getItemId()), mayCountDown);
	}

	protected void startCooldown(Perk perk) {
		startCooldown(perk, null);
	}

	protected void startCooldown(User user, PerkType perkType) {
		startCooldown(user, perkType, null);
	}

	protected void startCooldown(Perk perk, BooleanSupplier mayCountDown) {

		// if error add a check for 0 cooldown
		perk.setCooldown(perk.getMaxCooldown());
		new Scheduler() {

			int cd = perk.getMaxCooldown();
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
				if (this.cd <= 0) {
					this.cancel();
					perk.setCooldown(0);
					return;
				}
				perk.setCooldown(this.cd--);
			}

		}.runTaskTimer(1);
	}

}
