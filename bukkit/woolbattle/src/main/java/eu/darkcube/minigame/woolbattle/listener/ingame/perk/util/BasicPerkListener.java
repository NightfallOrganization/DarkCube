/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.util;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.listener.RegisterNotifyListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicReference;

public abstract class BasicPerkListener extends PerkListener implements RegisterNotifyListener {

	private final Handle handle = new Handle();

	private final Perk perk;

	public BasicPerkListener(PerkName perkName) {
		this(WoolBattle.getInstance().perkRegistry().perks().get(perkName));
	}

	public BasicPerkListener(Perk perk) {
		this.perk = perk;
	}

	@Override
	public void registered() {
		WoolBattle.registerListeners(this.handle);
	}

	@Override
	public void unregistered() {
		WoolBattle.unregisterListeners(this.handle);
	}

	public Perk perk() {
		return perk;
	}

	/**
	 * Called when the perk is activated
	 *
	 * @param perk the perk
	 *
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activate(UserPerk perk) {
		return false;
	}

	/**
	 * Called when the perk is activated with a right click
	 *
	 * @param perk the perk
	 *
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activateRight(UserPerk perk) {
		return false;
	}

	/**
	 * Called when the perk is activated with a left click
	 *
	 * @param perk the perk
	 *
	 * @return if the perk was activated and cooldown should start
	 */
	protected boolean activateLeft(UserPerk perk) {
		return false;
	}

	/**
	 * Called when any of the activate methods return true. Default implementation is paying wool
	 * and starting cooldown
	 *
	 * @param perk the perk
	 */
	protected void activated(UserPerk perk) {
		payForThePerk(perk);
		startCooldown(perk);
	}

	protected boolean mayActivate() {
		return true;
	}

	private class Handle implements Listener {

		@EventHandler
		private void handle(LaunchableInteractEvent event) {
			if (!mayActivate())
				return;
			ItemStack item = event.getItem();
			if (item == null) {
				return;
			}
			WBUser user = WBUser.getUser(event.getPlayer());
			AtomicReference<UserPerk> refUserPerk = new AtomicReference<>();
			if (!checkUsable(user, item, perk(), userPerk -> {
				refUserPerk.set(userPerk);
				if (event.getEntity() != null) {
					event.getEntity().remove();
					userPerk.currentPerkItem().setItem();
					new Scheduler(userPerk.currentPerkItem()::setItem).runTask();
				}
				event.setCancelled(true);
			})) {
				return;
			}
			UserPerk userPerk = refUserPerk.get();
			boolean left = event.getAction() == Action.LEFT_CLICK_AIR
					|| event.getAction() == Action.LEFT_CLICK_BLOCK;
			if (!activate(userPerk) && !(left ? activateLeft(userPerk) : activateRight(userPerk))) {
				return;
			}
			activated(userPerk);
		}

	}

}
