package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerWallGeneratorInteract extends Listener<PlayerInteractEvent> {

	public static final Item WALL = PerkType.WALL_GENERATOR.getItem();
	public static final Item WALL_COOLDOWN = PerkType.WALL_GENERATOR.getCooldownItem();

	private static final int[][] ids = {
			{
					-2, -1, 0, 1, 2
			}, {
					-2, -1, 0, 1, 2
			}, {
					-2, -1, 0, 1, 2
			},
			{
					-2, -1, 0, 1, 2
			}
	};

	public Map<User, WallBlockPlacerScheduler> placeTasks = new HashMap<>();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			ItemStack item = e.getItem();
			if (item == null) {
				return;
			}
			String itemid = ItemManager.getItemId(item);
			Perk perk = user.getPerkByItemId(itemid);
			if (perk == null) {
				return;
			}
			if (WALL_COOLDOWN.getItemId().equals(itemid)) {
				deny(user, perk);
				e.setCancelled(true);
				return;
			} else if (!WALL.getItemId().equals(itemid)) {
				return;
			}
			e.setCancelled(true);
			if (perk.getCooldown() > 0
					|| !p.getInventory().contains(Material.WOOL, PerkType.WALL_GENERATOR.getCost())) {
				deny(user, perk);
				return;
			}

			if (placeTasks.get(user) != null) {
				deny(user, perk);
				return;
			}

			placeTasks.put(user, new WallBlockPlacerScheduler(perk));
			placeTasks.get(user).runTaskTimer(1);

		}
	}

	private class WallBlockPlacerScheduler extends Scheduler {

		private final Perk perk;
		private int id0 = 0;
		private int id1 = 0;
		private final Location center;
		private final Vector normal;

		public WallBlockPlacerScheduler(Perk perk) {
			this.perk = perk;
			center = calculateCenter();
			normal = calculateNormal();
		}

		@Override
		public void run() {
			User user = perk.getOwner();
			Player p = user.getBukkitEntity();
			if (p.isSneaking()) {
				cancel();
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.WALL_GENERATOR.getCost())) {
				deny(user, perk);
				cancel();
				return;
			}

			setNextBlock();

			id1++;
			if (id1 >= ids[id0].length) {
				id1 %= ids[id0].length;
				id0++;
			}

			if (id0 >= ids.length) {
				cancel();
				return;
			}
		}

		private void setNextBlock() {
			Location loc = center.clone();
			Location loc2 = loc.clone();
			loc2.setDirection(normal.clone()).setYaw(loc2.getYaw() + 90);
			Vector vec = loc2.getDirection().normalize();
//			loc.add(vec.multiply(ids[id0][id1]));
			loc = add(loc, vec.multiply(ids[id0][id1]));
			loc.add(0, id0 - 1, 0);

			setBlock(loc, perk.getOwner());
		}

		private Location add(Location loc, Vector vec) {
//			for (int i = 0; i < blockCount; i++) {
//				loc = loc.getBlock().getLocation().add(vec);
//			}
//			if(blockCount < 0) {
//				vec = vec.clone().multiply(-1);
//			}
//			for (int i = 0; i > blockCount; i--) {
//				loc = loc.getBlock().getLocation().add(vec);
//			}
			Vector v2 = vec.clone().normalize();
			for (int i = 0; i < vec.length(); i++) {
				loc = loc.getBlock().getLocation().add(.5, 0, .5).add(v2);
			}
//			System.out.println(vec.length());
			return loc;
		}

		@SuppressWarnings("deprecation")
		private void setBlock(Location loc, User user) {
			if (loc.getBlock().getType() == Material.AIR
					&& !Main.getInstance().getIngame().breakedWool.containsKey(loc.getBlock())) {

				ItemManager.removeItems(perk.getOwner(), perk.getOwner().getBukkitEntity().getInventory(), user.getSingleWoolItem(),
						PerkType.WALL_GENERATOR.getCost());

				loc.getBlock().setType(Material.WOOL);
				loc.getBlock().setData(user.getTeam().getType().getWoolColor());
				Main.getInstance().getIngame().placedBlocks.add(loc.getBlock());
			}
		}

		private Vector calculateNormal() {
			Location loc = perk.getOwner().getBukkitEntity().getLocation();
			loc.setPitch(0);
			return loc.getDirection().normalize();
		}

		private Location calculateCenter() {
			Location loc = perk.getOwner().getBukkitEntity().getLocation();
			loc.add(loc.getDirection().setY(0).normalize().multiply(2));
			return loc;
		}

		@Override
		public void cancel() {
			super.cancel();
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

			placeTasks.remove(perk.getOwner());
		}
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}
}
