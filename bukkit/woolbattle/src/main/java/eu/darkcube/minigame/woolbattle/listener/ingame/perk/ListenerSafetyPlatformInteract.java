package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerSafetyPlatformInteract
				extends Listener<PlayerInteractEvent> {

	public static final Item PLATFORM = PerkType.SAFETY_PLATFORM.getItem();
	public static final Item PLATFORM_COOLDOWN = PerkType.SAFETY_PLATFORM.getCooldownItem();

	@Override
	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_AIR
						&& e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player p = e.getPlayer();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());

		ItemStack item = e.getItem();
		if (item == null) {
			return;
		}
		String itemid = ItemManager.getItemId(item);
		Perk perk = user.getPerkByItemId(itemid);
		if (perk == null) {
			return;
		}
		if (PLATFORM_COOLDOWN.getItemId().equals(itemid)) {
			deny(user, perk);
			e.setCancelled(true);
			return;
		} else if (!PLATFORM.getItemId().equals(itemid)) {
			return;
		}
		if (perk.getCooldown() > 0
						|| !p.getInventory().contains(Material.WOOL, PerkType.SAFETY_PLATFORM.getCost())) {
			deny(user, perk);
			e.setCancelled(true);
			return;
		}
		e.setCancelled(true);
		boolean success = setBlocks(user);
		if (!success) {
			deny(user, perk);
			return;
		}

		ItemManager.removeItems(user, p.getInventory(), user.getSingleWoolItem(), PerkType.SAFETY_PLATFORM.getCost());

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

	private boolean setBlocks(User p) {

		final Location pLoc = p.getBukkitEntity().getLocation();
		Location center = pLoc.getBlock().getLocation().add(0.5, 0.25, 0.5).setDirection(pLoc.getDirection());
//		// Pos pos
//		boolean pp = false;
//		// Pos neg
//		boolean pn = false;
//		// Neg pos
//		boolean np = false;
//		// Neg neg
//		boolean nn = false;
//		if (center.getBlock().getType() == Material.AIR
//						&& center.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
//			pp = true;
//		}
//		if (center.clone().add(0, 0, -1).getBlock().getType() == Material.AIR
//						&& center.clone().add(0, 1, -1).getBlock().getType() == Material.AIR) {
//			pn = true;
//		}
//		if (center.clone().add(-1, 0, 0).getBlock().getType() == Material.AIR
//						&& center.clone().add(-1, 1, 0).getBlock().getType() == Material.AIR) {
//			np = true;
//		}
//		if (center.clone().add(-1, 0, -1).getBlock().getType() == Material.AIR
//						&& center.clone().add(-1, 1, -1).getBlock().getType() == Material.AIR) {
//			nn = true;
//		}
		center.subtract(-0.5, 0, -0.5);
//
//		boolean blocked = !pp && !pn && !np && !nn;
//		if (blocked) {
//			return false;
//		}
//
//		boolean freeSpace = pp && pn && np && nn;
//		if (!freeSpace) {
//			if (pp) {
//				center = pLoc.getBlock().getLocation().add(0, 0.25, 0).setDirection(pLoc.getDirection());
//			} else if (pn) {
//				center = pLoc.getBlock().getLocation().add(0, 0.25, -1).setDirection(pLoc.getDirection());
//			} else if (np) {
//				center = pLoc.getBlock().getLocation().add(-1, 0.25, 0).setDirection(pLoc.getDirection());
//			} else if (nn) {
//				center = pLoc.getBlock().getLocation().add(-1, 0.25, -1).setDirection(pLoc.getDirection());
//			}
//		}
//
//		final double radius = freeSpace ? 2.5 : 2.0;
		final double radius = 2.5;
		for (double x = -radius; x <= radius; x++) {
			for (double z = -radius; z <= radius; z++) {
				if (isCorner(x, z, radius)) {
					continue;
				}
				block(center.clone().add(x, -1, z), p);
			}
		}

//		for (int x = -2; x < 3; x++) {
//			for (int z = -2; z < 3; z++) {
//				if (isCorner(x, z, 2))
//					continue;
//				block(p.getBukkitEntity().getLocation().add(x, -1, z), p);
//			}
//		}
		p.getBukkitEntity().teleport(center);
		return true;
//		p.getBukkitEntity().teleport(p.getBukkitEntity().getLocation().getBlock().getLocation().add(.5, .25, .5)
//				.setDirection(p.getBukkitEntity().getLocation().getDirection()));
	}

	private boolean isCorner(double x, double z, double r) {
		return (x == -r && z == -r) || (x == r && z == -r)
						|| (x == -r && z == r) || (x == r && z == r);
	}

	@SuppressWarnings("deprecation")
	private void block(Location loc, User u) {
		if (loc.getBlock().getType() == Material.AIR) {
			loc.getBlock().setType(Material.WOOL);
			loc.getBlock().setData(u.getTeam().getType().getWoolColorByte());
			WoolBattle.getInstance().getIngame().placedBlocks.add(loc.getBlock());
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
