/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.concurrent.atomic.AtomicReference;

public class ListenerGrapplingHook extends BasicPerkListener {
	private final Handle handle = new Handle();

	public ListenerGrapplingHook(Perk perk) {
		super(perk);
	}

	@Override
	public void registered() {
		super.registered();
		WoolBattle.registerListeners(handle);
	}

	@Override
	public void unregistered() {
		WoolBattle.unregisterListeners(handle);
		super.unregistered();
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		FishHook hook = perk.owner().getBukkitEntity().launchProjectile(FishHook.class);
		hook.setVelocity(hook.getVelocity().multiply(1.5));
		hook.setMetadata("perk", new FixedMetadataValue(WoolBattle.getInstance(), perk));
		return false;
	}

	@Override
	protected boolean mayActivate() {
		return false;
	}

	@EventHandler
	public void handle(PlayerFishEvent event) {
		FishHook hook = event.getHook();
		PlayerFishEvent.State state = event.getState();
		if (!hook.hasMetadata("perk"))
			return;
		Object objectPerk = hook.getMetadata("perk").get(0).value();
		if (!(objectPerk instanceof UserPerk))
			return;
		UserPerk perk = (UserPerk) objectPerk;
		if (!perk.perk().equals(perk()))
			return;
		if (state == PlayerFishEvent.State.IN_GROUND || state == PlayerFishEvent.State.CAUGHT_ENTITY
				|| hook.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
			if (!checkUsable(perk)) {
				hook.remove();
				return;
			}
			Location from = perk.owner().getBukkitEntity().getLocation();
			Location to = hook.getLocation();
			Vector v = to.toVector().subtract(from.toVector()).add(new Vector(0, 3, 0));
			double multiplier = Math.pow(v.length(), 0.35);
			v = v.normalize().multiply(new Vector(multiplier, multiplier * 0.9, multiplier));
			if (v.getY() > 0) {
				v = v.multiply(new Vector(1.2, 1.4, 1.2));
			}
			perk.owner().getBukkitEntity().setVelocity(v);
			hook.remove();
			activated(perk);
		}
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof FishHook) {
			if (!event.getDamager().hasMetadata("perk"))
				return;
			Object operk = event.getDamager().getMetadata("perk").get(0).value();
			if (operk instanceof UserPerk) {
				if (((UserPerk) operk).perk().equals(perk())) {
					event.setCancelled(true);
				}
			}
		}
	}

	private class Handle implements Listener {

		@EventHandler
		private void handle(ProjectileLaunchEvent event) {
			if (!(event.getEntity() instanceof FishHook)) {
				return;
			}
			if (!(event.getEntity().getShooter() instanceof Player))
				return;
			WBUser user = WBUser.getUser((Player) event.getEntity().getShooter());
			ItemStack item = user.getBukkitEntity().getItemInHand();
			if (item == null)
				return;
			AtomicReference<UserPerk> refUserPerk = new AtomicReference<>();
			if (!checkUsable(user, item, perk(), userPerk -> {
				refUserPerk.set(userPerk);
				userPerk.currentPerkItem().setItem();
				new Scheduler(userPerk.currentPerkItem()::setItem).runTask();
			})) {
				return;
			}
			UserPerk userPerk = refUserPerk.get();
			event.getEntity().setMetadata("perk",
					new FixedMetadataValue(WoolBattle.getInstance(), userPerk));
			event.getEntity().setVelocity(event.getEntity().getVelocity().multiply(1.5));
		}
	}
}
