package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;

public class ListenerRonjasToiletHit extends Listener<ProjectileHitEvent> {

	private static final double RANGE = 4;

	@Override
	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.EGG) {
			Egg egg = (Egg) e.getEntity();
			if (!(egg.getShooter() instanceof Player)) {
				return;
			}
			if (!egg.hasMetadata("perk")) {
				return;
			}
			if (!egg.getMetadata("perk")
					.get(0)
					.asString()
					.equals(PerkType.RONJAS_TOILET_SPLASH.getPerkName().getName())) {
				return;
			}
			ParticleEffect.DRIP_WATER.display(.3F, 1F, .3F, 1, 250, egg.getLocation(), 50);

			if (egg.getTicksLived() <= 3) {
				WoolBattle.getInstance()
						.getUserWrapper()
						.getUsers()
						.stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getLocation().distance(egg.getLocation()) < ListenerRonjasToiletHit.RANGE)
						.map(User::getBukkitEntity)
						.forEach(t -> {
							Vector v = egg.getVelocity().multiply(1.3);
							v.setY(egg.getVelocity().getY()).normalize().multiply(3).setY(v.getY() + 1.2);
							t.setVelocity(v);
						});
			} else {
				WoolBattle.getInstance()
						.getUserWrapper()
						.getUsers()
						.stream()
						.filter(u -> u.getTeam().getType() != TeamType.SPECTATOR)
						.filter(u -> u.getBukkitEntity().getWorld().equals(egg.getWorld()))
						.filter(u -> u.getBukkitEntity().getLocation().distance(egg.getLocation()) < ListenerRonjasToiletHit.RANGE + 1)
						.map(User::getBukkitEntity)
						.forEach(t -> {
							double x = t.getLocation().getX() - egg.getLocation().getX();
							double y = t.getLocation().getY() - egg.getLocation().getY();
							double z = t.getLocation().getZ() - egg.getLocation().getZ();
							t.setVelocity(new Vector(x, Math.max(1, y), z).normalize().multiply(2));
						});
			}
		}
	}

}