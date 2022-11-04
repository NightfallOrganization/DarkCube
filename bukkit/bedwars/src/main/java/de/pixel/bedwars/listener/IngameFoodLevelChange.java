package de.pixel.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.pixel.bedwars.team.Team;

public class IngameFoodLevelChange implements Listener {

	@EventHandler
	public void handle(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		Player p = (Player) e.getEntity();
		Team team = Team.getTeam(p);
		if(team == Team.SPECTATOR) {
			e.setCancelled(true);
		}
	}
}
