/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.GrabberPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ListenerGrabber extends BasicPerkListener {

	private static final Key DATA_GRABBED = new Key(WoolBattle.getInstance(), "grabberGrabbed");
	private static final Key DATA_SCHEDULER = new Key(WoolBattle.getInstance(), "grabberScheduler");
	private static final Key DATA_PERK = new Key(WoolBattle.getInstance(), "grabberPerk");

	public ListenerGrabber() {
		super(GrabberPerk.GRABBER);
	}

	public static boolean hasTarget(WBUser user) {
		return user.user().getMetaDataStorage().has(DATA_GRABBED);
	}

	public static boolean teleportTarget(WBUser user) {
		if (!hasTarget(user)) {
			return false;
		}
		WBUser grabbed = user.user().getMetaDataStorage().remove(DATA_GRABBED);
		user.user().getMetaDataStorage().<Scheduler>remove(DATA_SCHEDULER).cancel();
		UserPerk perk = user.user().getMetaDataStorage().<UserPerk>remove(DATA_PERK);
		perk.currentPerkItem().setItem();
		grabbed.getBukkitEntity().teleport(user.getBukkitEntity());
		startCooldown(perk);
		return true;
	}

	public static WBUser getTarget(WBUser user) {
		return user.user().getMetaDataStorage().get(DATA_GRABBED);
	}

	public static void setTarget(WBUser user, WBUser target) {
		user.user().getMetaDataStorage().set(DATA_GRABBED, target);
		user.user().getMetaDataStorage().<UserPerk>get(DATA_PERK).currentPerkItem().setItem();
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		WBUser user = perk.owner();
		if (teleportTarget(user)) {
			return true;
		}
		if (user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
			return false;
		}
		payForThePerk(perk);
		user.user().getMetaDataStorage().set(DATA_SCHEDULER, new Scheduler() {
			{
				runTaskLater(TimeUnit.SECOND.toTicks(5));
			}

			@Override
			public void cancel() {
				super.cancel();
			}

			@Override
			public void run() {
				startCooldown(perk);
				user.user().getMetaDataStorage().remove(DATA_GRABBED);
				user.user().getMetaDataStorage().remove(DATA_SCHEDULER);
				user.user().getMetaDataStorage().<UserPerk>remove(DATA_PERK).currentPerkItem()
						.setItem();
			}
		});
		Player p = user.getBukkitEntity();
		Egg egg = p.getWorld().spawn(p.getEyeLocation(), Egg.class);
		egg.setShooter(p);
		egg.setVelocity(p.getLocation().getDirection().multiply(1.5));
		egg.setMetadata("type", new FixedMetadataValue(WoolBattle.getInstance(), "grabber"));
		user.user().getMetaDataStorage().set(DATA_PERK, perk);
		return false;
	}

	@Override
	protected void activated(UserPerk perk) {
		startCooldown(perk);
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Egg && e.getEntity() instanceof Player
				&& ((Egg) e.getDamager()).getShooter() instanceof Player) {
			Egg egg = (Egg) e.getDamager();
			if (egg.hasMetadata("type") && egg.getMetadata("type").get(0).asString()
					.equals("grabber")) {
				WoolBattle wb = WoolBattle.getInstance();
				Player target = (Player) e.getEntity();
				WBUser targetUser = WBUser.getUser(target);
				Player p = (Player) egg.getShooter();
				WBUser user = WBUser.getUser(p);
				if (wb.getIngame().attack(user, targetUser) || (
						user.getTeam() == targetUser.getTeam() && user != targetUser)) {
					if (user.getTeam() == targetUser.getTeam() && user != targetUser) {
						e.setCancelled(true);
					}
					setTarget(user, targetUser);
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
}
