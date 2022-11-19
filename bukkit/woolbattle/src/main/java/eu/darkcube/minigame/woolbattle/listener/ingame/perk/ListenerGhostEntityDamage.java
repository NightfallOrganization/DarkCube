package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffectType;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;

public class ListenerGhostEntityDamage extends Listener<EntityDamageByEntityEvent> {

	public Map<User, Integer> attacks = new HashMap<>();

	public void reset(User user, Location loc) {
		user.getBukkitEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
		user.getBukkitEntity().teleport(loc);
		Main.getInstance().getIngame().listenerGhostInteract.ghosts.remove(user);
		this.attacks.remove(user);
		user.getBukkitEntity().setHealth(user.getBukkitEntity().getMaxHealth());
		Main.getInstance().getIngame().setArmor(user);
		ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, user.getBukkitEntity().getLocation(),
				user.getBukkitEntity());
		Main.getInstance().getIngame().listenerDoubleJump.refresh(user.getBukkitEntity());
	}

	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getCause() == DamageCause.FALL) {
			return;
		}
		if (!(e.getEntity() instanceof Zombie)) {
			if (e.getEntity() instanceof Player) {
				User user = Main.getInstance().getUserWrapper().getUser(e.getEntity().getUniqueId());
				if (e.getDamager() instanceof Projectile) {
					if (!(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
						return;
					}
				} else if (!(e.getDamager() instanceof Player)) {
					return;
				}

				User attacker = e.getDamager() instanceof Player
						? Main.getInstance().getUserWrapper().getUser(((Player) e.getDamager()).getUniqueId())
						: Main.getInstance()
								.getUserWrapper()
								.getUser(((Entity) ((Projectile) e.getDamager()).getShooter()).getUniqueId());
				if (attacker.getTeam().getType() == TeamType.SPECTATOR && !attacker.isTrollMode()) {
					e.setCancelled(true);
					return;
				}

				if (Main.getInstance().getIngame().listenerGhostInteract.ghosts.containsKey(user)) {
					Player p = user.getBukkitEntity();
					if (p.getNoDamageTicks() == 0) {
						if (Main.getInstance().getIngame().attack(attacker, user, true)) {
							if (!this.attacks.containsKey(user)) {
								this.attacks.put(user, 1);
								p.setHealth(p.getMaxHealth() / 2);
							} else {
								this.attacks.remove(user);
								p.setHealth(p.getMaxHealth());
								this.reset(user, Main.getInstance().getIngame().listenerGhostInteract.ghosts.remove(user));
							}
							p.damage(0);
							p.setNoDamageTicks(10);
							if (e.getDamager() instanceof Projectile) {
								attacker.getBukkitEntity()
										.playSound(attacker.getBukkitEntity().getLocation(), Sound.SUCCESSFUL_HIT, 1,
												0);
							}
						}
					}
					e.setCancelled(true);
				}
			}
			return;
		}
	}
}