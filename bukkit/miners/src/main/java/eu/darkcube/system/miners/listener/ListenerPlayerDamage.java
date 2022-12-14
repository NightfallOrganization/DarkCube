package eu.darkcube.system.miners.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerDamage implements Listener {

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;
		if (Miners.getGamephase() == 0 || Miners.getGamephase() == 3) // can't take damage in lobby or end
			e.setCancelled(true);
		if (Miners.getTeamManager().getPlayerTeam((Player) e.getEntity()) == 0) // spectators don't take damage
			e.setCancelled(true);

		// player death
		// don't actually kill the player to skip the respawn menu
		// TODO
	}

	@EventHandler
	public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e) {
		if (!((e.getEntity() instanceof Player) && (e.getDamager() instanceof Player)))
			return;
		if (Miners.getTeamManager().getPlayerTeam((Player) e.getEntity()) == Miners.getTeamManager()
				.getPlayerTeam((Player) e.getDamager())) // can't damage your teammates
			e.setCancelled(true);
	}

}
