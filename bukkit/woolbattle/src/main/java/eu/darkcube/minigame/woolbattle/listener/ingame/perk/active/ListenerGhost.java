/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.GhostPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerGhost extends BasicPerkListener {

	private static final Key DATA_GHOST_POS = new Key(WoolBattle.getInstance(), "ghostPos");
	private static final Key DATA_GHOST_ATTACKS = new Key(WoolBattle.getInstance(), "ghostAttacks");

	public ListenerGhost() {
		super(GhostPerk.GHOST);
	}

	public static void reset(WBUser user) {
		reset0(user, user.user().getMetaDataStorage().get(DATA_GHOST_POS));
	}

	public static boolean isGhost(WBUser user) {
		return user.user().getMetaDataStorage().has(DATA_GHOST_POS);
	}

	private static void reset0(WBUser user, Location loc) {
		user.getBukkitEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
		user.getBukkitEntity().teleport(loc);
		user.user().getMetaDataStorage().remove(DATA_GHOST_POS);
		user.user().getMetaDataStorage().remove(DATA_GHOST_ATTACKS);
		user.getBukkitEntity().setHealth(user.getBukkitEntity().getMaxHealth());
		WoolBattle.getInstance().getIngame().setArmor(user);
		ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, user.getBukkitEntity().getLocation(),
				user.getBukkitEntity());
		WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(user.getBukkitEntity());
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		WBUser user = perk.owner();
		if (isGhost(user)) {
			return false;
		}
		Player p = user.getBukkitEntity();
		p.addPotionEffect(
				new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 200, false, false));
		user.user().getMetaDataStorage().set(DATA_GHOST_POS, p.getLocation());
		WoolBattle.getInstance().getIngame().setArmor(user);
		p.setMaxHealth(20);

		WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(p);
		new Scheduler() {

			@Override
			public void run() {
				if (!isGhost(user)) {
					this.cancel();
					p.removePotionEffect(PotionEffectType.BLINDNESS);
					p.removePotionEffect(PotionEffectType.SPEED);
					return;
				}
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0, false, false),
						true);
				p.addPotionEffect(
						new PotionEffect(PotionEffectType.BLINDNESS, 15, 10, false, false), true);
			}

		}.runTaskTimer(1);
		return true;
	}

	@Override
	protected void activated(UserPerk perk) {
		payForThePerk(perk);
		startCooldown(perk, () -> !isGhost(perk.owner()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PlayerQuitEvent e) {
		WBUser user = WBUser.getUser(e.getPlayer());
		user.user().getMetaDataStorage().remove(DATA_GHOST_POS);
		user.user().getMetaDataStorage().remove(DATA_GHOST_ATTACKS);
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			WBUser user = WBUser.getUser((Player) e.getEntity());
			if (e.getDamager() instanceof Projectile) {
				if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
					return;
				}
			} else if (!(e.getDamager() instanceof Player)) {
				return;
			}

			WBUser attacker = e.getDamager() instanceof Player
					? WBUser.getUser((Player) e.getDamager())
					: WBUser.getUser((Player) ((Projectile) e.getDamager()).getShooter());
			if (attacker.getTeam().getType() == TeamType.SPECTATOR && !attacker.isTrollMode()) {
				e.setCancelled(true);
				return;
			}

			if (isGhost(user)) {
				Player p = user.getBukkitEntity();
				if (p.getNoDamageTicks() == 0) {
					if (WoolBattle.getInstance().getIngame().attack(attacker, user, true)) {
						if (!user.user().getMetaDataStorage().has(DATA_GHOST_ATTACKS)) {
							user.user().getMetaDataStorage().set(DATA_GHOST_ATTACKS, 1);
							p.setHealth(p.getMaxHealth() / 2);
						} else {
							p.setHealth(p.getMaxHealth());
							reset(user);
						}
						p.damage(0);
						p.setNoDamageTicks(10);
						if (e.getDamager() instanceof Projectile) {
							attacker.getBukkitEntity()
									.playSound(attacker.getBukkitEntity().getLocation(),
											Sound.SUCCESSFUL_HIT, 1, 0);
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}

}