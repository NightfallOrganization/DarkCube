package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerTNTEntityDamageByEntity extends Listener<EntityDamageByEntityEvent> {

	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void handle(EntityDamageByEntityEvent e) {
		if (e.getEntityType() != EntityType.PLAYER) {
			return;
		}
		Player p = (Player) e.getEntity();
		User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if(user.getTeam().getType() == TeamType.SPECTATOR) {
			e.setCancelled(true);
			return;
		}
		if (e.getDamager().getType() == EntityType.PRIMED_TNT) {
			TNTPrimed tnt = (TNTPrimed) e.getDamager();
			if (tnt.getLocation().distance(p.getLocation()) > tnt.getYield()) {
				e.setCancelled(true);
				return;
			}
			if (!(tnt.getSource() instanceof Player)) {
				return;
			}
			Player a = (Player) tnt.getSource();
			Location loc = p.getLocation().add(0, 0.5, 0);
			User attacker = Main.getInstance().getUserWrapper().getUser(a.getUniqueId());
			e.setCancelled(true);
			double x = loc.getX() - tnt.getLocation().getX();
//			double y = Math.abs(p.getLocation().getY() - tnt.getLocation().getY()) * .3 + 1;
//			double y = 1;
			double y = loc.getY() - tnt.getLocation().getY();
			y = y < 0.7 ? 0.7 : y;
			double z = loc.getZ() - tnt.getLocation().getZ();
//			x = Math.max(Math.min(x, 3), -3);
//			y = Math.max(Math.min(y, 3), -0) + .5;
//			z = Math.max(Math.min(z, 3), -3);
			Vector direction = new Vector(x, y, z).normalize();
//			v.multiply(str);
			double strength = 0;
			strength += tnt.getMetadata("boost").get(0).asDouble();
//			direction.multiply(tnt.getMetadata("boost").get(0).asDouble());

			double t = (tnt.getYield() - tnt.getLocation().distance(loc)) / (tnt.getYield() * 2) + 0.5;
			strength *= t;
			strength *= 1.2;
			if (!p.isOnGround()) {
				strength *= 1.2;
			}

			double strengthX = strength;
			double strengthY = strength;
			double strengthZ = strength;

			if (a.equals(p)) {
				if (p.getLocation().distance(tnt.getLocation()) < 1.3) {
//					direction.multiply(0.5);
					strengthX *= 0.2;
//					strengthY *= 0.5;
					strengthZ *= 0.2;
				}
			}

			Vector velocity = direction.clone();
			velocity.setX(velocity.getX() * strengthX);
			velocity.setY(1 + (velocity.getY() * strengthY / 5));
			velocity.setZ(velocity.getZ() * strengthZ);
			p.setVelocity(velocity);
//					.multiply(tnt.getYield() - p.getLocation().distance(tnt.getLocation())));
//					.multiply(calc(tnt.getLocation().distance(p.getLocation()), tnt.getYield() + 1)));

//			if (user.getTeam() != attacker.getTeam() && !attacker.isTrollMode()) {
//				user.setLastHit(attacker);
//				user.setTicksAfterLastHit(0);
//			}
			Main.getInstance().getIngame().attack(attacker, user);
		} else if (e.getDamager().getType() == EntityType.SNOWBALL) {
			Snowball bomb = (Snowball) e.getDamager();
			if (bomb.getMetadata("perk").size() != 0
					&& bomb.getMetadata("perk").get(0).asString().equals(PerkType.WOOL_BOMB.getPerkName().getName())) {
				e.setCancelled(true);
			}
		}
	}
//
//	private static double calc(double dist, double rad) {
////		return rad - dist <= 0 ? 0 : dist == 0 ? 1 : Math.pow(Math.pow(1 - dist / 1.3 / rad, 3), .6);
//		return rad - dist <= 0 ? 0 : dist == 0 ? 1 : Math.pow(1 - dist / 1.4 / rad, .7);
//	}
}