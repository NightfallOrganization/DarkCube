package eu.darkcube.minigame.woolbattle.listener.ingame;

import java.lang.reflect.Field;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerProjectileLaunch extends Listener<ProjectileLaunchEvent> {
	@Override
	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (e.getEntity().getShooter() instanceof Player) {
			Player p = (Player) e.getEntity().getShooter();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			if (e.getEntityType() == EntityType.ARROW) {
				Arrow arrow = (Arrow) e.getEntity();

				if (!p.getInventory().contains(Material.WOOL, 1)) {
					Ingame.playSoundNotEnoughWool(user);
					arrow.remove();
					e.setCancelled(true);
					return;
				}

				Main.getInstance().getIngame().arrows.put(arrow, user);
				ItemManager.removeItems(user, p.getInventory(),
						new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor()), 1);
				arrow.setVelocity(arrow.getVelocity());
				if (user.getPassivePerk().getPerkName().equals(PerkName.FAST_ARROW)) {
					Vector vec = arrow.getVelocity().multiply(1.7);

					double[] ds = new double[] {
							vec.getX(), vec.getY(), vec.getZ()
					};

					double div = 1;
					for (double d : ds) {
						if (d < -4 || d > 4) {
							d = Math.abs(d);
							double m = d / 4.0;
							if (div < m) {
								div = m;
							}
						}
					}
					vec = vec.divide(new Vector(div, div, div)).multiply(0.999);
					if (vec.length() > 4) {
						vec = vec.normalize().multiply(4);
					}

					arrow.setVelocity(vec);
				}
				if (user.getPassivePerk().getPerkName().equals(PerkType.ARROW_RAIN.getPerkName())) {
					int arrowCount = 6;
					arrowCount = (arrowCount / 2) * 2;

					if (user.getPassivePerk().getCooldown() > 0) {
						user.getPassivePerk().setCooldown(user.getPassivePerk().getCooldown() - 1);
						return;
					}
					if (!p.getInventory().contains(Material.WOOL, PerkType.ARROW_RAIN.getCost())) {
						new Scheduler() {
							@Override
							public void run() {
								user.getPassivePerk().setItem();
							}
						}.runTask();
						return;
					}

					int cost = PerkType.ARROW_RAIN.getCost() - arrowCount;
					ItemStack wool = new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor());

					if (cost < 0) {
						wool.setAmount(0 - cost);
						p.getInventory().addItem(wool);
					} else {
						ItemManager.removeItems(user, p.getInventory(), wool, cost);
					}

					user.getPassivePerk().setCooldown(user.getPassivePerk().getMaxCooldown() + arrowCount);

					shootArrows(user, 20F / arrowCount, arrowCount / 2, arrow);
					shootArrows(user, -20F / arrowCount, arrowCount / 2, arrow);

//					for (int i = 0; i < 4; i++) {
//						p.launchProjectile(Arrow.class);
//					}
				} else if (user.getPassivePerk().getPerkName().equals(PerkType.TNT_ARROW.getPerkName())) {
					if (user.getPassivePerk().getCooldown() > 0) {
						user.getPassivePerk().setCooldown(user.getPassivePerk().getCooldown() - 1);
						return;
					}
					user.getPassivePerk().setCooldown(user.getPassivePerk().getMaxCooldown());
					new Scheduler() {
						@Override
						public void run() {
							if (arrow.isDead() || !arrow.isValid()) {
								cancel();
								if (user.getPassivePerk().getPerkName().equals(PerkType.TNT_ARROW.getPerkName())) {
//								if (user.getPassivePerk().getCooldown() > 0) {
//									user.getPassivePerk().setCooldown(user.getPassivePerk().getCooldown() - 1);
//									return;
//								}
									if (!p.getInventory().contains(Material.WOOL, PerkType.TNT_ARROW.getCost())) {
										new Scheduler() {
											@Override
											public void run() {
												user.getPassivePerk().setItem();
											}
										}.runTask();
										return;
									}

									int cost = PerkType.TNT_ARROW.getCost();
									ItemStack wool =
											new ItemStack(Material.WOOL, 1, user.getTeam().getType().getWoolColor());
									if (cost < 0) {
										wool.setAmount(0 - cost);
										p.getInventory().addItem(wool);
									} else {
										ItemManager.removeItems(user, p.getInventory(), wool, cost);
									}
									CraftTNTPrimed tnt =
											(CraftTNTPrimed) arrow.getWorld().spawn(arrow.getLocation(),
													TNTPrimed.class);

									tnt.setMetadata("boost", new FixedMetadataValue(Main.getInstance(), 2));
									tnt.setIsIncendiary(false);
									tnt.setFuseTicks(2);
									try {
										Field f = tnt.getHandle().getClass().getDeclaredField("source");
										f.setAccessible(true);
										f.set(tnt.getHandle(), user.getBukkitEntity().getHandle());
									} catch (NoSuchFieldException ex) {
										ex.printStackTrace();
									} catch (SecurityException ex) {
										ex.printStackTrace();
									} catch (IllegalArgumentException ex) {
										ex.printStackTrace();
									} catch (IllegalAccessException ex) {
										ex.printStackTrace();
									}
								}
							}
						}
					}.runTaskTimer(1);
				}
			}
		}
	}

	private void shootArrows(User user, float angle, int count, Arrow original) {
		Player p = user.getBukkitEntity();
		for (int i = 0; i < count; i++) {
			Arrow arrow = p.launchProjectile(Arrow.class);
			arrow.setKnockbackStrength(2);
			Vector vec = original.getVelocity();
			Location l = new Location(null, 0, 0, 0);
			l.setDirection(vec);
			l.setYaw(l.getYaw() + (i + 1) * angle);
			vec = l.getDirection();
			vec.multiply(original.getVelocity().length());
			arrow.setVelocity(vec);
			Main.getInstance().getIngame().arrows.put(arrow, user);
		}
	}
}
