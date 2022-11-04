package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;

public class ListenerGhostEntityDamage extends Listener<EntityDamageByEntityEvent> {
//	@Override
//	@EventHandler
//	public void handle(EntityDamageEvent e) {
//		if (e.getEntity() instanceof Zombie && e.getCause() != DamageCause.ENTITY_ATTACK
//				&& e.getCause() != DamageCause.PROJECTILE) {
//			Zombie zombie = (Zombie) e.getEntity();
//			if (zombie.hasMetadata("isGhost") && zombie.getMetadata("isGhost").get(0).asBoolean()) {
//				UUID uuid = UUID.fromString(zombie.getMetadata("user").get(0).asString());
//				User user = Main.getInstance().getUserWrapper().getUser(uuid);
//				reset(user, zombie);
//			}
//		}
//	}

	public Map<User, Integer> attacks = new HashMap<>();

	public void reset(User user, Zombie zombie) {
		user.getBukkitEntity().removePotionEffect(PotionEffectType.INVISIBILITY);
		user.getBukkitEntity().teleport(zombie);
		zombie.remove();
		Main.getInstance().getIngame().listenerGhostInteract.ghosts.remove(user);
		attacks.remove(user);
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
							if (!attacks.containsKey(user)) {
								attacks.put(user, 1);
								p.setHealth(p.getMaxHealth() / 2);
							} else {
								attacks.remove(user);
								p.setHealth(p.getMaxHealth());
								reset(user, Main.getInstance().getIngame().listenerGhostInteract.ghosts.remove(user));
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
		Zombie zombie = (Zombie) e.getEntity();
		if (e.getDamager() instanceof Player || e.getDamager() instanceof Projectile) {
			if (e.getDamager() instanceof Projectile
					&& !(((Projectile) e.getDamager()).getShooter() instanceof Player)) {
				return;
			}

			User attacker = e.getDamager() instanceof Player
					? Main.getInstance().getUserWrapper().getUser(((Player) e.getDamager()).getUniqueId())
					: Main.getInstance()
							.getUserWrapper()
							.getUser(((Entity) ((Projectile) e.getDamager()).getShooter()).getUniqueId());
			if (zombie.hasMetadata("isGhost") && zombie.getMetadata("isGhost").size() >= 1
					&& zombie.getMetadata("isGhost").get(0).asBoolean()) {
				UUID uuid = UUID.fromString(zombie.getMetadata("user").get(0).asString());
				User user = Main.getInstance().getUserWrapper().getUser(uuid);
				if (Main.getInstance().getIngame().attack(attacker, user, true)) {
					reset(user, zombie);
					user.getBukkitEntity().damage(0);
					applyKnockback(user.getBukkitEntity(), e.getDamager());
				}
			}
		}
	}

	private void applyKnockback(LivingEntity entity, Entity attacker) {
//		double dist = (attacker instanceof Player && ((Player) attacker).isSprinting()) ? 1.5 : 1;
//		dist += new Random().nextDouble() * 0.4 - 0.2;
//		int kblevel = getKnockBackLevel(attacker);
//		dist += 3 * kblevel;
//		double mag = toMag(dist);
//		Location loc = attacker.getLocation();
//		loc.setPitch(loc.getPitch() - 15);
//		Vector velo = setMag(loc.getDirection(), mag);
//		entity.setVelocity(velo);
		if (attacker instanceof Arrow) {
			entity.setVelocity(attacker.getVelocity()
					.setY(0)
					.normalize()
					.multiply(.47 + new Random().nextDouble() / 70 + ((Arrow) attacker).getKnockbackStrength() / 1.42)
					.setY(.400023));
		} else {
			double mult = attacker instanceof Player && ((Player) attacker).isSprinting() ? 1 : 0;
			if (attacker instanceof Player) {
				ItemStack hand = ((Player) attacker).getItemInHand();
				if (hand != null) {
					if (hand.hasItemMeta() && hand.getItemMeta().hasEnchant(Enchantment.KNOCKBACK)) {
						int level = hand.getItemMeta().getEnchantLevel(Enchantment.KNOCKBACK);
						if (level < 0)
							level = 0;
						mult += level;
					}
				}
			}

			Location ploc = attacker.getLocation();
			Location eloc = entity.getLocation();
			double x = eloc.getX() - ploc.getX();
			double y = 0.400023;
			double z = eloc.getZ() - ploc.getZ();
			entity.setVelocity(new Vector(x, y, z).normalize()
					.multiply(0.47 + new Random().nextDouble() / 70 + mult / 1.42)
					.setY(y));
		}
	}
//
//	private double toMag(double dist) {
//		return ((dist + 1.5) / 5D);
//	}
//
//	private Vector setMag(Vector vector, double mag) {
//		double x = vector.getX();
//		double y = vector.getY();
//		double z = vector.getZ();
//		double denominator = Math.sqrt(x * x + y * y + z * z);
//		if (denominator != 0) {
//			return vector.multiply(mag / denominator);
//		}
//		return vector;
//	}
//
//	private int getKnockBackLevel(Entity entity) {
//		if (entity instanceof LivingEntity) {
//			ItemStack mainHand = ((LivingEntity) entity).getEquipment().getItemInHand();
//			if (mainHand != null && mainHand.hasItemMeta() && mainHand.getItemMeta().hasEnchants()
//					&& mainHand.getItemMeta().hasEnchant(Enchantment.KNOCKBACK)) {
//				return mainHand.getItemMeta().getEnchantLevel(Enchantment.KNOCKBACK);
//			}
//			return 0;
//		} else if (entity instanceof Arrow) {
//			return ((Arrow) entity).getKnockbackStrength();
//		}
//		return 0;
//	}
}