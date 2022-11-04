package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.RayTrace;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerBlinkLaunchable extends Listener<LaunchableInteractEvent> {

	public static final Item BLINK = PerkType.BLINK.getItem();
	public static final Item BLINK_COOLDOWN = PerkType.BLINK.getCooldownItem();

//	@EventHandler
//	public void handle(PlayerInteractEntityEvent e) {
//		System.out.println("Called");
//	}

	@Override
	@EventHandler
	public void handle(LaunchableInteractEvent e) {
		if (e.getEntityType() == EntityType.ENDER_SIGNAL || e.getEntityType() == EntityType.ENDER_PEARL) {
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());

			ItemStack item = e.getItem() == null ? p.getItemInHand() : e.getItem();
			if (item == null) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (BLINK_COOLDOWN.getItemId().equals(itemid)) {
				deny(user, perk);
				e.setCancelled(true);
				return;
			} else if (!BLINK.getItemId().equals(itemid)) {
				return;
			}
			if (perk.getCooldown() > 0 || !p.getInventory().contains(Material.WOOL, PerkType.BLINK.getCost())) {
				deny(user, perk);
				e.setCancelled(true);
				return;
			}

			e.setCancelled(true);

			if (!teleport(user)) {
				return;
			}

			ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.BLINK.getCost());

			new Scheduler() {
				int cd = perk.getMaxCooldown();

				@Override
				public void run() {
					if (cd <= 0) {
						this.cancel();
						perk.setCooldown(0);
						return;
					}
					perk.setCooldown(cd--);
				}
			}.runTaskTimer(20);
		}
	}
//	@Override
//	@EventHandler
//	public void handle(PlayerInteractEvent e) {
//		Player p = e.getPlayer();
//		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
//		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
//			ItemStack item = e.getItem();
//			if (item == null)
//				return;
//			String itemid = ItemManager.getItemId(item);
//			Perk perk = user.getPerkByItemId(itemid);
//			if (BLINK_COOLDOWN.getItemId().equals(itemid)) {
//				Ingame.playSoundNotEnoughWool(user);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					};
//				}.runTask();
//				e.setCancelled(true);
//				return;
//			} else if (!BLINK.getItemId().equals(itemid)) {
//				return;
//			} else if (perk == null) {
//				e.setCancelled(true);
//				return;
//			}
//			e.setCancelled(true);
//			if (!p.getInventory().contains(Material.WOOL, PerkType.BLINK.getCost()) || perk.getCooldown() != 0) {
//				Ingame.playSoundNotEnoughWool(user);
//				new Scheduler() {
//					@Override
//					public void run() {
//						perk.setItem();
//					};
//				}.runTask();
//				return;
//			}
//			if(!teleport(user)) {
//				return;
//			}
//
//			new Scheduler() {
//				int cd = PerkType.BLINK.getCooldown() + 1;
//
//				@Override
//				public void run() {
//					if (cd <= 1) {
//						this.cancel();
//						perk.setCooldown(0);
//						return;
//					}
//					perk.setCooldown(--cd);
//				}
//			}.runTaskTimer(20);
//
//		}
//	}

	public static boolean teleport(User user) {
		Player p = user.getBukkitEntity();
		int dist = 15;
		if (p.isSneaking()) {
			RayTrace raytrace = new RayTrace(p.getEyeLocation().toVector(), p.getEyeLocation().getDirection());
			List<Vector> positions = raytrace.traverse(dist, 0.1);
			for (int i = 0; i < positions.size(); i++) {
				Location pos = positions.get(i).toLocation(p.getWorld());
				if (!checkBlock(pos)) {
					if (checkBlock(pos.add(0, 1, 0)) && checkBlock(pos.add(0, 1, 0))) {
						pos.subtract(0, 2, 0).setDirection(p.getEyeLocation().getDirection());
						pos.setY(pos.getBlockY() + 1.01);
						p.teleport(pos);
						return true;
					}
				}
			}
		}
		Vector v = user.getBukkitEntity().getEyeLocation().getDirection().normalize();
		for (; dist > 1; dist--) {
			Location loc = user.getBukkitEntity().getEyeLocation().add(v.clone().multiply(dist));
			Location loc2 = loc.clone().add(0, 1, 0);
			if (loc.getBlock().getType() == Material.AIR && loc2.getBlock().getType() == Material.AIR) {
				boolean freeUp = checkLayer(loc2.clone().add(0, 1, 0));
				boolean freeHead = checkLayer(loc2);
				boolean freeFoot = checkLayer(loc);
				boolean freeDown = checkLayer(loc.clone().subtract(0, 1, 0));

				boolean free = freeUp && freeHead && freeFoot && freeDown;
				if (free) {
					user.getBukkitEntity().teleport(loc.setDirection(v));
				} else {
					user.getBukkitEntity().teleport(loc.getBlock().getLocation().add(.5, .1, .5).setDirection(v));
				}
				return true;
			}
		}
		return false;
	}

	private static boolean checkBlock(Location block) {
		return block.getBlock().getType() == Material.AIR;
	}

	private static boolean checkLayer(Location layer) {
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				if (layer.clone().add(x, 0, z).getBlock().getType() != Material.AIR) {
					return false;
				}
			}
		}
		return true;
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}
}