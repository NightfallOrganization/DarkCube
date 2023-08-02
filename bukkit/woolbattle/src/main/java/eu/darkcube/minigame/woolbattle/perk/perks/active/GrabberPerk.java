/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class GrabberPerk extends Perk {
	public static final PerkName GRABBER = new PerkName("GRABBER");

	public GrabberPerk() {
		super(ActivationType.ACTIVE, GRABBER, 7, 10, Item.PERK_GRABBER,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.PERK_GRABBER_COOLDOWN));
		addListener(new ListenerGrabber(this));
	}

	public static class ListenerGrabber extends BasicPerkListener {

		private static final Key DATA_GRABBED = new Key(WoolBattle.instance(), "grabber_grabbed");
		private static final Key DATA_SCHEDULER =
				new Key(WoolBattle.instance(), "grabber_scheduler");
		private static final Key DATA_PERK = new Key(WoolBattle.instance(), "perk_grabber");

		public ListenerGrabber(Perk perk) {
			super(perk);
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
			UserPerk perk = user.user().getMetaDataStorage().remove(DATA_PERK);
			perk.currentPerkItem().setItem();
			grabbed.getBukkitEntity().teleport(user.getBukkitEntity());
			perk.cooldown(perk.perk().cooldown().cooldown());
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
					runTaskLater(TimeUnit.SECOND.itoTicks(5));
				}

				@Override
				public void run() {
					perk.cooldown(perk.perk().cooldown().cooldown());
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
			egg.setMetadata("perk",
					new FixedMetadataValue(WoolBattle.instance(), GrabberPerk.GRABBER));
			user.user().getMetaDataStorage().set(DATA_PERK, perk);
			return false;
		}

		@Override
		protected void activated(UserPerk perk) {
			perk.cooldown(perk.perk().cooldown().cooldown());
		}

		@EventHandler
		public void handle(EntityDamageByEntityEvent e) {
			if (e.getDamager() instanceof Egg && e.getEntity() instanceof Player
					&& ((Egg) e.getDamager()).getShooter() instanceof Player) {
				Egg egg = (Egg) e.getDamager();
				if (egg.hasMetadata("perk") && egg.getMetadata("perk").get(0).value()
						.equals(GrabberPerk.GRABBER)) {
					WoolBattle wb = WoolBattle.instance();
					Player target = (Player) e.getEntity();
					WBUser targetUser = WBUser.getUser(target);
					if (targetUser.projectileImmunityTicks() > 0) {
						e.setCancelled(true);
						return;
					}
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

}
