package eu.darkcube.minigame.woolbattle.listener.ingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.util.BoundingBox;

public class ListenerPlayerMove extends Listener<PlayerMoveEvent> {

	public Map<Player, Integer> ghostBlockFixCount = new HashMap<>();

	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void handle(PlayerMoveEvent e) {
		Main.getInstance().getIngame().schedulerHeightDisplay.display(e.getPlayer());

		if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) {
			return;
		}
		Player p = e.getPlayer();
		Collection<Location> blocksToCheck = new HashSet<>();

		BoundingBox pbox = new BoundingBox(p, e.getTo());

//		double x = e.getTo().getX();
//		double z = e.getTo().getZ();
//		double offsetX = x > 0 ? 1 - (x - (long) x) : 0 - (x - (long) x);
//		double offsetZ = z > 0 ? 1 - (z - (long) z) : 0 - (z - (long) z);
//		Player p = e.getPlayer();
		Location to = e.getTo();
////		Y down
//		if (to.getBlock().getType() != Material.AIR && (to.getY() % 1.0 < 1.0 && to.getY() % 1.0 > .5)) {
//			if (ghostBlockFixCount.get(p) == null)
//				ghostBlockFixCount.put(p, 1);
//			else if (ghostBlockFixCount.get(p) > 4) {
//				ghostBlockFixCount.remove(p);
//				p.teleport(p.getLocation().add(0, .1, 0));
//			} else {
//				ghostBlockFixCount.put(p, ghostBlockFixCount.get(p) + 1);
//			}
//			blocksToCheck.add(to.clone());
//		}
//
////		Y up
//		Location t = to.clone();
////		t.setY(t.getY() + 1.5800000131130201);
////		if (t.getBlock().getType() != Material.AIR) {
////			blocksToCheck.add(t);
////		}
//		int additionX = 0;
////		X positive (else X negative)
//		if (offsetX < .3) {
//			additionX = 1;
//		} else if (offsetX > .7) {
//			additionX = -1;
//		}
//
//		if (additionX != 0) {
//			t = to.clone();
//			t.setX(t.getBlockX() + additionX);
//			blocksToCheck.add(t.clone());
//			blocksToCheck.add(t.add(0, 1, 0));
//		}
//
//		int additionZ = 0;
////		Z positive (else Z negative)
//		if (offsetZ < .3) {
//			additionZ = 1;
//		} else if (offsetZ > .7) {
//			additionZ = -1;
//		}
//
//		if (additionZ != 0) {
//			t = to.clone();
//			t.setZ(t.getBlockZ() + additionZ);
//			if (additionX != 0) {
//				blocksToCheck.add(t.clone().add(additionX, 0, 0));
//				blocksToCheck.add(t.clone().add(additionX, 1, 0));
//			}
//			blocksToCheck.add(t.clone());
//			blocksToCheck.add(t.add(0, 1, 0));
//		}
		Collection<Block> ignoreTeleport = new HashSet<>();
		boolean flawless = true;
		for (int x = -1; x < 2; x++) {
			for (int z = -1; z < 2; z++) {
				for (int y = -1; y < 3; y++) {
					Block r = to.clone().add(x, y, z).getBlock();
					if (r.getType() == Material.AIR) {
						continue;
					}
					BoundingBox box = new BoundingBox(r).shrink(0.2, 0, 0.2);
					if (box.collides(pbox)) {
						p.sendBlockChange(r.getLocation(), r.getType(), r.getData());
					}
//					if (r.getType() == Material.WOOL) {
						if (box.collides(pbox) && (box.collidesVertically(pbox) || r.getType() != Material.WOOL)) {
							if (to.getY() % 1 != 0 && r.getLocation().getBlockY() == to.getBlockY()) {
								flawless = false;
							}
							if (ghostBlockFixCount.getOrDefault(p, 0) > 5) {
								blocksToCheck.add(r.getLocation());
//								ignoreTeleport.add(r);
							}
						}
//					} else {
//						if (box.collides(pbox)) {
//							blocksToCheck.add(r.getLocation());
//							if (to.getBlockY() != r.getLocation().getBlockY()) {
//								ignoreTeleport.add(r);
//							}
//						}
//					}
				}
			}
		}
		if (flawless || e.getFrom().getY() < e.getTo().getY()) {
			ghostBlockFixCount.remove(p);
		} else {
			ghostBlockFixCount.put(p, ghostBlockFixCount.getOrDefault(p, 0) + 1);
		}

		Collection<Block> packets = new ArrayList<>();
		for (Location loc : blocksToCheck) {
			packets.add(loc.getBlock());
		}

		if (!packets.isEmpty()) {
			double y = -1;
			for (Block b : packets) {
				if (!ignoreTeleport.contains(b)) {
					BoundingBox box = new BoundingBox(b);
					y = Math.max(y, box.box.e);
				}
			}
			if (y != -1) {
				Location loc = new Location(to.getWorld(), to.getX(), y, to.getZ(), to.getYaw(), to.getPitch());
				p.teleport(loc);
			}
		}
	}

}
