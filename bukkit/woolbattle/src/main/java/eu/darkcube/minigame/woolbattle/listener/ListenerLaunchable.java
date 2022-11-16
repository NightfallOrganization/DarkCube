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

import eu.darkcube.minigame.woolbattle.event.LaunchableInteractEvent;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerLaunchable implements Listener {

	private Set<Player> executed = new HashSet<>();

	public void start() {
		new Scheduler() {

			@Override
			public void run() {
				ListenerLaunchable.this.executed.clear();
			}

		}.runTaskTimer(1, 1);
	}

	@EventHandler
	public void handle(ProjectileLaunchEvent e) {
		if (!(e.getEntity().getShooter() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity().getShooter();
		if (this.executed.contains(p)) {
			this.executed.remove(p);
			return;
		}
		LaunchableInteractEvent pe = new LaunchableInteractEvent(p, e.getEntity(), p.getItemInHand());
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
			break;
		}
		if (this.executed.contains(e.getPlayer())) {
			e.setCancelled(true);
			return;
		}
		this.executed.add(e.getPlayer());
		LaunchableInteractEvent pe = new LaunchableInteractEvent(e.getPlayer(), type, e.getItem());
		Bukkit.getPluginManager().callEvent(pe);
		if (pe.isCancelled()) {
			e.setCancelled(true);
		}
	}

}