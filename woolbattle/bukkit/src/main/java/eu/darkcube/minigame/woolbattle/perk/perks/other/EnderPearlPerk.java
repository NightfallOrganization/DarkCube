/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.other;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventEnderPearl;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.user.CooldownUserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EnderPearlPerk extends Perk {
	public static final PerkName ENDERPEARL = new PerkName("ENDERPEARL");

	public EnderPearlPerk() {
		super(ActivationType.MISC, ENDERPEARL, 5, 8, Item.DEFAULT_PEARL,
				(user, perk, id, perkSlot) -> new CooldownUserPerk(user, id, perkSlot, perk,
						Item.DEFAULT_PEARL_COOLDOWN));
		addListener(new ListenerEnderpearl(this));
	}

	public static class ListenerEnderpearl extends BasicPerkListener {
		public ListenerEnderpearl(Perk perk) {
			super(perk);
		}

		@Override
		protected boolean activateRight(UserPerk perk) {
			EnderPearl enderPearl =
					perk.owner().getBukkitEntity().launchProjectile(EnderPearl.class);
			enderPearl.setMetadata("perk", new FixedMetadataValue(WoolBattle.instance(), perk));
			return true;
		}

		@EventHandler
		public void handle(EntityDamageByEntityEvent event) {
			if (!(event.getEntity() instanceof Player))
				return;
			if (!(event.getDamager() instanceof EnderPearl))
				return;
			WBUser user = WBUser.getUser((Player) event.getEntity());
			EnderPearl enderPearl = (EnderPearl) event.getDamager();
			if (!enderPearl.hasMetadata("perk"))
				return;
			if (!(enderPearl.getMetadata("perk").get(0).value() instanceof UserPerk))
				return;
			UserPerk perk = (UserPerk) enderPearl.getMetadata("perk").get(0).value();
			if (!perk.perk().equals(perk()))
				return;
			if (user.projectileImmunityTicks() == 0) {
				if (!WoolBattle.instance().getIngame().attack(perk.owner(), user)) {
					event.setCancelled(true);
				}
			}
		}

		@EventHandler
		public void handle(ProjectileHitEvent event) {
			if (!(event.getEntity() instanceof EnderPearl))
				return;
			EnderPearl ep = (EnderPearl) event.getEntity();
			if (!ep.hasMetadata("perk"))
				return;
			if (!(ep.getMetadata("perk").get(0).value() instanceof UserPerk))
				return;
			UserPerk perk = (UserPerk) ep.getMetadata("perk").get(0).value();
			if (!perk.perk().equals(perk()))
				return;
			Block b1 = ep.getLocation().getBlock();
			Block b2 = ep.getLocation().add(0, 1, 0).getBlock();
			Block b3 = ep.getLocation().add(0, 2, 0).getBlock();
			boolean elevate = checkTPUp(b1) || checkTPUp(b2) || checkTPUp(b3);

			EventEnderPearl epe = new EventEnderPearl(perk.owner(),
					b1.getType() != Material.AIR || b2.getType() != Material.AIR
							|| b3.getType() != Material.AIR, elevate);
			Bukkit.getPluginManager().callEvent(epe);
			elevate = epe.elevate();

			if (epe.canElevate() && elevate) {
				Block block = b1;
				for (int i = 0; i < 16 && block.getType() == Material.AIR; i++) {
					block = block.getRelative(0, 1, 0);
				}
				if (block.getType() == Material.AIR) {
					return;
				}
				do {
					block = block.getRelative(0, 1, 0);
				} while (block.getType() != Material.AIR
						|| block.getRelative(0, 1, 0).getType() != Material.AIR);
				tp(perk.owner().getBukkitEntity(), block.getLocation().add(0.5, 0.1, 0.5));
			}
		}

		private void tp(Player p, Location loc) {
			new Scheduler() {
				@Override
				public void run() {
					loc.setDirection(p.getLocation().getDirection());
					p.teleport(loc);
				}
			}.runTask();
		}

		private boolean checkTPUp(Block b) {
			return b.getType() == Material.GLASS || b.getType() == Material.STAINED_GLASS;
		}
	}

}
