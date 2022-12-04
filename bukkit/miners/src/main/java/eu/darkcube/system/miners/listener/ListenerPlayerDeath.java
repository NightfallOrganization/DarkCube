package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.gamephase.miningphase.Miningphase;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlayerDeath implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (Miners.getGamephase() == 2)
			Miners.getTeamManager().setPlayerTeam(e.getEntity(), 0, true);
		Player p = getKiller(e.getDeathMessage(), e.getEntity());
		if (p == null)
			Miners.sendTranslatedMessageAll(Message.PLAYER_DIED, e.getEntity().getCustomName());
		else
			Miners.sendTranslatedMessageAll(Message.PLAYER_WAS_KILLED, e.getEntity().getCustomName(),
					p.getCustomName());
		e.setDeathMessage(null);
	}

	private Player getKiller(String deathMessage, Player whoDied) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (!p.equals(whoDied))
				if (deathMessage.contains(p.getName()))
					return p;
		return null;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (Miners.getGamephase() == 1)
			e.setRespawnLocation(Miningphase.getSpawn(Miners.getTeamManager().getPlayerTeam(e.getPlayer())));
		else if (Miners.getGamephase() == 2)
			e.setRespawnLocation(e.getPlayer().getLocation());
	}

}
