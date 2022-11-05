package eu.darkcube.system.lobbysystem.listener;

import static org.bukkit.Material.*;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class ListenerBoostPlate extends BaseListener {
	
	@EventHandler
	public void handle(PlayerMoveEvent e) {
		double x = e.getFrom().getBlockX();
		double z = e.getFrom().getBlockZ();
		double X = e.getTo().getBlockX();
		double Z = e.getTo().getBlockZ();
		if (x != X || z != Z) {
			Player p = e.getPlayer();
			Location loc = e.getTo().clone();
			Material type = loc.getBlock().getType();
			if (type == WOOD_PLATE || type == STONE_PLATE || type == GOLD_PLATE || type == IRON_PLATE) {
				loc.subtract(0, 2, 0);
				double power = 0;
				double ypower = 0;
				while (loc.getBlock().getType() == Material.REDSTONE_BLOCK
						|| loc.getBlock().getType() == Material.EMERALD_BLOCK) {
					if (loc.getBlock().getType() == Material.EMERALD_BLOCK) {
						ypower++;
						ypower += ypower / 2.0;
					} else {
						power++;
						ypower++;
						power += power / 2.0;
						ypower += ypower / 2.0;
					}
					loc.subtract(0, 1, 0);
				}
//				if (power > 7)
//					power = 7;
				if (power > 0 || ypower > 0) {
					Vector vec = p.getLocation().getDirection();
					vec.multiply(2.8);
					vec.multiply(power / 2.0);
					vec.setY(ypower / 5.0 + 0.7);
					p.playSound(p.getLocation(), Sound.GHAST_FIREBALL, 10F, 1.3F);
					p.setVelocity(vec);
				}
			}
		}
	}
}
