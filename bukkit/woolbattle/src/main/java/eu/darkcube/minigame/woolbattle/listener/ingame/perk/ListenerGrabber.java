/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGrabber extends BasicPerkListener {

	public ListenerGrabber() {
		super(PerkType.GRABBER);
	}

	private static final String DATA_GRABBED = "grabberGrabbed";
	private static final String DATA_SCHEDULER = "grabberScheduler";
	private static final String DATA_PERK = "grabberPerk";

	@Override
	protected boolean activateRight(User user, Perk perk) {
		if (teleportTarget(user)) {
			return true;
		}
		if (user.getTemporaryDataStorage().has(DATA_SCHEDULER)) {
			return false;
		}
		payForThePerk(perk);
		user.getTemporaryDataStorage().set(DATA_SCHEDULER, new Scheduler() {
			{
				runTaskLater(TimeUnit.SECOND.toTicks(5));
			}

			@Override
			public void run() {
				startCooldown(perk);
				user.getTemporaryDataStorage().remove(DATA_GRABBED);
				user.getTemporaryDataStorage().remove(DATA_SCHEDULER);
				user.getTemporaryDataStorage().<Perk>remove(DATA_PERK).setItem();
			}

			@Override
			public void cancel() {
				super.cancel();
			}
		});
		Player p = user.getBukkitEntity();
		Egg egg = p.getWorld().spawn(p.getEyeLocation(), Egg.class);
		egg.setShooter(p);
		egg.setVelocity(p.getLocation().getDirection().multiply(1.5));
		egg.setMetadata("type", new FixedMetadataValue(WoolBattle.getInstance(), "grabber"));
		user.getTemporaryDataStorage().set(DATA_PERK, perk);
		return false;
	}

	@Override
	protected void activated(User user, Perk perk) {
		startCooldown(perk);
	}

	public static boolean hasTarget(User user) {
		return user.getTemporaryDataStorage().has(DATA_GRABBED);
	}

	public static boolean teleportTarget(User user) {
		if (!hasTarget(user)) {
			return false;
		}
		User grabbed = user.getTemporaryDataStorage().remove(DATA_GRABBED);
		user.getTemporaryDataStorage().<Scheduler>remove(DATA_SCHEDULER).cancel();
		Perk perk = user.getTemporaryDataStorage().<Perk>remove(DATA_PERK);
		perk.setItem();
		grabbed.getBukkitEntity().teleport(user.getBukkitEntity());
		startCooldown(perk);
		return true;
	}

	public static User getTarget(User user) {
		return user.getTemporaryDataStorage().get(DATA_GRABBED);
	}

	public static void setTarget(User user, User target) {
		user.getTemporaryDataStorage().set(DATA_GRABBED, target);
		user.getTemporaryDataStorage().<Perk>get(DATA_PERK).setItem();
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Egg && e.getEntity() instanceof Player
				&& ((Egg) e.getDamager()).getShooter() instanceof Player) {
			Egg egg = (Egg) e.getDamager();
			if (egg.hasMetadata("type")
					&& egg.getMetadata("type").get(0).asString().equals("grabber")) {
				WoolBattle wb = WoolBattle.getInstance();
				Player target = (Player) e.getEntity();
				User tuser = wb.getUserWrapper().getUser(target.getUniqueId());
				Player p = (Player) egg.getShooter();
				User user = wb.getUserWrapper().getUser(p.getUniqueId());
				if (wb.getIngame().attack(user, tuser)
						|| (user.getTeam() == tuser.getTeam() && user != tuser)) {
					if (user.getTeam() == tuser.getTeam() && user != tuser) {
						e.setCancelled(true);
					}
					setTarget(user, tuser);
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
}
