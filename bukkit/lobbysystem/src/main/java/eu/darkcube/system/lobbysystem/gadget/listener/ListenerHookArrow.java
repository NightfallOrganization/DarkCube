package eu.darkcube.system.lobbysystem.gadget.listener;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventGadgetSelect;
import eu.darkcube.system.lobbysystem.gadget.Gadget;
import eu.darkcube.system.lobbysystem.listener.BaseListener;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerHookArrow extends BaseListener {

	private Collection<UUID> cooldownPlayers = new HashSet<>();
	private Map<UUID, Collection<Bat>> bats = new HashMap<>();
	private Map<UUID, Collection<BukkitRunnable>> pullTaskByUUID = new HashMap<>();

	@EventHandler
	public void handle(ProjectileHitEvent e) {
		Projectile projectile = e.getEntity();
		ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) {
			Player p = (Player) source;
			LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
			if (user.getGadget() == Gadget.HOOK_ARROW) {
				if (this.cooldownPlayers.contains(p.getUniqueId())) {
					p.getInventory().setItem(35,
							Item.GADGET_HOOK_ARROW_ARROW.getItem(user.getUser()));
					this.cooldownPlayers.remove(p.getUniqueId());
					this.pullEntityToLocation(p, projectile);
					p.playSound(p.getLocation(), Sound.MAGMACUBE_JUMP, 10.0F, 1.0F);
					p.setAllowFlight(false);
				}
			}
		}
	}

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		final Projectile projectile = e.getEntity();
		final ProjectileSource source = projectile.getShooter();
		if (source instanceof Player) {
			Player p = (Player) source;
			LobbyUser user = UserWrapper.fromUser(UserAPI.getInstance().getUser(p));
			if (user.getGadget() == Gadget.HOOK_ARROW) {
				projectile.setMetadata("hookarrow",
						new FixedMetadataValue(Lobby.getInstance(), true));
				p.getInventory().setItem(35, null);
				this.cooldownPlayers.add(p.getUniqueId());
				p.setLeashHolder(projectile);
				new BukkitRunnable() {

					private int[] xs = new int[] {-1, 0, 1};
					private int[] zs = new int[] {-1, 0, 1};

					@Override
					public void run() {
						if (projectile != null && projectile.isValid() && !projectile.isDead()) {
							int chunkx = projectile.getLocation().getChunk().getX();
							int chunkz = projectile.getLocation().getChunk().getZ();
							World world = projectile.getWorld();
							for (int x : this.xs) {
								for (int z : this.zs) {
									if (!world.isChunkLoaded(chunkx + x, chunkz + z))
										world.loadChunk(chunkx + x, chunkz + z);
								}
							}
						}
					}
				}.runTaskTimer(Lobby.getInstance(), 2, 2);
				new BukkitRunnable() {

					@Override
					public void run() {
						final Bat bat = p.getWorld().spawn(p.getEyeLocation(), Bat.class);
						// TODO: Removed, idk what it does anymore
						// ((CraftBat) bat).getHandle().b(true);
						bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000,
								100000, true, false));
						bat.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100000,
								true, false));

						bat.setLeashHolder(projectile);
						if (!ListenerHookArrow.this.bats.containsKey(p.getUniqueId())) {
							ListenerHookArrow.this.bats.put(p.getUniqueId(), new HashSet<>());
						}
						ListenerHookArrow.this.bats.get(p.getUniqueId()).add(bat);
						new BukkitRunnable() {
							Border border = Lobby.getInstance().getDataManager().getBorder();

							@Override
							public void run() {
								if (p.isOnline() && bat.isValid() && !bat.isDead()
										&& bat.isLeashed()) {
									if (this.border.isOutside(projectile)) {
										projectile.remove();
										p.getInventory().setItem(35, Item.GADGET_HOOK_ARROW_ARROW
												.getItem(user.getUser()));
										p.setAllowFlight(true);
										this.cancel();
										return;
									}
									bat.teleport(p.getEyeLocation());
								} else {
									this.cancel();
								}
							}

							@Override
							public synchronized void cancel() {
								if (!ListenerHookArrow.this.bats.containsKey(p.getUniqueId())) {
									ListenerHookArrow.this.bats.get(p.getUniqueId()).remove(bat);
								}
								if (!bat.isDead()) {
									bat.setLeashHolder(null);
									bat.remove();
								}
								projectile.remove();
								super.cancel();
							};
						}.runTaskTimer(Lobby.getInstance(), 1L, 1L);
					}
				}.runTaskLater(Lobby.getInstance(), 5);

				p.setAllowFlight(false);
			}
		}
	}

	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getDamager().hasMetadata("hookarrow")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(EntityDamageEvent e) {
		if (e.getEntity() instanceof Bat) {
			if (((Bat) e.getEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void handle(ItemSpawnEvent e) {
		if (e.getEntity().getItemStack().getType() == Material.LEASH)
			e.setCancelled(true);
	}

	@EventHandler
	public void handle(EventGadgetSelect e) {
		LobbyUser user = e.getUser();
		Player p = user.getUser().asPlayer();
		Gadget g = user.getGadget();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (p != null) {
					if (e.getGadget() == Gadget.HOOK_ARROW) {
						p.getInventory().setItem(35,
								Item.GADGET_HOOK_ARROW_ARROW.getItem(user.getUser()));
					} else {
						if (g == Gadget.HOOK_ARROW) {
							p.getInventory().setItem(35, new ItemStack(Material.AIR));
						}
					}
				}
			}
		}.runTask(Lobby.getInstance());
	}

	private void pullEntityToLocation(Player p, Projectile projectile) {
		Location location = projectile.getLocation();
		if (this.pullTaskByUUID.get(p.getUniqueId()) == null)
			this.pullTaskByUUID.put(p.getUniqueId(), new HashSet<>());
		BukkitRunnable runnable = new BukkitRunnable() {

			private int count = 0;
			private double oldDist = 0;

			@Override
			public void run() {
				if (this.count > 6) {
					this.cancel();
					return;
				}
				Location entityLoc = p.getLocation();

				double d = location.distance(entityLoc);
				if (Math.abs(this.oldDist - d) < 3) {
					this.count++;
				} else {
					this.count = 0;
				}
				this.oldDist = d;
				if (d < 2) {
					this.cancel();
					return;
				}
				double v_x = (location.getX() - entityLoc.getX());
				double v_y = (location.getY() - entityLoc.getY());
				double v_z = (location.getZ() - entityLoc.getZ());
				Vector v = new Vector(v_x, v_y, v_z).normalize();
				v.multiply(2);
				p.setVelocity(v);
			}

			@Override
			public synchronized void cancel() {
				p.setAllowFlight(true);
				projectile.remove();
				if (!ListenerHookArrow.this.pullTaskByUUID.containsKey(p.getUniqueId())) {
					ListenerHookArrow.this.pullTaskByUUID.get(p.getUniqueId()).remove(this);
				}
				super.cancel();
			}
		};
		this.pullTaskByUUID.get(p.getUniqueId()).add(runnable);
		runnable.runTaskTimer(Lobby.getInstance(), 3, 2);
	}

}
