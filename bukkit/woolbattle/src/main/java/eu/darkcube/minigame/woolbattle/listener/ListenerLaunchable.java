package eu.darkcube.minigame.woolbattle.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;

public class ListenerLaunchable implements Listener {

	private Set<Player> executed = new HashSet<>();

	public void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				executed.clear();
			}
		}.runTaskTimer(Main.getInstance(), 1, 1);
	}

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity().getShooter();
		if (executed.contains(p)) {
			executed.remove(p);
			return;
		}
		LaunchableInteractEvent pe = new LaunchableInteractEvent(p, e.getEntity(),
				null);
		Bukkit.getPluginManager().callEvent(pe);
		if (pe.isCancelled()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (e.getItem() == null || e.getItem().getType() == Material.AIR) {
			return;
		}
		EntityType type = null;
		switch (e.getItem().getType()) {
		case ENDER_PEARL:
			type = EntityType.ENDER_PEARL;
			break;
		case EYE_OF_ENDER:
			type = EntityType.ENDER_SIGNAL;
			break;
		case EGG:
			type = EntityType.EGG;
			break;
		case SNOW_BALL:
			type = EntityType.SNOWBALL;
			break;
		case EXP_BOTTLE:
			type = EntityType.THROWN_EXP_BOTTLE;
			break;
		default:
			return;
		}
		if (executed.contains(e.getPlayer())) {
			e.setCancelled(true);
			return;
		}
		executed.add(e.getPlayer());
		LaunchableInteractEvent pe = new LaunchableInteractEvent(e.getPlayer(), type, e.getItem());
		Bukkit.getPluginManager().callEvent(pe);
		if (pe.isCancelled()) {
			e.setCancelled(true);
		}
	}
}