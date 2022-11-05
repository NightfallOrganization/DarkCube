package eu.darkcube.system.lobbysystem.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerFish extends BaseListener {
	private Collection<UUID> ids = new HashSet<>();

	@EventHandler
	public void handle(PlayerArmorStandManipulateEvent e) {
		if (!UserWrapper.getUser(e.getPlayer().getUniqueId()).isBuildMode())
			e.setCancelled(true);
	}

	@EventHandler
	public void handle(PlayerFishEvent e) {
		if (e.getState() == State.CAUGHT_FISH || e.getState() == State.CAUGHT_ENTITY) {
			e.setExpToDrop(0);
			e.getCaught().remove();
			if (e.getCaught().getType() == EntityType.DROPPED_ITEM) {
				Location spawnloc = e.getHook().getLocation();
				spawnloc.subtract(0, 1, 0);
				while (spawnloc.getBlock().getType() != Material.AIR) {
					spawnloc = spawnloc.getBlock().getLocation().add(0, 1, 0);
				}
				spawnloc.subtract(0, 0.8, 0);
				final ArmorStand stand = e.getPlayer().getWorld().spawn(spawnloc, ArmorStand.class);
				stand.setArms(true);
				stand.setRightArmPose(new EulerAngle(0, 0.5, 0));
				stand.setVisible(false);
				stand.setSmall(true);
				stand.setItemInHand(new ItemStack(Material.RAW_FISH));
				ids.add(stand.getUniqueId());
				new BukkitRunnable() {

					private Location p;
					private Location s;

					@Override
					public void run() {
						p = e.getPlayer().getLocation();
						s = stand.getEyeLocation();
						if (stand.getEyeLocation().distance(e.getPlayer().getLocation()) < 2) {
							cancel();
							return;
						}
//						e.getPlayer().playSound(e.getHook().getLocation(), Sound.ANVIL_LAND, 100, 4);
						Vector v = new Vector(p.getX() - s.getX(), p.getY() - stand.getLocation().getY(),
								p.getZ() - s.getZ()).normalize();
						stand.teleport(new Location(s.getWorld(), s.getX() + v.getX() / 100,
								stand.getLocation().getY() + v.getY(), s.getZ() + v.getZ() / 100));
						stand.setVelocity(v.multiply(0.3));
					}

					@Override
					public synchronized void cancel() {
						e.getPlayer().getInventory().addItem(new ItemStack(Material.RAW_FISH));
						stand.remove();
						ids.remove(stand.getUniqueId());
						super.cancel();
					};

				}.runTaskTimer(Lobby.getInstance(), 5, 1);
			}

		}
	}
}
