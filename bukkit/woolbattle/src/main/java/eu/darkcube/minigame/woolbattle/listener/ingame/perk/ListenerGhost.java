/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerGhost extends BasicPerkListener {

	private static final String DATA_GHOSTPOS = "ghostPos";
	private static final String DATA_GHOSTATTACKS = "ghostAttacks";

	public ListenerGhost() {
		super(PerkType.GHOST);
	}

	public static void reset(User user) {
		reset0(user, user.getTemporaryDataStorage().get(DATA_GHOSTPOS));
	}

	public static boolean isGhost(User user) {
		return user.getTemporaryDataStorage().has(DATA_GHOSTPOS);
	}

	@Override
	protected boolean activateRight(User user, Perk perk) {
		if (isGhost(user)) {
			return false;
		}
		Player p = user.getBukkitEntity();
		p.addPotionEffect(
				new PotionEffect(PotionEffectType.INVISIBILITY, 10000000, 200, false, false));
		user.getTemporaryDataStorage().set(DATA_GHOSTPOS, p.getLocation());
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
	protected void activated(User user, Perk perk) {
		payForThePerk(user, getPerkType());
		startCooldown(user, getPerkType(), () -> !isGhost(user));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PlayerQuitEvent e) {
		User user = WoolBattle.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
		user.getTemporaryDataStorage().remove(DATA_GHOSTPOS);
		user.getTemporaryDataStorage().remove(DATA_GHOSTATTACKS);
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			return;
		}
		if (e.getEntity() instanceof Player) {
			User user =
					WoolBattle.getInstance().getUserWrapper().getUser(e.getEntity().getUniqueId());
			if (e.getDamager() instanceof Projectile) {
				if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
					return;
				}
			} else if (!(e.getDamager() instanceof Player)) {
				return;
			}

			User attacker = e.getDamager() instanceof Player
					? WoolBattle.getInstance().getUserWrapper()
							.getUser(((Player) e.getDamager()).getUniqueId())
					: WoolBattle.getInstance().getUserWrapper().getUser(
							((Entity) ((Projectile) e.getDamager()).getShooter()).getUniqueId());
			if (attacker.getTeam().getType() == TeamType.SPECTATOR && !attacker.isTrollMode()) {
				e.setCancelled(true);
				return;
			}

			if (isGhost(user)) {
				Player p = user.getBukkitEntity();
				if (p.getNoDamageTicks() == 0) {
					if (WoolBattle.getInstance().getIngame().attack(attacker, user, true)) {
						if (!user.getTemporaryDataStorage().has(DATA_GHOSTATTACKS)) {
							user.getTemporaryDataStorage().set(DATA_GHOSTATTACKS, 1);
							p.setHealth(p.getMaxHealth() / 2);
						} else {
							p.setHealth(p.getMaxHealth());
							reset(user);
						}
						p.damage(0);
						p.setNoDamageTicks(10);
						if (e.getDamager() instanceof Projectile) {
							attacker.getBukkitEntity().playSound(
									attacker.getBukkitEntity().getLocation(), Sound.SUCCESSFUL_HIT,
									1, 0);
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}

	private static void reset0(User user, Location loc) {
		user.getBukkitEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
		user.getBukkitEntity().teleport(loc);
		user.getTemporaryDataStorage().remove(DATA_GHOSTPOS);
		user.getTemporaryDataStorage().remove(DATA_GHOSTATTACKS);
		user.getBukkitEntity().setHealth(user.getBukkitEntity().getMaxHealth());
		WoolBattle.getInstance().getIngame().setArmor(user);
		ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, user.getBukkitEntity().getLocation(),
				user.getBukkitEntity());
		WoolBattle.getInstance().getIngame().listenerDoubleJump.refresh(user.getBukkitEntity());
	}

}
