package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

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
	// perk item lore, and thenn add the effect
	/**
	 * @param user
	 * @param perk
	 * @return if the given perk is usable
	 */
	protected boolean checkUsable(User user, Perk perk) {
		Player p = user.getBukkitEntity();
		if (!p.getInventory().contains(Material.WOOL, PerkType.CAPSULE.getCost()) || perk.getCooldown() > 0) {
			Ingame.playSoundNotEnoughWool(user);
			new Scheduler() {

				@Override
				public void run() {
					perk.setItem();
				}

			}.runTask();
			return false;
		}
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
		return perk != null ? this.checkUsable(user, perk) : false;
	}

	/**
	 * if the usedItem matches the perkItem and the perk given by the perkItem is
	 * usable
	 * 
	 * @param user
	 * @param perkItem
	 * @param usedItem
	 * @return if the checks are successful
	 */
	protected boolean checkUsable(User user, Item perkItem, ItemStack usedItem) {
		if (perkItem.getItemId().equals(ItemManager.getItemId(usedItem))) {
			return this.checkUsable(user, perkItem);
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
		Perk perk = user.getPerkByItemId(ItemManager.getItemId(user.getBukkitEntity().getItemInHand()));
		if (perk != null) {
			return this.checkUsable(user, perk);
		}
		return false;
	}

	protected void payForThePerk(Player p, User user, PerkType perkType) {
		ItemManager.removeItems(user, p.getInventory(),
				new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()), perkType.getCost());
	}

	protected void startCooldown(Perk perk) {

		// if error add a check for 0 cooldown

		new Scheduler() {

			int cd = perk.getMaxCooldown();

			@Override
			public void run() {
				if (this.cd <= 0) {
					this.cancel();
					perk.setCooldown(0);
					return;
				}
				perk.setCooldown(this.cd--);
			}

		}.runTaskTimer(20);
	}

}
