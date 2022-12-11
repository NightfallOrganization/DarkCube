/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public abstract class BasicPerkListener extends PerkListener implements RegisterNotifyListener {

	private final Handle handle = new Handle();

	private final PerkType perkType;

	public BasicPerkListener(PerkType perkType) {
		this.perkType = perkType;
	}

	@Override
	public final void registered() {
		WoolBattle.registerListeners(this.handle);
	}

	@Override
	public final void unregistered() {
		WoolBattle.unregisterListeners(this.handle);
	}

	public PerkType getPerkType() {
		return this.perkType;
	}

	/**
	 * Called when the perk is activated
	 * 
	 * @param user
	 * @param perk
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activate(User user, Perk perk) {
		return false;
	}

	/**
	 * Called when the perk is activated with a right click
	 * 
	 * @param user
	 * @param perk
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activateRight(User user, Perk perk) {
		return false;
	}

	/**
	 * Called when the perk is activated with a left click
	 * 
	 * @param user
	 * @param perk
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activateLeft(User user, Perk perk) {
		return false;
	}

	/**
	 * Called when any of the activate methods return true. Default implementation is paying wool
	 * and starting cooldown
	 * 
	 * @param user
	 * @param perk
	 */
	protected void activated(User user, Perk perk) {
		payForThePerk(user, perkType);
		startCooldown(user, perkType);
	}

	private class Handle implements Listener {

		@EventHandler
		private void handle(LaunchableInteractEvent event) {
			ItemStack item = event.getItem();
			if (item == null) {
				return;
			}
			User user = WoolBattle.getInstance().getUserWrapper()
					.getUser(event.getPlayer().getUniqueId());
			if (!checkUsable(user, perkType, item, () -> {
				if (event.getEntity() != null) {
					Perk perk = user.getPerkByItemId(perkType.getItem().getItemId());
					perk.setItem();
					new Scheduler(perk::setItem).runTask();
				}
				event.setCancelled(true);
			})) {
				return;
			}
			Perk perk = user.getPerkByItemId(perkType.getItem().getItemId());
			boolean left = event.getAction() == Action.LEFT_CLICK_AIR
					|| event.getAction() == Action.LEFT_CLICK_BLOCK;
			if (!activate(user, perk)
					&& !(left ? activateLeft(user, perk) : activateRight(user, perk))) {
				return;
			}
			activated(user, perk);
		}

	}

}
